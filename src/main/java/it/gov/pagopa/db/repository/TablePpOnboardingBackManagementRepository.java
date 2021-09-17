package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TablePpOnboardingBackManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePpOnboardingBackManagementRepository extends JpaRepository<TablePpOnboardingBackManagement, Long> {

    TablePpOnboardingBackManagement findByIdAppIo(String idAppIo);
}
