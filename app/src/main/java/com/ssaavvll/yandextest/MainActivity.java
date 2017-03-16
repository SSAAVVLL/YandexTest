package com.ssaavvll.yandextest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.toolbox.JsonObjectRequest;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setting appBar
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        //initial spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinnerLeft);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //get list of languages
        Uri.Builder yandexTranslateBuilder = new Uri.Builder();
        yandexTranslateBuilder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("getLangs")
                .appendQueryParameter("key", getString(R.string.api_key))
                .appendQueryParameter("ui", Locale.getDefault().getDisplayLanguage());
        String yandexTranslateURL = yandexTranslateBuilder.build().toString();
        //Log.d("Test", yandexTranslateURL);
//        https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trn
        //get Json with languages
//        JsonObjectRequest
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}
