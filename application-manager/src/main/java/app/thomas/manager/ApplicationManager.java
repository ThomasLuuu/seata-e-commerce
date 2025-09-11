package app.thomas.manager;

import app.thomas.model.ShippingResult;
import app.thomas.service.CreditService;
import app.thomas.service.ShippingService;
import io.micrometer.observation.annotation.Observed;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Observed(contextualName = "manager")
@AllArgsConstructor
public class ApplicationManager {

    private final CreditService creditService;
    private final ShippingService shippingService;

    @GlobalTransactional
    public ShippingResult buyShipping(Long userId, BigDecimal cost) {
        var wallet = creditService.updateBalance(userId, cost);
        var shipping = shippingService.buyShipping(userId, cost);
        wallet = creditService.getWallet(userId);
        var result = new ShippingResult();
        result.setCost(cost);
        result.setShippingId(shipping.getId());
        result.setCurrentBalance(wallet.getBalance());
        return result;
    }
}