package com.dar.mymal.entries.inspector;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.dar.mymal.ViewLoader;
import com.dar.mymal.adapters.dataEntry.StaffAdapter;
import com.dar.mymal.adapters.seachEntry.CharacterAdapter;
import com.dar.mymal.downloader.DownloadURL;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by atopp on 31/07/2017.
 */

public class Characters {
    int id;
    boolean anime;
    String html;
    String characterCode,staffCode;
    List<Character> characterList=new ArrayList<>();
    List<Character> staffList=new ArrayList<>();
    DownloadURL async;
    Context context;
    View views[];

    public Characters(int id, boolean anime, Context context, View[] views) {
        this.id = id;
        this.anime = anime;
        this.context = context;
        this.views = views;
        getHTML();
    }

    public Characters(int id,boolean anime) {
       this(id,anime,null,null);
    }
    void getHTML(){
        async=new DownloadURL(new DownloadURL.AsyncResponse() {
        @Override
        public void processFinish(String output) {
            html=output;
            int pos=html.indexOf(anime?"floatRightHeader":"panel.php");
            if(anime)characterCode=html.substring(pos,pos=html.indexOf("floatRightHeader",pos+1));
            staffCode=html.substring(pos,html.indexOf("<script",pos));
            if(anime) {
                Log.d("OnMALDebug","Loading Characters");
                pos = 0;
                while ((pos = (characterCode.indexOf("<tr>", pos)) + 10) > 10)
                    characterList.add(new Character(characterCode.substring(pos, pos = characterCode.indexOf("</table>", pos))));
                if(context!=null) ViewLoader.loadAdapterView(views[0],new StaffAdapter(context,characterList),new LinearLayoutManager(context));
            }
            pos=0;
            Log.d("OnMALDebug","Loading Staff");
            while((pos=(staffCode.indexOf("<table",pos))+10)>10){
                staffList.add(new Character(new People(staffCode.substring(pos,pos=(staffCode.indexOf("</table",pos))),anime)));
            }
            if(context!=null) ViewLoader.loadAdapterView(views[1],new StaffAdapter(context,staffList),new LinearLayoutManager(context));
        }
    });
    async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://myanimelist.net/" + (anime ? "anime" : "manga") + "/" + id + "/x/characters");
    }
    public int getId() {
        return id;
    }

    public boolean isAnime() {
        return anime;
    }

    public List<Character> getCharacterList() {
        return characterList;
    }

    public List<Character> getStaffList() {
        return staffList;
    }

    public class Character{
        String code;
        People character;
        List<People>voices=new ArrayList<>();
        public Character(People staff){
            character=staff;
        }
        public Character(String code) {
            this.code = code;
            int pos=code.indexOf("<tr>")+1;
            if(pos<1){
                character=new People(code,false);return;
            }
            character=new People(code.substring(0,pos),false);
            try {
                while (true) {
                    voices.add(new People(code.substring(pos, (pos = code.indexOf("</tr><", pos) + 6)), true));
                }
            }catch (StringIndexOutOfBoundsException e){
                //try {
                voices.add(new People(code.substring(code.lastIndexOf("</tr><")+6), true));//}
                //catch(StringIndexOutOfBoundsException e1){}
            }
        }

        @Override
        public String toString() {
            return "Character{" +
                    "character=" + character +
                    ", voices=" + voices +
                    '}';
        }

        public People getCharacter() {
            return character;
        }

        public List<People> getVoices() {
            return voices;
        }
    }
    public class People{
        int id;
        String desc,name,image,code;
        boolean isStaff;

        public People(String code, boolean isStaff) {
            this.code = code;
            this.isStaff = isStaff;
            findImage();
            findDesc();
            findName();
            findLink();
        }

        @Override
        public String toString() {
            return "People{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", isStaff=" + isStaff +
                    ", image='" + image + '\'' +
                    '}';
        }

        public String getLink(){
            return String.format("https://myanimelist.net/%s/%d",isStaff?"people":"character",id);
        }
        private void findName(){
            String h=code.substring(code.indexOf("<img alt=\"")+10);
            name=h.substring(0,h.indexOf('"'));
        }
        private void findLink() {
            String h=code.substring(code.indexOf("<a href")+9);
            h=h.substring(0,h.indexOf('"'));
            h=h.substring(0,h.lastIndexOf('/'));
            id=Integer.parseInt(h.substring(h.lastIndexOf('/')+1));
        }

        private void findDesc() {
            String h=code.substring(code.indexOf("<small>")+7);
            desc=h.substring(0,h.indexOf('<'));
        }

        private void findImage() {
            String h=code.substring(code.indexOf("data-src")+10);
            image=h.substring(0,h.indexOf('"'));
        }

        public int getId() {
            return id;
        }

        public String getDesc() {
            return desc;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public boolean isStaff() {
            return isStaff;
        }
    }




}

