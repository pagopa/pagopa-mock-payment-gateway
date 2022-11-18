package it.gov.pagopa.vpos.utils;

import it.gov.pagopa.vpos.dto.request.BPWXmlRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VposUtils {
    public static BPWXmlRequest unmarshallEng(String data) throws JAXBException {
        StringReader sr = new StringReader(data);
        JAXBContext jaxbContext = JAXBContext.newInstance(BPWXmlRequest.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return (BPWXmlRequest) unmarshaller.unmarshal(sr);
    }
}
