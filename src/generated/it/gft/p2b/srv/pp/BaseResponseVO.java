//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.11.17 alle 06:05:15 PM CET 
//


package it.gft.p2b.srv.pp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per baseResponseVO complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="baseResponseVO">
 *   &lt;complexContent>
 *     &lt;extension base="{http://p2b.gft.it/srv/pp}baseVO">
 *       &lt;sequence>
 *         &lt;element name="esito" type="{http://p2b.gft.it/srv/pp}esitoVO" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseResponseVO", propOrder = {
    "esito"
})
@XmlSeeAlso({
    ResponseStornoPagamentoVO.class,
    ResponseInquiryTransactionStatusVO.class,
    ResponseInserimentoRichiestaPagamentoPagoPaVO.class
})
public class BaseResponseVO
    extends BaseVO
{

    protected EsitoVO esito;

    /**
     * Recupera il valore della proprietà esito.
     * 
     * @return
     *     possible object is
     *     {@link EsitoVO }
     *     
     */
    public EsitoVO getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della proprietà esito.
     * 
     * @param value
     *     allowed object is
     *     {@link EsitoVO }
     *     
     */
    public void setEsito(EsitoVO value) {
        this.esito = value;
    }

}
