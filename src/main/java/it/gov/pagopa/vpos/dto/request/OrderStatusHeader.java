package it.gov.pagopa.vpos.dto.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderStatusHeader {

    @XmlElement(name = "ShopID")
    private String shopId;

    @XmlElement(name = "OperatorID")
    private String operatorId;

    @XmlElement(name = "ReqRefNum")
    private String reqRefNum;

}
