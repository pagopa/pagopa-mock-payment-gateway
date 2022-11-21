package it.gov.pagopa.vpos.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderStatus implements Serializable {
    @XmlElement(name = "Header")
    private OrderStatusHeader header;

    @XmlElement(name = "OrderID")
    private String orderId;
}
