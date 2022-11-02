package it.gov.pagopa.vpos.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VposCreditCardGenerator {
    private static final List<String> VISA_PREFIX_LIST = Arrays.asList("4539","4556", "4916", "4532", "4929", "40240071", "4485", "4716", "4");
    private static final List<String> MASTERCARD_PREFIX_LIST = Arrays.asList("51", "52", "53", "54", "55");
    private static final List<String> AMEX_PREFIX_LIST = Arrays.asList("34", "37");
    private static final List<String> MAESTRO_PREFIX_LIST = Arrays.asList("5018", "5020", "5038", "5893", "6304", "6759", "6761", "6762", "6763");
    private static final List<String> DINERS_NEW_PREFIX_LIST = Collections.singletonList("38");

    private static String getCreditCardNumber(List<String> prefixList, int numberLength) {
        //Get random prefix
        int randomArrayIndex = (int) Math.floor(Math.random() * prefixList.size());
        StringBuilder prefix = new StringBuilder(prefixList.get(randomArrayIndex));

        return getCompleteCCNumber(prefix, numberLength);
    }

    private static String getCompleteCCNumber(StringBuilder ccNumber, int numberLength) {
        while (ccNumber.length() < numberLength-1)
            ccNumber.append(new Double(Math.floor(Math.random() * 10)).intValue());

        ccNumber.reverse();
        int sum = getSum(ccNumber, numberLength);

        //Calculating the check digit
        int checkdigit = new Double(((Math.floor(sum / 10) + 1) * 10 - sum) % 10).intValue();
        ccNumber.append(checkdigit);

        return ccNumber.toString();
    }

    private static int getSum(StringBuilder ccNumber, int length) {
        int sum = 0;
        int pos = 0;

        while (pos < length - 1) {
            int odd = Character.getNumericValue(ccNumber.charAt(pos)) * 2;

            if (odd > 9)
                odd -= 9;

            sum += odd;

            if (pos != (length - 2))
                sum += Character.getNumericValue(ccNumber.charAt(pos + 1));

            pos += 2;
        }

        return sum;
    }

    public static String getCircuitCodeFromPan(String pan) {
        String code = "00";
        if (StringUtils.startsWithAny(pan, toArray(MASTERCARD_PREFIX_LIST))) {
            code = "02";
        } else if (StringUtils.startsWithAny(pan, toArray(VISA_PREFIX_LIST))) {
            code = "01";
        } else if (StringUtils.startsWithAny(pan, toArray(MAESTRO_PREFIX_LIST))) {
            code = "04";
        } else if (StringUtils.startsWithAny(pan, toArray(AMEX_PREFIX_LIST))) {
            code = "06";
        } else if (StringUtils.startsWithAny(pan, toArray(DINERS_NEW_PREFIX_LIST))) {
            code = "07";
        }
        return code;
    }

    private static String[] toArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    public static String generateMasterCardNumber() {
        return getCreditCardNumber(MASTERCARD_PREFIX_LIST, 16);
    }

    public static String generateVisaCardNumber() {
        return getCreditCardNumber(VISA_PREFIX_LIST, 16);
    }

    public static String generateMaestroCardNumber() {
        return getCreditCardNumber(MAESTRO_PREFIX_LIST, 16);
    }

    public static String generateAmexCardNumber() {
        return getCreditCardNumber(AMEX_PREFIX_LIST, 15);
    }

    public static String generateDinersCardNumber() {
        return getCreditCardNumber(DINERS_NEW_PREFIX_LIST, 15);
    }
}
