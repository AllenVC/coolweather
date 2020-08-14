package com.example.weathertest.util;

import android.text.TextUtils;

import com.example.weathertest.db.City;
import com.example.weathertest.db.Country;
import com.example.weathertest.db.Provience;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Create by LZF-PC on .
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省份数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces=new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Provience provience=new Provience();
                    provience.setProvienceName(provinceObject.getString("name"));
                    provience.setProvienceCode(provinceObject.getInt("id"));
                    provience.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountryResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountries=new JSONArray(response);
                for(int i=0;i<allCountries.length();i++){
                    JSONObject countryObject=allCountries.getJSONObject(i);
                    Country country=new Country();
                    country.setCountryName(countryObject.getString("name"));
                    country.setWeatherId(countryObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
