package com.example.videoex;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String phone;

    private String url;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl() {
        this.url = url;
    }


//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("email", email);
//        result.put("name", name);
//        result.put("phone", phone);
//        result.put("url", url);
//        return result;
//    }
}
