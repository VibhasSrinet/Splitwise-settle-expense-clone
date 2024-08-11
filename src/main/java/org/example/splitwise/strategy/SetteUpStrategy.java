package org.example.splitwise.strategy;

import org.example.splitwise.models.Expense;
import org.example.splitwise.models.ExpenseUser;

import java.util.List;

public interface SetteUpStrategy {
    List<String> settleUp(List<ExpenseUser> expenseUsers);
}
