package pl.arturchub.PersonalFinanceTracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.arturchub.PersonalFinanceTracker.models.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
}
