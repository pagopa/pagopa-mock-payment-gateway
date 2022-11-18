//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.11.17 alle 06:05:15 PM CET 
//


package it.gft.p2b.srv.pp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per responseStornoPagamentoVO complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="responseStornoPagamentoVO">
 *   &lt;complexContent>
 *     &lt;extension base="{http://p2b.gft.it/srv/pp}baseResponseVO">
 *       &lt;sequence>
 *         &lt;element name="endToEndIdStorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "responseStornoPagamentoVO", propOrder = {
    "endToEndIdStorno"
})
public class ResponseStornoPagamentoVO
    extends BaseResponseVO
{

    protected String endToEndIdStorno;

    /**
     * Recupera il valore della proprietà endToEndIdStorno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndToEndIdStorno() {
        return endToEndIdStorno;
    }

    /**
     * Imposta il valore della proprietà endToEndIdStorno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndToEndIdStorno(String value) {
        this.endToEndIdStorno = value;
    }

}
