package it.gov.pagopa.vpos.dto.response;
import it.gov.pagopa.vpos.dto.request.XmlData;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "BPWXmlResponse")
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class BPWXmlResponse implements Serializable {
    @XmlElement(name = "Timestamp")
    private String timestamp;

    @XmlElement(name = "Result")
    private String result;

    @XmlElement(name = "MAC")
    private String mac;

    @XmlElement(name = "Data")
    private XmlData data;
}
