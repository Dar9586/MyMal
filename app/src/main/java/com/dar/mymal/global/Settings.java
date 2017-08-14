package com.dar.mymal.global;

import android.view.View;

/**
 * Created by atopp on 01/08/2017.
 */

public class Settings {
    private static boolean useLessData=false;
    private static boolean anime=true;


    public static boolean isAnime() {
        return anime;
    }

    public static void setAnime(boolean anime) {
        Settings.anime = anime;
    }

    public static boolean isUsingLessData() {
        return useLessData;
    }

    public static void setUseLessData(boolean useLessData) {
        Settings.useLessData = useLessData;
    }
}
