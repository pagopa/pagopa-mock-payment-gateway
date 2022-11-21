package it.gov.pagopa.vpos.dto.request;

import it.gov.pagopa.vpos.dto.response.*;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlData {

    @XmlElement(name = "OrderStatus")
    private OrderStatus orderStatus;

    @XmlElement(name = "ProductRef")
    private String productRef;

    @XmlElement(name = "NumberOfItems")
    private Integer numberOfItems;

    @XmlElement(name = "Authorization")
    private ThreeDSAuthorization authorization;

    @XmlElement(name = "PanAliasData")
    private PanAliasData panAliasData;

    @XmlElement(name = "Header")
    private OrderStatusHeader header;

    @XmlElement(name = "OrderID")
    private String orderId;

    @XmlElement(name = "ThreeDSAuthorizationRequest0")
    private ThreeDSAuthorizationRequest0 threeDSAuthorizationRequest0;

    @XmlElement(name = "ThreeDSAuthorizationRequest1")
    private ThreeDSAuthorizationRequest1 threeDSAuthorizationRequest1;

    @XmlElement(name = "ThreeDSAuthorizationRequest2")
    private ThreeDSAuthorizationRequest2 threeDSAuthorizationRequest2;

    // returnCode 20, 3dsV2 method response (from authReq 0)
    @XmlElement(name = "ThreeDSMethod")
    private ThreeDSMethod threeDSMethod;

    // returnCode 20, 3dsV2 challenge response (from authReq 0 or 1)
    @XmlElement(name = "ThreeDSChallenge")
    private ThreeDSChallenge threeDSChallenge;

    // returnCode 00, 3dsV2 authorization response (from authReq 0, 1 or 2)
    @XmlElement(name = "Authorization")
    private ThreeDSAuthorization threeDSAuthorization;
}
