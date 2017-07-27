package com.dar.mymal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.SearchView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.dar.mymal.entries.SearchEntry;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.utils.EntryList;
import com.dar.mymal.utils.LoginData;
import com.dar.mymal.utils.MALUtils;
import com.dar.mymal.utils.MalAPI;
import com.dar.mymal.utils.Sorter;

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
    Tuple2<String,Integer>[] listDataHeader;
    List<Tuple2<String,Integer>>[] listDataChild;
    ExpandableListView expand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EntryList.reloadLists();
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ((SwipeRefreshLayout)findViewById(R.id.swiperefresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actualUser=LoginData.getUsername();
        setTitle(actualUser+"'s List");

        final SearchView search=(SearchView)findViewById(R.id.search_button);
        search.setOnSearchClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          search.setIconified(true);
                                          Intent i=new Intent(getApplicationContext(),ActivitySearch.class);
                                          i.putExtra("ISANIME",anime);
                                          startActivity(i);

                                          //makeSearch(s);
                                      }
                                  });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadList(actuallySee,anime);
        // setting list adapter
        expand=(ExpandableListView)findViewById(R.id.expand_view);
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


    }

    private void refreshList() {
        ((SwipeRefreshLayout)findViewById(R.id.swiperefresh)).setRefreshing(true);
        loadEntries(actualUser);
        loadList(actuallySee,anime);
        loadExtendMenu();
        ((SwipeRefreshLayout)findViewById(R.id.swiperefresh)).setRefreshing(false);
    }

    private void switchUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setTitle("Inserire username");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                actualUser=input.getText().toString();
                refreshList();
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
        for(int a=0;a<5;a++)sum+=EntryList.getActualList()[anime?0:1][a].size();
        return sum;
    }
    private void loadExtendMenu() {
        listDataHeader = new Tuple2[]{new Tuple2<>("Switch to "+(anime?"manga":"anime"),-1),new Tuple2<>("List",totalEntry()),new Tuple2<>("Change user",-1)};
        listDataChild = new List[]{new ArrayList(),new ArrayList(),new ArrayList()};
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(anime)[0],EntryList.getActualList()[anime?0:1][0].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(anime)[1],EntryList.getActualList()[anime?0:1][1].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(anime)[2],EntryList.getActualList()[anime?0:1][2].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(anime)[3],EntryList.getActualList()[anime?0:1][3].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(anime)[4],EntryList.getActualList()[anime?0:1][4].size()));
        expand.setAdapter(new MenuAdapter(this,listDataHeader, listDataChild));
    }

    void loadEntries(String user){
        EntryList.loadOtherList(user);
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
    protected void onResume() {
        refreshList();
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    private void loadList(int actuallySee, boolean anime) {
        if(anime){mRecyclerView.setAdapter(new AnimeAdapter(this,actuallySee,useLessData,getCacheFile()));}
        else{mRecyclerView.setAdapter(new MangaAdapter(this,actuallySee,useLessData,getCacheFile()));}
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




