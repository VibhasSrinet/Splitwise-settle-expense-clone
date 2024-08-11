package org.example.splitwise.strategy;
import org.example.splitwise.models.ExpenseUser;
import org.example.splitwise.models.ExpenseUserType;
import org.example.splitwise.models.Pair;
import org.example.splitwise.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HeapSettleUpStrategy implements SetteUpStrategy {
    @Override
    public List<String> settleUp(List<ExpenseUser> expenseUsers) {
        PriorityQueue<Pair<User, Integer>> donerQueue = new PriorityQueue<>((a, b) -> b.getSecond() - a.getSecond());
        PriorityQueue<Pair<User, Integer>> receiverQueue = new PriorityQueue<>((a, b) -> b.getSecond() - a.getSecond());
        Set<User> users = expenseUsers.stream().map(ExpenseUser::getUser).collect(Collectors.toSet());
        for(User user: users) {
            int balance = getBalance(user, expenseUsers);
            if(balance > 0) {
                receiverQueue.add(new Pair<>(user, balance));
            } else {
                donerQueue.add(new Pair<>(user, -balance));
            }
        }
        List<String> transactions = new ArrayList<>();

        while (!donerQueue.isEmpty() && !receiverQueue.isEmpty()) {
            Pair<User, Integer> doner = donerQueue.poll();
            Pair<User, Integer> receiver = receiverQueue.poll();
            int amount = Math.min(doner.getSecond(), receiver.getSecond());
            doner.setSecond(doner.getSecond()-amount);
            receiver.setSecond(receiver.getSecond()-amount);
            String transaction = doner.getFirst().getEmail() + " needs to pay " + amount + " to " + receiver.getFirst().getEmail();
            if(doner.getSecond() > 0) {
                donerQueue.add(doner);
            }
            if(receiver.getSecond() > 0) {
                receiverQueue.add(receiver);
            }
            transactions.add(transaction);
        }
        return transactions;
    }

    private int getBalance(User user, List<ExpenseUser> expenseUsers) {
        int balance = 0;
        for(ExpenseUser expenseUser: expenseUsers) {
            if(expenseUser.getUser().getId() == user.getId()) {
                if(expenseUser.getExpenseUserType().equals(ExpenseUserType.PAID_BY)){
                    balance += expenseUser.getAmount();
                } else {
                    balance -= expenseUser.getAmount();
                }
            }
        }
        return balance;
    }

}
