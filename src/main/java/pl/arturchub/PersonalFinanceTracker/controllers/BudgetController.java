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
import java.time.LocalDateTime;
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
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Budget> budgetsGreater = budgetService.findTop5ByUserWhereBudgetGreaterThan0(user); // Income&Expense UPD: where budget > 0 & budget < 0 TODO
        List<Budget> budgetsLess = budgetService.findTop5ByUserWhereBudgetLessThan0(user); // Income&Expense UPD: where budget > 0 & budget < 0 TODO
        model.addAttribute("username", username);
        model.addAttribute("amountG", budgetsGreater); // Income&Expense TODO
        model.addAttribute("amountL", budgetsLess); // Income&Expense TODO
        return "budgets/budget";
    }

    @GetMapping("/{id}/editPage")
    public String editPage(@PathVariable int id, Model model) {
        Budget budget = budgetService.findById(id).orElseThrow(() -> new IllegalArgumentException("Budget not found"));
        model.addAttribute("budget", budget);
        return "budgets/edit/editPage";
    }

    @PostMapping("/{id}/editBudgetType")
    public String editBudgetType(@PathVariable int id, @RequestParam String type, Model model) {
        Budget budget = budgetService.findById(id).orElseThrow(() -> new IllegalArgumentException("Budget not found"));
        model.addAttribute("categories", categoryService.findAll());
        if (type.equals("income")) {
            if (budget.getAmount() < 0) {
                budget.setAmount(-budget.getAmount());
            }
            model.addAttribute("budget", budget);
            return "budgets/edit/editBudgetIncome";
        } else {
            if (budget.getAmount() > 0) {
                budget.setAmount(-budget.getAmount());
            }
            budget.setAmount(Math.abs(budget.getAmount()));
            model.addAttribute("budget", budget);
            return "budgets/edit/editBudgetExpense";
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

    @PostMapping("/{id}/edit")
    public String editBudget(@ModelAttribute Budget budget, @RequestParam String type, BindingResult bindingResult, Principal principal, Model model) {
        return handleEditBudget(budget, type, bindingResult, principal, model);
    }

    @PostMapping("/{id}/delete")
    public String deleteBudget(@PathVariable int id, Principal principal) {
        Budget budget = budgetService.findById(id).orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        String username = principal.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getBudgets().contains(budget)) {
            user.getBudgets().remove(budget);
            budgetService.deleteById(budget.getId());
        }

        return "redirect:/budget";
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
            budget.setCreatedAt(LocalDateTime.now());
            budgetService.save(budget);
            return "redirect:/budget";
        } else {
            model.addAttribute("error", "User not found");
            return isExpense ? "budgets/budgetExpenses" : "budgets/budgetIncome";
        }
    }

    private String handleEditBudget(Budget budget, String type, BindingResult bindingResult, Principal principal, Model model) {
        budgetValidator.validate(budget, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return budget.getAmount() < 0 ? "budgets/edit/editBudgetExpense" : "budgets/edit/editBudgetIncome";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (type.equals("expense") && budget.getAmount() > 0) {
            budget.setAmount(-budget.getAmount());
        } else if (type.equals("income") && budget.getAmount() < 0) {
            budget.setAmount(-budget.getAmount());
        }

        budget.setUser(user);
        budget.setCreatedAt(LocalDateTime.now());
        budgetService.save(budget);
        return "redirect:/budget";
    }
}