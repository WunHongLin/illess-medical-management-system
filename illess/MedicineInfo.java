package com.example.illess;

public class MedicineInfo {
    private String Name;
    private Long Number;
    private String ImageUri;
    private String sideEffect;

    public MedicineInfo(){}

    public MedicineInfo(String name, Long number, String imageUri,String sideeffect) {
        Name = name;
        Number = number;
        ImageUri = imageUri;
        sideEffect = sideeffect;
    }

    public String getName() {
        return Name;
    }

    public Long getNumber() {
        return Number;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public String getSideEffect() {
        return sideEffect;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public void setNumber(Long number) {
        Number = number;
    }

    public void setSideEffect(String sideEffect) {
        this.sideEffect = sideEffect;
    }
}
