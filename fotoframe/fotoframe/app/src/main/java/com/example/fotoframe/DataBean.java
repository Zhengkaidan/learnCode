package com.example.fotoframe;

import java.util.List;

public class DataBean {
    private String Date;
    private List<imageInfo> pic_list;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public List<imageInfo> getPic_list() {
        return pic_list;
    }

    public void setPic_list(List<imageInfo> pic_list) {
        this.pic_list = pic_list;
    }
}
