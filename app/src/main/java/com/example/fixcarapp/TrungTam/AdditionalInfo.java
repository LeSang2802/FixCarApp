package com.example.fixcarapp.TrungTam;

public class AdditionalInfo {
    private String time;
    private String description;
    private int quantity;

    // Constructor mặc định
    public AdditionalInfo() {
    }

    // Constructor có tham số
    public AdditionalInfo(String time, String description, int quantity) {
        this.time = time;
        this.description = description;
        this.quantity = quantity;
    }

    // Getters và Setters
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}