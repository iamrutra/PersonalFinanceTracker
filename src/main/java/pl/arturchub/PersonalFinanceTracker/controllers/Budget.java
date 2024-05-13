package pl.arturchub.PersonalFinanceTracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/budget")
public class Budget {

    @GetMapping
    public String budget() {
        return "budgets/budget";
    }
}
