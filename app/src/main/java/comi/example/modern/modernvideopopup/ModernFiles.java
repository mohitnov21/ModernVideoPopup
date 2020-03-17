package comi.example.modern.modernvideopopup;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Instafeed2 on 10/24/2019.
 */

public class ModernFiles {
    private String img;
    private String name;
    private String date;
    private String format;
    private String resoltion;
    String duration;
    private String location;
    private String id;

    public ArrayList<PlayBackHistory> playbackHistoryData = new ArrayList<>();



    public ModernFiles(String id,String name, String location, String duration,String format, String img,String date) {
        this.name = name;
        this.id = id;
        this.format = format;
        this.img = img;
        this.date = date;

        this.location = location;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getResoltion() {
        return resoltion;
    }

    public void setResoltion(String resoltion) {
        this.resoltion = resoltion;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<PlayBackHistory> getPlaybackHistoryData() {
        return playbackHistoryData;
    }

    public void setPlaybackHistoryData(ArrayList<PlayBackHistory> playbackHistoryData) {
        this.playbackHistoryData = playbackHistoryData;
    }
}
