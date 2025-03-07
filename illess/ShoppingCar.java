package com.example.illess;

public class ShoppingCar {
    private String Name;
    private String Category;
    private Long Num;
    private Long TotalPrice;

    public ShoppingCar(){}

    public ShoppingCar(String name, String category, Long num, Long totalPrice) {
        Name = name;
        Category = category;
        Num = num;
        TotalPrice = totalPrice;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setNum(Long num) {
        Num = num;
    }

    public void setTotalPrice(Long totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getName() {
        return Name;
    }

    public String getCategory() {
        return Category;
    }

    public Long getNum() {
        return Num;
    }

    public Long getTotalPrice() {
        return TotalPrice;
    }
}
