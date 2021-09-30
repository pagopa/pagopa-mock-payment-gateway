package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePpPaypalManagementRepository extends JpaRepository<TablePpPaypalManagement, Long> {

    TablePpPaypalManagement findByIdAppIoAndApiId(String idAppIo, ApiPaypalIdEnum apiId);
}
