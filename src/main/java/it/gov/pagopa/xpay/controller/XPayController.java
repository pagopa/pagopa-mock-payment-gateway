package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.xpay.dto.*;
import it.gov.pagopa.xpay.entity.XPayPayment;
import it.gov.pagopa.xpay.repository.XPayRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_AUTH_ERROR;
import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_AUTH_OUTCOME;

@RestController
@RequestMapping("/xpay")
@Log4j2
public class XPayController {

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

    private static final String HTML_TO_RETURN = createHtml();

    private void refreshConfigs() {
        outcomeConfig = configRepository.findByPropertyKey(XPAY_AUTH_OUTCOME).getPropertyValue();
        errorConfig = getErrorConfig();
    }

    @PostMapping("/ecomm/api/paga/autenticazione3DS")
    public ResponseEntity<Object> paymentAuthorization(@Valid @RequestBody XPayAuthRequest request) {
        refreshConfigs();

        String codiceTransazione = request.getCodiceTransazione();
        log.info("XPay autenticazione3DS - create AuthResponse for transactionId: " + codiceTransazione);
        Long timeStamp = System.currentTimeMillis();
        String idOperazione = UUID.randomUUID().toString();

        saveXpayAuthRequest(idOperazione, request, timeStamp);

        String macToReturn;
        try {
            macToReturn = getMacToReturn(codiceTransazione, request.getDivisa(), request.getImporto(), request.getTimeStamp());
        } catch (Exception e) {
            log.error("An exception occurred while processing the request: ", e);
            XPayErrorEnum macError = XPayErrorEnum.ERROR_50;
            XPayAuthResponse xPayAuthResponse = createXpayAuthResponse(EsitoXpay.KO, idOperazione, timeStamp, "error");
            xPayAuthResponse.setErrore(new XpayError(macError.getErrorCode(), macError.getDescription()));

            return ResponseEntity.status(macError.getHttpStatus()).body(xPayAuthResponse);
        }

        if(outcomeConfig.equals("OK")) {
            if (macToReturn.equals(request.getMac())) {
                log.info("MAC verified. Generating response");
                XPayAuthResponse xpayResponse = createXpayAuthResponse(EsitoXpay.OK, idOperazione, timeStamp, macToReturn);
                xpayResponse.setHtml(HTML_TO_RETURN);

                return ResponseEntity.ok().body(xpayResponse);
            } else {
                log.info("MAC not verified. Generating KO response");
                XPayErrorEnum macNotVerified = XPayErrorEnum.ERROR_3;
                XPayAuthResponse xPayAuthResponse = createXpayAuthResponse(EsitoXpay.KO, idOperazione, timeStamp, macToReturn);
                xPayAuthResponse.setErrore(new XpayError(macNotVerified.getErrorCode(), macNotVerified.getDescription()));

                return ResponseEntity.status(macNotVerified.getHttpStatus()).body(xPayAuthResponse);
            }
        } else {
            XPayAuthResponse xPayAuthResponse = createXpayAuthResponse(EsitoXpay.KO, idOperazione, timeStamp, macToReturn);
            xPayAuthResponse.setErrore(new XpayError(errorConfig.getErrorCode(), errorConfig.getDescription()));

            return ResponseEntity.status(errorConfig.getHttpStatus()).body(xPayAuthResponse);
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

    private XPayAuthResponse createXpayAuthResponse(EsitoXpay esitoXpay, String idOperazione, Long timeStamp, String mac) {
        XPayAuthResponse xPayAuthResponse = new XPayAuthResponse();
        xPayAuthResponse.setEsito(esitoXpay);
        xPayAuthResponse.setIdOperazione(idOperazione);
        xPayAuthResponse.setTimeStamp(timeStamp);
        xPayAuthResponse.setMac(mac);
        return xPayAuthResponse;
    }

    private String getMacToReturn(String codiceTransazione, Long divisa, BigInteger importo, String timeStamp) throws Exception {
        log.info("Generating MAC for transactionId: " + codiceTransazione);
        String realMac = String.format("apiKey=%scodiceTransazione=%sdivisa=%simporto=%stimeStamp=%s%s",
                apiKey, codiceTransazione, divisa, importo, timeStamp, chiaveSegreta);
        return hashMac(realMac);
    }

    private String hashMac(String realMac) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] in = digest.digest(realMac.getBytes(StandardCharsets.UTF_8));
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private XPayErrorEnum getErrorConfig() {
        String errorCode = configRepository.findByPropertyKey(XPAY_AUTH_ERROR).getPropertyValue();

        //If the errorCode is valid, the correct XPayErrorEnum is returned.
        //If it's invalid, a Generic Error (ERROR_97) is returned as default.
        if(EnumUtils.isValidEnum(XPayErrorEnum.class, "ERROR_" + errorCode)) {
            return XPayErrorEnum.valueOf("ERROR_" + errorCode);
        }

        return XPayErrorEnum.ERROR_97;
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
