package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * @author：liush on 2017/1/10 22:06
 */
public class ProvinceEntiry {

    private int ProvinceID;
    private String Title;
    private ArrayList<City> City;

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceID) {
        ProvinceID = provinceID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public ArrayList<ProvinceEntiry.City> getCity() {
        return City;
    }

    public void setCity(ArrayList<ProvinceEntiry.City> city) {
        City = city;
    }

    public class City{
        int CityID;
        String Title;

        public int getCityID() {
            return CityID;
        }

        public void setCityID(int cityID) {
            CityID = cityID;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }
    }
}
