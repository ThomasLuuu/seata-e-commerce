package app.thomas.repository;

import app.thomas.repository.entity.CreditTransactionsEntity;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Observed(contextualName = "tx-repository")
public interface CreditTransactionsRepository extends JpaRepository<CreditTransactionsEntity, Long> {

}
