package com.github.s1ckcode.SalesManagement.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.s1ckcode.SalesManagement.User.Role.Role;
import com.github.s1ckcode.SalesManagement.Utils.Utils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
public class User {
    @TableGenerator(name = "User_Gen",
            table = "USER_ID_GEN",
            pkColumnName = "USER_ID",
            valueColumnName = "GEN_VAL",
            pkColumnValue = "User_Gen",
            initialValue = 100000,
            allocationSize = 100)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "User_Gen")
    private long userId;

    private String userName;

    private String userFirst;

    private String userLast;

    private double monthlyGoal;

    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    private final LocalDate createDate = LocalDate.now();

    private LocalDateTime lastLogin;
    private LocalDateTime currentLogin;

    public User(){}

    public User(String userName, String userFirst, String userLast, String password, double monthlyGoal) {
        this.userName = userName;
        this.userFirst = userFirst;
        this.userLast = userLast;
        this.password = Utils.hashMyPassword(password);
        this.monthlyGoal = monthlyGoal;
        this.lastLogin = LocalDateTime.now();
        this.currentLogin = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId){ this.userId = userId; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFirst() {
        return userFirst;
    }

    public void setUserFirst(String userFirst) {
        this.userFirst = userFirst;
    }

    public String getUserLast() {
        return userLast;
    }

    public void setUserLast(String userLast) {
        this.userLast = userLast;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = this.password = Utils.hashMyPassword(password);
    }

    public Collection<Role> getRoles() { return roles; }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public double getMonthlyGoal() {
        return monthlyGoal;
    }

    public void setMonthlyGoal(double monthlyGoal) {
        this.monthlyGoal = monthlyGoal;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setCurrentLogin(LocalDateTime currentLogin) {
        this.currentLogin = currentLogin;
    }

    public LocalDateTime getCurrentLogin() {
        return currentLogin;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void clone(User user) {

        setUserFirst(user.getUserFirst());
        setUserLast(user.getUserLast());
        // setRole(user.getRole());
        setMonthlyGoal(user.getMonthlyGoal());
    }
}
