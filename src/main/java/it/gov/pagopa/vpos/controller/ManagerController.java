package it.gov.pagopa.vpos.controller;

import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.service.ConfigService;
import it.gov.pagopa.vpos.dto.Method3Ds2ResponseEnum;
import it.gov.pagopa.vpos.dto.TransactionStatusEnum;
import it.gov.pagopa.vpos.dto.response.Response3Ds2Step0Enum;
import it.gov.pagopa.vpos.dto.response.Response3Ds2Step1Enum;
import it.gov.pagopa.vpos.dto.response.ResponseOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static it.gov.pagopa.vpos.utils.VposConstants.*;

@Controller
@RequestMapping(value = "/3ds2.0-manager")
public class ManagerController {
    @Autowired
    private ConfigService configService;

    @GetMapping("/home")
    public String vposMockHome(Model model) {
        Function<String,TableConfig> findByKey = key -> Optional.ofNullable(configService.getByKey(key)).orElse(new TableConfig());

        model.addAttribute("method3Ds2Response", findByKey.apply(VPOS_METHOD_3DS2_RESPONSE));
        model.addAttribute("method3Ds2bancomatResponseEnum", Arrays.asList(Method3Ds2ResponseEnum.values()));

        model.addAttribute("step0Response", findByKey.apply(VPOS_STEP0_3DS2_RESPONSE));
        model.addAttribute("step0ResponseEnum", Arrays.asList(Response3Ds2Step0Enum.values()));

        model.addAttribute("step1Response", findByKey.apply(VPOS_STEP1_3DS2_RESPONSE));
        model.addAttribute("step1ResponseEnum", Arrays.asList(Response3Ds2Step1Enum.values()));

        model.addAttribute("orderStatusResponse", findByKey.apply(VPOS_ORDER_STATUS_RESPONSE));
        model.addAttribute("orderStatusResponseEnum", Arrays.asList(ResponseOrderStatusEnum.values()));

        model.addAttribute("vposHttpCodeResponse", findByKey.apply(VPOS_HTTP_CODE_RESPONSE));
        model.addAttribute("vposHttpCodeResponseEnum", Arrays.asList(HttpStatus.OK, HttpStatus.NOT_FOUND, HttpStatus.SERVICE_UNAVAILABLE));

        model.addAttribute("transactionStatus", findByKey.apply(VPOS_TRANSACTION_STATUS));
        model.addAttribute("transactionStatusEnum", Arrays.asList(TransactionStatusEnum.values()));

        return "vpos/3ds2-manager.html";
    }

    @PostMapping("/change-response")
    private String changeVposConfig(@ModelAttribute TableConfig tableConfig) {
        configService.save(tableConfig);

        return "redirect:/3ds2.0-manager/home";
    }
}
