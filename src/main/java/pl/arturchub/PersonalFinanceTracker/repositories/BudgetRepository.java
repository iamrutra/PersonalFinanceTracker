package pl.arturchub.PersonalFinanceTracker.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.arturchub.PersonalFinanceTracker.models.Budget;
import pl.arturchub.PersonalFinanceTracker.models.User;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.amount > 0 ORDER BY b.createdAt DESC")
    List<Budget> findTop5ByUserWhereBudgetGreaterThan0(@Param("user") User user, Pageable pageable);

    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.amount < 0 ORDER BY b.createdAt DESC")
    List<Budget> findTop5ByUserWhereBudgetLessThan0(@Param("user") User user, Pageable pageable);
}
