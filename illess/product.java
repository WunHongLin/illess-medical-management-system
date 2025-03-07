package com.example.illess;

public class product {
    private String Name;
    private String Category;
    private String Uri;
    private Long Price;

    public product(){}

    public product(String name, String category, String uri, Long price) {
        Name = name;
        Category = category;
        Uri = uri;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public Long getPrice() {
        return Price;
    }

    public String getCategory() {
        return Category;
    }

    public String getUri() {
        return Uri;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setPrice(Long price) {
        Price = price;
    }

    public void setUri(String uri) {
        Uri = uri;
    }
}
