package com.dar.mymal;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.tuple.Tuple3;

import java.util.List;

/**
 * Created by atopp on 07/08/2017.
 */

public class ViewLoader {
    public static View loadViewDescription(View master, LayoutInflater inflater,String text){
        removeLoadScreen(master);
        LinearLayout rootView =(LinearLayout)master.findViewById(R.id.entry_infos);
        View v=inflater.inflate(R.layout.void_textview,rootView,false);
        ((TextView)v.findViewById(R.id.name)).setText(text);
        rootView.addView(v);
        return rootView;
    }
    public static View loadViewInfos(View master,LayoutInflater inflater,Tuple2<String,List<Tuple2<String,String>>>[] infos){
        removeLoadScreen(master);
        LinearLayout rootView =(LinearLayout)master.findViewById(R.id.entry_infos);
        for(int a=0;a<infos.length;a++){
            View v=inflater.inflate(R.layout.entry_info_title,rootView,false);
            ((TextView)v.findViewById(R.id.entry_info_header)).setText(infos[a].getA());
            rootView.addView(v);
            for(int b=0;b<infos[a].getB().size();b++){
                View v1=inflater.inflate(R.layout.entry_info,rootView,false);
                ((TextView)v1.findViewById(R.id.name)) .setText(infos[a].getB().get(b).getA());
                ((TextView)v1.findViewById(R.id.value)).setText(infos[a].getB().get(b).getB());
                rootView.addView(v1);
            }
        }
        return rootView;
    }
    public static View loadViewRelations(View master,LayoutInflater inflater,final List<Tuple2<String,List<Tuple3<Integer,Boolean,String>>>> rela){
        removeLoadScreen(master);
        LinearLayout rootView =(LinearLayout)master.findViewById(R.id.entry_infos);
        for(int a=0;a<rela.size();a++){
            View v=inflater.inflate(R.layout.entry_info_title,rootView,false);
            ((TextView)v.findViewById(R.id.entry_info_header)).setText(rela.get(a).getA());
            rootView.addView(v);
            for(int b=0;b<rela.get(a).getB().size();b++){
                View v1=inflater.inflate(R.layout.basic_relation,rootView,false);
                ((TextView)v1.findViewById(R.id.basic_relation2)).setText(rela.get(a).getB().get(b).getC());
                final int c=a,d=b;
                v1.findViewById(R.id.basic_relation2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(v.getContext(), EntryActivity.class);
                        i.putExtra("ENTRY_ID",rela.get(c).getB().get(d).getA());
                        i.putExtra("ENTRY_TITLE",rela.get(c).getB().get(d).getC());
                        i.putExtra("ENTRY_ISANIME",rela.get(c).getB().get(d).getB());
                        v.getContext().startActivity(i);
                    }
                });
                rootView.addView(v1);
            }
        }
        return rootView;
    }
    public static View loadViewSong(View master, LayoutInflater inflater,String[][]songs){
        removeLoadScreen(master);
        LinearLayout rootView =(LinearLayout)master.findViewById(R.id.entry_infos);
        String[]names={"Opening","Ending"};
        for(int a=0;a<songs.length;a++){
            View v=inflater.inflate(R.layout.entry_info_title,rootView,false);
            ((TextView)v.findViewById(R.id.entry_info_header)).setText(names[a]);
            rootView.addView(v);
            for(int b=0;b<songs[a].length;b++){
                View v1=inflater.inflate(R.layout.void_textview,rootView,false);
                ((TextView)v1.findViewById(R.id.name)).setText(Html.fromHtml(songs[a][b]).toString());
                rootView.addView(v1);
            }
        }
        return rootView;
    }
    public static void loadAdapterView(View master, RecyclerView.Adapter adapter,LinearLayoutManager manager){
        removeLoadScreen(master);
        RecyclerView rootView = (RecyclerView) master.findViewById(R.id.recycler);
        rootView.setLayoutManager(manager);
        rootView.setAdapter(adapter);
    }
    public static void removeLoadScreen(View view){
        /*if(view.findViewById(R.id.loading_layout)!=null)
            view.findViewById(R.id.loading_layout).setVisibility(View.GONE);*/
    }
}
