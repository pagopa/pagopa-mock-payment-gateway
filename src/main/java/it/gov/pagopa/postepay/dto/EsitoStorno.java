package it.gov.pagopa.postepay.dto;

public enum EsitoStorno {

    OK("OK"),

    KO("KO");


   private final String value;

    EsitoStorno(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static EsitoStorno fromValue(String value) {
        for (EsitoStorno b : EsitoStorno.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
