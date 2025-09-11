package app.thomas.controller;

import app.thomas.manager.ShippingTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/shipping")
@AllArgsConstructor
public class ShippingController {

    private final ShippingTCCService shippingTCCService;

    @PostMapping(value = "/{userId}/try", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> tryBuyShipping(@RequestHeader(name = RootContext.KEY_XID, required = false) String xid,
                                              @PathVariable Long userId,
                                              @RequestBody BigDecimal cost) {
        BusinessActionContext actionContext = new BusinessActionContext();
        actionContext.setXid(xid);
        actionContext.setActionContext(new HashMap<>()); // Initialize action context map
        boolean success = shippingTCCService.tryBuyShipping(actionContext, userId, cost);
        return Map.of("success", success, "shippingId", actionContext.getActionContext("shippingId", Long.class));
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean confirm(@RequestBody BusinessActionContext actionContext) {
        return shippingTCCService.confirm(actionContext);
    }

    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean cancel(@RequestBody BusinessActionContext actionContext) {
        return shippingTCCService.cancel(actionContext);
    }
}