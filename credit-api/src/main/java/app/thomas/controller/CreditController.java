package app.thomas.controller;

import app.thomas.controller.model.WalletBalanceInput;
import app.thomas.manager.CreditTCCService;
import app.thomas.model.Wallet;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/credit/wallets")
@AllArgsConstructor
public class CreditController {

    private final CreditTCCService creditTCCService;

    @PostMapping(value = "/{userId}/try", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> tryUpdateBalance(@RequestHeader(name = RootContext.KEY_XID, required = false) String xid,
                                                @PathVariable Long userId,
                                                @RequestBody WalletBalanceInput input) {
        BusinessActionContext actionContext = new BusinessActionContext();
        actionContext.setXid(xid);
        actionContext.setActionContext(new HashMap<>());
        boolean success = creditTCCService.tryUpdateBalance(actionContext, userId, input.getAmount(), input.getTransactionType());
        return Map.of("success", success, "transactionId", actionContext.getActionContext("transactionId", Long.class));
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean confirm(@RequestBody BusinessActionContext actionContext) {
        return creditTCCService.confirm(actionContext);
    }

    @PostMapping(value = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean cancel(@RequestBody BusinessActionContext actionContext) {
        return creditTCCService.cancel(actionContext);
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId) {
        return creditTCCService.getWallet(userId);
    }
}