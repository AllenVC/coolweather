package com.example.weathertest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathertest.db.City;
import com.example.weathertest.db.Country;
import com.example.weathertest.db.Provience;
import com.example.weathertest.util.HttpUtil;
import com.example.weathertest.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by LZF-PC on .
 */
public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTRY=2;

    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private ProgressBar progressBar;
    /**
     * 省列表/市列表/县列表
     */
    private List<Provience> provienceList;
    private List<City> cityList;
    private List<Country> countryList;
    /**
     * 选中的省市区
     */
    private Provience selectProvince;
    private City selectCity;
    private Country selectCountry;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        progressBar=(ProgressBar)view.findViewById(R.id.progress);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectProvince=provienceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTRY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    public void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provienceList= LitePal.findAll(Provience.class);
        if(provienceList.size()>0){
            dataList.clear();
            for(Provience provience:provienceList){
                dataList.add(provience.getProvienceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    public void queryCities(){
        titleText.setText(selectProvince.getProvienceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=LitePal.where("provinceId=?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectProvince.getProvienceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }
    public void queryCounties(){
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList=LitePal.where("cityId=?",String.valueOf(selectCity.getId())).find(Country.class);
        if(countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTRY;
        }else {
            int provinceCode=selectProvince.getProvienceCode();
            int cityCode=selectCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"country");
        }
    }
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(responseText,selectProvince.getId());
                }else if ("country".equals(type)){
                    result=Utility.handleCountryResponse(responseText,selectCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("country".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void closeProgressDialog(){
        progressBar.setVisibility(View.GONE);
    }
}
