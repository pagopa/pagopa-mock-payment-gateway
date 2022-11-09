package it.gov.pagopa.xpay.utils;

import it.gov.pagopa.xpay.dto.XPayErrorEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.MessageFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XPayUtils {
    public static XPayErrorEnum getErrorConfig(String errorCode) {
        //If the errorCode is valid, the correct XPayErrorEnum is returned.
        //If it's invalid, a Generic Error (ERROR_97) is returned as default.
        if(EnumUtils.isValidEnum(XPayErrorEnum.class, "ERROR_" + errorCode))
            return XPayErrorEnum.valueOf("ERROR_" + errorCode);

        return XPayErrorEnum.ERROR_97;
    }

    public static String getBaseMac(String codiceTransazione, Long divisa, BigInteger importo, String timeStamp,
                                    String apiKey, String chiaveSegreta) throws Exception {

        String mac = MessageFormat.format("apiKey={0}codiceTransazione={1}divisa={2}importo={3}timeStamp={4}{5}",
                apiKey, codiceTransazione, divisa, importo, timeStamp, chiaveSegreta);

        return hashMac(mac);
    }

    public static String getMacWithNonce(String esito, String operationId, String nonce, String timeStamp, String chiaveSegreta) throws Exception {
        String mac = MessageFormat.format("esito={0}idOperazione={1}xpayNonce={2}timeStamp={3}{4}",
                esito, operationId, nonce, timeStamp, chiaveSegreta);

        return hashMac(mac);
    }

    public static String getMacWithoutNonce(String esito, String operationId, String timeStamp, String chiaveSegreta) throws Exception {
        String mac = MessageFormat.format("esito={0}idOperazione={1}timeStamp={2}{3}",
                esito, operationId, timeStamp, chiaveSegreta);

        return hashMac(mac);
    }

    public static String getRefundMac(String codiceTransazione, String timeStamp, String apiKey, String chiaveSegreta) throws Exception {
        String mac = MessageFormat.format("apiKey={0}codiceTransazione={1}timeStamp={2}{3}",
                apiKey, codiceTransazione, timeStamp, chiaveSegreta);

        return hashMac(mac);
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
