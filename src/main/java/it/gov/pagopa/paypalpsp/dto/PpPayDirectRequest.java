package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PpPayDirectRequest {

    @NotNull
    @JsonProperty("id_appio")
    private String idAppIo;

    @NotNull
    @JsonProperty("id_trs_appio")
    private String idTrsAppIo;

    @NotNull
    @Digits(fraction = 2, integer = Integer.MAX_VALUE)
    private BigDecimal importo;

    @NotNull
    @Digits(fraction = 2, integer = Integer.MAX_VALUE)
    private BigDecimal fee;
}
