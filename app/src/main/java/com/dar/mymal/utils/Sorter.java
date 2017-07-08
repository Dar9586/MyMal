package com.dar.mymal.utils;

import com.dar.mymal.entries.Entry;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by atopp on 08/07/2017.
 */

public class Sorter {
    public static List<Entry> sortByName(List<Entry>entries){
        //entries.sort((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()));
        Collections.sort(entries,new Comparator<Entry>(){
            public int compare(Entry o1, Entry o2){
                return o1.getTitle().compareToIgnoreCase(o2.getTitle());
            }
        });
        return entries;
    }
}