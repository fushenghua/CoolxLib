package com.rtmap.cxdemo.bean;

import java.io.Serializable;

/**
 * Created by silver on 15-9-6.
 */
public class CityBean implements Serializable {
    public String lon;
    public String level;
    public String address;
    public String cityName;
    public String lat;

    @Override
    public String toString() {
        return "CityBean{" +
                "lon='" + lon + '\'' +
                ", level='" + level + '\'' +
                ", address='" + address + '\'' +
                ", cityName='" + cityName + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }
}
