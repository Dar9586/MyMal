package com.dar.mymal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dar.mymal.entries.Anime;
import com.dar.mymal.entries.Entry;
import com.dar.mymal.entries.Manga;
import com.dar.mymal.utils.LoginData;
import com.dar.mymal.utils.MALUtils;
import com.dar.mymal.utils.downloader.DownloadImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListLoader extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean anime=true,useLessData=false;
    int actuallySee=0;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

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
        Log.e("Entries","Starter");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);


        /*for(int a=0;a<entries.length;a++)
            for(int b=0;b<entries[a].size();b++)
                Log.e("Entries",a+" "+b+" "+entries[a].get(b).toString());*/
        loadList(actuallySee,anime);
    }

    void loadEntries(String user){
        entries=MALUtils.getEntries(user);
        setTitle(user+"'s List");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        GestureDetector gd=new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {return false;}
            @Override
            public void onShowPress(MotionEvent e) {}
            @Override
            public boolean onSingleTapUp(MotionEvent e) {return false;}
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {return false;}
            @Override
            public void onLongPress(MotionEvent e) {}
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {return false;}
        });
        gd.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {return false;}

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {return false;}
        });
        LayoutInflater factory = LayoutInflater.from(this);
        View myView=null;
        LinearLayout sss=(LinearLayout)findViewById(R.id.entry_scroll);
        sss.removeAllViews();
        String pathImg= Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache";
        File cacheFolder=new File(pathImg);
        cacheFolder.mkdir();
        File[] temp=cacheFolder.listFiles();
        List<String> files= new ArrayList<>();
        for(int a=0;a<temp.length;a++)files.add(temp[a].getName());
        for(int a=0;a<entries[anime?0:1].get(actuallySee).size();a++){
            Log.e("logEntry",entries[anime?0:1].get(actuallySee).get(a).toString());
            Entry ent=entries[anime?0:1].get(actuallySee).get(a);
            myView= factory.inflate(anime?R.layout.entry_layout_anime:R.layout.entry_layout_manga,null);
            if(!useLessData){
                if(files.contains(ent.getID()+".jpg")){
                    ((ImageView)myView.findViewById(R.id.image)).setBackground(new BitmapDrawable(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache/"+ent.getID()+".jpg")));
                }
                else if(!useLessData){
                    new DownloadImage((ImageView)myView.findViewById(R.id.image),Environment.getExternalStorageDirectory().getAbsolutePath()+"/myMalCache",Integer.toString(ent.getID())).execute(ent.getImageURL());
                }
            }
            ((TextView)myView.findViewById(R.id.title)).setText(Html.fromHtml(ent.getTitle()).toString());
            try{((TextView)myView.findViewById(R.id.entry_id)).setText(ent.getID());}catch(Exception e){Log.e("IDLOADERERROR",e.getMessage());}
            ((TextView)myView.findViewById(R.id.status)).setText(ent.getType(true)+" - "+ent.getStatus(anime));
            if(anime){
                ((TextView)myView.findViewById(R.id.progress)).setText(((Anime)ent).getMyEpisodes()+"/"+((Anime)ent).getEpisodes());}
            else{
                try{((TextView)myView.findViewById(R.id.progress1)).setText(((Manga)ent).getMyChapter()+"/"+((Manga)ent).getChapter());
                ((TextView)myView.findViewById(R.id.progress2)).setText(((Manga)ent).getMyVolumes()+"/"+((Manga)ent).getVolumes());}catch(Exception e){Log.e("IDLOADERERROR",e.getMessage());}
            }
            myView.setMinimumHeight(200);

            sss.addView(myView);
        }


    }
}




