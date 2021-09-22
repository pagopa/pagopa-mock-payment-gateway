package it.gov.pagopa.paypalpsp;

import com.google.common.hash.Hashing;
import it.gov.pagopa.db.repository.TableConfigRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PaypalUtils {
    @Autowired
    private TableConfigRepository tableConfigRepository;

    @SuppressWarnings("UnstableApiUsage")
    public String calculateHmac(String esito, String idPp, String emailPp, String errCod, String errDesc, String idBack) {
        String result = String.format(StringUtils.joinWith("=%s&", "esito", "id_pp", "email_pp", "err_cod", "err_desc", "id_back"), esito, StringUtils.defaultString(idPp), StringUtils.defaultString(emailPp), StringUtils.defaultString(errCod), StringUtils.defaultString(errDesc), idBack);
        String paypalPspHmacKey = tableConfigRepository.findByPropertyKey("PAYPAL_PSP_HMAC_KEY").getPropertyValue();
        return Hashing.hmacSha256(paypalPspHmacKey.getBytes()).hashString(result, StandardCharsets.UTF_8).toString();
    }
}
