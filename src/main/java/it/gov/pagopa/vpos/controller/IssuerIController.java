package it.gov.pagopa.vpos.controller;

import com.google.gson.Gson;
import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.db.entity.Transaction3DsEntity;
import it.gov.pagopa.service.ConfigService;
import it.gov.pagopa.service.Transaction3DsService;
import it.gov.pagopa.vpos.dto.Method3Ds2ResponseEnum;
import it.gov.pagopa.vpos.dto.request.MethodData3Ds;
import it.gov.pagopa.vpos.utils.VposConstants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Controller
@RequestMapping("/issuer/3ds2.0/")
@Log4j2
public class IssuerIController {
    @Autowired
    private ConfigService configService;

    @Autowired
    private Transaction3DsService transaction3DsService;

    @PostMapping("/method")
    public String methodUrl(@RequestParam String threeDSMethodData, Model model) {
        Function<String, TableConfig> findByKey = key -> Optional.ofNullable(configService.getByKey(key)).orElse(new TableConfig());

        String decodedMethodData = new String(Base64Utils.decodeFromString(threeDSMethodData));
        MethodData3Ds methodData3Ds = new Gson().fromJson(decodedMethodData, MethodData3Ds.class);
        model.addAttribute("threeDSMethodNotificationUrl", methodData3Ds.getThreeDSMethodNotificationUrl());

        MethodData3Ds methodData3DsReturn = new MethodData3Ds();
        String threeDSServerTransID = methodData3Ds.getThreeDSServerTransID();
        methodData3DsReturn.setThreeDSServerTransID(threeDSServerTransID);
        model.addAttribute("methodData3Ds", Base64Utils.encodeToString(new Gson().toJson(methodData3DsReturn).getBytes()));
        log.info(String.format("Request %s", threeDSServerTransID));
        String byKeyConfig = findByKey.apply(VposConstants.METHOD_3DS2_RESPONSE).getPropertyValue();
        String redirect = "vpos/method3ds.html";

        if (StringUtils.equals(byKeyConfig, Method3Ds2ResponseEnum.NoResponse.name())) {
            redirect = "/home";
        }

        log.info(String.format("Request %s going to url %s", threeDSServerTransID, redirect));

        return redirect;
    }

    @PostMapping("/challenge")
    public String acsFor3dsV2Challenge(Model model, @RequestParam String creq) {
        String decodedCreq = new String(Base64Utils.decodeFromString(creq));
        String threeDSServerTransID = (String) new Gson().fromJson(decodedCreq, Map.class).get("threeDSServerTransID");
        Transaction3DsEntity byId = transaction3DsService.getByThreeDSServerTransId(threeDSServerTransID);
        model.addAttribute("notifyUrl", byId.getNotifyUrl());
        model.addAttribute("threeDSServerTransID", byId.getThreeDSServerTransId());
        model.addAttribute("cres", Base64Utils.encodeToString("FAKE_CRES".getBytes()));
        model.addAttribute("threeDSMtdComplInd", StringUtils.defaultIfBlank(byId.getThreeDSMtdComplInd(), "UNKNOWN"));

        return "vpos/challenge3ds.html";
    }

}
