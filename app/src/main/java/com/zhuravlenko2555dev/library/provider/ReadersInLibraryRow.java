package com.zhuravlenko2555dev.library.provider;

public class ReadersInLibraryRow {
    String accountName, accountSurname, accountMiddleName, bookName, bookEditionLanguage, bookEditionPublisher;

    public ReadersInLibraryRow(String accountName, String accountSurname, String accountMiddleName, String bookName, String bookEditionLanguage, String bookEditionPublisher) {
        this.accountName = accountName;
        this.accountSurname = accountSurname;
        this.accountMiddleName = accountMiddleName;
        this.bookName = bookName;
        this.bookEditionLanguage = bookEditionLanguage;
        this.bookEditionPublisher = bookEditionPublisher;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountSurname() {
        return accountSurname;
    }

    public void setAccountSurname(String accountSurname) {
        this.accountSurname = accountSurname;
    }

    public String getAccountMiddleName() {
        return accountMiddleName;
    }

    public void setAccountMiddleName(String accountMiddleName) {
        this.accountMiddleName = accountMiddleName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookEditionLanguage() {
        return bookEditionLanguage;
    }

    public void setBookEditionLanguage(String bookEditionLanguage) {
        this.bookEditionLanguage = bookEditionLanguage;
    }

    public String getBookEditionPublisher() {
        return bookEditionPublisher;
    }

    public void setBookEditionPublisher(String bookEditionPublisher) {
        this.bookEditionPublisher = bookEditionPublisher;
    }
}
