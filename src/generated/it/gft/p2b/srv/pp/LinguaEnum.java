//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.11.17 alle 06:05:15 PM CET 
//


package it.gft.p2b.srv.pp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per linguaEnum.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="linguaEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IT"/>
 *     &lt;enumeration value="EN"/>
 *     &lt;enumeration value="DE"/>
 *     &lt;enumeration value="AT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "linguaEnum")
@XmlEnum
public enum LinguaEnum {

    IT,
    EN,
    DE,
    AT;

    public String value() {
        return name();
    }

    public static LinguaEnum fromValue(String v) {
        return valueOf(v);
    }

}
