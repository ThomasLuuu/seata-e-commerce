package app.thomas.service;


import app.thomas.service.model.CreditTransactionType;
import app.thomas.service.model.Wallet;
import app.thomas.service.model.WalletBalanceInput;
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
public class CreditService {

    @Value("${credit.url}")
    private String baseURL;

    @Observed(contextualName = "credit-update-service")
    public Wallet updateBalance(Long userId, BigDecimal cost) {
        BusinessActionContext actionContext = new BusinessActionContext();
        actionContext.setXid(RootContext.getXID());
        actionContext.setActionContext(new HashMap<>());

        try {
            WalletBalanceInput input = new WalletBalanceInput();
            input.setAmount(cost);
            input.setTransactionType(CreditTransactionType.DEBIT);

            Map<String, Object> response = RestClient.builder()
                    .baseUrl(baseURL)
                    .build()
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("/{userId}/try").build(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(RootContext.KEY_XID, RootContext.getXID())
                    .body(input) // Send WalletBalanceInput with amount and transactionType
                    .retrieve()
                    .body(Map.class);

            if (Boolean.TRUE.equals(response.get("success"))) {
                actionContext.getActionContext().put("transactionId", ((Number) response.get("transactionId")).longValue());
            } else {
                throw new RuntimeException("Failed to reserve balance");
            }
        } catch (Exception e) {
            log.error("Error in tryUpdateBalance: {}", e.getMessage());
            throw e;
        }

        return getWallet(userId);
    }

    @Observed(contextualName = "credit-get-service")
    public Wallet getWallet(Long userId) {
        return RestClient.builder()
                .baseUrl(baseURL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder.path("/{userId}").build(userId))
                .retrieve()
                .body(Wallet.class);
    }
}