package it.gov.pagopa.paypalpsp.util;

import com.google.common.hash.Hashing;
import it.gov.pagopa.db.entity.TableClient;
import it.gov.pagopa.db.repository.TableClientRepository;
import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PaypalUtils {
    @Autowired
    private TableConfigRepository tableConfigRepository;

    @Autowired
    private TableClientRepository tableClientRepository;

    private static final String BEARER_REGEX = "Bearer\\s.{3,}";

    @SuppressWarnings("UnstableApiUsage")
    public String calculateHmac(String esito, String idPp, String emailPp, PpResponseErrCode errCodeEnum, String idBack) {
        String errCode = errCodeEnum != null ? errCodeEnum.getCode() : "";
        String errDesc = errCodeEnum != null ? errCodeEnum.getDescription() : "";
        String result = String.format("esito=%s&id_pp=%s&email_pp=%s&err_cod=%s&err_desc=%s&id_back=%s", esito, StringUtils.defaultString(idPp), StringUtils.defaultString(emailPp), StringUtils.defaultString(errCode), StringUtils.defaultString(errDesc), idBack);
        String paypalPspHmacKey = tableConfigRepository.findByPropertyKey("PAYPAL_PSP_HMAC_KEY").getPropertyValue();
        return Hashing.hmacSha256(paypalPspHmacKey.getBytes()).hashString(result, StandardCharsets.UTF_8).toString();
    }

    public static String obfuscateEmail(String email) {
        if (StringUtils.isNotBlank(email))
            email = email.replaceAll("\\b(\\w{3})[^@]+@\\S+(\\.[^\\s.]+)", "$1***@****$2");

        return email;
    }

    public TableClient getClientAuthenticated(String bearerToken) {
        TableClient tableClient = null;
        if (StringUtils.isBlank(bearerToken) || !bearerToken.matches(BEARER_REGEX)) {
            String authKey = StringUtils.remove(bearerToken, "Bearer ");
            tableClient = tableClientRepository.findByAuthKeyAndDeletedFalse(authKey);
        }
        return tableClient;
    }
}
