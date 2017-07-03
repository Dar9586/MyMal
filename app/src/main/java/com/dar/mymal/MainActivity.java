package com.dar.mymal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.content.*;

import com.dar.mymal.utils.LoginData;


public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView user;
    EditText pass;
    Button log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("PART","1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("PART","2");
        user = (AutoCompleteTextView) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        log = (Button) findViewById(R.id.email_sign_in_button);
        Log.i("PART","3");
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginData s=new LoginData(user.getText().toString(), pass.getText().toString());
                if (s!=null&&s.isLogged()) {
                    SharedPreferences sharedPref = getSharedPreferences("IDvalue", 0);
                    SharedPreferences.Editor editor=sharedPref.edit();
                    Log.e("LOGDATA", "User: " + s.getUsername());
                    Log.e("LOGDATA", "Enco: " + s.getEncoded());
                    editor.clear();
                    editor.putBoolean(getString(R.string.prop_logged), true);
                    editor.putString(getString(R.string.prop_user), s.getUsername());
                    editor.putString(getString(R.string.prop_head), s.getEncoded());
                    editor.commit();
                    launchIntent();
                }
            }
        });
        Log.i("PART","4");
        SharedPreferences sharedPref = getSharedPreferences("IDvalue", 0);
        if(sharedPref.contains(getString(R.string.prop_logged))&&sharedPref.getBoolean(getString(R.string.prop_logged),true)){
            new LoginData(sharedPref.getString(getString(R.string.prop_user),"ERRORE"), sharedPref.getString(getString(R.string.prop_head),"ERRORE"),true);
            launchIntent();
        }
    }
    void launchIntent(){
        Intent i=new Intent(getApplicationContext(), ListLoader.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("EXIT", true);
        startActivity(i);
    }
}

/*class MyAsyncTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... arg) {

        return null;
    }


    @Override
    protected void onPostExecute(Void a) {

    }
}*/