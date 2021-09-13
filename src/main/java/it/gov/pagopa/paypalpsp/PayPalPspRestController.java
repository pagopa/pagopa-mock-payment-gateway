package it.gov.pagopa.paypalpsp;


import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.entity.TablePpOnboardingBackManagement;
import it.gov.pagopa.db.repository.TablePpOnboardingBackManagementRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TableUserPayPalRepository;
import it.gov.pagopa.eception.NotFoundException;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackManagement;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackRequest;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackResponse;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseErrCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/paypalpsp")
@Log4j2
public class PayPalPspRestController {

    @Autowired
    private TablePpOnboardingBackManagementRepository onboardingBackManagementRepository;

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    @Autowired
    private TableUserPayPalRepository tableUserPayPalRepository;

    @PostMapping("/api/pp_oboarding_back")
    public PpOnboardingBackResponse homePage(@Valid @RequestHeader("Authorization") @Pattern(regexp = "Bearer .*{5,}") String authorization,
                                             @Valid @RequestBody PpOnboardingBackRequest ppOnboardingBackRequest) {
        log.info(ppOnboardingBackRequest);
        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        TablePpOnboardingBackManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIo(idAppIo);

        if (onboardingBackManagement != null && onboardingBackManagement.getErrCode() != null) {
            return manageErrorResponse(onboardingBackManagement.getErrCode());
        } else if (tableUserPayPalRepository.findByIdAppIoAndDeletedFalse(idAppIo) != null) {
            return manageErrorResponse(PpOnboardingBackResponseErrCode.CODICE_CONTRATTO_PRESENTE);
        }

        String idBack = UUID.randomUUID().toString();
        saveOnTable(ppOnboardingBackRequest, idBack);
        return PpOnboardingBackResponse.builder().esito(PpOnboardingBackResponseCode.OK).urlToCall("?id_back=" + idBack).build();
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @PatchMapping("/management/pp_oboarding_back/response")
    public PpOnboardingBackManagement changeIdUserIoResponse(@Valid @RequestBody PpOnboardingBackManagement ppOnboardingBackManagement) {
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

    @GetMapping("/management/pp_oboarding_back/response/{idAppIo}")
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

    private void saveOnTable(PpOnboardingBackRequest ppOnboardingBackRequest, String idBack) {
        TablePpOnboardingBack tablePpOnboardingBack = new TablePpOnboardingBack();
        tablePpOnboardingBack.setIdAppIo(ppOnboardingBackRequest.getIdAppIo());
        tablePpOnboardingBack.setTimestamp(ppOnboardingBackRequest.getTimestamp());
        tablePpOnboardingBack.setUrlReturn(ppOnboardingBackRequest.getUrlReturn());
        tablePpOnboardingBack.setIdBack(idBack);
        tablePpOnboardingBackRepository.save(tablePpOnboardingBack);
    }

    private PpOnboardingBackResponse manageErrorResponse(PpOnboardingBackResponseErrCode errCode) {
        return PpOnboardingBackResponse.builder().esito(PpOnboardingBackResponseCode.KO).errCode(errCode).errDesc(errCode.name()).build();
    }
}