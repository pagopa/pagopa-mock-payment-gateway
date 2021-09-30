package it.gov.pagopa.paypalpsp.mockcontroller;


import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entity.TableUserPayPal;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.db.repository.TableClientRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TablePpPaypalManagementRepository;
import it.gov.pagopa.db.repository.TableUserPayPalRepository;
import it.gov.pagopa.paypalpsp.PaypalUtils;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackRequest;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackResponse;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseErrCode;
import it.gov.pagopa.util.UrlUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Validated
@RestController
@RequestMapping("/paypalpsp")
@Log4j2
public class PayPalPspRestController {

    @Autowired
    private TablePpPaypalManagementRepository onboardingBackManagementRepository;

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    @Autowired
    private TableUserPayPalRepository tableUserPayPalRepository;

    @Autowired
    private TableClientRepository tableClientRepository;

    @Autowired
    private PaypalUtils paypalUtils;

    @Value("${server.public-url}")
    private String publicUrl;

    private static final String BEARER_REGEX = "Bearer\\s.{3,}";

    @PostMapping("/api/pp_onboarding_back")
    @Transactional
    public PpOnboardingBackResponse homePage(@RequestHeader(value = "Authorization", required = false) String authorization,
                                             @Valid @RequestBody PpOnboardingBackRequest ppOnboardingBackRequest) throws URISyntaxException, InterruptedException, TimeoutException {
        if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || !tableClientRepository.existsByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) {
            log.error("Invalid authorization: " + authorization);
            return manageErrorResponse(PpOnboardingBackResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiId(idAppIo, ApiPaypalIdEnum.ONBOARDING);

        //Manage error defined by user
        if (onboardingBackManagement != null
                && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpOnboardingBackResponseErrCode.TIMEOUT.getCode())) {
            log.info("Going in timeout: " + ppOnboardingBackRequest.getIdAppIo());
            Thread.sleep(20000);
            throw new TimeoutException();
        } else if (onboardingBackManagement != null && StringUtils.isNotBlank(onboardingBackManagement.getErrCodeValue())) {
            return manageErrorResponse(PpOnboardingBackResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
        }

        //manage error code 19
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndDeletedFalse(idAppIo);
        if (byIdAppIoAndDeletedFalse != null) {
            return manageErrorResponseAlreadyOnboarded(byIdAppIoAndDeletedFalse);
        }

        String idBack = UUID.randomUUID().toString();
        saveAndUpdateTable(ppOnboardingBackRequest, idBack);
        String returnUrl = UrlUtils.normalizeUrl(publicUrl + "/paypalweb/pp_onboarding_call");
        return PpOnboardingBackResponse.builder().esito(PpOnboardingBackResponseCode.OK).urlToCall(UrlUtils.addQueryParams(returnUrl, "id_back", idBack)).build();
    }

    private void saveAndUpdateTable(PpOnboardingBackRequest ppOnboardingBackRequest, String idBack) {
        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        tablePpOnboardingBackRepository.setUsedTrueByIdBack(idAppIo);
        TablePpOnboardingBack tablePpOnboardingBack = new TablePpOnboardingBack();
        tablePpOnboardingBack.setIdAppIo(idAppIo);
        tablePpOnboardingBack.setTimestamp(Instant.now());
        tablePpOnboardingBack.setUrlReturn(ppOnboardingBackRequest.getUrlReturn());
        tablePpOnboardingBack.setIdBack(idBack);
        tablePpOnboardingBackRepository.save(tablePpOnboardingBack);
    }

    private PpOnboardingBackResponse manageErrorResponseAlreadyOnboarded(TableUserPayPal tableUserPayPal) {
        PpOnboardingBackResponse ppOnboardingBackResponse = manageErrorResponse(PpOnboardingBackResponseErrCode.CODICE_CONTRATTO_PRESENTE);
        ppOnboardingBackResponse.setEmailPp(paypalUtils.obfuscateEmail(tableUserPayPal.getPaypalEmail()));
        ppOnboardingBackResponse.setIdPp(tableUserPayPal.getPaypalId());
        return ppOnboardingBackResponse;
    }

    private PpOnboardingBackResponse manageErrorResponse(PpOnboardingBackResponseErrCode errCode) {
        return PpOnboardingBackResponse.builder().esito(PpOnboardingBackResponseCode.KO)
                .errCod(errCode)
                .errDesc(errCode.name())
                .build();
    }
}