package com.chat.java.simple;

public class User {

    private String userName, password;
    
    public User(String userName, String password) {
        this.userName = userName;    
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void mostrarInformacoes() {
        System.out.println("\nUsername: " + userName +
                         "\nPassword: " + password);
    }
}

