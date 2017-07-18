package com.dar.mymal.entries.recommendation;

import android.text.Html;

/**
 * Created by stopp on 18/07/2017.
 */

public class SingleRecommendation {
    String user,text;

    public SingleRecommendation(String user, String text) {
        this.user = user;
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "SingleRecommendation{" +
                "user='" + user + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

}
