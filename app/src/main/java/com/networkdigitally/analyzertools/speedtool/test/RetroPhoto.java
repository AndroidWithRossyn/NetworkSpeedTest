package com.networkdigitally.analyzertools.speedtool.test;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RetroPhoto implements Serializable {
    @SerializedName("carrier")
    private String carrier;
    @SerializedName("country_code")
    private String country_code;
    @SerializedName("country_name")
    private String country_name;
    @SerializedName("country_prefix")
    private String country_prefix;
    @SerializedName("international_format")
    private String international_format;
    @SerializedName("line_type")
    private String line_type;
    @SerializedName("local_format")
    private String local_format;
    @SerializedName("location")
    private String location;
    @SerializedName("number")
    private String number;
    @SerializedName("valid")
    private boolean valid;

    public RetroPhoto() {
    }

    public RetroPhoto(boolean z, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
        this.valid = z;
        this.number = str;
        this.local_format = str2;
        this.international_format = str3;
        this.country_prefix = str4;
        this.country_code = str5;
        this.country_name = str6;
        this.location = str7;
        this.carrier = str8;
        this.line_type = str9;
    }

    public boolean getValid() {
        return this.valid;
    }

    public void setValid(boolean z) {
        this.valid = z;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String str) {
        this.number = str;
    }

    public String getLocal_format() {
        return this.local_format;
    }

    public void setLocal_format(String str) {
        this.local_format = str;
    }

    public String getInternational_format() {
        return this.international_format;
    }

    public void setInternational_format(String str) {
        this.international_format = str;
    }

    public String getCountry_prefix() {
        return this.country_prefix;
    }

    public void setCountry_prefix(String str) {
        this.country_prefix = str;
    }

    public String getCountry_code() {
        return this.country_code;
    }

    public void setCountry_code(String str) {
        this.country_code = str;
    }

    public String getCountry_name() {
        return this.country_name;
    }

    public void setCountry_name(String str) {
        this.country_name = str;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String str) {
        this.location = str;
    }

    public String getCarrier() {
        return this.carrier;
    }

    public void setCarrier(String str) {
        this.carrier = str;
    }

    public String getLine_type() {
        return this.line_type;
    }

    public void setLine_type(String str) {
        this.line_type = str;
    }
}