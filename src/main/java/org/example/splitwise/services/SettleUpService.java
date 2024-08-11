package org.example.splitwise.services;

import org.example.splitwise.exceptions.ExpenseNotFoundException;
import org.example.splitwise.exceptions.GroupDoesNotExistException;
import org.example.splitwise.exceptions.UserNotFoundException;
import org.example.splitwise.models.*;
import org.example.splitwise.repositories.ExpenseRepository;
import org.example.splitwise.repositories.ExpenseUserRepository;
import org.example.splitwise.repositories.GroupRepository;
import org.example.splitwise.repositories.UserRepository;
import org.example.splitwise.strategy.SetteUpStrategy;
import org.example.splitwise.utils.SettlementUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SettleUpService {
    private UserRepository userRepository;
    private ExpenseUserRepository expenseUserRepository;
    private SetteUpStrategy setteUpStrategy;
    private ExpenseRepository expenseRepository;
    private SettlementUtils settlementUtils;
    private GroupRepository groupRepository;

    public SettleUpService(
            UserRepository userRepository,
            ExpenseUserRepository expenseUserRepository,
            SetteUpStrategy setteUpStrategy,
            ExpenseRepository expenseRepository,
            SettlementUtils settlementUtils,
            GroupRepository groupRepository
    ) {
        this.userRepository = userRepository;
        this.expenseUserRepository = expenseUserRepository;
        this.setteUpStrategy = setteUpStrategy;
        this.expenseRepository = expenseRepository;
        this.settlementUtils = settlementUtils;
        this.groupRepository = groupRepository;
    }

    public List<String> expenseSettleUp(Long expenseId) throws ExpenseNotFoundException {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if (optionalExpense.isEmpty()) {
            throw new ExpenseNotFoundException("Expense not found!");
        }

        Expense expense = optionalExpense.get();
        List<ExpenseUser> expenseUsers = expenseUserRepository.findAllByExpense(expense);
        return setteUpStrategy.settleUp(expenseUsers);
    }

    public List<String> groupSettleUp(Long groupId) throws GroupDoesNotExistException {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if(optionalGroup.isEmpty()) {
            throw new GroupDoesNotExistException("Group not found!");
        }
        Group group = optionalGroup.get();
        List<Expense> expenses = expenseRepository.findByGroupAndIsSettledFalse(group);

        List<ExpenseUser> groupExpenseUsers = new ArrayList<>();
        for(Expense expense: expenses){
            groupExpenseUsers.addAll(expense.getExpenseUsers());
        }
        return setteUpStrategy.settleUp(groupExpenseUsers);
    }

    public void makeExpenseSettlement(Long senderId, Long receiverId, Integer amount, Long expenseId) throws UserNotFoundException, ExpenseNotFoundException {
        Optional<User> optionalSender = userRepository.findById(senderId);
        Optional<User> optionalReceiver = userRepository.findById(receiverId);
        if (optionalSender.isEmpty()) {
            throw new UserNotFoundException("Sender not found!");
        }
        if(optionalReceiver.isEmpty()) {
            throw new UserNotFoundException("Receiver not found!");
        }
        User sender = optionalSender.get();
        User receiver = optionalReceiver.get();
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if(optionalExpense.isEmpty()) {
            throw new ExpenseNotFoundException("Expense not found!");
        }
        Expense expense = optionalExpense.get();
        List<ExpenseUser> expenseUsers = expense.getExpenseUsers();
        ExpenseUser expenseSender = new ExpenseUser();
        expenseSender.setAmount(amount);
        expenseSender.setExpense(expense);
        expenseSender.setUser(sender);
        expenseSender.setExpenseUserType(ExpenseUserType.PAID_BY);
        expenseSender.setExpenseType(ExpenseType.SETTLEMENT);
        ExpenseUser expenseReceiver = new ExpenseUser();
        expenseReceiver.setAmount(amount);
        expenseReceiver.setExpense(expense);
        expenseReceiver.setUser(receiver);
        expenseReceiver.setExpenseUserType(ExpenseUserType.HAD_TO_PAY);
        expenseReceiver.setExpenseType(ExpenseType.SETTLEMENT);
        expenseUsers.add(expenseSender);
        expenseUsers.add(expenseReceiver);
        if(settlementUtils.isExpenseSettled(expense)){
            expense.setIsSettled(true);
        }
        expenseRepository.save(expense);
    }

    @Transactional
    public void makeGroupSettlement(Long senderId, Long receiverId, Integer amount, Long groupId) throws UserNotFoundException, GroupDoesNotExistException {
        Optional<User> optionalSender = userRepository.findById(senderId);
        Optional<User> optionalReceiver = userRepository.findById(receiverId);
        if (optionalSender.isEmpty()) {
            throw new UserNotFoundException("Sender not found!");
        }
        if(optionalReceiver.isEmpty()) {
            throw new UserNotFoundException("Receiver not found!");
        }
        User sender = optionalSender.get();
        User receiver = optionalReceiver.get();
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if(optionalGroup.isEmpty()) {
            throw new GroupDoesNotExistException("Group not found!");
        }
        Group group = optionalGroup.get();
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setAmount(amount);
        expense.setExpenseType(ExpenseType.SETTLEMENT);
        expense.setIsSettled(false);
        List<ExpenseUser> expenseUsers = new ArrayList<>();
        ExpenseUser expenseSender = new ExpenseUser();
        expenseSender.setAmount(amount);
        expenseSender.setExpense(expense);
        expenseSender.setUser(sender);
        expenseSender.setExpenseUserType(ExpenseUserType.PAID_BY);
        expenseSender.setExpenseType(ExpenseType.SETTLEMENT);
        ExpenseUser expenseReceiver = new ExpenseUser();
        expenseReceiver.setAmount(amount);
        expenseReceiver.setExpense(expense);
        expenseReceiver.setUser(receiver);
        expenseReceiver.setExpenseUserType(ExpenseUserType.HAD_TO_PAY);
        expenseReceiver.setExpenseType(ExpenseType.SETTLEMENT);
        expenseUsers.add(expenseSender);
        expenseUsers.add(expenseReceiver);
        expense.setExpenseUsers(expenseUsers);
        group.getExpenses().add(expense);
        groupRepository.save(group);
    }
}
