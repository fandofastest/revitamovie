package com.revita.hdmovies2020.revita_model;

import java.util.ArrayList;
import java.util.List;

public class EpiModel {
    String seson,epi,streamURL,serverType, imageUrl;

    public List<SubtitleModel> listSubepi= new ArrayList<>();
    public String subtitleURLtv;

    public List<SubtitleModel> getListSubepi() {
        return listSubepi;
    }

    public void setListSubepi(List<SubtitleModel> listSubepi) {
        this.listSubepi = listSubepi;
    }

    public String getSubtitleURLtv() {
        return subtitleURLtv;
    }

    public void setSubtitleURLtv(String subtitleURLtv) {
        this.subtitleURLtv = subtitleURLtv;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public String getSeson() {
        return seson;
    }

    public void setSeson(String seson) {
        this.seson = seson;
    }

    public String getEpi() {
        return epi;
    }

    public List<SubtitleModel> getListsubtv(int position) {
        return listsubtv;
    }

    public void setListsubtv(List<SubtitleModel> listsubtv) {
        this.listsubtv = listsubtv;
    }

    public void setEpi(String epi) {
        this.epi = epi;
    }

    public List<SubtitleModel> listsubtv = new ArrayList<>();

}
