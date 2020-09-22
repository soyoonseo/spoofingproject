package com.example.videoex;

public class Member {

    private String VideoName;
    private String VideoUrl;

    private Member(){}

    public Member(String name, String videoUrl){

        if (name.trim().equals("")){
            name =  "not available";

        }
        VideoName =name ;
        VideoUrl = videoUrl;

    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }
}
