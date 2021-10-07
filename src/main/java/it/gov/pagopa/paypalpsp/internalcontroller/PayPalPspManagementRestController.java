package it.gov.pagopa.paypalpsp.internalcontroller;


import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TablePpPaypalManagementRepository;
import it.gov.pagopa.exception.BadRequestException;
import it.gov.pagopa.exception.NotFoundException;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackManagement;
import it.gov.pagopa.paypalpsp.dto.PpResponseErrorInfo;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public PpOnboardingBackManagement changeIdUserIoResponse(@Valid @RequestBody PpOnboardingBackManagement ppOnboardingBackManagement) throws Exception {
        String idAppIo = ppOnboardingBackManagement.getIdAppIo();
        ApiPaypalIdEnum apiId = ppOnboardingBackManagement.getApiId();
        PpResponseErrCode errCode = ppOnboardingBackManagement.getErrCode();

        if (errCode != null && errCode.getAllowed().stream().noneMatch(e -> e.equals(apiId))) {
            throw new BadRequestException(String.format("Invalid code '%s' for apiId '%s'", errCode, apiId));
        }
        TablePpPaypalManagement onboardingBackManagementSaved = tablePpPaypalManagementRepository.findByIdAppIoAndApiId(idAppIo, apiId);
        if (onboardingBackManagementSaved == null) {
            onboardingBackManagementSaved = TablePpPaypalManagement.builder().idAppIo(idAppIo).apiId(apiId).build();
        }
        onboardingBackManagementSaved.setErrCodeValue(errCode != null ? errCode.getCode() : null);
        onboardingBackManagementSaved.setLastUpdateDate(Instant.now());
        TablePpPaypalManagement newOnboardingBackManagement = tablePpPaypalManagementRepository.save(onboardingBackManagementSaved);
        return convertToPpOnboardingBackManagement(newOnboardingBackManagement);
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping("/response/{idAppIo}/{apiId}")
    public PpOnboardingBackManagement getIdUserIoResponse(@PathVariable String
                                                                  idAppIo, @PathVariable ApiPaypalIdEnum apiId) throws NotFoundException {
        TablePpPaypalManagement onboardingBackManagement = tablePpPaypalManagementRepository.findByIdAppIoAndApiId(idAppIo, apiId);
        if (onboardingBackManagement == null) {
            throw new NotFoundException();
        }
        return convertToPpOnboardingBackManagement(onboardingBackManagement);
    }


    @GetMapping("/response")
    public List<PpResponseErrorInfo> getErrorList() {
        return Stream.of(PpResponseErrCode.values()).map(PpResponseErrorInfo::new).collect(Collectors.toList());
    }

    private PpOnboardingBackManagement convertToPpOnboardingBackManagement(TablePpPaypalManagement
                                                                                   newOnboardingBackManagement) {
        return PpOnboardingBackManagement.builder().idAppIo(newOnboardingBackManagement.getIdAppIo())
                .errCode(PpResponseErrCode.of(newOnboardingBackManagement.getErrCodeValue()))
                .apiId(newOnboardingBackManagement.getApiId())
                .lastUpdateDate(newOnboardingBackManagement.getLastUpdateDate())
                .build();
    }
}