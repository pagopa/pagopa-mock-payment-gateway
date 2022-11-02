package it.gov.pagopa.vpos.dto.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Request3dsV2 implements Serializable {
    @XmlElement(name = "Operation")
    private String operation;
    @XmlElement(name = "Timestamp")
    private String timestamp;
    @XmlElement(name = "MAC")
    private String mac;
}
