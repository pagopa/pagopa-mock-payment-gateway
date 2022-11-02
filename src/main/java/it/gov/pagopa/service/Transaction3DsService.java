package it.gov.pagopa.service;

import it.gov.pagopa.db.entity.Transaction3DsEntity;
import it.gov.pagopa.db.repository.Transaction3Ds2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Transaction3DsService {
    @Autowired
    private Transaction3Ds2Repository transaction3Ds2Repository;

    public Transaction3DsEntity getByThreeDSServerTransId(String threeDSServerTransId) {
        return transaction3Ds2Repository.findByThreeDSServerTransId(threeDSServerTransId);
    }

    public void save(Transaction3DsEntity entity) {
        transaction3Ds2Repository.save(entity);
    }
}
