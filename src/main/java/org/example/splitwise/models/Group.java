package org.example.splitwise.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "Groups_info")
public class Group extends BaseModel {
    private String name;
    @ManyToOne
    private User createdBy;
    @ManyToMany
    private List<User> members;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Expense> expenses;
}
