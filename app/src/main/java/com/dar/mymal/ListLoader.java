package com.dar.mymal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.dar.mymal.adapters.AnimeAdapter;
import com.dar.mymal.adapters.MangaAdapter;
import com.dar.mymal.adapters.MenuAdapter;
import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Entry;
import com.dar.mymal.entries.Manga;
import com.dar.mymal.utils.LoginData;
import com.dar.mymal.utils.MALUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListLoader extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean anime=true;
    static boolean useLessData=false;
    int actuallySee=0;
    public static String actualUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Pair<String,Integer>[] listDataHeader;
    List<Pair<String,Integer>>[] listDataChild;
    ExpandableListView expand;
    final static List<Entry>[][] ownList=MALUtils.getEntries(LoginData.getUsername());;
    List<Entry>[][] entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        final SwipeRefreshLayout swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEntries(actualUser);
                loadList(actuallySee,anime);
                swipeRefresh.setRefreshing(false);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actualUser=LoginData.getUsername();
        entries=ownList;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // setting list adapter
        expand=(ExpandableListView)findViewById(R.id.expand_view);
        loadExtendMenu();
        expand.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(groupPosition==0){
                    switchList();
                }else if(groupPosition==2){
                    switchUser();
                }
                return false;
            }
        });
        expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition==1){
                    if(actuallySee!=childPosition){
                        actuallySee=childPosition;
                        loadList(actuallySee,anime);
                    }
                }
                return true;
            }
        });
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);
        loadList(actuallySee,anime);
    }

    private void switchUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setTitle("Inserire username");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                actualUser=input.getText().toString();
                loadEntries(actualUser);
                loadList(actuallySee,anime);
                loadExtendMenu();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    void switchList(){
        anime=!anime;
        loadList(actuallySee,anime);
        loadExtendMenu();

    }
    int totalEntry(){
        int sum=0;
        for(int a=0;a<5;a++)sum+=entries[anime?0:1][a].size();
        return sum;
    }
    private void loadExtendMenu() {
        listDataHeader = new Pair[]{new Pair<>("Switch to "+(anime?"manga":"anime"),-1),new Pair<>("List",totalEntry()),new Pair<>("Change user",-1)};
        listDataChild = new List[]{new ArrayList(),new ArrayList(),new ArrayList()};
        listDataChild[1].add(new Pair<>(Entry.getMyStatusList(anime)[0],entries[anime?0:1][0].size()));
        listDataChild[1].add(new Pair<>(Entry.getMyStatusList(anime)[1],entries[anime?0:1][1].size()));
        listDataChild[1].add(new Pair<>(Entry.getMyStatusList(anime)[2],entries[anime?0:1][2].size()));
        listDataChild[1].add(new Pair<>(Entry.getMyStatusList(anime)[3],entries[anime?0:1][3].size()));
        listDataChild[1].add(new Pair<>(Entry.getMyStatusList(anime)[4],entries[anime?0:1][4].size()));
        expand.setAdapter(new MenuAdapter(this,listDataHeader, listDataChild));
    }

    void loadEntries(String user){
        entries=MALUtils.getEntries(user);
        setTitle(user+"'s List");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    private void loadList(int actuallySee, boolean anime) {

        if(anime){mAdapter = new AnimeAdapter(this,entries[0][actuallySee].toArray(new Anime[entries[0][actuallySee].size()]),useLessData,getCacheFile());}
        else{mAdapter = new MangaAdapter(this,entries[1][actuallySee].toArray(new Manga[entries[1][actuallySee].size()]),useLessData,getCacheFile());}
        mRecyclerView.setAdapter(mAdapter);

    }
    protected static List<String> getCacheFile(){
        String pathImg= Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache";
        File cacheFolder=new File(pathImg);
        cacheFolder.mkdir();
        File[] temp=cacheFolder.listFiles();
        List<String> files= new ArrayList<>();
        for(int a=0;a<temp.length;a++)files.add(temp[a].getName());
        return files;
    }
}




