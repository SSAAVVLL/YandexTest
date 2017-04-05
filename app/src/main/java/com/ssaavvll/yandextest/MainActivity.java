package com.ssaavvll.yandextest;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ssaavvll.yandextest.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity  implements TranslateFragment.OnFragmentInteractionListener,
        HistoryFragment.OnListFragmentInteractionListener, FavouriteFragment.OnFragmentInteractionListener{
    private TranslateFragment translateFragment;
    private FavouriteFragment favouriteFragment;
    private HistoryFragment historyFragment;
    private BottomNavigationView bottomNavigationView;
    private String currentFragment = TAG_TRANSLATE;
    private static final String TAG_TRANSLATE = "Translate_fragment";
    private static final String TAG_FAVOURITE = "Favourite_fragment";
    private static final String TAG_HISTORY= "History_fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        translateFragment = TranslateFragment.newInstance();
        favouriteFragment = FavouriteFragment.newInstance();
        historyFragment = HistoryFragment.newInstance();

        /*setting actionBar */
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        /* initial navDrawer */
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        /* Setting bottomNavigation */
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /* Show translate fragment on start */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.frameLayout, TranslateFragment.newInstance(), TAG_TRANSLATE);
        fragmentTransaction.commit();


        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("select", "success");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        /*spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);*/
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("activity", "restoreInstance");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key", 2);
        Log.d("saved", "intance of activity was saved");
    }

    public void onListFragmentInteraction(DummyContent.DummyItem dummy){
        //you can leave it empty
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("activity", "destroy");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.hide(fragmentManager.findFragmentByTag(currentFragment));
            Fragment fragment = null;
            String tag;
            switch (item.getItemId()) {
                case R.id.translateItem:
                    tag = TAG_TRANSLATE;
                    if (currentFragment != tag) {
                        fragment = fragmentManager.findFragmentByTag(tag);
                        if (fragment == null) {
                            fragment = TranslateFragment.newInstance();
                            fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                        } else
                            fragmentTransaction.show(fragment);
                        currentFragment = tag;
                    }
                    break;
                case R.id.favouriteItem:
                    tag = TAG_FAVOURITE;
                    if (currentFragment != tag) {
                        fragment = fragmentManager.findFragmentByTag(tag);
                        if (fragment == null) {
                            fragment = FavouriteFragment.newInstance();
                            fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                        } else
                            fragmentTransaction.show(fragment);
                        currentFragment = tag;
                    }
                    break;
                case R.id.historyItem:
                    tag = TAG_HISTORY;
                    if (currentFragment != tag) {
                        fragment = fragmentManager.findFragmentByTag(tag);
                        if (fragment == null) {
                            fragment = HistoryFragment.newInstance();
                            fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                        } else
                            fragmentTransaction.show(fragment);
                        currentFragment = tag;
                    }
                    break;
            }
            Utils.hideKeyboard(MainActivity.this);
            if (fragment != null) {
                fragmentTransaction.commit();
            }
            fragment = fragmentManager.findFragmentByTag(TAG_TRANSLATE);
            if (fragment != null)
                Log.d("Activity fragment find", "yes");
            else
                Log.d("Activity fragment find", "no");
            return true;
        }

    };
}
