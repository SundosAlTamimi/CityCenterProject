package com.example.irbidcitycenter.Models;

public class appSettings {
    String IP;
String CompanyNum;
String years;
String UpdateQTY;
String userNumber;

    public appSettings(String IP, String companyNum, String years, String updateQTY, String userNumber) {
        this.IP = IP;
        CompanyNum = companyNum;
        this.years = years;
        UpdateQTY = updateQTY;
        this.userNumber = userNumber;
    }

    public appSettings() {

    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getCompanyNum() {
        return CompanyNum;
    }

    public void setCompanyNum(String companyNum) {
        CompanyNum = companyNum;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getUpdateQTY() {
        return UpdateQTY;
    }

    public void setUpdateQTY(String updateQTY) {
        UpdateQTY = updateQTY;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }
}