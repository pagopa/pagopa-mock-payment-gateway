package it.gov.pagopa.xpay.service;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.xpay.dto.*;
import it.gov.pagopa.xpay.repository.XPayRepository;
import it.gov.pagopa.xpay.utils.XPayUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_REFUND_ERROR;
import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_REFUND_OUTCOME;

@Service
@Log4j2
public class XPayRefundService {
    @Value("${xpay.apikey-alias}")
    private String apiKey;

    @Value("${xpay.secret-key}")
    private String chiaveSegreta;

    @Autowired
    private XPayRepository xPayRepository;

    @Autowired
    private TableConfigRepository configRepository;

    private String outcomeConfig;
    private XPayErrorEnum errorConfig;

    private void refreshConfigs() {
        outcomeConfig = configRepository.findByPropertyKey(XPAY_REFUND_OUTCOME).getPropertyValue();
        errorConfig = XPayUtils.getErrorConfig(configRepository.findByPropertyKey(XPAY_REFUND_ERROR).getPropertyValue());
    }

    public ResponseEntity<XPayRefundResponse> getMock(XPayRefundRequest request) {
        log.info("XPay Refund - Request from PGS: " + request);
        refreshConfigs();

        String codiceTransazione = request.getCodiceTransazione();
        String idOperazione = UUID.randomUUID().toString();
        long timeStamp = System.currentTimeMillis();
        String macToCheck;
        String macToReturn;
        String macForError;

        try {
            log.info("XPay Refund - Generating MAC for transactionId: " + codiceTransazione);
            macToCheck = XPayUtils.getBaseMac(codiceTransazione, request.getDivisa(), request.getImporto(), request.getTimeStamp(), apiKey, chiaveSegreta);
            macToReturn = XPayUtils.getMacWithoutNonce(XPayOutcome.OK.toString(), idOperazione, Long.toString(timeStamp), chiaveSegreta);
            macForError = XPayUtils.getMacWithoutNonce(XPayOutcome.KO.toString(), idOperazione, Long.toString(timeStamp), chiaveSegreta);
        } catch (Exception e) {
            log.error("XPay Refund - Exception during the creation of the MAC string: ", e);
            XPayErrorEnum error = XPayErrorEnum.ERROR_50;

            return ResponseEntity.status(error.getHttpStatus())
                    .body(createXPayRefundResponse(XPayOutcome.KO, idOperazione, null, error, timeStamp));
        }

        if(outcomeConfig.equals("OK")) {
            if (macToCheck.equals(request.getMac())) {
                log.info("XPay Refund - MAC verified");

                return ResponseEntity.ok()
                        .body(createXPayRefundResponse(XPayOutcome.OK, idOperazione, macToReturn, null, timeStamp));
            } else {
                log.info("XPay Refund - MAC not verified");
                XPayErrorEnum error = XPayErrorEnum.ERROR_3;

                return ResponseEntity.status(error.getHttpStatus())
                        .body(createXPayRefundResponse(XPayOutcome.KO, idOperazione, macForError, error, timeStamp));
            }
        } else {
            return ResponseEntity.status(errorConfig.getHttpStatus())
                    .body(createXPayRefundResponse(XPayOutcome.KO, idOperazione, macForError, errorConfig, timeStamp));
        }
    }

    private XPayRefundResponse createXPayRefundResponse(XPayOutcome xPayOutcome, String idOperazione, String mac,
                                                          XPayErrorEnum error, Long timeStamp) {

        XPayRefundResponse xPayRefundResponse = new XPayRefundResponse();
        xPayRefundResponse.setEsito(xPayOutcome);
        xPayRefundResponse.setIdOperazione(idOperazione);
        xPayRefundResponse.setTimeStamp(timeStamp);
        xPayRefundResponse.setMac(mac);

        if(error == null)
            xPayRefundResponse.setInfoAPM(new XPayInfoApm("12345", "Info APM"));
        else
            xPayRefundResponse.setErrore(new XPayError(error.getErrorCode(), error.getDescription()));

        return xPayRefundResponse;
    }
}
