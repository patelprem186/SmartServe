package com.easy.easybook.models;

public class CountryCode {
    private String name;
    private String code;
    private String dialCode;
    private String flag;

    public CountryCode(String name, String code, String dialCode, String flag) {
        this.name = name;
        this.code = code;
        this.dialCode = dialCode;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return flag + " " + name + " (" + dialCode + ")";
    }
}
