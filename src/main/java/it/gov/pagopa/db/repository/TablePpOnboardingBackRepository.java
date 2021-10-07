package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePpOnboardingBackRepository extends JpaRepository<TablePpOnboardingBack, Long> {
    TablePpOnboardingBack findByIdBack(String idBack);

    @Modifying
    @Query("update TablePpOnboardingBack po set po.used = true where po.idAppIo= :idAppIo and po.used = false ")
    void setUsedTrueByIdBack(@Param("idAppIo") String idAppIo);
}
