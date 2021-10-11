package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TableClient;
import it.gov.pagopa.db.entity.TableUserPayPal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableUserPayPalRepository extends JpaRepository<TableUserPayPal, Long> {
    TableUserPayPal findByIdAppIoAndClientAndDeletedFalse(String idAppIo, TableClient client);
}
