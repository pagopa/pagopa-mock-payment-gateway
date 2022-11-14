package it.gov.pagopa.vpos.repository;

import it.gov.pagopa.vpos.entity.Transaction3DsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Transaction3Ds2Repository extends JpaRepository<Transaction3DsEntity, String> {
    Transaction3DsEntity findByThreeDSServerTransId(String threeDSServerTransId);
}
