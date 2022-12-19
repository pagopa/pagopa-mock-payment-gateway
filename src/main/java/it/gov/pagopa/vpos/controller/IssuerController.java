package it.gov.pagopa.vpos.controller;

import com.google.gson.Gson;
import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.service.ConfigService;
import it.gov.pagopa.vpos.dto.Method3Ds2ResponseEnum;
import it.gov.pagopa.vpos.dto.request.MethodData3Ds;
import it.gov.pagopa.vpos.entity.Transaction3DsEntity;
import it.gov.pagopa.vpos.service.Transaction3DsService;
import it.gov.pagopa.vpos.utils.VposConstants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.function.Function;

@Controller
@RequestMapping("/issuer/3ds20/")
@Log4j2
public class IssuerController {
    @Autowired
    private ConfigService configService;

    @Autowired
    private Transaction3DsService transaction3DsService;

    @Value("${mock-pgs-url}")
    private String mockPgsUrl;

    @PostMapping("/method")
    public String methodUrl(@RequestParam String threeDSMethodData, Model model) {
        Function<String, TableConfig> findByKey = key -> configService.getOptionalByKey(key).orElse(new TableConfig());

        String decodedMethodData = new String(Base64Utils.decodeFromString(threeDSMethodData));
        MethodData3Ds methodData3Ds = new Gson().fromJson(decodedMethodData, MethodData3Ds.class);
        model.addAttribute("threeDSMethodNotificationUrl", methodData3Ds.getThreeDSMethodNotificationUrl());

        String threeDSServerTransID = methodData3Ds.getThreeDSServerTransID();
        MethodData3Ds methodData3DsReturn = new MethodData3Ds();
        methodData3DsReturn.setThreeDSServerTransID(threeDSServerTransID);
        model.addAttribute("methodData3Ds", Base64Utils.encodeToString(new Gson().toJson(methodData3DsReturn).getBytes()));
        model.addAttribute("MOCK_PGS_URL", mockPgsUrl);
        log.info(String.format("Request %s", threeDSServerTransID));

        String method3dsResponse = findByKey.apply(VposConstants.VPOS_METHOD_3DS2_RESPONSE).getPropertyValue();

        String redirect = "vpos/method3ds.html";
        if (StringUtils.equals(method3dsResponse, Method3Ds2ResponseEnum.KO.toString())) {
            redirect = "/";
        }

        log.info(String.format("Request %s going to url %s", threeDSServerTransID, redirect));

        return redirect;
    }

    @PostMapping("/challenge")
    public String challengeUrl(Model model, @RequestParam String creq) {
        String decodedCreq = new String(Base64Utils.decodeFromString(creq));
        String threeDSServerTransID = (String) new Gson().fromJson(decodedCreq, Map.class).get("threeDSServerTransID");
        Transaction3DsEntity transaction = transaction3DsService.getByThreeDSServerTransId(threeDSServerTransID);

        model.addAttribute("notifyUrl", transaction.getNotifyUrl());
        model.addAttribute("threeDSServerTransID", transaction.getThreeDSServerTransId());
        model.addAttribute("cres", Base64Utils.encodeToString("FAKE_CRES".getBytes()));
        model.addAttribute("threeDSMtdComplInd", StringUtils.defaultIfBlank(transaction.getThreeDSMtdComplInd(), "UNKNOWN"));
        model.addAttribute("MOCK_PGS_URL", mockPgsUrl);

        return "vpos/challenge3ds.html";
    }

}
