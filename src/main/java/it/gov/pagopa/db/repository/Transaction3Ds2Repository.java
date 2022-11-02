package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.Transaction3DsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Transaction3Ds2Repository extends JpaRepository<Transaction3DsEntity, String> {
    Transaction3DsEntity findByThreeDSServerTransId(String threeDSServerTransId);
}
