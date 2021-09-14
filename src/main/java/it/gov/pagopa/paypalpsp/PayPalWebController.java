package it.gov.pagopa.paypalpsp;


import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.entity.TableUserPayPal;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TableUserPayPalRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/paypalweb")
@SessionAttributes({PayPalWebController.TABLE_PP_ONBOARDING_BACK_ATTRIBUTE})
public class PayPalWebController {
    protected static final String TABLE_PP_ONBOARDING_BACK_ATTRIBUTE = "tablePpOnboardingBack";
    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    @Autowired
    private TableUserPayPalRepository tableUserPayPalRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @GetMapping("/pp_onboarding_call")
    public String homePage(Model model, @RequestParam("id_back") String idBack, ModelMap modelMap, HttpServletRequest request) {
        request.getSession().invalidate();
        modelMap.remove(TABLE_PP_ONBOARDING_BACK_ATTRIBUTE);
        TablePpOnboardingBack tablePpOnboardingBack = tablePpOnboardingBackRepository.findByIdBack(idBack);
        log.info("Onboardingback " + tablePpOnboardingBack + "with id_back" + idBack);
        if (tablePpOnboardingBack != null && !tablePpOnboardingBack.isUsed()) {
            tablePpOnboardingBack.setUsed(true);
            tablePpOnboardingBackRepository.save(tablePpOnboardingBack);
            model.addAttribute("id_back", idBack);
            model.addAttribute("urlReturn", tablePpOnboardingBack.getUrlReturn());
            model.addAttribute("timestamp", DATE_TIME_FORMATTER.format(tablePpOnboardingBack.getTimestamp()));
            model.addAttribute("ioAppIo", tablePpOnboardingBack.getIdAppIo());
            modelMap.addAttribute(TABLE_PP_ONBOARDING_BACK_ATTRIBUTE, tablePpOnboardingBack);
        }

        return "paypal/paypalwebpage.html";
    }

    @PostMapping("/success")
    public String successLoginPaypal(ModelMap modelMap, SessionStatus sessionStatus) {
        String urlReturn = "";
        try {
            TablePpOnboardingBack tablePpOnboardingBack = (TablePpOnboardingBack) modelMap.getAttribute(TABLE_PP_ONBOARDING_BACK_ATTRIBUTE);
            if (tablePpOnboardingBack == null) {
                return "redirect:/paypalweb/pp_onboarding_call?id_back=unknown";
            }
            urlReturn = tablePpOnboardingBack.getUrlReturn();
            TableUserPayPal tableUserPayPal = TableUserPayPal.builder().idAppIo(tablePpOnboardingBack.getIdAppIo())
                    .contractNumber(UUID.randomUUID().toString()).build();
            log.info("Trying to create contract: " + tableUserPayPal);
            tableUserPayPal = tableUserPayPalRepository.save(tableUserPayPal);
            log.info("New Contract established: " + tableUserPayPal);
            return String.format("redirect:%s?esito=1", urlReturn);
        } catch (Exception e) {
            return String.format("redirect:%s?esito=67&err_cod=11&err_desc=Unexpected DB Error", urlReturn);
        } finally {
            sessionStatus.setComplete();
        }
    }
}