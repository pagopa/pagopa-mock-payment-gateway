package it.gov.pagopa.db.entity;

import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseErrCode;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "pp_onboarding_back_management")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TablePpOnboardingBackManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_appio")
    private String idAppIo;

    @Builder.Default
    @Column(name = "lastUpdateDate")
    private Instant lastUpdateDate = Instant.now();

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    @Column(name = "err_code")
    private String errCodeValue;

    @Transient
    private PpOnboardingBackResponseErrCode errCode;

    @PrePersist
    @PreUpdate
    void setErrCodeValue() {
        if (errCode != null) {
            errCodeValue = errCode.getCode();
        } else {
            errCodeValue = null;
        }
    }

    @PostLoad
    void fillErrCode() {
        if (StringUtils.isNotBlank(errCodeValue)) {
            this.errCode = PpOnboardingBackResponseErrCode.of(errCodeValue);
        } else {
            errCodeValue = null;
        }
    }
}
