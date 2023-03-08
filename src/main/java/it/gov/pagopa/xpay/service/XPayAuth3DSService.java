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

import java.util.Random;
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

    private String outcomeConfig;
    private XPayErrorEnum errorConfig;

    private void refreshConfigs() {
        outcomeConfig = configRepository.findByPropertyKey(XPAY_AUTH_OUTCOME).getPropertyValue();
        errorConfig = XPayUtils.getErrorConfig(configRepository.findByPropertyKey(XPAY_AUTH_ERROR).getPropertyValue());
    }

    public ResponseEntity<XPayAuthResponse> getMock(XPayAuthRequest request) {
        log.info("XPay Autenticazione3DS - Request from PGS: " + request);
        refreshConfigs();

        String codiceTransazione = request.getCodiceTransazione();
        log.info("XPay Autenticazione3DS - create AuthResponse for transactionId: " + codiceTransazione);
        Long timeStamp = System.currentTimeMillis();
        String idOperazione = UUID.randomUUID().toString();
        String nonce = String.valueOf(new Random().nextInt(99999));;

        saveXpayAuthRequest(idOperazione, request, timeStamp);

        String macToCheck;
        String macForHtml;
        String macForResponse;
        String macForError;
        try {
            log.info("XPay Autenticazione3DS - Generating MAC for transactionId: " + codiceTransazione);
            macToCheck = XPayUtils.getBaseMac(codiceTransazione, request.getDivisa(), request.getImporto(), request.getTimeStamp(), apiKey, chiaveSegreta);
            macForResponse = XPayUtils.getMacWithoutNonce(XPayOutcome.OK.toString(), idOperazione, timeStamp.toString(), chiaveSegreta);
            macForError = XPayUtils.getMacWithoutNonce(XPayOutcome.KO.toString(), idOperazione, timeStamp.toString(), chiaveSegreta);
            macForHtml = XPayUtils.getMacWithNonce(XPayOutcome.OK.toString(), idOperazione, nonce, timeStamp.toString(), chiaveSegreta);
        } catch (Exception e) {
            log.error("XPay Autenticazione3DS - Exception during the creation of the MAC: ", e);
            XPayErrorEnum error = XPayErrorEnum.ERROR_50;

            return ResponseEntity.status(error.getHttpStatus())
                    .body(createXpayAuthResponse(XPayOutcome.KO, idOperazione, timeStamp, null, error));
        }

        if(outcomeConfig.equals("OK")) {
            if (macToCheck.equals(request.getMac())) {
                log.info("XPay Autenticazione3DS - MAC verified");
                XPayAuthResponse xpayResponse = createXpayAuthResponse(XPayOutcome.OK, idOperazione, timeStamp, macForResponse, null);
                String html = createHtml(request.getUrlRisposta());
                xpayResponse.setHtml(html);

                return ResponseEntity.ok().body(xpayResponse);
            } else {
                log.info("XPay Autenticazione3DS - MAC not verified");
                XPayErrorEnum error = XPayErrorEnum.ERROR_3;

                return ResponseEntity.status(error.getHttpStatus())
                        .body(createXpayAuthResponse(XPayOutcome.KO, idOperazione, timeStamp, macForError, error));
            }
        } else {
            return ResponseEntity.status(errorConfig.getHttpStatus())
                    .body(createXpayAuthResponse(XPayOutcome.KO, idOperazione, timeStamp, macForError, errorConfig));
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
        xPayPayment.setTimestampRequest(request.getTimeStamp());
        xPayPayment.setTimestampResponse(timeStamp);
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
        if(error != null) xPayAuthResponse.setErrore(new XPayError(error.getErrorCode(), error.getDescription()));

        return xPayAuthResponse;
    }

    private static String createHtml(String url) {
        return String.format("\n\n\n" +
                "<html>" +
                "\n" +
                "<head>" +
                "\n <script type=\"text/javascript\">\n function moveWindow() {\n var form = document.getElementById(\"downloadForm\");\n if (form != null) {\n form.submit();\n }\n }\n </script>\n" +
                "<title>3DSECURE</title>" +
                "\n" +
                "<link rel=\"icon\" href=\"acs/images/favicon.ico\" type=\"image/x-icon\" />" +
                "\n" +
                "<link rel=\"shortcut icon\" href=\"acs/images/favicon.ico\" type=\"image/x-icon\" />" +
                "\n" +
                "</head>" +
                "\n\n" +
                "<body>" +
                "\n" +
                "<form id=\"downloadForm\"\n action=\"%s\"\n method=\"GET\">" +
                "\n <!-- Dati Carta -->\n <input type=\"hidden\" name=\"PaReq\" value=\"WSxZLDEsMDEsQUxscWthUU1kbm93dEtJd0xPK0JwYlI1dC9JPSxVU0QsWSwxLEE=\">\n <input type=\"hidden\" name=\"TermUrl\" value=\"https://int-ecommerce.nexi.it/ecomm/3dsResponse\">\n <!-- Parametri MPI -->\n <input type=\"hidden\" name=\"mpi_xid\" value=\"vN2/Y9GNHe9yQ8fZfnXWSQKOFBE=\" /><input type=\"hidden\" name=\"mpi_digest\" value=\"RchIeGJTjSaArFCRMqpA4TWPe3Q=\" /><input type=\"hidden\" name=\"mpi_recurEnd\" value=\"\" /><input type=\"hidden\" name=\"mpi_purchAmount\" value=\"12200\" /><input type=\"hidden\" name=\"mpi_okUrl\" value=\"https://int-ecommerce.nexi.it/ecomm/3dsResponse\" /><input type=\"hidden\" name=\"mpi_expiry\" value=\"3012\" /><input type=\"hidden\" name=\"mpi_failUrl\" value=\"https://int-ecommerce.nexi.it/ecomm/3dsResponse\" /><input type=\"hidden\" name=\"mpi_version\" value=\"2.0\" /><input type=\"hidden\" name=\"mpi_currency\" value=\"978\" /><input type=\"hidden\" name=\"mpi_MD\" value=\"ImwDDTTbmo0is44kgnkHSJfR.xpdemoas1\" /><input type=\"hidden\" name=\"mpi_installment\" value=\"\" /><input type=\"hidden\" name=\"mpi_exponent\" value=\"2\" /><input type=\"hidden\" name=\"mpi_merchantID\" value=\"00061134\" /><input type=\"hidden\" name=\"mpi_recurFreq\" value=\"\" /><input type=\"hidden\" name=\"mpi_description\" value=\"7090144925_1675798849994\" /><input type=\"hidden\" name=\"mpi_deviceCategory\" value=\"0\" /><input type=\"hidden\" name=\"mpi_pan\" value=\"4000000000000101\" />\n <!-- To support javascript unaware/disabled browsers -->\n" +
                "<div style=\"text-align:center;\">" +
                "\n" +
                "<noscript>" +
                "\n" +
                "<p align=\"center\">Please click the submit button below.<br>\n <input type=\"submit\" name=\"submit\" value=\"Submit\"></p>" +
                "\n" +
                "</noscript>" +
                "\n" +
                "</div>" +
                "\n" +
                "</form>" +
                "\n <script type=\"text/javascript\">\nmoveWindow();\n</script>\n" +
                "</body>" +
                "\n" +
                "</html>" +
                "\n", url);
    }
}
