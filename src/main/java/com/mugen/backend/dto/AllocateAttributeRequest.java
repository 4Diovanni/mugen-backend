package com.mugen.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocateAttributeRequest {

    @NotBlank(message = "Attribute name is required")
    private String attributeName; // "STR", "DEX", "CON", "WIL", "MND", "SPI"

    @NotNull(message = "Points to allocate is required")
    @Min(value = 1, message = "Must allocate at least 1 point")
    @Max(value = 20, message = "Cannot allocate more than 20 points at once")
    private Integer points;
}
