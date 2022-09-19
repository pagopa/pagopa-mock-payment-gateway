package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TableConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableConfigRepository extends JpaRepository<TableConfig, Long> {
    TableConfig findByPropertyKey(String key);
}
