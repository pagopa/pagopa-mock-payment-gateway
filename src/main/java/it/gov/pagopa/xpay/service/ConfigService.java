package it.gov.pagopa.xpay.service;

import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.db.repository.TableConfigRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ConfigService {
    @Autowired
    private TableConfigRepository configRepository;

    public void updateXPayAuthConfig(String paramToUpdate, String newValue) {
        log.info("Updating the param " + paramToUpdate + " with the value " + newValue);
        TableConfig outcomeConfig = configRepository.findByPropertyKey(paramToUpdate);
        outcomeConfig.setPropertyValue(newValue);

        configRepository.save(outcomeConfig);
    }
}
