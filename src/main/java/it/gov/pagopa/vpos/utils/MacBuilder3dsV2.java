package it.gov.pagopa.vpos.utils;

import it.gov.pagopa.vpos.dto.AuthRequest3dsV2Enum;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;


public class MacBuilder3dsV2 {
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private StringBuilder mac = new StringBuilder();

    public void clear() {
        mac = new StringBuilder();
    }

    public void addElement(AuthRequest3dsV2Enum tag, Object value) throws IllegalArgumentException {
        if (value != null) {
            if (mac.length() > 0) {
                mac.append(AND);
            }
            mac.append(tag.getTagName().toUpperCase());
            mac.append(EQUAL);
            mac.append(value.toString());
        }
    }

    public void addString(String value) throws IllegalArgumentException {
        if (value != null) {
            if (mac.length() > 0) {
                mac.append(AND);
            }
            mac.append(value);
        }
    }

    @Override
    public String toString() {
        return mac.toString();
    }

    public String toSha1Hex(Charset charset) {
        return DigestUtils.sha1Hex(mac.toString().getBytes(charset));
    }
}
