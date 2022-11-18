//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.7 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2022.11.17 alle 06:05:15 PM CET 
//


package it.gft.p2b.srv.pp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per contestoVO complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="contestoVO">
 *   &lt;complexContent>
 *     &lt;extension base="{http://p2b.gft.it/srv/pp}p2BAbstractPresentationBean">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utenteAttivo" type="{http://p2b.gft.it/srv/pp}utenteAttivoVO"/>
 *         &lt;element name="dataEsecuzione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="sessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lingua" type="{http://p2b.gft.it/srv/pp}linguaEnum" minOccurs="0"/>
 *         &lt;element name="device" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="matricola" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clientTransactionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contestoVO", propOrder = {
    "guid",
    "token",
    "utenteAttivo",
    "dataEsecuzione",
    "sessionId",
    "ip",
    "lingua",
    "device",
    "matricola",
    "versione",
    "clientTransactionId"
})
public class ContestoVO
    extends P2BAbstractPresentationBean
{

    @XmlElement(required = true)
    protected String guid;
    @XmlElement(required = true)
    protected String token;
    @XmlElement(required = true)
    protected UtenteAttivoVO utenteAttivo;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataEsecuzione;
    protected String sessionId;
    protected String ip;
    protected LinguaEnum lingua;
    protected String device;
    protected String matricola;
    protected String versione;
    protected String clientTransactionId;

    /**
     * Recupera il valore della proprietà guid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Imposta il valore della proprietà guid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Recupera il valore della proprietà token.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Imposta il valore della proprietà token.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

    /**
     * Recupera il valore della proprietà utenteAttivo.
     * 
     * @return
     *     possible object is
     *     {@link UtenteAttivoVO }
     *     
     */
    public UtenteAttivoVO getUtenteAttivo() {
        return utenteAttivo;
    }

    /**
     * Imposta il valore della proprietà utenteAttivo.
     * 
     * @param value
     *     allowed object is
     *     {@link UtenteAttivoVO }
     *     
     */
    public void setUtenteAttivo(UtenteAttivoVO value) {
        this.utenteAttivo = value;
    }

    /**
     * Recupera il valore della proprietà dataEsecuzione.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataEsecuzione() {
        return dataEsecuzione;
    }

    /**
     * Imposta il valore della proprietà dataEsecuzione.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataEsecuzione(XMLGregorianCalendar value) {
        this.dataEsecuzione = value;
    }

    /**
     * Recupera il valore della proprietà sessionId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Imposta il valore della proprietà sessionId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

    /**
     * Recupera il valore della proprietà ip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIp() {
        return ip;
    }

    /**
     * Imposta il valore della proprietà ip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIp(String value) {
        this.ip = value;
    }

    /**
     * Recupera il valore della proprietà lingua.
     * 
     * @return
     *     possible object is
     *     {@link LinguaEnum }
     *     
     */
    public LinguaEnum getLingua() {
        return lingua;
    }

    /**
     * Imposta il valore della proprietà lingua.
     * 
     * @param value
     *     allowed object is
     *     {@link LinguaEnum }
     *     
     */
    public void setLingua(LinguaEnum value) {
        this.lingua = value;
    }

    /**
     * Recupera il valore della proprietà device.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevice() {
        return device;
    }

    /**
     * Imposta il valore della proprietà device.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevice(String value) {
        this.device = value;
    }

    /**
     * Recupera il valore della proprietà matricola.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatricola() {
        return matricola;
    }

    /**
     * Imposta il valore della proprietà matricola.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatricola(String value) {
        this.matricola = value;
    }

    /**
     * Recupera il valore della proprietà versione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersione() {
        return versione;
    }

    /**
     * Imposta il valore della proprietà versione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersione(String value) {
        this.versione = value;
    }

    /**
     * Recupera il valore della proprietà clientTransactionId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientTransactionId() {
        return clientTransactionId;
    }

    /**
     * Imposta il valore della proprietà clientTransactionId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientTransactionId(String value) {
        this.clientTransactionId = value;
    }

}
