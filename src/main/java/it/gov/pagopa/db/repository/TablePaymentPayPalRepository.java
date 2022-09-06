package it.gov.pagopa.db.repository;

import it.gov.pagopa.db.entity.TablePaymentPayPal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePaymentPayPalRepository extends JpaRepository<TablePaymentPayPal, Long> {
    TablePaymentPayPal findByIdTrsAppIo(String idTrsAppIo);
}
