package com.example.weathertest.db;

import org.litepal.crud.LitePalSupport;

/**
 * Create by LZF-PC on .
 */
public class Country extends LitePalSupport {
    private int id;
    private String countryName;
    private String weatherId;
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public int getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
