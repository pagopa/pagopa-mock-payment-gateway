package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.xpay.dto.EsitoXpay;
import it.gov.pagopa.xpay.dto.XPayAuthRequest;
import it.gov.pagopa.xpay.dto.XPayAuthResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@Validated
@RestController
@RequestMapping("/xpay")
@Log4j2
public class XPayController {

    @Value("${XPAY_APIKEY_ALIAS}")
    private String apiKey;

    @Value("${XPAY_SECRET_KEY}")
    private String chiaveSegreta;

    @Autowired
    private TableConfigRepository configRepository;

    private String htmlToReturn = "<html>\n" +
            "   <head>\n" +
            "      <title>\\nGestione Pagamento - Autenticazione</title>\n" +
            "      <script type=\\\"text/javascript\\\" language=\\\"javascript\\\">\\nfunction moveWindow() {\\n    document.tdsFraudForm.submit();\\n}\\n</script>\\n\n" +
            "   </head>\n" +
            "   <body>\n" +
            "      <form name=\\\"tdsFraudForm\\\" action=\\\"mock-psp-xpay-view\\\" method=\\\"POST\\\">\\n<input type=\\\"hidden\\\" name=\\\"action\\\"     value=\\\"fraud\\\">\\n<input type=\\\"hidden\\\" name=\\\"merchantId\\\" value=\\\"31320986\\\">\\n<input type=\\\"hidden\\\" name=\\\"description\\\" value=\\\"7090132540_1663077273191\\\">\\n<input type=\\\"hidden\\\" name=\\\"gdiUrl\\\"      value=\\\"\\\">\\n<input type=\\\"hidden\\\" name=\\\"gdiNotify\\\"   value=\\\"\\\">\\n</form>\n" +
            "      <script type=\\\"text/javascript\\\">\\n  moveWindow();\\n</script>\\n\n" +
            "   </body>\n" +
            "</html>";

    @PostMapping("/ecomm/api/paga/autenticazione3DS")
    public ResponseEntity<Object> paymentAuthorization(@RequestBody XPayAuthRequest request) {

        log.info("Create AuthResponse for transactioId: " + request.getCodiceTransazione());

        Boolean isMacEqual = checkMac(request.getMac(), request.getCodiceTransazione(), request.getDivisa(), request.getImporto(), request.getTimeStamp());

        XPayAuthResponse xPayAuthResponse = new XPayAuthResponse();

        if (isMacEqual) {
            xPayAuthResponse.setEsito(EsitoXpay.OK);
            xPayAuthResponse.setHtml(htmlToReturn);
        } else {
            xPayAuthResponse.setEsito(EsitoXpay.KO);
            return ResponseEntity.internalServerError().body(xPayAuthResponse);
        }

        return ResponseEntity.ok().body(xPayAuthResponse);
    }

    private Boolean checkMac(String macToCheck, String codiceTransazione, Long divisa, BigInteger importo, String timeStamp) {
        log.info("Start checking mac");
        String realMac = String.format("apiKey=%scodiceTransazione=%sdivisa=%simporto=%stimeStamp=%s%s", apiKey, codiceTransazione, divisa, importo, timeStamp, chiaveSegreta);
        return macToCheck.equals(realMac);
    }
}
