package it.gov.pagopa.paypalpsp.internalcontroller;


import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TablePpPaypalManagementRepository;
import it.gov.pagopa.exception.NotFoundException;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackManagement;
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
    private TablePpPaypalManagementRepository tablePpPaypalManagementRepository;

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @PatchMapping("/response")
    public PpOnboardingBackManagement changeIdUserIoResponse(@Valid @RequestBody PpOnboardingBackManagement ppOnboardingBackManagement) {
        String idAppIo = ppOnboardingBackManagement.getIdAppIo();
        ApiPaypalIdEnum apiId = ppOnboardingBackManagement.getApiId();
        TablePpPaypalManagement onboardingBackManagementSaved = tablePpPaypalManagementRepository.findByIdAppIoAndApiId(idAppIo, apiId);
        if (onboardingBackManagementSaved == null) {
            onboardingBackManagementSaved = TablePpPaypalManagement.builder().idAppIo(idAppIo).apiId(apiId).build();
        }
        onboardingBackManagementSaved.setErrCodeValue(ppOnboardingBackManagement.getErrCode());
        onboardingBackManagementSaved.setLastUpdateDate(Instant.now());
        TablePpPaypalManagement newOnboardingBackManagement = tablePpPaypalManagementRepository.save(onboardingBackManagementSaved);
        return convertToPpOnboardingBackManagement(newOnboardingBackManagement);
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping("/response/{idAppIo}/{apiId}")
    public PpOnboardingBackManagement getIdUserIoResponse(@PathVariable String idAppIo, @PathVariable ApiPaypalIdEnum apiId) throws NotFoundException {
        TablePpPaypalManagement onboardingBackManagement = tablePpPaypalManagementRepository.findByIdAppIoAndApiId(idAppIo, apiId);
        if (onboardingBackManagement == null) {
            throw new NotFoundException();
        }
        return convertToPpOnboardingBackManagement(onboardingBackManagement);
    }

    private PpOnboardingBackManagement convertToPpOnboardingBackManagement(TablePpPaypalManagement newOnboardingBackManagement) {
        return PpOnboardingBackManagement.builder().idAppIo(newOnboardingBackManagement.getIdAppIo())
                .errCode(newOnboardingBackManagement.getErrCodeValue())
                .apiId(newOnboardingBackManagement.getApiId()).build();
    }
}