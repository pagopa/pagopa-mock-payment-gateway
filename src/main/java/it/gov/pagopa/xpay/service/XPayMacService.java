package it.gov.pagopa.xpay.service;

import it.gov.pagopa.xpay.dto.XPayAuthResponse;
import it.gov.pagopa.xpay.dto.XPayError;
import it.gov.pagopa.xpay.dto.XPayErrorEnum;
import it.gov.pagopa.xpay.dto.XPayOutcome;
import it.gov.pagopa.xpay.utils.XPayUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@Log4j2
public class XPayMacService {
    @Value("${xpay.apikey-alias}")
    private String apiKey;

    @Value("${xpay.secret-key}")
    private String chiaveSegreta;

    public ResponseEntity<XPayAuthResponse> getMac(String codiceTransazione, Long divisa, BigInteger importo, String timeStamp) {
        try {
            log.info("XPay Mac - Generating MAC for transactionId: " + codiceTransazione);

            String mac;
            if(areImportoAndDivisaPresent(importo, divisa))
                mac = XPayUtils.getBaseMac(codiceTransazione, divisa, importo, timeStamp, apiKey, chiaveSegreta);
            else
                mac = XPayUtils.getRefundMac(codiceTransazione, timeStamp, apiKey, chiaveSegreta);

            XPayAuthResponse response = new XPayAuthResponse();
            response.setEsito(XPayOutcome.OK);
            response.setMac(mac);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("XPay Mac - Exception during the creation of the MAC string: ", e);
            XPayErrorEnum error = XPayErrorEnum.ERROR_50;

            XPayAuthResponse response = new XPayAuthResponse();
            response.setEsito(XPayOutcome.KO);
            response.setErrore(new XPayError(error.getErrorCode(), error.getDescription()));

            return ResponseEntity.status(error.getHttpStatus()).body(response);
        }
    }

    private static boolean areImportoAndDivisaPresent(BigInteger importo, Long divisa) throws Exception {
        boolean isImportoPresent = importo != null;
        boolean isDivisaPresent = divisa != null;

        if(isImportoPresent && isDivisaPresent)
            return true;
        else if(!isImportoPresent && !isDivisaPresent)
            return false;

        throw new Exception();
    }
}
