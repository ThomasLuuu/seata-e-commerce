package app.thomas.service;

import app.thomas.service.model.Shipping;
import io.micrometer.observation.annotation.Observed;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ShippingService {

    @Value("${shipping.url}")
    private String baseURL;

    @Observed(contextualName = "shipping-service")
    public Shipping buyShipping(Long userId, BigDecimal cost) {
        BusinessActionContext actionContext = new BusinessActionContext();
        actionContext.setXid(RootContext.getXID());
        actionContext.setActionContext(new HashMap<>()); // Initialize action context map

        try {
            Map<String, Object> response = RestClient.builder()
                    .baseUrl(baseURL)
                    .build()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("/{userId}/try").build(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RootContext.KEY_XID, RootContext.getXID())
                    .body(cost)
                    .retrieve()
                    .body(Map.class);

            if (Boolean.TRUE.equals(response.get("success"))) {
                Long shippingId = ((Number) response.get("shippingId")).longValue();
                actionContext.getActionContext().put("shippingId", shippingId);
                Shipping shipping = new Shipping();
                shipping.setId(shippingId);
                return shipping;
            } else {
                throw new RuntimeException("Failed to reserve shipping");
            }
        } catch (Exception e) {
            log.error("Error in tryBuyShipping: {}", e.getMessage());
            throw e;
        }
    }
}