package com.networkdigitally.analyzertools.speedtool.activity;
import com.google.gson.annotations.SerializedName;
public class wModel {
    @SerializedName("ip")
    private String ip;
    @SerializedName("ip_decimal")
    private String ipDecimal;
    @SerializedName("country")
    private String country;
    @SerializedName("country_eu")
    private Boolean countryEu;
    @SerializedName("country_iso")
    private String countryIso;
    @SerializedName("city")
    private String city;
    @SerializedName("hostname")
    private String hostname;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("asn")
    private String asn;
    @SerializedName("asn_org")
    private String asnOrg;
    private final static long serialVersionUID = -3301703959314051828L;
    public wModel() {
    }
    public wModel(String ip, String ipDecimal, String country, Boolean countryEu, String countryIso, String city, String hostname, Double latitude, Double longitude, String asn, String asnOrg) {
        super();
        this.ip = ip;
        this.ipDecimal = ipDecimal;
        this.country = country;
        this.countryEu = countryEu;
        this.countryIso = countryIso;
        this.city = city;
        this.hostname = hostname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.asn = asn;
        this.asnOrg = asnOrg;
    }
    public String getIp() {
        return ip;
    }
    public String getIpDecimal() {
        return ipDecimal;
    }
    public String getCountry() {
        return country;
    }
    public Boolean getCountryEu() {
        return countryEu;
    }
    public String getCountryIso() {
        return countryIso;
    }
    public String getCity() {
        return city;
    }
    public String getHostname() {
        return hostname;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public String getAsn() {
        return asn;
    }
    public String getAsnOrg() {
        return asnOrg;
    }
}
