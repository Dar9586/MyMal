package com.dar.mymal.adapters.seachEntry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.R;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.entries.searches.People;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {
    private List<People.PeopleSearchEntry> mDataset;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView name,desc;
        public LinearLayout master,animes,mangas;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            name=(TextView)v.findViewById(R.id.name);
            desc=(TextView)v.findViewById(R.id.description);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
            animes=(LinearLayout)v.findViewById(R.id.animes);
            mangas=(LinearLayout)v.findViewById(R.id.mangas);
        }
    }

    public PeopleAdapter(Context cont, List<People.PeopleSearchEntry> myDataset) {
        this.context=cont;
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public PeopleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PeopleAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_character, parent, false));
    }

    @Override
    public void onBindViewHolder(final PeopleAdapter.ViewHolder holder, final int position) {
        final People.PeopleSearchEntry ent=mDataset.get(position);
        if(!Settings.isUsingLessData()){
            new DownloadImage(holder.imgView).execute(ent.getImageURL());
        }
        holder.animes.setVisibility(View.GONE);
        holder.mangas.setVisibility(View.GONE);
        holder.name.setText(ent.getName());
        holder.desc.setText(ent.getId()+"");
        holder.desc.setVisibility(View.GONE);
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}