package com.ssaavvll.yandextest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity  implements TranslateFragment.OnFragmentInteractionListener,
        HistoryFragment.OnListFragmentInteractionListener, FavouriteFragment.OnListFragmentInteractionListener{
    private BottomNavigationView bottomNavigationView;
    private String currentFragment = TAG_TRANSLATE;
    private static final String TAG_TRANSLATE = "Translate_fragment";
    private static final String TAG_FAVOURITE = "Favourite_fragment";
    private static final String TAG_HISTORY = "History_fragment";
    private static  SQLiteDatabase db;
    /* method for get database */
    public static SQLiteDatabase getDb() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.YandexTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* setting connection to database */
        HistorySQLiteHelper mDbHelper = new HistorySQLiteHelper(this);
        db = mDbHelper.getWritableDatabase();

        /* setting actionBar */
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        /* Setting bottomNavigation */
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /* Show translate fragment on start */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.replace(R.id.frameLayout, TranslateFragment.newInstance(), TAG_TRANSLATE);
        fragmentTransaction.commit();

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutItem:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected (MenuItem item) {
            /* change fragments through bottom menu */
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.hide(fragmentManager.findFragmentByTag(currentFragment));
            Fragment fragment = null;
            String tag;

            /* remove fragments */
            if (currentFragment == TAG_HISTORY || currentFragment == TAG_FAVOURITE) {
                Fragment removeFragment = fragmentManager.findFragmentByTag(currentFragment);
                fragmentTransaction.remove(removeFragment);
            }

            /* action on click on certain item */
            switch (item.getItemId()) {
                case R.id.translateItem:
                    tag = TAG_TRANSLATE;
                    if (currentFragment != tag) {
                        fragment = fragmentManager.findFragmentByTag(tag);
                        if (fragment == null) {
                            fragment = TranslateFragment.newInstance();
                            fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                        } else {
                            ((TranslateFragment) fragment).updateFav();
                            fragmentTransaction.show(fragment);
                        }
                        currentFragment = tag;
                    }
                    break;
                case R.id.favouriteItem:
                    tag = TAG_FAVOURITE;
                    fragment = FavouriteFragment.newInstance();
                    fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                    fragmentTransaction.show(fragment);
                    currentFragment = tag;
                    break;
                case R.id.historyItem:
                    tag = TAG_HISTORY;
                    fragment = HistoryFragment.newInstance();
                    fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                    fragmentTransaction.show(fragment);
                    currentFragment = tag;
                    break;
            }
            /* hide keyboard on change fragment */
            Utils.hideKeyboard(MainActivity.this);
            if (fragment != null) {
                fragmentTransaction.commit();
            }
            return true;
        }

    };

    public void openTranslate(long id) {
        if (id != 0) {
            String selection = TranslateContract.History._ID + " = ?";
            String[] selectionArgs = {id + ""};
            Cursor cursor = db.query(TranslateContract.History.TABLE_NAME,
                    TranslateContract.History.allColumns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);


            if (cursor.getCount() == 1) {
                cursor.moveToNext();

                /* change fragments through bottom menu */
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.hide(fragmentManager.findFragmentByTag(currentFragment));
                Fragment fragment = null;
                String tag;

                /* remove fragment */
                Fragment removeFragment = fragmentManager.findFragmentByTag(currentFragment);
                fragmentTransaction.remove(removeFragment);

                tag = TAG_TRANSLATE;
                fragment = fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = TranslateFragment.newInstance();
                    fragmentTransaction.add(R.id.frameLayout, fragment, tag);
                } else {
                    fragmentTransaction.show(fragment);
                }
                String textFrom = cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_TEXT_FROM));
                String textTo = cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_TEXT_TO));
                String langFrom = cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_LANG_FROM));
                String langTo =  cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_LANG_TO));
                boolean fav = cursor.getInt(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_FAVOURITE)) == 1;

                ((TranslateFragment) fragment).setValues(id, textFrom, textTo, langFrom, langTo, fav);
                currentFragment = tag;
                bottomNavigationView.setSelectedItemId(R.id.translateItem);


                if (fragment != null) {
                    fragmentTransaction.commit();
                }
            }
        }
    }

    @Override
    public void onListFragmentInteraction(TranslateItem item) {
    }
}
