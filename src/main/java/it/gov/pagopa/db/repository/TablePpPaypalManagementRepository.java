package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TableClient;
import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TablePpPaypalManagementRepository extends JpaRepository<TablePpPaypalManagement, Long> {

    TablePpPaypalManagement findByIdAppIoAndApiIdAndClient(String idAppIo, ApiPaypalIdEnum apiId, TableClient client);

    List<TablePpPaypalManagement> findByIdAppIoAndClient(String idAppIo, TableClient client);
}
