package pl.arturchub.PersonalFinanceTracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.arturchub.PersonalFinanceTracker.models.Budget;
import pl.arturchub.PersonalFinanceTracker.models.User;
import pl.arturchub.PersonalFinanceTracker.services.BudgetService;
import pl.arturchub.PersonalFinanceTracker.services.CategoryService;
import pl.arturchub.PersonalFinanceTracker.services.UserService;
import pl.arturchub.PersonalFinanceTracker.util.BudgetValidator;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;
    private final BudgetValidator budgetValidator;

    public BudgetController(UserService userService, CategoryService categoryService, BudgetService budgetService, BudgetValidator budgetValidator) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
        this.budgetValidator = budgetValidator;
    }

    @GetMapping
    public String budget(Principal principal, Model model) {
        String username = principal.getName();
        List<Budget> budgets = userService.findByUsername(username).get().getBudgets();
        model.addAttribute("username", username);
        model.addAttribute("amount", budgets);
        return "budgets/budget";
    }

    @GetMapping("/{id}/editPage")
    public String editPage(@PathVariable int id, Model model) {
        model.addAttribute("budget", budgetService.findById(id));
        return "budgets/editPage";
    }

    @PostMapping("/{id}/editBudgetType")
    public String editBudgetType(@PathVariable int id, @RequestParam String type, Model model) {
        Optional<Budget> budget = budgetService.findById(id);
        if (type.equals("income")) {
            if (budget.get().getAmount() < 0) {
                budget.get().setAmount(-budget.get().getAmount());  // Convert expense to income
            }
            model.addAttribute("budget", budget);
            return "budgets/editBudgetIncome";
        } else {
            if (budget.get().getAmount() < 0) {
                budget.get().setAmount(-budget.get().getAmount());  // Convert income to expense
            }
            model.addAttribute("budget", budget);
            return "budgets/editBudgetExpense";
        }
    }


    @GetMapping("/income")
    public String budgetIncome(@ModelAttribute("budget") Budget budget, Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "budgets/budgetIncome";
    }

    @GetMapping("/expenses")
    public String budgetExpense(@ModelAttribute("budget") Budget budget, Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "budgets/budgetExpenses";
    }

    @PostMapping("/income")
    public String addIncome(@Valid @ModelAttribute("budget") Budget budget, BindingResult bindingResult, Principal principal, Model model) {
        return handleBudget(budget, bindingResult, principal, model, false);
    }

    @PostMapping("/expenses")
    public String addExpense(@Valid @ModelAttribute("budget") Budget budget, BindingResult bindingResult, Principal principal, Model model) {
        return handleBudget(budget, bindingResult, principal, model, true);
    }

    private String handleBudget(Budget budget, BindingResult bindingResult, Principal principal, Model model, boolean isExpense) {
        budgetValidator.validate(budget, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return isExpense ? "budgets/budgetExpenses" : "budgets/budgetIncome";
        }

        String username = principal.getName();
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            if (isExpense && budget.getAmount() > 0) {
                budget.setAmount(-budget.getAmount());
            }
            budget.setUser(user.get());
            budgetService.save(budget);
            return "redirect:/budget";
        } else {
            model.addAttribute("error", "User not found");
            return isExpense ? "budgets/budgetExpenses" : "budgets/budgetIncome";
        }
    }
}
