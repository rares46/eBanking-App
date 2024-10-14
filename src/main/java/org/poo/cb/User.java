package org.poo.cb;

import java.util.ArrayList;
import java.util.*;

public class User {
    final private String email;
    final private String firstname;
    final private String lastname;
    final private String address;
    final private ArrayList<String> friends;
    final private ArrayList<Account> accounts;
    final private Map<String, Integer> stocks;
    private boolean premium = false;

    private User(Builder builder) {
        this.email = builder.email;
        this.firstname = builder.firstname;
        this.lastname = builder.lastname;
        this.address = builder.address;
        this.friends = new ArrayList<>();
        this.accounts = new ArrayList<>();
        this.stocks = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public String getAddress() {
        return address;
    }
    public ArrayList<String> getFriends() {
        return friends;
    }
    public ArrayList<Account> getAccounts() {
        return accounts;
    }
    public Map<String, Integer> getStocks() {
        return stocks;
    }
    public boolean getPremium() {
        return premium;
    }
    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public static class Builder {
        private String email;
        private String firstname;
        private String lastname;
        private String address;

        public Builder() {
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder firstName(String firstname) {
            this.firstname = firstname;
            return this;
        }
        public Builder lastName(String lastname) {
            this.lastname = lastname;
            return this;
        }
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        public User build() {
            return new User(this);
        }
    }
}