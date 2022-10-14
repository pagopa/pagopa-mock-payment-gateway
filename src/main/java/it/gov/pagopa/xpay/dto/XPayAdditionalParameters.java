package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class XPayAdditionalParameters {
    private String mail;
    private String nome;
    private String cognome;
    private String descrizione;
    private String note1;
    private XPayTconTabEnum tconTab;
}
