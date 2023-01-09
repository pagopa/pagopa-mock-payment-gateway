package it.gov.pagopa.vpos.controller;

import it.gov.pagopa.vpos.dto.request.SaveResponseChallenge3Ds2;
import it.gov.pagopa.vpos.entity.Transaction3DsEntity;
import it.gov.pagopa.vpos.service.Transaction3DsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/3ds2.0-manager")
@Log4j2
public class ManagerRestController {
    @Autowired
    private Transaction3DsService transaction3DsService;

    @PostMapping("/challenge/save/response")
    public void saveChallengeOutcome(@RequestBody SaveResponseChallenge3Ds2 saveResponseChallenge3Ds2) {
        log.info("Saving challenge outcome " + saveResponseChallenge3Ds2.getOutcome() + " for transactionId " + saveResponseChallenge3Ds2.getThreeDSServerTransID());
        Transaction3DsEntity transaction3DsEntity = transaction3DsService.getByThreeDSServerTransId(saveResponseChallenge3Ds2.getThreeDSServerTransID());
        transaction3DsEntity.setOutcome(saveResponseChallenge3Ds2.getOutcome());

        transaction3DsService.save(transaction3DsEntity);
    }
}
