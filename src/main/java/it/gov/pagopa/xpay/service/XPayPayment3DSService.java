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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_PAYMENT_ERROR;
import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_PAYMENT_OUTCOME;

@Service
@Log4j2
public class XPayPayment3DSService {
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
        outcomeConfig = configRepository.findByPropertyKey(XPAY_PAYMENT_OUTCOME).getPropertyValue();
        errorConfig = XPayUtils.getErrorConfig(configRepository.findByPropertyKey(XPAY_PAYMENT_ERROR).getPropertyValue());
    }

    public ResponseEntity<XPayPaymentResponse> getMock(XPayPaymentRequest request) {
        log.info("XPay Paga3DS - Request from PGS: " + request);
        refreshConfigs();

        String codiceTransazione = request.getCodiceTransazione();
        String idOperazione = UUID.randomUUID().toString();
        String macToReturn;

        try {
            log.info("XPay Paga3DS - Generating MAC for transactionId: " + codiceTransazione);
            macToReturn = XPayUtils.getMacToReturn(codiceTransazione, request.getDivisa(), request.getImporto(),
                    request.getTimeStamp(), apiKey, chiaveSegreta);
        } catch (Exception e) {
            log.error("XPay Paga3DS - Exception during the creation of the MAC string: ", e);
            XPayErrorEnum error = XPayErrorEnum.ERROR_50;

            return ResponseEntity.status(error.getHttpStatus())
                    .body(createXPayPaymentResponse(XPayOutcome.KO, idOperazione, null, error));
        }

        if(outcomeConfig.equals("OK")) {
            if (macToReturn.equals(request.getMac())) {
                log.info("XPay Paga3DS - MAC verified");
                XPayPaymentResponse xPayPaymentResponse = createXPayPaymentResponse(XPayOutcome.OK, idOperazione, macToReturn, null);
                if(request.getParametriAggiuntivi() != null)
                    xPayPaymentResponse.setParametriAggiuntivi(request.getParametriAggiuntivi());

                return ResponseEntity.ok().body(xPayPaymentResponse);
            } else {
                log.info("XPay Paga3DS - MAC not verified");
                XPayErrorEnum error = XPayErrorEnum.ERROR_3;

                return ResponseEntity.status(error.getHttpStatus())
                        .body(createXPayPaymentResponse(XPayOutcome.KO, idOperazione, macToReturn, error));
            }
        } else {
            return ResponseEntity.status(errorConfig.getHttpStatus())
                    .body(createXPayPaymentResponse(XPayOutcome.KO, idOperazione, macToReturn, errorConfig));
        }
    }

    private XPayPaymentResponse createXPayPaymentResponse(XPayOutcome xPayOutcome, String idOperazione, String mac,
                                                          XPayErrorEnum error) {

        XPayPaymentResponse xPayPaymentResponse = new XPayPaymentResponse();
        xPayPaymentResponse.setEsito(xPayOutcome);
        xPayPaymentResponse.setIdOperazione(idOperazione);
        xPayPaymentResponse.setTimeStamp(System.currentTimeMillis());
        xPayPaymentResponse.setMac(mac);

        if(error == null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            xPayPaymentResponse.setCodiceAutorizzazione("A14619");
            xPayPaymentResponse.setCodiceConvenzione("C94537456313");
            xPayPaymentResponse.setData(LocalDate.now().format(formatter));
            xPayPaymentResponse.setNazione("ITA");
            xPayPaymentResponse.setRegione("Europa");
            xPayPaymentResponse.setBrand("APPLEPAY");
            xPayPaymentResponse.setTipoProdotto("VISA CLASSIC - DEBIT – N");
            xPayPaymentResponse.setTipoTransazione("3DS_FULL");
            xPayPaymentResponse.setPpo("Apple pay");
        } else {
            xPayPaymentResponse.setErrore(new XPayError(error.getErrorCode(), error.getDescription()));
        }

        return xPayPaymentResponse;
    }
}
