package pl.arturchub.PersonalFinanceTracker.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.arturchub.PersonalFinanceTracker.models.Budget;
import pl.arturchub.PersonalFinanceTracker.services.BudgetService;

@Component
public class BudgetValidator implements Validator {
    private final BudgetService budgetService;

    public BudgetValidator(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Budget.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Budget budget = (Budget) o;
        if (budget.getAmount() == 0) {
            errors.rejectValue("amount", "", "Amount should not be zero");
        }
    }
}