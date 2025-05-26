package com.rod.adenvi.Domains;

public class ItemsDomain {

    private String title;
    private int count;

    public ItemsDomain() {

    }

    public ItemsDomain(String title, int count) {
        this.title = title;
        this.count = count;
    }

    public void setTitle() {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCount() {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

}
