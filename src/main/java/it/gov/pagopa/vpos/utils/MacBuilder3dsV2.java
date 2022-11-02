package it.gov.pagopa.vpos.utils;

import it.gov.pagopa.vpos.dto.AuthRequest3dsV2Enum;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.Charset;

/**
 * The Class MacBuilder.
 */
public class MacBuilder3dsV2 {

    private static final String AND = "&";
    private static final String EQUAL = "=";
    private StringBuilder mac = new StringBuilder();

    /**
     * Clear.
     */
    public void clear() {
        mac = new StringBuilder();
    }

    /**
     * Adds the element.
     *
     * @param tag   the tag
     * @param value the value
     * @throws IllegalArgumentException the illegal argument exception
     */
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

    /**
     * Adds the string.
     *
     * @param value the value
     * @throws IllegalArgumentException the illegal argument exception
     */
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

    /**
     * To sha 1 hex.
     *
     * @return the string
     */
    public String toSha1Hex(Charset charset) {
        return DigestUtils.sha1Hex(mac.toString().getBytes(charset));
    }
}
