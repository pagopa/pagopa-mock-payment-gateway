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
 * <p>Classe Java per p2BAbstractPresentationBean complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="p2BAbstractPresentationBean">
 *   &lt;complexContent>
 *     &lt;extension base="{http://p2b.gft.it/srv/pp}abstractPresentationBean">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "p2BAbstractPresentationBean")
@XmlSeeAlso({
    ContestoVO.class,
    OtpContestoVO.class,
    DeviceIdDataVO.class,
    BaseVO.class,
    RichiestaPagamentoOnlineVO.class,
    UtenteVO.class,
    EsitoVO.class
})
public abstract class P2BAbstractPresentationBean
    extends AbstractPresentationBean
{


}
