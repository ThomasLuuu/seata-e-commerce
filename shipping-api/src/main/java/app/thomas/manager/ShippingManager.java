package app.thomas.manager;

import app.thomas.model.Shipping;
import app.thomas.repository.ShippingRepository;
import app.thomas.repository.entity.ShippingEntity;
import io.micrometer.observation.annotation.Observed;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Observed(contextualName = "manager")
@AllArgsConstructor
public class ShippingManager {

    private final ShippingRepository shippingRepository;

    @Transactional
    public Shipping buyShipping(Long userID, BigDecimal cost) throws InterruptedException {

        if (ThreadLocalRandom.current().nextBoolean()) {
            Thread.sleep(10_000);
            throw new RuntimeException("Good luck with the transaction!");
        }

        var entity = new ShippingEntity();
        entity.setCost(cost);
        entity.setUserId(userID);
        entity.setCreatedAt(new Date());
        entity = shippingRepository.save(entity);
        return new Shipping(entity.getId());
    }

}
