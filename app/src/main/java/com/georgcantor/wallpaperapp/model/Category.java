package com.georgcantor.wallpaperapp.model;

public class Category {

    private String categoryName;
    private String categoryDrawId;

    public Category() {
    }

    public Category(String categoryName, String categoryDrawId) {
        setCategoryName(categoryName);
        setCategoryDrawId(categoryDrawId);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryDrawId(String categoryDrawId) {
        this.categoryDrawId = categoryDrawId;
    }

    public String getCategoryDrawId() {
        return categoryDrawId;
    }
}
