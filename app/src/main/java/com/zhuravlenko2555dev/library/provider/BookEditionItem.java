package com.zhuravlenko2555dev.library.provider;

public class BookEditionItem {
    private String language, publisher;

    public BookEditionItem(String language, String publisher) {
        this.language = language;
        this.publisher = publisher;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
