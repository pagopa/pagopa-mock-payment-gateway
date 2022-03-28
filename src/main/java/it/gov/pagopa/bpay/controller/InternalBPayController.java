package it.gov.pagopa.bpay.controller;

import it.gov.pagopa.bpay.dto.*;
import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.annotation.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;

@Log4j2
@Controller
@RequestMapping("/bpayweb/management")
public class InternalBPayController {

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private BPayController bPayController;

    @Value("${server.public-url}")
    private String publicUrl;

    private TableConfig outcomeConfig;
    private TableConfig timeoutConfig;
    private DocumentBuilder documentBuilder;
    private Transformer transformer;

    @PostConstruct
    public void init() throws ParserConfigurationException, TransformerConfigurationException {
        outcomeConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_OUTCOME");
        timeoutConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_TIMEOUT_MS");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    }

    @PostMapping(value = "/home", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String changeOutcome(CallBPayRequest request, Model model) throws Exception {
        outcomeConfig.setPropertyValue(request.getOutcome());
        configRepository.save(outcomeConfig);
        if (request.getTimeout() != null) {
            timeoutConfig.setPropertyValue(request.getTimeout().toString());
        }
        configRepository.save(timeoutConfig);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> soapRequest = new HttpEntity<>(request.getRequest(), headers);
        String result;
        try {
            result = new RestTemplate().postForObject(publicUrl + "/bpay", soapRequest, String.class);
        } catch (Exception e) {
            result = ((HttpServerErrorException.InternalServerError)e).getResponseBodyAsString();
        }
        model.addAttribute("result", formatXml(result));
        return "bpay/home.html";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("callBPayRequest", new CallBPayRequest());
        return "bpay/home.html";
    }

    private String formatXml(String xmlString) throws Exception {
        Document xmlDoc = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        Writer out = new StringWriter();
        transformer.transform(new DOMSource(xmlDoc), new StreamResult(out));
        return out.toString();
    }

}
