package app.thomas.manager;

import app.thomas.model.CreditTransactionType;
import app.thomas.model.Wallet;
import app.thomas.repository.CreditTransactionsRepository;
import app.thomas.repository.CreditWalletsRepository;
import app.thomas.repository.entity.CreditTransactionsEntity;
import io.micrometer.observation.annotation.Observed;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
@Observed(contextualName = "manager")
@AllArgsConstructor
public class CreditManager implements CreditTCCService {

    private final CreditTransactionsRepository creditTransactionsRepository;
    private final CreditWalletsRepository creditWalletsRepository;

    @Override
    @Transactional
    public boolean tryUpdateBalance(BusinessActionContext actionContext, Long userId, BigDecimal amount, CreditTransactionType transactionType) {
        var wallet = creditWalletsRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User or wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        var transaction = new CreditTransactionsEntity();
        transaction.setAmount(amount);
        transaction.setType(CreditTransactionType.DEBIT);
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(new Date());
        transaction.setUserId(userId);
        transaction.setXid(actionContext.getXid()); // Store XID for TCC fence
        transaction.setBranchId(actionContext.getBranchId()); // Store branch ID
        creditTransactionsRepository.save(transaction);

        switch (transactionType) {
            case DEBIT -> wallet.setBalance(wallet.getBalance().subtract(amount));
            case REFUND -> wallet.setBalance(wallet.getBalance().add(amount));
        }

        creditWalletsRepository.save(wallet);
        actionContext.getActionContext().put("transactionId", transaction.getId());
        return true;
    }

    @Override
    @Transactional
    public boolean confirm(BusinessActionContext actionContext) {
        Long transactionId = (Long) actionContext.getActionContext("transactionId");
        var transaction = creditTransactionsRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found"));

        if ("PENDING".equals(transaction.getStatus())) {
            var wallet = creditWalletsRepository.findByUserId(transaction.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("Wallet not found"));
            wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            creditWalletsRepository.save(wallet);
            transaction.setStatus("CONFIRMED");
            creditTransactionsRepository.save(transaction);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean cancel(BusinessActionContext actionContext) {
        Long transactionId = (Long) actionContext.getActionContext("transactionId");
        var transaction = creditTransactionsRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found"));

        if ("PENDING".equals(transaction.getStatus())) {
            transaction.setStatus("CANCELED");
            creditTransactionsRepository.save(transaction);
        }
        return true;
    }

    @Transactional
    public Wallet updateBalance(Long userId, BigDecimal amount, CreditTransactionType transactionType) {
        var wallet = creditWalletsRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User or wallet not found"));

        if (CreditTransactionType.DEBIT.equals(transactionType) && wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        var transaction = new CreditTransactionsEntity();
        transaction.setAmount(amount);
        transaction.setType(transactionType);
        transaction.setCreatedAt(new Date());
        transaction.setUserId(userId);
        creditTransactionsRepository.save(transaction);

        switch (transactionType) {
            case DEBIT -> wallet.setBalance(wallet.getBalance().subtract(amount));
            case REFUND -> wallet.setBalance(wallet.getBalance().add(amount));
        }

        creditWalletsRepository.save(wallet);
        return new Wallet(wallet.getBalance());
    }

    public Wallet getWallet(Long userId) {
        return new Wallet(creditWalletsRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User or wallet not found")).getBalance());
    }
}