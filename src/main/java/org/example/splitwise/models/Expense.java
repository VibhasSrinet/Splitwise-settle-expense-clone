package org.example.splitwise.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Getter
@Setter
@Entity
public class Expense extends BaseModel{
    private String description;
    private Integer amount;
    @OneToMany(mappedBy = "expense", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ExpenseUser> expenseUsers;
    @ManyToOne
    private User createdBy;
    @ManyToOne
    private Group group;
    private Boolean isSettled;
    @Enumerated(EnumType.ORDINAL)
    private ExpenseType expenseType;
}
