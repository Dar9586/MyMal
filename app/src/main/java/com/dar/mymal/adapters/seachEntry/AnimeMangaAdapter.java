package com.dar.mymal.adapters.seachEntry;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.EntryActivity;
import com.dar.mymal.R;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.entries.SearchEntry;
import com.dar.mymal.entries.searches.AnimeManga;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class AnimeMangaAdapter extends RecyclerView.Adapter<AnimeMangaAdapter.ViewHolder> {
    private List<AnimeManga.SearchEntry> mDataset;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView title,id,type,episodes,score,description;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.entry_pic);
            title=(TextView)v.findViewById(R.id.title);
            id=(TextView)v.findViewById(R.id.id);
            type=(TextView)v.findViewById(R.id.type);
            episodes=(TextView)v.findViewById(R.id.episodes);
            description=(TextView)v.findViewById(R.id.description);
            score=(TextView)v.findViewById(R.id.score);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public AnimeMangaAdapter(Context cont, List<AnimeManga.SearchEntry> myDataset) {
        this.context=cont;
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public AnimeMangaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AnimeManga.SearchEntry ent=mDataset.get(position);
        if(files.contains((ent.isAnime()?"A":"M")+ent.getId()+".jpg")){
            holder.imgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+(ent.isAnime()?"A":"M")+ent.getId()+".jpg")));
        }else if(!Settings.isUsingLessData()){
            new DownloadImage(holder.imgView,Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache","A"+Integer.toString(ent.getId())).execute(ent.getImageURL());
        }
        holder.title.setText(Html.fromHtml(ent.getTitle()).toString());
        holder.id.setText(Integer.toString(ent.getId()));
        holder.description.setText(Html.fromHtml(ent.getDescription()).toString());
        holder.type.setText("Type: "+ent.getType());
        holder.score.setText("Score: "+ent.getScore());
        holder.episodes.setText((ent.isAnime()?"Episodes: ":"Chapters: ")+(ent.isAnime()?ent.getEpisodes():ent.getChapters()));
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(view.getContext(), EntryActivity.class);
                i.putExtra("ENTRY_ID",ent.getId());
                i.putExtra("ENTRY_ISANIME",ent.isAnime());
                i.putExtra("ENTRY_TITLE",ent.getTitle());
                view.getContext().startActivity(i);
            }
        });

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
