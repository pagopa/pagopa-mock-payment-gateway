package it.gov.pagopa.xpay.utils;

import it.gov.pagopa.xpay.dto.XPayErrorEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XPayUtils {
    public static XPayErrorEnum getErrorConfig(String errorCode) {
        //If the errorCode is valid, the correct XPayErrorEnum is returned.
        //If it's invalid, a Generic Error (ERROR_97) is returned as default.
        if(EnumUtils.isValidEnum(XPayErrorEnum.class, "ERROR_" + errorCode))
            return XPayErrorEnum.valueOf("ERROR_" + errorCode);

        return XPayErrorEnum.ERROR_97;
    }

    public static String getMacToReturn(String codiceTransazione, Long divisa, BigInteger importo, String timeStamp,
                                        String apiKey, String chiaveSegreta) throws Exception {

        String realMac = String.format("apiKey=%scodiceTransazione=%sdivisa=%simporto=%stimeStamp=%s%s",
                apiKey, codiceTransazione, divisa, importo, timeStamp, chiaveSegreta);

        return hashMac(realMac);
    }

    public static String getMacToReturn(String codiceTransazione, String timeStamp, String apiKey, String chiaveSegreta) throws Exception {

        String realMac = String.format("apiKey=%scodiceTransazione=%stimeStamp=%s%s",
                apiKey, codiceTransazione, timeStamp, chiaveSegreta);

        return hashMac(realMac);
    }

    private static String hashMac(String realMac) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] in = digest.digest(realMac.getBytes(StandardCharsets.UTF_8));
        final StringBuilder builder = new StringBuilder();

        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }
}
