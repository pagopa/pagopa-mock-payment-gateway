package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.xpay.dto.EsitoXpay;
import it.gov.pagopa.xpay.dto.XPayAuthRequest;
import it.gov.pagopa.xpay.dto.XPayAuthResponse;
import it.gov.pagopa.xpay.dto.XpayError;
import it.gov.pagopa.xpay.entity.XPayPayment;
import it.gov.pagopa.xpay.repository.XPayRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/xpay")
@Log4j2
public class XPayController {

    @Value("${xpay-apikey-alias}")
    private String apiKey;

    @Value("${xpay-secret-key}")
    private String chiaveSegreta;

    @Autowired
    private XPayRepository xPayRepository;

    private static final String HTML_TO_RETURN = "<html>" +
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

    @PostMapping("/ecomm/api/paga/autenticazione3DS")
    public ResponseEntity<Object> paymentAuthorization(@RequestBody XPayAuthRequest request) {
        String codiceTransazione = request.getCodiceTransazione();
        log.info("XPay autenticazione3DS - create AuthResponse for transactionId: " + codiceTransazione);
        Long timeStamp = System.currentTimeMillis();
        String idOperazione = UUID.randomUUID().toString();
        try {
            saveXpayRequest(idOperazione, request, timeStamp);
            String macToReturn = getMacToReturn(codiceTransazione, request.getDivisa(), request.getImporto(), request.getTimeStamp());
            if (StringUtils.equals(request.getMac(), macToReturn)) {
                log.info("MAC verified. Generating response");
                return createOkResponse(timeStamp, macToReturn, idOperazione);
            } else {
                log.info("MAC not verified. Generating KO response");
                XpayError xpayError = new XpayError(3L, "MAC errato");
                return createKoResponse(timeStamp, xpayError, macToReturn, idOperazione);
            }
        } catch (Exception e) {
            log.error("An exception occurred while processing the request", e);
            XpayError xpayError = new XpayError(50L, "Impossibile calcolare il mac");
            return createKoResponse(timeStamp, xpayError, "error", idOperazione);
        }
    }

    private void saveXpayRequest(String idOperazione, XPayAuthRequest request, Long timeStamp) {
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

    private ResponseEntity<Object> createOkResponse(Long timeStamp, String mac, String idOperazione) {
        XPayAuthResponse xPayAuthResponse = new XPayAuthResponse();
        xPayAuthResponse.setEsito(EsitoXpay.OK);
        xPayAuthResponse.setIdOperazione(idOperazione);
        xPayAuthResponse.setHtml(HTML_TO_RETURN);
        xPayAuthResponse.setTimeStamp(timeStamp);
        xPayAuthResponse.setMac(mac);
        return ResponseEntity.ok().body(xPayAuthResponse);
    }

    private ResponseEntity<Object> createKoResponse(Long timeStamp, XpayError xpayError, String mac, String idOperazione) {
        XPayAuthResponse xPayAuthResponse = new XPayAuthResponse();
        xPayAuthResponse.setEsito(EsitoXpay.KO);
        xPayAuthResponse.setIdOperazione(idOperazione);
        xPayAuthResponse.setTimeStamp(timeStamp);
        xPayAuthResponse.setMac(mac);
        xPayAuthResponse.setErrore(xpayError);
        return ResponseEntity.internalServerError().body(xPayAuthResponse);
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
}
