package app.thomas.manager;

import app.thomas.model.CreditTransactionType;
import app.thomas.model.Wallet;
import io.seata.rm.tcc.api.BusinessActionContext;

import java.math.BigDecimal;

public interface CreditTCCService {
    boolean tryUpdateBalance(BusinessActionContext actionContext, Long userId, BigDecimal amount, CreditTransactionType transactionType);

    boolean confirm(BusinessActionContext actionContext);

    boolean cancel(BusinessActionContext actionContext);

    Wallet getWallet(Long userId); // Added to match CreditManager implementation
}