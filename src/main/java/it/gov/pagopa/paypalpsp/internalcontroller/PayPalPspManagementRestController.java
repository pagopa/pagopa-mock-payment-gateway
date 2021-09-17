package it.gov.pagopa.paypalpsp.internalcontroller;


import it.gov.pagopa.db.entity.TablePpOnboardingBackManagement;
import it.gov.pagopa.db.repository.TablePpOnboardingBackManagementRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.exception.NotFoundException;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackManagement;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseErrCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@Validated
@RestController
@RequestMapping("/paypalpsp/management")
@Log4j2
public class PayPalPspManagementRestController {

    @Autowired
    private TablePpOnboardingBackManagementRepository onboardingBackManagementRepository;

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @PatchMapping("/pp_onboarding_back/response")
    public PpOnboardingBackManagement changeIdUserIoResponse(@Valid @RequestBody PpOnboardingBackManagement ppOnboardingBackManagement) throws Exception {
        if (ppOnboardingBackManagement.getErrCode() == PpOnboardingBackResponseErrCode.CODICE_CONTRATTO_PRESENTE) {
            throw new Exception("Cannot set error code " + PpOnboardingBackResponseErrCode.CODICE_CONTRATTO_PRESENTE + ". Create a contract and then you'll receive the code 19");
        }
        String idAppIo = ppOnboardingBackManagement.getIdAppIo();
        TablePpOnboardingBackManagement onboardingBackManagementSaved = onboardingBackManagementRepository.findByIdAppIo(idAppIo);
        if (onboardingBackManagementSaved == null) {
            onboardingBackManagementSaved = TablePpOnboardingBackManagement.builder().idAppIo(idAppIo).build();
        }
        onboardingBackManagementSaved.setErrCode(ppOnboardingBackManagement.getErrCode());
        onboardingBackManagementSaved.setLastUpdateDate(Instant.now());
        TablePpOnboardingBackManagement newOnboardingBackManagement = onboardingBackManagementRepository.save(onboardingBackManagementSaved);
        return convertToPpOnboardingBackManagement(newOnboardingBackManagement);
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping("/pp_onboarding_back/response/{idAppIo}")
    public PpOnboardingBackManagement getIdUserIoResponse(@PathVariable String idAppIo) throws NotFoundException {
        TablePpOnboardingBackManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIo(idAppIo);
        if (onboardingBackManagement == null) {
            throw new NotFoundException();
        }
        return convertToPpOnboardingBackManagement(onboardingBackManagement);
    }

    private PpOnboardingBackManagement convertToPpOnboardingBackManagement(TablePpOnboardingBackManagement newOnboardingBackManagement) {
        return PpOnboardingBackManagement.builder().idAppIo(newOnboardingBackManagement.getIdAppIo())
                .errCode(newOnboardingBackManagement.getErrCode()).build();
    }
}