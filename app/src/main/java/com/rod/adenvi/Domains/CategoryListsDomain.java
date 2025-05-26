package com.rod.adenvi.Domains;

public class CategoryListsDomain {

    private String title;
    private int count;

    public CategoryListsDomain() {

    }

    public CategoryListsDomain(String title, int count) {
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
