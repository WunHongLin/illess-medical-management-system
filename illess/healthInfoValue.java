package com.example.illess;

public class healthInfoValue {
    private String Year;
    private String Month;
    private String Day;
    private Long Value;

    public healthInfoValue(){}

    public healthInfoValue(String year, String month, String day, Long value) {
        Year = year;
        Month = month;
        Day = day;
        Value = value;
    }

    public Long getValue() {
        return Value;
    }

    public String getDay() {
        return Day;
    }

    public String getMonth() {
        return Month;
    }

    public String getYear() {
        return Year;
    }

    public void setDay(String day) {
        Day = day;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public void setValue(Long value) {
        Value = value;
    }

    public void setYear(String year) {
        Year = year;
    }
}
