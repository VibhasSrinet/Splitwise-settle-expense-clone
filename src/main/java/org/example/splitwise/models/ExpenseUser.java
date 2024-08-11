package org.example.splitwise.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ExpenseUser extends BaseModel {
    @ManyToOne
    private User user;
    @ManyToOne
    private Expense expense;
    private Integer amount;
    @Enumerated(EnumType.ORDINAL)
    private ExpenseUserType expenseUserType;
    @Enumerated(EnumType.ORDINAL)
    private ExpenseType expenseType;
}
