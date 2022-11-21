package it.gov.pagopa.vpos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class VposHeader implements Serializable {
    @XmlElement(name = "ShopID")
    private String shopID;

    @XmlElement(name = "OperatorID")
    private String operatorID;

    @XmlElement(name = "ReqRefNum")
    private String reqRefNum;
}
