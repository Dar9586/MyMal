package com.dar.mymal.adapters.seachEntry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.EntryActivity;
import com.dar.mymal.R;
import com.dar.mymal.entries.SearchEntry;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.entries.searches.Character;
import com.dar.mymal.entries.searches.User;
import com.dar.mymal.global.EntryList;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {
    private List<Character.CharacterSearchEntry> mDataset;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView name,desc,id;
        public LinearLayout master,animes,mangas;
        boolean set;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            name=(TextView)v.findViewById(R.id.name);
            id=(TextView)v.findViewById(R.id.entry_id);
            desc=(TextView)v.findViewById(R.id.description);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
            animes=(LinearLayout)v.findViewById(R.id.animes);
            mangas=(LinearLayout)v.findViewById(R.id.mangas);
        }
    }

    public CharacterAdapter(Context cont, List<Character.CharacterSearchEntry> myDataset) {
        this.context=cont;
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public CharacterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_character, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final Character.CharacterSearchEntry ent=mDataset.get(position);
        if(!holder.set){
            if(!Settings.isUsingLessData()){
                new DownloadImage(holder.imgView).execute(ent.getImageURL());
            }
            holder.id.setText(Integer.toString(ent.getId()));
            holder.desc.setText(ent.getDesc());
            holder.name.setText(ent.getName());
            Log.d("OnMALDebug","Loading: "+ent.getName());
            for (int a=0;a<ent.getAnimes().size();a++){
                View v=inflater.inflate(R.layout.basic_relation, holder.animes,false);
                holder.animes.addView(v);
                populateView(v,ent.getAnimes().get(a).getB());
                Log.d("OnMALDebug","Anime: "+ent.getAnimes().get(a).getB());
                v.findViewById(R.id.basic_relation2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
            for (int a=0;a<ent.getMangas().size();a++){
                View v=inflater.inflate(R.layout.basic_relation, holder.mangas,false);
                holder.mangas.addView(v);
                populateView(v,ent.getMangas().get(a).getB());
                Log.d("OnMALDebug","Manga: "+ent.getMangas().get(a).getB());
                v.findViewById(R.id.basic_relation2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
            if(ent.getAnimes().size()==0)holder.animes.setVisibility(View.GONE);
            if(ent.getMangas().size()==0)holder.mangas.setVisibility(View.GONE);
            holder.set=true;
        }

    }
    void populateView(View v,String string){
        ((Button)v.findViewById(R.id.basic_relation2)).setText(string);
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
