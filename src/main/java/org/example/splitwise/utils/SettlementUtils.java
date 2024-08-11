package org.example.splitwise.utils;

import org.example.splitwise.models.Expense;
import org.example.splitwise.models.ExpenseUser;
import org.example.splitwise.models.ExpenseUserType;
import org.example.splitwise.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettlementUtils {
    public Boolean isExpenseSettled(Expense expense){
        List<User> users = expense.getExpenseUsers().stream().map(ExpenseUser::getUser).toList();
        for(User user: users){
            int balance = 0;
            for(ExpenseUser expenseUser: expense.getExpenseUsers()){
                if(expenseUser.getUser().getId() == user.getId()){
                    if(expenseUser.getExpenseUserType().equals(ExpenseUserType.PAID_BY)){
                        balance += expenseUser.getAmount();
                    }
                    else{
                        balance -= expenseUser.getAmount();
                    }
                }
            }
            if(balance != 0){
                return false;
            }
        }
        return true;
    }
}
