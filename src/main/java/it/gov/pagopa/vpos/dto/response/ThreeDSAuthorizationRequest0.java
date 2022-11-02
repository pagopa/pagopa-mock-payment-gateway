package it.gov.pagopa.vpos.dto.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreeDSAuthorizationRequest0 implements Serializable {
    @XmlElement(name = "Header")
    private VposHeader header;
    @XmlElement(name = "OrderID")
    private String orderID;
    @XmlElement(name = "Pan")
    private String pan;
    @XmlElement(name = "CVV2")
    private String cvv2;
    @XmlElement(name = "CreatePanAlias")
    private String createPanAlias;
    @XmlElement(name = "ExpDate")
    private String expDate;
    @XmlElement(name = "Amount")
    private String amount;
    @XmlElement(name = "Currency")
    private String currency;
    @XmlElement(name = "Exponent")
    private String exponent;
    @XmlElement(name = "AccountingMode")
    private String accountingMode;
    @XmlElement(name = "Network")
    private String network;
    @XmlElement(name = "Userid")
    private String userid;
    @XmlElement(name = "OpDescr")
    private String opDescr;
    @XmlElement(name = "ProductRef")
    private String productRef;
    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "Surname")
    private String surname;
    @XmlElement(name = "TaxID")
    private String taxID;
    @XmlElement(name = "ThreeDSData")
    private String threeDSData;
    @XmlElement(name = "NotifUrl")
    private String notifUrl;
    @XmlElement(name = "EmailCH")
    private String emailCH;
    @XmlElement(name = "NameCH")
    private String nameCH;
    @XmlElement(name = "Acquirer")
    private String acquirer;
    @XmlElement(name = "IpAddress")
    private String ipAddress;
    @XmlElement(name = "UsrAuthFlag")
    private String usrAuthFlag;
    @XmlElement(name = "Options")
    private String options;
    @XmlElement(name = "AntiFraud")
    private String antiFraud;
    @XmlElement(name = "TRecurr")
    private String tRecurr;
    @XmlElement(name = "Crecurr")
    private String cRecurr;
    @XmlElement(name = "InstallmentsNumber")
    private String installmentsNumber;
    @XmlElement(name = "ThreeDSMtdNotifUrl")
    private String threeDSMtdNotifUrl;
    @XmlElement(name = "ChallengeWinSize")
    private String challengeWinSize;
    @XmlElement(name = "CProf")
    private String cProf;

}
