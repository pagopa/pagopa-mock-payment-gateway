package it.gov.pagopa.vpos.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.vpos.dto.response.BPWXmlResponse;
import it.gov.pagopa.vpos.service.VposService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("vpos")
public class VposRestController {
    @Autowired
    private VposService vposService;

    @Tag(name = "VPOS Mock for 3dsV2")
    @PostMapping(value = "/authorize3dsV2", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "text/xml;charset=ISO-8859-1")
    public BPWXmlResponse vposAuthorize(@RequestParam String data) throws Exception {
        return vposService.getMock(data);
    }
}
