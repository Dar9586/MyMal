package com.dar.mymal.global;

import com.dar.mymal.entries.api.Entry;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by atopp on 10/07/2017.
 */

public class EntryList {
    private static String ownUser= LoginData.getUsername(),actualUser=ownUser;
    private static List<Entry>[][] ownlist,actualList;
    private static boolean issame=true;

    public static void reloadOwn(boolean anime){
        ownlist[anime?0:1]=anime? MALUtils.getEntriesAnime(ownUser):MALUtils.getEntriesManga(ownUser);
    }
    public static void reloadOwn(){
        ownlist=MALUtils.getEntries(ownUser);
    }
    public static void reloadOther(boolean anime){
        actualList[anime?0:1]=anime?MALUtils.getEntriesAnime(actualUser):MALUtils.getEntriesManga(actualUser);
    }
    public static void reloadOther(){
        actualList=MALUtils.getEntries(actualUser);
    }

    public static void reloadLists(boolean anime){
        reloadOwn(anime);
        reloadOther(anime);
    }
    public static void reloadLists(){
        reloadOwn();
        reloadOther();
    }
    public static void loadOtherList(String user){
        actualUser=user;
        issame=actualUser.equals(ownUser);
        if(issame){reloadOwn();actualList=ownlist;}
        else
            reloadOther();
    }
    public static int totalEntries(boolean anime){
        int sum=0;
        for(int a=0;a<5;a++)sum+=actualList[anime?0:1][a].size();
        return sum;
    }

    public static int totalEntries(){
        return totalEntries(true)+totalEntries(false);
    }
    public static String getOwnUser() {
        return ownUser;
    }

    public static String getActualUser() {
        return actualUser;
    }

    public static List<Entry>[][] getOwnlist() {
        return ownlist;
    }

    public static boolean isSame() {
        return issame;
    }

    public static List<Entry>[][] getActualList() {
        return actualList;
    }
}
    /*public static void reloadOther(boolean anime){
        if(issame) {
            reloadOwn(anime);
            actualList[anime ? 0 : 1] = ownlist[anime ? 0 : 1];
        }
        else
            actualList[anime?0:1]=anime?MALUtils.getEntriesAnime(actualUser):MALUtils.getEntriesManga(actualUser);
    }
    public static void reloadOther(){
        if(issame) {
            reloadOwn();
            actualList = ownlist;
        }
        else
            actualList=MALUtils.getEntries(actualUser);
    }*/