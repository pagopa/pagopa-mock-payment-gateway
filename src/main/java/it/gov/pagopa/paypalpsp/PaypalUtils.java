package it.gov.pagopa.paypalpsp;

import com.google.common.hash.Hashing;
import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingCallResponseErrCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PaypalUtils {
    @Autowired
    private TableConfigRepository tableConfigRepository;

    @SuppressWarnings("UnstableApiUsage")
    public String calculateHmac(String esito, String idPp, String emailPp, PpOnboardingCallResponseErrCode errCodeEnum, String idBack) {
        idPp = StringUtils.defaultString(idPp);
        emailPp = StringUtils.defaultString(emailPp);
        String errCode = errCodeEnum != null ? errCodeEnum.getCode() : "";
        String errDesc = errCodeEnum != null ? errCodeEnum.getDescription() : "";
        String string = String.format("esito=%s&ip_pp=%s&email_pp=%s&err_code=%s&err_desc=%s&id_back=%s", esito, idPp, emailPp, errCode, errDesc, idBack);
        String paypalPspHmacKey = tableConfigRepository.findByPropertyKey("PAYPAL_PSP_HMAC_KEY").getPropertyValue();
        return Hashing.hmacSha256(paypalPspHmacKey.getBytes()).hashString(string, StandardCharsets.UTF_8).toString();
    }
}
