package pl.arturchub.PersonalFinanceTracker.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.arturchub.PersonalFinanceTracker.models.Budget;
import pl.arturchub.PersonalFinanceTracker.models.User;
import pl.arturchub.PersonalFinanceTracker.repositories.BudgetRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<Budget> findTop5ByUserWhereBudgetGreaterThan0(User user) {
        Pageable pageable = PageRequest.of(0, 5);
        return budgetRepository.findTop5ByUserWhereBudgetGreaterThan0(user, pageable);
    }

    public List<Budget> findTop5ByUserWhereBudgetLessThan0(User user) {
        Pageable pageable = PageRequest.of(0, 5);
        return budgetRepository.findTop5ByUserWhereBudgetLessThan0(user, pageable);
    }

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> findById(int id) {
        return budgetRepository.findById(id);
    }

    @Transactional
    public void deleteById(int id) {
        budgetRepository.deleteById(id);
    }

    @Transactional
    public void save(Budget budget) {
        budgetRepository.save(budget);
    }
}
