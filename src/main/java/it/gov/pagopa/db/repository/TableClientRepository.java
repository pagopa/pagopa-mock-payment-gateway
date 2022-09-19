package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TableClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableClientRepository extends JpaRepository<TableClient, Long> {

    TableClient findByClientName(String name);

}
