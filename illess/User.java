package com.example.illess;

public class User {
    private String ID;
    private String Password;
    private String Name;
    private String Sexual;
    private Long Age;
    private Long Height;
    private String Mail;
    private Long Money;

    public User(){};

    public User(String id, String password, String name, String sexual, Long age, Long height, String mail,Long money) {
        ID = id;
        Password = password;
        Name = name;
        Sexual = sexual;
        Age = age;
        Height = height;
        Mail = mail;
        Money = money;
    }

    public String getID() {
        return ID;
    }

    public String getPassword() {
        return Password;
    }

    public String getName() {
        return Name;
    }
    public String getSexual(){
        return Sexual;
    }

    public Long getAge() {
        return Age;
    }

    public Long getHeight() {
        return Height;
    }

    public String getMail() {
        return Mail;
    }

    public Long getMoney() {
        return Money;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAge(Long age) {
        Age = age;
    }

    public void setHeight(Long height) {
        Height = height;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setMoney(Long money) {
        Money = money;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setSexual(String sexual) {
        Sexual = sexual;
    }
}
