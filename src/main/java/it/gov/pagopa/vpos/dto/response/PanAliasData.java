package it.gov.pagopa.vpos.dto.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class PanAliasData implements Serializable {
    @XmlElement(name = "PanAlias")
    private String panAlias;

    @XmlElement(name = "PanAliasExpDate")
    private String panAliasExpDate;

    @XmlElement(name = "PanAliasTail")
    private String panAliasTail;

    @XmlElement(name = "MAC")
    private String mac;
}
