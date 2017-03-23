package com.ssaavvll.yandextest;

import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    protected ArrayAdapter<CharSequence> adapter;
    protected HashMap<CharSequence, CharSequence> hmLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        hmLang = new HashMap<CharSequence, CharSequence>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting appBar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        //get list of languages
        Uri.Builder yandexTranslateBuilder = new Uri.Builder();
        yandexTranslateBuilder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("getLangs")
                .appendQueryParameter("key", getString(R.string.api_key))
                .appendQueryParameter("ui", Locale.getDefault().getLanguage());
        String yandexTranslateURL = yandexTranslateBuilder.build().toString();
        Log.d("Test", yandexTranslateURL);
        //get Json, parse them

        JsonObjectRequest jsObjReq = new JsonObjectRequest
                (Request.Method.GET, yandexTranslateURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("resultJson", response.toString());
                try {
                    JSONObject resp = response.getJSONObject("langs");
                    JSONArray respNames = resp.names();
                    for (int i = 0; i < respNames.length(); i++) {
                        Object lang = respNames.get(i);
                        String langStr = lang.toString();
                        String langName = resp.get(langStr).toString();
                        adapter.add(langName);
                        hmLang.put(langName, langStr);
                        Log.d("lang", resp.get(langStr).toString());
                    }
                    /*Log.d("names", respNames.toString());
                    Log.d("resp", resp.get("ru").toString());
                    Log.d("respAll", resp.toString());
                    Log.d("length", Integer.toString(resp.length()));*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resultJson", "error");
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsObjReq);


//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.spinner, android.R.layout.simple_spinner_item);

        //setting adapter

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setNotifyOnChange(true);
        hmLang.put("lol", "lol1");
        Log.d("hm", hmLang.toString());
        //initial spinner
        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("select", "success");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        /* initial navDrawer*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }*/
}
