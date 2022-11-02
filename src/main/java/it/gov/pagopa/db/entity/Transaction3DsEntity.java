package it.gov.pagopa.db.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "TRANSACTION_3DS")
public class Transaction3DsEntity {
    @Id
    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "THREE_DS_SERVER_TRANS_ID")
    private String threeDSServerTransId;

    @Column(name = "NOTIFY_URL")
    private String notifyUrl;

    @Column(name = "THREE_DS_MTD_NOTIFY_URL")
    private String threeDSMTDNotifUrl;

    @Column(name = "OUTCOME")
    private String outcome;

    @Column(name = "THREE_DS_MTD_COMPL_IND")
    private String threeDSMtdComplInd;

    @Column(name = "PAN")
    private String pan;

    @Column(name = "AMOUNT")
    private String amount;

    @Column(name = "CURRENCY")
    private String currency;
}
