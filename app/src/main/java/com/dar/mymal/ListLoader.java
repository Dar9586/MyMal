package com.dar.mymal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.dar.mymal.adapters.AnimeAdapter;
import com.dar.mymal.adapters.MangaAdapter;
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

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<List<Entry>>[] entries;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadEntries(LoginData.getUsername());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);
        loadList(actuallySee,anime);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            /*Intent i=new Intent(getApplicationContext(), SettingActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("EXIT", true);
            startActivity(i);*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_switch) {
            anime=!anime;
            loadList(actuallySee,anime);
        } else if (id == R.id.nav_list1) {
            if(actuallySee!=0){
                actuallySee=0;
                loadList(actuallySee,anime);
            }
        } else if (id == R.id.nav_list2) {
            if(actuallySee!=1){
                actuallySee=1;
                loadList(actuallySee,anime);
            }
        } else if (id == R.id.nav_list3) {
            if(actuallySee!=2){
                actuallySee=2;
                loadList(actuallySee,anime);
            }
        } else if (id == R.id.nav_list4) {
            if(actuallySee!=3){
                actuallySee=3;
                loadList(actuallySee,anime);
            }
        } else if (id == R.id.nav_list6) {
            if(actuallySee!=4){
                actuallySee=4;
                loadList(actuallySee,anime);
            }
            }else if (id == R.id.nav_switch_account) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText input = new EditText(this);
                builder.setTitle("Inserire username");
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadEntries(input.getText().toString());
                        loadList(actuallySee,anime);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadList(int actuallySee, boolean anime) {
        String pathImg= Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache";
        File cacheFolder=new File(pathImg);
        cacheFolder.mkdir();
        File[] temp=cacheFolder.listFiles();
        List<String> files= new ArrayList<>();
        for(int a=0;a<temp.length;a++)files.add(temp[a].getName());

        if(anime){mAdapter = new AnimeAdapter(entries[0].get(actuallySee).toArray(new Anime[entries[0].get(actuallySee).size()]),useLessData,files);}
        else{mAdapter = new MangaAdapter(entries[1].get(actuallySee).toArray(new Manga[entries[1].get(actuallySee).size()]),useLessData,files);}
        mRecyclerView.setAdapter(mAdapter);

    }
}




