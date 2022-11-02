package it.gov.pagopa.vpos.dto.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "BPWXmlRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class BPWXmlRequest implements Serializable {
    @XmlElement(name = "Release")
    private String release;
    @XmlElement(name = "Request")
    private Request3dsV2 request;
    @XmlElement(name = "Data")
    private XmlData data;
}
