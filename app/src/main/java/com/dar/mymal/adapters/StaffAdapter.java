package com.dar.mymal.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Characters;
import com.dar.mymal.utils.EntryList;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.downloader.DownloadImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stopp on 03/07/2017.
 */

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    boolean useLessData;
    Context context;
    List<String>files;
    List<Characters.Character> mData;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout right,left;
        boolean isSet=false;
        public ViewHolder(View v) {
            super(v);
            left= (LinearLayout)v.findViewById(R.id.staff_left );
            right=(LinearLayout)v.findViewById(R.id.staff_right);
            //right.addView(princ);
        }

        public boolean isSet() {
            return isSet;
        }

        public void setSet(boolean set) {
            isSet = set;
        }
    }

    public StaffAdapter(boolean useLessData, Context context, List<String> files, List<Characters.Character> mData) {
        this.useLessData = useLessData;
        this.context = context;
        this.files = files;
        this.mData = mData;
    }

    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_staff, parent, false));
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        populateView(inflater.inflate(R.layout.simple_staff_left, holder.left,true), mData.get(position).getCharacter());
        if(mData.get(position).getVoices().size()!=0) {
            for (int a = 0; a < mData.get(position).getVoices().size(); a++) {
                View s = inflater.inflate(R.layout.simple_staff_right, holder.right, false);
                holder.right.addView(s);
                populateView(s, mData.get(position).getVoices().get(a));
            }
        }else{
            holder.right.setVisibility(View.GONE);
        }

    }
    void populateView(View v, Characters.People p){
        if(!useLessData){
            ((ImageView)v.findViewById(R.id.staff_image)).setScaleType(ImageView.ScaleType.FIT_START);
            new DownloadImage((ImageView)v.findViewById(R.id.staff_image)).execute(p.getImage());
        }
        ((TextView)v.findViewById(R.id.staff_name)).setText(p.getName());
        ((TextView)v.findViewById(R.id.staff_type)).setText(p.getDesc());
    }

    @Override
    public int getItemCount() {
       return mData.size();
    }

}
