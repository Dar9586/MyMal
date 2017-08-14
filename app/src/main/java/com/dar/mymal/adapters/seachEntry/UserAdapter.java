package com.dar.mymal.adapters.seachEntry;

import android.app.Activity;
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
import com.dar.mymal.entries.SearchEntry;
import com.dar.mymal.downloader.DownloadImage;
import com.dar.mymal.entries.searches.User;
import com.dar.mymal.global.EntryList;
import com.dar.mymal.global.Settings;
import com.dar.mymal.utils.MALUtils;

import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User.UserSearchEntry> mDataset;
    Context context;
    List<String>files;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView name,desc;
        public LinearLayout master;
        public ViewHolder(View v) {
            super(v);
            imgView=(ImageView)v.findViewById(R.id.image);
            name=(TextView)v.findViewById(R.id.name);
            desc=(TextView)v.findViewById(R.id.description);
            master=(LinearLayout)v.findViewById(R.id.master_layout);
        }
    }

    public UserAdapter(Context cont, List<User.UserSearchEntry> myDataset) {
        this.context=cont;
        this.mDataset = myDataset;
        this.files= MALUtils.getCacheFile();
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_search, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User.UserSearchEntry ent=mDataset.get(position);
        if(!Settings.isUsingLessData()){
            new DownloadImage(holder.imgView).execute(ent.getImageURL());
        }
        holder.name.setText(ent.getName());
        holder.desc.setText(ent.getDesc());
        holder.master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryList.loadOtherList(ent.getName());
                ((Activity) context).finish();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
