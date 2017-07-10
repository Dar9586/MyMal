package com.dar.mymal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SearchView;

import com.dar.mymal.adapters.SearchAdapter;
import com.dar.mymal.utils.MalAPI;

public class ActivitySearch extends AppCompatActivity {
    RecyclerView mRecyclerView;
    boolean isAnime=true,useLessData=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SearchView search=(SearchView)findViewById(R.id.searcher);
        search.setIconified(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                makeSearch(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /*mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

*/
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
    void makeSearch(String s){
        mRecyclerView.setAdapter(new SearchAdapter(this, MalAPI.search(this,s,isAnime),useLessData,ListLoader.getCacheFile()));
    }
}
