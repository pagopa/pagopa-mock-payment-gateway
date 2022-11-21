package it.gov.pagopa.vpos.dto.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreeDSChallenge implements Serializable {
    @XmlElement(name = "ThreeDSTransId")
    private String threeDSTransId;

    @XmlElement(name = "CReq")
    private String cReq;

    @XmlElement(name = "ACSUrl")
    private String acsUrl;

    @XmlElement(name = "MAC")
    private String mac;
}
