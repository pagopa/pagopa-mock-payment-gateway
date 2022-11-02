package it.gov.pagopa.vpos.dto.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreeDSAuthorizationRequest2 implements Serializable {

    @XmlElement(name = "Header")
    private VposHeader header;

    @XmlElement(name = "ThreeDSTransID")
    private String threeDSTransId;

}
