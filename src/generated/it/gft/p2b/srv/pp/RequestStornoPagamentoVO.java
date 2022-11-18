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
 * <p>Classe Java per requestStornoPagamentoVO complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="requestStornoPagamentoVO">
 *   &lt;complexContent>
 *     &lt;extension base="{http://p2b.gft.it/srv/pp}baseRequestVO">
 *       &lt;sequence>
 *         &lt;element name="idPagoPa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endToEndId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="causale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoStorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestStornoPagamentoVO", propOrder = {
    "idPagoPa",
    "endToEndId",
    "causale",
    "tipoStorno"
})
public class RequestStornoPagamentoVO
    extends BaseRequestVO
{

    protected String idPagoPa;
    protected String endToEndId;
    protected String causale;
    protected String tipoStorno;

    /**
     * Recupera il valore della proprietà idPagoPa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPagoPa() {
        return idPagoPa;
    }

    /**
     * Imposta il valore della proprietà idPagoPa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPagoPa(String value) {
        this.idPagoPa = value;
    }

    /**
     * Recupera il valore della proprietà endToEndId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndToEndId() {
        return endToEndId;
    }

    /**
     * Imposta il valore della proprietà endToEndId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndToEndId(String value) {
        this.endToEndId = value;
    }

    /**
     * Recupera il valore della proprietà causale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCausale() {
        return causale;
    }

    /**
     * Imposta il valore della proprietà causale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCausale(String value) {
        this.causale = value;
    }

    /**
     * Recupera il valore della proprietà tipoStorno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoStorno() {
        return tipoStorno;
    }

    /**
     * Imposta il valore della proprietà tipoStorno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoStorno(String value) {
        this.tipoStorno = value;
    }

}
