package it.gov.pagopa.paypalpsp;


import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.entity.TablePpOnboardingBackManagement;
import it.gov.pagopa.db.repository.TableClientRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackManagementRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TableUserPayPalRepository;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackRequest;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackResponse;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseErrCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @Autowired
    private TableClientRepository tableClientRepository;

    private static final String BEARER_REGEX = "Bearer .*{5,}";

    @PostMapping("/api/pp_oboarding_back")
    public PpOnboardingBackResponse homePage(@RequestHeader(value = "Authorization", required = false) String authorization,
                                             @Valid @RequestBody PpOnboardingBackRequest ppOnboardingBackRequest) {
        if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || !tableClientRepository.existsByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) {
            log.error("Invalid authorization: " + authorization);
            return manageErrorResponse(PpOnboardingBackResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }
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
        return PpOnboardingBackResponse.builder().esito(PpOnboardingBackResponseCode.OK).urlToCall("/paypalweb/pp_onboarding_call?id_back=" + idBack).build();
    }

    private void saveOnTable(PpOnboardingBackRequest ppOnboardingBackRequest, String idBack) {
        TablePpOnboardingBack tablePpOnboardingBack = new TablePpOnboardingBack();
        tablePpOnboardingBack.setIdAppIo(ppOnboardingBackRequest.getIdAppIo());
        tablePpOnboardingBack.setTimestamp(Instant.now());
        tablePpOnboardingBack.setUrlReturn(ppOnboardingBackRequest.getUrlReturn());
        tablePpOnboardingBack.setIdBack(idBack);
        tablePpOnboardingBackRepository.save(tablePpOnboardingBack);
    }

    private PpOnboardingBackResponse manageErrorResponse(PpOnboardingBackResponseErrCode errCode) {
        return PpOnboardingBackResponse.builder().esito(PpOnboardingBackResponseCode.KO).errCode(errCode).errDesc(errCode.name()).build();
    }
}