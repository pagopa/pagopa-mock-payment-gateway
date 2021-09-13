package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePpOnboardingBackRepository extends JpaRepository<TablePpOnboardingBack, Long> {
}
