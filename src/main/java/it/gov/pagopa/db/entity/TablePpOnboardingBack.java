package it.gov.pagopa.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Table(name = "pp_onboarding_back")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TablePpOnboardingBack {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "url_return")
    private String urlReturn;

    @Column(name = "t_timestamp")
    private Instant timestamp;

    @Column(name = "id_appio")
    private String idAppIo;

    @Column(name = "id_back")
    private String idBack;

    @Type(type= "org.hibernate.type.NumericBooleanType")
    private boolean used;

}
