package com.YaakoviCorp.Fit1337.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record WeightUpdateRequest(
        @DecimalMin(value = "0.0", message = "weight must be at least 0")
        @DecimalMax(value = "40.0", message = "weight must be at most 40")
        double weight
) {}
