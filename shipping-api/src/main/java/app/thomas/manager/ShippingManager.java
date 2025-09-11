package app.thomas.manager;

import app.thomas.model.Shipping;
import app.thomas.repository.ShippingRepository;
import app.thomas.repository.entity.ShippingEntity;
import io.micrometer.observation.annotation.Observed;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@Observed(contextualName = "manager")
@AllArgsConstructor
public class ShippingManager implements ShippingTCCService {

    private final ShippingRepository shippingRepository;

    @Override
    @Transactional
    public boolean tryBuyShipping(BusinessActionContext actionContext, Long userId, BigDecimal cost) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RuntimeException("Good luck with the transaction!");
        }

        var entity = new ShippingEntity();
        entity.setCost(cost);
        entity.setUserId(userId);
        entity.setCreatedAt(new Date());
        entity.setStatus("PENDING");
        entity.setXid(actionContext.getXid());
        entity.setBranchId(actionContext.getBranchId());
        entity = shippingRepository.save(entity);

        actionContext.getActionContext().put("shippingId", entity.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean confirm(BusinessActionContext actionContext) {
        Long shippingId = (Long) actionContext.getActionContext("shippingId");
        var entity = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new NoSuchElementException("Shipping not found"));

        if ("PENDING".equals(entity.getStatus())) {
            entity.setStatus("CONFIRMED");
            shippingRepository.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean cancel(BusinessActionContext actionContext) {
        Long shippingId = (Long) actionContext.getActionContext("shippingId");
        var entity = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new NoSuchElementException("Shipping not found"));

        if ("PENDING".equals(entity.getStatus())) {
            entity.setStatus("CANCELED");
            shippingRepository.save(entity);
        }
        return true;
    }

    @Transactional
    public Shipping buyShipping(Long userId, BigDecimal cost) throws InterruptedException {
        if (ThreadLocalRandom.current().nextBoolean()) {
            Thread.sleep(10_000);
            throw new RuntimeException("Good luck with the transaction!");
        }

        var entity = new ShippingEntity();
        entity.setCost(cost);
        entity.setUserId(userId);
        entity.setCreatedAt(new Date());
        entity.setStatus("COMPLETED");
        entity = shippingRepository.save(entity);
        return new Shipping(entity.getId());
    }
}