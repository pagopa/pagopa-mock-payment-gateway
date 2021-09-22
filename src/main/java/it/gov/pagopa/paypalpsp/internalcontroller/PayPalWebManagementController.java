package it.gov.pagopa.paypalpsp.internalcontroller;


import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.entity.TableUserPayPal;
import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.db.repository.TableUserPayPalRepository;
import it.gov.pagopa.paypalpsp.PaypalUtils;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingCallResponseErrCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingCallResponseEsito;
import it.gov.pagopa.util.UrlUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/paypalweb/management")
public class PayPalWebManagementController {
    private static final String REDIRECT_PAYPALWEB_PP_ONBOARDING_CALL_ID_BACK_UNKNOWN = "redirect:/paypalweb/pp_onboarding_call?id_back=unknown";

    @Autowired
    private TableUserPayPalRepository tableUserPayPalRepository;

    @Autowired
    private PaypalUtils paypalUtils;

    @Autowired
    private TableConfigRepository configRepository;

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @PostMapping("/success")
    public String success(SessionStatus sessionStatus, @RequestParam String paypalEmail, @RequestParam String paypalId, @RequestParam boolean selectRedirect, @SessionAttribute(required = false) TablePpOnboardingBack tablePpOnboardingBack, ModelMap modelMap) {
        try {
            if (tablePpOnboardingBack == null) {
                return REDIRECT_PAYPALWEB_PP_ONBOARDING_CALL_ID_BACK_UNKNOWN;
            }
            String urlReturn = tablePpOnboardingBack.getUrlReturn();
            String idAppIo = tablePpOnboardingBack.getIdAppIo();
            TableUserPayPal tableUserPayPal = TableUserPayPal.builder()
                    .idAppIo(idAppIo)
                    .paypalEmail(paypalEmail)
                    .paypalId(paypalId)
                    .contractNumber(UUID.randomUUID().toString()).build();
            log.info("Trying to create contract: " + tableUserPayPal);
            tableUserPayPal = tableUserPayPalRepository.save(tableUserPayPal);
            log.info("New Contract established: " + tableUserPayPal);

            String esito = PpOnboardingCallResponseEsito.OK.getCode();
            String emailPpObfuscated = paypalUtils.obfuscateEmail(paypalEmail);
            if (selectRedirect) {
                String hmac = paypalUtils.calculateHmac(esito, paypalId, emailPpObfuscated, null, tablePpOnboardingBack.getIdBack());
                String redirectUrl = createRedirectUrlSuccess(urlReturn, esito, emailPpObfuscated, paypalId, hmac);
                log.info(String.format("Success paypal redirect for user id: '%s' and redirect url '%s'", idAppIo, redirectUrl));
                return redirectUrl;
            } else {
                modelMap.addAttribute("paypalEmail", emailPpObfuscated);
                modelMap.addAttribute("idPayPal", paypalId);
                modelMap.addAttribute("idAppIo", idAppIo);
                log.info("successNoRedirect not follow the redirect");
                return "paypal/successNoRedirect.html";
            }
        } finally {
            sessionStatus.setComplete();
        }
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping("/cancel")
    public String cancel(SessionStatus sessionStatus, @SessionAttribute(required = false) TablePpOnboardingBack tablePpOnboardingBack) {
        try {
            if (tablePpOnboardingBack == null) {
                return REDIRECT_PAYPALWEB_PP_ONBOARDING_CALL_ID_BACK_UNKNOWN;
            }
            String esito = PpOnboardingCallResponseEsito.CANCEL.getCode();
            String hmac = paypalUtils.calculateHmac(esito, null, null, null, tablePpOnboardingBack.getIdBack());
            String redirectUrl = createRedirectUrlError(tablePpOnboardingBack.getUrlReturn(), esito, null, null, hmac);

            log.info(String.format("Cancel paypal redirect for user id: '%s' and redirect url '%s'", tablePpOnboardingBack.getIdAppIo(), redirectUrl));
            return redirectUrl;
        } finally {
            sessionStatus.setComplete();
        }
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping("/error/{errCode}")
    public String error(@PathVariable String errCode, SessionStatus sessionStatus, @SessionAttribute(required = false) TablePpOnboardingBack tablePpOnboardingBack) {
        try {
            if (tablePpOnboardingBack == null) {
                return REDIRECT_PAYPALWEB_PP_ONBOARDING_CALL_ID_BACK_UNKNOWN;
            }
            PpOnboardingCallResponseErrCode callResponseErrCode = PpOnboardingCallResponseErrCode.of(errCode);

            String esito = PpOnboardingCallResponseEsito.KO.getCode();
            String hmac = paypalUtils.calculateHmac(esito, null, null, callResponseErrCode, tablePpOnboardingBack.getIdBack());
            String redirectBaseUrl = getPaypalBaseRedirectUrl(tablePpOnboardingBack, callResponseErrCode);
            String redirectUrl = createRedirectUrlError(redirectBaseUrl, esito, callResponseErrCode.getCode(), callResponseErrCode.getDescription(), hmac);

            log.info(String.format("Error paypal redirect for user id: '%s' and redirect url '%s'", tablePpOnboardingBack.getIdAppIo(), redirectUrl));
            return redirectUrl;
        } finally {
            sessionStatus.setComplete();
        }
    }

    private String getPaypalBaseRedirectUrl(@SessionAttribute(required = false) TablePpOnboardingBack tablePpOnboardingBack, PpOnboardingCallResponseErrCode callResponseErrCode) {
        return callResponseErrCode == PpOnboardingCallResponseErrCode.ID_BACK_USATO_NON_VALIDO
                ? configRepository.findByPropertyKey("PAYPAL_PSP_DEFAULT_BACK_URL").getPropertyValue() : tablePpOnboardingBack.getUrlReturn();
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping("/invalidShaVal")
    public String invalidShaVal(SessionStatus sessionStatus, @SessionAttribute(required = false) TablePpOnboardingBack tablePpOnboardingBack) {
        try {
            if (tablePpOnboardingBack == null) {
                return REDIRECT_PAYPALWEB_PP_ONBOARDING_CALL_ID_BACK_UNKNOWN;
            }
            String esito = PpOnboardingCallResponseEsito.OK.getCode();
            String hmac = paypalUtils.calculateHmac(esito, null, null, null, "invalidShaVal" + UUID.randomUUID().toString());
            String redirectUrl = createRedirectUrlSuccess(tablePpOnboardingBack.getUrlReturn(), esito, "sha@invalid.com", "00000", hmac);
            log.info(String.format("invalidShaVal paypal redirect for user id: '%s' and redirect url '%s'", tablePpOnboardingBack.getIdAppIo(), redirectUrl));
            return redirectUrl;
        } finally {
            sessionStatus.setComplete();
        }
    }

    private String createRedirectUrlSuccess(String url, String esito, String emailPp, String idPp, String shaVal) {
        return createRedirectUrl(url, esito, null, null, emailPp, idPp, shaVal);
    }

    private String createRedirectUrlError(String url, String esito, String errCode, String errDesc, String shaVal) {
        return createRedirectUrl(url, esito, errCode, errDesc, null, null, shaVal);
    }

    private String createRedirectUrl(String url, String esito, String errCode, String errDesc, String emailPp, String idPp, String shaVal) {
        String newUrl = UrlUtils.addQueryParams(url, "esito", esito);

        if (StringUtils.isNotBlank(errCode)) {
            newUrl = UrlUtils.addQueryParams(newUrl, "err_cod", errCode);
        }

        if (StringUtils.isNotBlank(errDesc)) {
            newUrl = UrlUtils.addQueryParams(newUrl, "err_desc", errDesc);
        }

        if (StringUtils.isNotBlank(emailPp)) {
            newUrl = UrlUtils.addQueryParams(newUrl, "email_pp", emailPp);
        }

        if (StringUtils.isNotBlank(idPp)) {
            newUrl = UrlUtils.addQueryParams(newUrl, "id_pp", idPp);
        }

        return "redirect:" + UrlUtils.addQueryParams(newUrl, "sha_val", shaVal);
    }
}

