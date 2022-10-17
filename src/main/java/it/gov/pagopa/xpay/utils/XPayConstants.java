package it.gov.pagopa.xpay.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XPayConstants {
    public static String XPAY_AUTH_OUTCOME = "XPAY_AUTH_OUTCOME";
    public static String XPAY_AUTH_ERROR = "XPAY_AUTH_ERROR";
    public static String XPAY_PAYMENT_OUTCOME = "XPAY_PAYMENT_OUTCOME";
    public static String XPAY_PAYMENT_ERROR = "XPAY_PAYMENT_ERROR";
    public static String XPAY_REFUND_OUTCOME = "XPAY_REFUND_OUTCOME";
    public static String XPAY_REFUND_ERROR = "XPAY_REFUND_ERROR";
}
