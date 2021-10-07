package it.gov.pagopa.paypalpsp.mockcontroller;


import com.github.javafaker.Faker;
import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.paypalpsp.PaypalUtils;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingCallResponseEsito;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Log4j2
@Controller
@RequestMapping("/paypalweb")
@SessionAttributes({PayPalWebController.TABLE_PP_ONBOARDING_BACK_ATTRIBUTE})
public class PayPalWebController {
    protected static final String TABLE_PP_ONBOARDING_BACK_ATTRIBUTE = "tablePpOnboardingBack";

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private PaypalUtils paypalUtils;

    private static final Faker FAKER = new Faker(Locale.ITALIAN);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @GetMapping("/pp_onboarding_call")
    public String homePage(Model model, @RequestParam("id_back") String idBack, ModelMap modelMap, HttpServletRequest request) {
        request.getSession().invalidate();
        modelMap.remove(TABLE_PP_ONBOARDING_BACK_ATTRIBUTE);

        TableConfig tableConfig = configRepository.findByPropertyKey("PAYPAL_PSP_DEFAULT_BACK_URL");
        String esito = PpOnboardingCallResponseEsito.KO.getCode();
        PpResponseErrCode idBackUsatoNonValido = PpResponseErrCode.ID_BACK_USATO_NON_VALIDO;

        String hmac = paypalUtils.calculateHmac(esito, null, null, idBackUsatoNonValido, idBack);
        String urlReturnFallBackPaypalPsp = String.format("%s?esito=%s&err_cod=%s&err_desc=%s&sha_val=%s",
                StringUtils.defaultString(tableConfig.getPropertyValue()), esito, idBackUsatoNonValido.getCode(),
                idBackUsatoNonValido.getDescription(), hmac);
        model.addAttribute("urlReturnFallBackPaypalPsp", urlReturnFallBackPaypalPsp);

        setModelFromOnboardingBack(model, idBack, modelMap);

        return "paypal/paypalwebpage.html";
    }

    private void setModelFromOnboardingBack(Model model, @RequestParam("id_back") String idBack, ModelMap modelMap) {
        TablePpOnboardingBack tablePpOnboardingBack = tablePpOnboardingBackRepository.findByIdBack(idBack);
        log.info("Onboardingback " + tablePpOnboardingBack + "with id_back" + idBack);
        if (tablePpOnboardingBack != null && !tablePpOnboardingBack.isUsed()) {
            tablePpOnboardingBack.setUsed(true);
            tablePpOnboardingBackRepository.save(tablePpOnboardingBack);
            model.addAttribute("id_back", idBack);
            model.addAttribute("urlReturn", tablePpOnboardingBack.getUrlReturn());
            model.addAttribute("timestamp", DATE_TIME_FORMATTER.format(tablePpOnboardingBack.getTimestamp()));
            model.addAttribute("idAppIo", tablePpOnboardingBack.getIdAppIo());
            model.addAttribute("paypalId", StringUtils.leftPad(tablePpOnboardingBack.getIdAppIo(), 5, '0'));
            model.addAttribute("paypalEmail", FAKER.internet().safeEmailAddress());
            modelMap.addAttribute(TABLE_PP_ONBOARDING_BACK_ATTRIBUTE, tablePpOnboardingBack);
        }
    }
}

