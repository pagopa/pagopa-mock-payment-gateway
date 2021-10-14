package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PpRefundDirectRequest  extends PPPayPalIdAppIoRequest{
    @NotNull
    @JsonProperty("id_trs_appio")
    private String idTrsAppIo;

    @NotNull
    @Digits(fraction = 2, integer = Integer.MAX_VALUE)
    private BigDecimal importo;
}
