package it.gov.pagopa.vpos.dto.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreeDSAuthorization implements Serializable {

    @XmlElement(name = "PaymentType")
    private String paymentType;
    @XmlElement(name = "AuthorizationType")
    private String authorizationType;
    @XmlElement(name = "TransactionID")
    private String transactionID;
    @XmlElement(name = "Network")
    private String network;
    @XmlElement(name = "OrderID")
    private String orderID;
    @XmlElement(name = "TransactionAmount")
    private String transactionAmount;
    @XmlElement(name = "AuthorizedAmount")
    private String authorizedAmount;
    @XmlElement(name = "Currency")
    private String currency;
    @XmlElement(name = "Exponent")
    private String exponent;
    @XmlElement(name = "AccountedAmount")
    private String accountedAmount;
    @XmlElement(name = "RefundedAmount")
    private String refundedAmount;
    @XmlElement(name = "TransactionResult")
    private String transactionResult;
    @XmlElement(name = "Timestamp")
    private String timestamp;
    @XmlElement(name = "AuthorizationNumber")
    private String authorizationNumber;
    @XmlElement(name = "AcquirerBIN")
    private String acquirerBIN;
    @XmlElement(name = "MerchantID")
    private String merchantID;
    @XmlElement(name = "TransactionStatus")
    private String transactionStatus;
    @XmlElement(name = "ResponseCodeISO")
    private String responseCodeISO;
    @XmlElement(name = "RRN")
    private String rrn;
    @XmlElement(name = "MAC")
    private String mac;
}
