package it.gov.pagopa.xpay.service;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.xpay.dto.*;
import it.gov.pagopa.xpay.entity.XPayPayment;
import it.gov.pagopa.xpay.repository.XPayRepository;
import it.gov.pagopa.xpay.utils.XPayUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_AUTH_ERROR;
import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_AUTH_OUTCOME;

@Service
@Log4j2
public class XPayAuth3DSService {
    @Value("${xpay.apikey-alias}")
    private String apiKey;

    @Value("${xpay.secret-key}")
    private String chiaveSegreta;

    @Autowired
    private XPayRepository xPayRepository;

    @Autowired
    private TableConfigRepository configRepository;

    private static final String HTML_TO_RETURN = createHtml();
    private String outcomeConfig;
    private XPayErrorEnum errorConfig;

    private void refreshConfigs() {
        outcomeConfig = configRepository.findByPropertyKey(XPAY_AUTH_OUTCOME).getPropertyValue();
        errorConfig = XPayUtils.getErrorConfig(configRepository.findByPropertyKey(XPAY_AUTH_ERROR).getPropertyValue());;
    }

    public ResponseEntity<XPayAuthResponse> getMock(XPayAuthRequest request) {
        log.info("XPay Autenticazione3DS - Request from PGS: " + request);
        refreshConfigs();

        String codiceTransazione = request.getCodiceTransazione();
        log.info("XPay Autenticazione3DS - create AuthResponse for transactionId: " + codiceTransazione);
        Long timeStamp = System.currentTimeMillis();
        String idOperazione = UUID.randomUUID().toString();

        saveXpayAuthRequest(idOperazione, request, timeStamp);

        String macToReturn;
        try {
            log.info("XPay Autenticazione3DS - Generating MAC for transactionId: " + codiceTransazione);
            macToReturn = XPayUtils.getMacToReturn(codiceTransazione, request.getDivisa(), request.getImporto(),
                    request.getTimeStamp(), apiKey, chiaveSegreta);
        } catch (Exception e) {
            log.error("XPay Autenticazione3DS - Exception during the creation of the MAC: ", e);
            XPayErrorEnum error = XPayErrorEnum.ERROR_50;

            return ResponseEntity.status(error.getHttpStatus())
                    .body(createXpayAuthResponse(XPayOutcome.KO, idOperazione, timeStamp, null, error));
        }

        if(outcomeConfig.equals("OK")) {
            if (macToReturn.equals(request.getMac())) {
                log.info("XPay Autenticazione3DS - MAC verified");
                XPayAuthResponse xpayResponse = createXpayAuthResponse(XPayOutcome.OK, idOperazione, timeStamp, macToReturn, null);
                xpayResponse.setHtml(HTML_TO_RETURN.replace("mock-psp-xpay-view", request.getUrlRisposta()));

                return ResponseEntity.ok().body(xpayResponse);
            } else {
                log.info("XPay Autenticazione3DS - MAC not verified");
                XPayErrorEnum error = XPayErrorEnum.ERROR_3;

                return ResponseEntity.status(error.getHttpStatus())
                        .body(createXpayAuthResponse(XPayOutcome.KO, idOperazione, timeStamp, macToReturn, error));
            }
        } else {
            return ResponseEntity.status(errorConfig.getHttpStatus())
                    .body(createXpayAuthResponse(XPayOutcome.KO, idOperazione, timeStamp, macToReturn, errorConfig));
        }
    }

    private void saveXpayAuthRequest(String idOperazione, XPayAuthRequest request, Long timeStamp) {
        XPayPayment xPayPayment = new XPayPayment();
        xPayPayment.setIdOperazione(idOperazione);
        xPayPayment.setApiKey(request.getApiKey());
        xPayPayment.setCvv(request.getCvv());
        xPayPayment.setDivisa(request.getDivisa());
        xPayPayment.setPan(request.getPan());
        xPayPayment.setImporto(request.getImporto());
        xPayPayment.setMac(request.getMac());
        xPayPayment.setScadenza(request.getScadenza());
        xPayPayment.setCodiceTransazione(request.getCodiceTransazione());
        xPayPayment.setTimeStamp_request(request.getTimeStamp());
        xPayPayment.setTimeStamp_response(timeStamp);
        xPayPayment.setUrlRisposta(request.getUrlRisposta());
        xPayRepository.save(xPayPayment);
    }

    private XPayAuthResponse createXpayAuthResponse(XPayOutcome XPayOutcome, String idOperazione, Long timeStamp, String mac,
                                                    XPayErrorEnum error) {

        XPayAuthResponse xPayAuthResponse = new XPayAuthResponse();
        xPayAuthResponse.setEsito(XPayOutcome);
        xPayAuthResponse.setIdOperazione(idOperazione);
        xPayAuthResponse.setTimeStamp(timeStamp);
        xPayAuthResponse.setMac(mac);
        if(error != null) xPayAuthResponse.setErrore(new XpayError(error.getErrorCode(), error.getDescription()));

        return xPayAuthResponse;
    }

    private static String createHtml() {
        return "<html>" +
                "   <head>" +
                "      <title>Gestione Pagamento - Autenticazione</title>" +
                "      <script type=\"text/javascript\" language=\"javascript\">function moveWindow() { " +
                "           document.tdsFraudForm.submit();}</script>" +
                "   </head>" +
                "   <body>" +
                "      <form name=\"tdsFraudForm\" action=\"mock-psp-xpay-view\" method=\"POST\"><input type=\"hidden\" " +
                "       name=\"action\" value=\"fraud\"><input type=\"hidden\" name=\"merchantId\" value=\"31320986\">" +
                "       <input type=\"hidden\" name=\"description\" value=\"7090132540_1663077273191\"><input type=\"hidden\"" +
                "       name=\"gdiUrl\" value=\"\"><input type=\"hidden\" name=\"gdiNotify\" value=\"\"></form>" +
                "      <script type=\"text/javascript\"> moveWindow(); </script>" +
                "   </body>" +
                "</html>";
    }
}
