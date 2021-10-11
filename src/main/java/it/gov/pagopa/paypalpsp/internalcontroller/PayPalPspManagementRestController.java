package it.gov.pagopa.paypalpsp.internalcontroller;


import it.gov.pagopa.db.entity.TableClient;
import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.db.repository.TableClientRepository;
import it.gov.pagopa.db.repository.TablePpPaypalManagementRepository;
import it.gov.pagopa.exception.BadRequestException;
import it.gov.pagopa.exception.NotFoundException;
import it.gov.pagopa.exception.UnauthorizedException;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackManagementRequest;
import it.gov.pagopa.paypalpsp.dto.PpOnboardingBackManagementResponse;
import it.gov.pagopa.paypalpsp.dto.PpResponseErrorInfo;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Collections;
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
    private TableClientRepository tableClientRepository;

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @PatchMapping("/response")
    public PpOnboardingBackManagementResponse changeIdUserIoResponse(@RequestHeader(value = "Authorization") String authorization,
                                                                     @Valid @RequestBody PpOnboardingBackManagementRequest ppOnboardingBackManagementRequest) throws BadRequestException, UnauthorizedException {
        TableClient tableClient = tableClientRepository.findByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "));
        if (tableClient == null) {
            throw new UnauthorizedException();
        }
        String idAppIo = ppOnboardingBackManagementRequest.getIdAppIo();
        ApiPaypalIdEnum apiId = ppOnboardingBackManagementRequest.getApiId();
        PpResponseErrCode errCode = ppOnboardingBackManagementRequest.getErrCode();

        if (apiId == ApiPaypalIdEnum.ONBOARDING_REDIRECT) {
            throw new BadRequestException(String.format("Cannot be set response for apiId '%s', please use the web page", apiId));
        } else if (errCode != null && errCode.getAllowed().stream().noneMatch(e -> e.equals(apiId))) {
            throw new BadRequestException(String.format("Invalid code '%s' for apiId '%s'", errCode, apiId));
        }

        TablePpPaypalManagement onboardingBackManagementSaved = tablePpPaypalManagementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, apiId, tableClient);
        if (onboardingBackManagementSaved == null) {
            onboardingBackManagementSaved = TablePpPaypalManagement.builder().idAppIo(idAppIo).apiId(apiId).client(tableClient).build();
        }
        onboardingBackManagementSaved.setErrCodeValue(errCode != null ? errCode.getCode() : null);
        onboardingBackManagementSaved.setLastUpdateDate(Instant.now());
        TablePpPaypalManagement newOnboardingBackManagement = tablePpPaypalManagementRepository.save(onboardingBackManagementSaved);
        return convertToPpOnboardingBackManagement(newOnboardingBackManagement);
    }

    //ONLY INTERNAL API - NOT INCLUDED IN PRODUCTION ENV
    @GetMapping(value = {"/response/{idAppIo}/{apiId}", "/response/{idAppIo}"})
    public List<PpOnboardingBackManagementResponse> getIdUserIoResponse(@RequestHeader(value = "Authorization") String authorization,
                                                                        @PathVariable String idAppIo,
                                                                        @PathVariable(required = false) ApiPaypalIdEnum apiId) throws NotFoundException, BadRequestException, UnauthorizedException {

        TableClient tableClient = tableClientRepository.findByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "));
        if (tableClient == null) {
            throw new UnauthorizedException();
        }
        List<TablePpPaypalManagement> paypalManagements;
        if (apiId != null) {
            TablePpPaypalManagement byIdAppIoAndApiId = tablePpPaypalManagementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, apiId, tableClient);
            paypalManagements = byIdAppIoAndApiId != null ? Collections.singletonList(byIdAppIoAndApiId) : null;
        } else {
            paypalManagements = tablePpPaypalManagementRepository.findByIdAppIoAndClient(idAppIo, tableClient);
        }

        if (CollectionUtils.isEmpty(paypalManagements)) {
            throw new NotFoundException();
        }
        return paypalManagements.stream().map(this::convertToPpOnboardingBackManagement).collect(Collectors.toList());
    }

    @GetMapping("/response")
    public List<PpResponseErrorInfo> getErrorList(@RequestParam(required = false, name = "apiId") ApiPaypalIdEnum apiPaypalIdEnum) {
        Stream<PpResponseErrCode> values;
        if (apiPaypalIdEnum != null) {
            values = PpResponseErrCode.valuesByApiId(apiPaypalIdEnum).stream();
        } else {
            values = Stream.of(PpResponseErrCode.values());
        }
        return values.map(PpResponseErrorInfo::new).collect(Collectors.toList());
    }

    private PpOnboardingBackManagementResponse convertToPpOnboardingBackManagement(TablePpPaypalManagement newOnboardingBackManagement) {
        PpOnboardingBackManagementResponse ppOnboardingBackManagementResponse = new PpOnboardingBackManagementResponse();
        ppOnboardingBackManagementResponse.setErrorInfo(PpResponseErrCode.of(newOnboardingBackManagement.getErrCodeValue()));
        ppOnboardingBackManagementResponse.setApiId(newOnboardingBackManagement.getApiId());
        ppOnboardingBackManagementResponse.setLastUpdateDate(newOnboardingBackManagement.getLastUpdateDate());
        ppOnboardingBackManagementResponse.setIdAppIo(newOnboardingBackManagement.getIdAppIo());
        return ppOnboardingBackManagementResponse;
    }
}