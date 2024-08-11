package org.example.splitwise.controllers;

import org.example.splitwise.dtos.*;
import org.example.splitwise.exceptions.ExpenseNotFoundException;
import org.example.splitwise.exceptions.GroupDoesNotExistException;
import org.example.splitwise.exceptions.GroupNotFoundException;
import org.example.splitwise.exceptions.UserNotFoundException;
import org.example.splitwise.services.SettleUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settleup")
public class SettleUpController {
    private SettleUpService settleUpService;

    public SettleUpController(SettleUpService settleUpService) {
        this.settleUpService = settleUpService;
    }

    @GetMapping("/expense/{expenseId}")
    public SettleUpResponseDto settleUpExpense(@PathVariable Long expenseId) throws ExpenseNotFoundException {
        SettleUpResponseDto settleUpResponseDto = new SettleUpResponseDto();
        settleUpResponseDto.setRequiredTransactions(settleUpService.expenseSettleUp(expenseId));
        return settleUpResponseDto;
    }

    @PostMapping("/expense")
    public ResponseEntity makeExpenseSettlement(@RequestBody ExpenseSettlementRequestDto expenseSettlementRequestDto) throws UserNotFoundException, ExpenseNotFoundException {
        settleUpService.makeExpenseSettlement(expenseSettlementRequestDto.getSenderId(), expenseSettlementRequestDto.getReceiverId(), expenseSettlementRequestDto.getAmount(), expenseSettlementRequestDto.getExpenseId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/group/{groupId}")
    public SettleUpResponseDto settleUpGroup(@PathVariable Long groupId) throws GroupDoesNotExistException {
        SettleUpResponseDto settleUpResponseDto = new SettleUpResponseDto();
        settleUpResponseDto.setRequiredTransactions(settleUpService.groupSettleUp(groupId));
        return settleUpResponseDto;
    }

    @PostMapping("/group")
    public ResponseEntity makeGroupSettlement(@RequestBody GroupSettlementRequestDto groupSettlementRequestDto) throws UserNotFoundException, GroupDoesNotExistException {
        settleUpService.makeGroupSettlement(groupSettlementRequestDto.getSenderId(), groupSettlementRequestDto.getReceiverId(), groupSettlementRequestDto.getAmount(), groupSettlementRequestDto.getGroupId());
        return ResponseEntity.ok().build();
    }
}
