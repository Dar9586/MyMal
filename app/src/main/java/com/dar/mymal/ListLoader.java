package com.dar.mymal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.dar.mymal.adapters.listEntries.AnimeAdapter;
import com.dar.mymal.adapters.listEntries.MangaAdapter;
import com.dar.mymal.adapters.MenuAdapter;
import com.dar.mymal.entries.api.Entry;
import com.dar.mymal.global.Settings;
import com.dar.mymal.tuple.Tuple2;
import com.dar.mymal.global.EntryList;
import com.dar.mymal.global.LoginData;
import com.dar.mymal.utils.MALUtils;

import java.util.ArrayList;
import java.util.List;

public class ListLoader extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //boolean anime=true;
    int actuallySee=0;
    public static String actualUser;
    private RecyclerView mRecyclerView;
    Tuple2<String,Integer>[] listDataHeader;
    List<Tuple2<String,Integer>>[] listDataChild;
    ExpandableListView expand;
    LinearLayoutManager linearLayoutManager;
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
                                          Intent i=new Intent(getApplicationContext(),SearchActivity2.class);
                                          startActivity(i);
                                      }
                                  });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        linearLayoutManager=new LinearLayoutManager(this);
        loadList(actuallySee);
        // setting list adapter
        expand=(ExpandableListView)findViewById(R.id.expand_view);
        expand.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(groupPosition==0){
                    switchList();
                }else if(groupPosition==4){
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
                        loadList(actuallySee);
                    }
                }
                else if(groupPosition==2||groupPosition==3){
                    Intent i=new Intent(getApplicationContext(),TopActivity.class);
                    i.putExtra("IS_ANIME",groupPosition==2);
                    i.putExtra("TYPE",childPosition);
                    startActivity(i);
                }
                return true;
            }
        });


    }

    private void refreshList() {
        /*EntryList.loadOtherList(actualUser);*/
        MALUtils.getListAsync(actualUser,EntryList.getOwnUser().equals(actualUser),findViewById(R.id.master_layout),this,actuallySee,((SwipeRefreshLayout)findViewById(R.id.swiperefresh)));
        setTitle(actualUser+"'s List");
        //loadList(actuallySee);
        loadExtendMenu();
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
        Settings.setAnime(!Settings.isAnime());
        loadList(actuallySee);
        loadExtendMenu();

    }
    int totalEntry(){
        int sum=0;
        for(int a=0;a<5;a++)sum+=EntryList.getActualList()[Settings.isAnime()?0:1][a].size();
        return sum;
    }
    private void loadExtendMenu() {
        listDataHeader = new Tuple2[]{new Tuple2<>("Switch to "+(Settings.isAnime()?"manga":"anime"),-1),new Tuple2<>("List",totalEntry()),new Tuple2<>("Top Anime",-1),new Tuple2<>("Top Manga",-1),new Tuple2<>("Change user",-1)};
        listDataChild = new List[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(Settings.isAnime())[0],EntryList.getActualList()[Settings.isAnime()?0:1][0].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(Settings.isAnime())[1],EntryList.getActualList()[Settings.isAnime()?0:1][1].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(Settings.isAnime())[2],EntryList.getActualList()[Settings.isAnime()?0:1][2].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(Settings.isAnime())[3],EntryList.getActualList()[Settings.isAnime()?0:1][3].size()));
        listDataChild[1].add(new Tuple2<>(Entry.getMyStatusList(Settings.isAnime())[4],EntryList.getActualList()[Settings.isAnime()?0:1][4].size()));
        listDataChild[2].add(new Tuple2<>("Top",-1));
        listDataChild[2].add(new Tuple2<>("Popularity",-1));
        listDataChild[2].add(new Tuple2<>("Airing",-1));
        listDataChild[2].add(new Tuple2<>("Upcoming",-1));
        listDataChild[2].add(new Tuple2<>("Favorites",-1));
        listDataChild[3].add(new Tuple2<>("Top",-1));
        listDataChild[3].add(new Tuple2<>("Popularity",-1));
        listDataChild[3].add(new Tuple2<>("Airing",-1));
        listDataChild[3].add(new Tuple2<>("Upcoming",-1));
        listDataChild[3].add(new Tuple2<>("Favorites",-1));
        expand.setAdapter(new MenuAdapter(this,listDataHeader, listDataChild));
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
            case R.id.action_alternative_search:
                Intent i=new Intent(getApplicationContext(),ActivitySearch.class);
                startActivity(i);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void loadList(int actuallySee) {
        ViewLoader.loadAdapterView(findViewById(R.id.master_layout),Settings.isAnime()?new AnimeAdapter(this,actuallySee):new MangaAdapter(this,actuallySee),linearLayoutManager);
    }
}




