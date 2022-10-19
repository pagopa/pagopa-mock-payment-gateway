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

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_ORDER_STATUS_ERROR;
import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_ORDER_STATUS_OUTCOME;

@Service
@Log4j2
public class XPayOrderStatusService {
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
    private String codiceTransazione;
    private final LocalDateTime nowDateTime = LocalDateTime.now();

    private void refreshConfigs() {
        outcomeConfig = configRepository.findByPropertyKey(XPAY_ORDER_STATUS_OUTCOME).getPropertyValue();
        errorConfig = XPayUtils.getErrorConfig(configRepository.findByPropertyKey(XPAY_ORDER_STATUS_ERROR).getPropertyValue());
    }

    public ResponseEntity<XPayOrderResponse> getMock(XPayOrderRequest request) {
        log.info("XPay OrderStatus - Request from PGS: " + request);
        refreshConfigs();

        this.codiceTransazione = request.getCodiceTransazione();
        String idOperazione = UUID.randomUUID().toString();
        String macToReturn;

        try {
            log.info("XPay OrderStatus - Generating MAC for transactionId: " + codiceTransazione);
            macToReturn = XPayUtils.getMacToReturn(codiceTransazione, request.getTimeStamp(), apiKey, chiaveSegreta);
        } catch (Exception e) {
            log.error("XPay OrderStatus - Exception during the creation of the MAC string: ", e);
            XPayErrorEnum error = XPayErrorEnum.ERROR_50;

            return ResponseEntity.status(error.getHttpStatus())
                    .body(createXPayOrderResponse(XPayOutcome.KO, idOperazione, null, error));
        }

        if(outcomeConfig.equals("OK")) {
            if (macToReturn.equals(request.getMac())) {
                log.info("XPay OrderStatus - MAC verified");

                return ResponseEntity.ok()
                        .body(createXPayOrderResponse(XPayOutcome.OK, idOperazione, macToReturn, null));
            } else {
                log.info("XPay OrderStatus - MAC not verified");
                XPayErrorEnum error = XPayErrorEnum.ERROR_3;

                return ResponseEntity.status(error.getHttpStatus())
                        .body(createXPayOrderResponse(XPayOutcome.KO, idOperazione, macToReturn, error));
            }
        } else {
            return ResponseEntity.status(errorConfig.getHttpStatus())
                    .body(createXPayOrderResponse(XPayOutcome.KO, idOperazione, macToReturn, errorConfig));
        }
    }

    private XPayOrderResponse createXPayOrderResponse(XPayOutcome xPayOutcome, String idOperazione, String mac,
                                                      XPayErrorEnum error) {

        XPayOrderResponse xPayOrderResponse = new XPayOrderResponse();
        xPayOrderResponse.setEsito(xPayOutcome);
        xPayOrderResponse.setIdOperazione(idOperazione);
        xPayOrderResponse.setTimeStamp(System.currentTimeMillis());
        xPayOrderResponse.setMac(mac);

        if(error == null)
            xPayOrderResponse.setReport(createXPayReport());
        else
            xPayOrderResponse.setErrore(new XpayError(error.getErrorCode(), error.getDescription()));

        return xPayOrderResponse;
    }

    private XPayReport createXPayReport() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");

        XPayReport xPayReport = new XPayReport();
        xPayReport.setNumeroMerchant("999999");
        xPayReport.setCodiceTransazione("999999");
        xPayReport.setImporto(BigInteger.valueOf(5000));
        xPayReport.setDivisa("EUR");
        xPayReport.setCodiceAutorizzazione("CA123456");
        xPayReport.setBrand("APPLEPAY");
        xPayReport.setTipoPagamento("3D-Secure");
        xPayReport.setTipoTransazione("3DS_FULL");
        xPayReport.setNazione("ITA");
        xPayReport.setTipoProdotto("VISA CLASSIC - DEBIT – N");
        xPayReport.setPan("123456");
        xPayReport.setDataTransazione(nowDateTime.format(formatter));
        xPayReport.setMail("prova@mail.it");
        xPayReport.setDettaglio(createXPayReportDetail());

        return xPayReport;
    }

    private XPayReportDetail createXPayReportDetail() {
        XPayReportDetail detail = new XPayReportDetail();
        detail.setNome("Mario");
        detail.setCognome("Rossi");
        detail.setMail("prova@mail.it");
        detail.setImporto(BigInteger.valueOf(5000));
        detail.setDivisa("EUR");
        detail.setStato("Contabilizzato");
        detail.setCodiceTransazione(codiceTransazione);
        detail.setControvaloreValuta(BigInteger.valueOf(5000));
        detail.setDecimaliValuta(2);
        detail.setTassoCambio(BigInteger.valueOf(1));
        detail.setCodiceValuta(840);
        detail.setFlagValuta(true);
        detail.setOperazioni(createXPayReportOperations());

        return detail;
    }

    private List<XPayReportOperations> createXPayReportOperations() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        XPayReportOperations operations = new XPayReportOperations();
        operations.setTipoOperazione("contabilizzazione");
        operations.setImporto(BigInteger.valueOf(5000));
        operations.setDivisa("EUR");
        operations.setStato("Contabilizzato");
        operations.setDataOperazione(nowDateTime.format(formatter));
        operations.setUtente("utente");

        return Collections.singletonList(operations);
    }
}
