package org.example.splitwise.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseSettlementRequestDto {
    private Long senderId;
    private Long receiverId;
    private Integer amount;
    private Long expenseId;
}
