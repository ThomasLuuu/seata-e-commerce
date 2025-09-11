package app.thomas.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShippingResult {
    private Long shippingId; // Note: Field name updated to match getter/setter
    private BigDecimal cost;
    private BigDecimal currentBalance;
}