package app.thomas.manager;

import io.seata.rm.tcc.api.BusinessActionContext;

import java.math.BigDecimal;

public interface ShippingTCCService {
    boolean tryBuyShipping(BusinessActionContext actionContext, Long userId, BigDecimal cost);

    boolean confirm(BusinessActionContext actionContext);

    boolean cancel(BusinessActionContext actionContext);
}