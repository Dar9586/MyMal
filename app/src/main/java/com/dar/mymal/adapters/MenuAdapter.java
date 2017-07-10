package com.dar.mymal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.dar.mymal.R;
import com.dar.mymal.tuple.Tuple2;

import java.util.List;

/**
 * Created by atopp on 04/07/2017.
 */

public class MenuAdapter extends BaseExpandableListAdapter {
    public Tuple2<String,Integer>[]headers;
    List<Tuple2<String,Integer>>childs[];
    Context context;

    public MenuAdapter(Context context,Tuple2<String,Integer>[]headers,List<Tuple2<String,Integer>>childs[]){
        this.context = context;
        this.headers=headers;
        this.childs=childs;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.header_menu, null);
        }
        View f;
        if(headers[groupPosition].getB()==-1){
            f=LayoutInflater.from(convertView.getContext()).inflate(R.layout.header_menu_void, parent, false);
            ((TextView)f.findViewById(R.id.expand_name)).setText(headers[groupPosition].getA());
            return f;
        }
        f=LayoutInflater.from(convertView.getContext()).inflate(R.layout.header_menu, parent, false);
        ((TextView)f.findViewById(R.id.expand_name)).setText(headers[groupPosition].getA());
        ((TextView)f.findViewById(R.id.expand_num)).setText(headers[groupPosition].getB().toString());
        return f;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_menu, null);
        }
        View f=LayoutInflater.from(convertView.getContext()).inflate(R.layout.child_menu, parent, false);
        ((TextView)f.findViewById(R.id.expand_name)).setText(childs[groupPosition].get(childPosition).getA());
        ((TextView)f.findViewById(R.id.expand_num)).setText(childs[groupPosition].get(childPosition).getB().toString());
        return f;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getGroupCount() {
        return headers.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs[groupPosition].size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers[groupPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs[groupPosition].get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*100+childPosition;
    }
}