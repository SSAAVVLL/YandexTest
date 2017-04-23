package com.ssaavvll.yandextest;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TranslateFragment extends Fragment {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LANG_FROM = "langFrom";
    public static final String APP_PREFERENCES_LANG_TO = "langTo";
    private SharedPreferences sPref;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private EditText textField;
    private TextView textTranslated;
    private ImageButton buttonClear;
    private ImageButton buttonSpeak;
    private ImageButton buttonSwapLangs;
    private ImageButton buttonFav;
    private RelativeLayout loadingScreen;
    private ConstraintLayout constraintLayout;
    private BottomNavigationView bottomNavigation;
    private MenuItem itemProgress;
    private List<String> keys;
    private HashMap<String, String> hmLang;
    private HashMap<String, String> hmLangInverse;
    private ArrayAdapter<CharSequence> adapter;
    private Uri.Builder yandexTranslateRequestBuilder;
    private TextToSpeech textToSpeech;
    private String langFrom = "";
    private String langTo = "";
    private String textFrom;
    private GsonTemplates.ResponseTranslate lastResponse;
    private long lastRecordId;

    private OnFragmentInteractionListener listener;

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* get shared preferences */
        /* use shared preference for storage custom use of languages from and to */
        Activity activity = getActivity();
        sPref = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        bottomNavigation = (BottomNavigationView) activity.findViewById(R.id.navigation);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        /* get views from layout */
        spinnerFrom = (Spinner) view.findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) view.findViewById(R.id.spinnerTo);
        buttonClear = (ImageButton) view.findViewById(R.id.clearButton);
        buttonSpeak = (ImageButton) view.findViewById(R.id.speakButton);
        buttonSwapLangs = (ImageButton) view.findViewById(R.id.swapLangs);
        buttonFav = (ImageButton) view.findViewById(R.id.favButton);
        loadingScreen = (RelativeLayout) getActivity().findViewById(R.id.loadingScreen);
        textField = (EditText) view.findViewById(R.id.editTextTrans);
        TextView copyrights = (TextView) view.findViewById(R.id.copyright);
        copyrights.setMovementMethod(LinkMovementMethod.getInstance());
        if (textField != null) {
            /* settings text field translate from */
            textField.setMinLines(5);
            textField.setMaxLines(5000);
            textField.setTypeface(Typeface.DEFAULT);
            textField.setHorizontallyScrolling(false);
        }
        textTranslated = (TextView) view.findViewById(R.id.translatedText);
        constraintLayout = (ConstraintLayout) view.findViewById(R.id.translateResult);
        /* correct focus on root layout */
        if (constraintLayout != null) {
            constraintLayout.setFocusableInTouchMode(true);
            constraintLayout.setFocusable(true);
        }
        /* setting textToSpeech */
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        buttonSpeak.setImageResource(R.drawable.ic_speaker);
                                    }
                                });
                            };
                        };
                        thread.run();

                    }

                    @Override
                    public void onError(String utteranceId) {

                    }

                });
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        hmLang = new HashMap<String, String>();
        hmLangInverse = new HashMap<String, String>();

        /* Build uri for get list of langs from yandex api */
        final Uri.Builder yandexTranslateBuilder = new Uri.Builder();
        yandexTranslateBuilder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("getLangs")
                .appendQueryParameter("key", getString(R.string.api_key))
                .appendQueryParameter("ui", Locale.getDefault().getLanguage());
        String yandexTranslateURL = yandexTranslateBuilder.build().toString();

        /* get Json from http request, parse them */
        JsonObjectRequest jsObjReq = new JsonObjectRequest
                (Request.Method.GET, yandexTranslateURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        GsonTemplates.ResponseLangs responseLangs = gson.fromJson(response.toString(), GsonTemplates.ResponseLangs.class);
                        if (responseLangs.langs != null) {
                            /* filling adapter and hash map */
                            HashMap<String, String> langs = responseLangs.langs;
                            keys = new ArrayList<String>(langs.values());
                            Collections.sort(keys);

                            for (Map.Entry<String, String> entry : langs.entrySet()) {
                                hmLang.put(entry.getValue(), entry.getKey());
                                hmLangInverse.put(entry.getKey(), entry.getValue());
                            }
                            for (int i = 0; i < keys.size(); i++) {
                                adapter.add(keys.get(i));
                            }
                            /* set adapter on spinners */
                            spinnerFrom.setAdapter(adapter);
                            spinnerTo.setAdapter(adapter);
                            /* set selection on spinners */
                            int prefFrom = sPref.getInt(APP_PREFERENCES_LANG_FROM, -1);
                            int prefTo = sPref.getInt(APP_PREFERENCES_LANG_TO, -1);
                            if (prefFrom == -1 && prefTo == -1) {
                                prefFrom = keys.indexOf(hmLangInverse.get(Locale.getDefault().getLanguage()));
                                prefTo = keys.indexOf(hmLangInverse.get("en"));
                                if (prefFrom == prefTo)
                                    prefTo = keys.indexOf(hmLangInverse.get("de"));
                            }
                            spinnerFrom.setSelection(prefFrom);
                            spinnerTo.setSelection(prefTo);
                            /* hide progress bar */
                            loadingScreen.setVisibility(View.GONE);
                            textField.requestFocus();
                        }
                        else {
                            /* show alertDialog for not supported languages */
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.alertResponseSystemLangProblemTitle)
                                    .setMessage(R.string.alertResponseSystemLangProblemMessage)
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            getActivity().finish();
                                        }
                                    })
                                    .setPositiveButton(R.string.alertResponseSystemLangProblemSettings, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivityForResult(new Intent(Settings.ACTION_LOCALE_SETTINGS), 0);
                                            getActivity().finish();
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            getActivity().finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                }, new Response.ErrorListener() {
                    /* show alert dialog if can't get response from yandex api*/
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /* Problem with connection to API*/
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.alertResponseLangProblemTitle)
                                .setMessage(R.string.alertResponseLangProblemMessage)
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                })
                                .setPositiveButton(R.string.alertResponseLangProblemRetry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = getActivity().getIntent();
                                        getActivity().finish();
                                        startActivity(intent);
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        getActivity().finish();
                                    }
                                })
                                .show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjReq);
        constraintLayout.setAlpha(0);
        textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    Utils.hideKeyboard(getActivity());
            }
        });
        /* listener for clear button*/
        buttonClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textField.getText().clear();
                textTranslated.setText("...");
                buttonSpeak.setEnabled(false);
                buttonFav.setEnabled(false);
            }
        });
        /* listener for swap languages button */
        buttonSwapLangs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int spinnerFromPosition = spinnerFrom.getSelectedItemPosition();
                spinnerFrom.setSelection(spinnerTo.getSelectedItemPosition());
                spinnerTo.setSelection(spinnerFromPosition);
            }
        });
        /* listener for speak button*/
        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (textToSpeech.isSpeaking()) {
                buttonSpeak.setImageResource(R.drawable.ic_speaker);
                textToSpeech.stop();
            } else
            {
                int result = textToSpeech.setLanguage(new Locale(langTo));
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getActivity(), R.string.langNotSupported, Toast.LENGTH_SHORT).show();
                } else {
                    /* change icon of button and speak text */
                    buttonSpeak.setImageResource(R.drawable.ic_stop);
                    Toast.makeText(getActivity(), hmLangInverse.get(langTo), Toast.LENGTH_SHORT).show();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
                    textToSpeech.speak(textTranslated.getText().toString(), TextToSpeech.QUEUE_FLUSH, map);

                }
            }
            }
        });
        /* listener for favourite button */
        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastResponse != null) {
                    String selection = TranslateContract.History._ID + " = ?";
                    String[] selectionArgs = { lastRecordId + "" };
                    Cursor cursor = MainActivity.getDb().query(TranslateContract.History.TABLE_NAME,
                            TranslateContract.History.allColumns,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null);
                    if (cursor.getCount() == 1) {
                        cursor.moveToFirst();
                        int fav = cursor.getInt(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_FAVOURITE));
                        fav ^= 1;
                        if (fav == 1) {
                            buttonFav.setImageResource(R.drawable.ic_favicon_active);
                            Toast.makeText(getContext(), R.string.addToFav, Toast.LENGTH_SHORT).show();
                        } else {
                            buttonFav.setImageResource(R.drawable.ic_favicon);
                            Toast.makeText(getContext(), R.string.delFromFav, Toast.LENGTH_SHORT).show();
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(TranslateContract.History.COLUMN_NAME_FAVOURITE, fav);
                        int af = MainActivity.getDb().update(TranslateContract.History.TABLE_NAME,
                                contentValues,
                                selection,
                                selectionArgs);
                    }
                }
            }
        });
        /* set onkey listener on textField */
        textField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    makeTranslateRequest(true);
                }
                return false;
            }
        });
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                makeTranslateRequest(false);
            }
        });
        /* Copy text to clipboard */
        textTranslated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("result", textTranslated.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), R.string.copyText, Toast.LENGTH_SHORT).show();
            }
        });
        /* listener of keyboard opens */
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                            bottomNavigation.setVisibility(View.GONE);
                        else {
                            makeTranslateRequest(true);
                            bottomNavigation.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        /* set listener for spinnerFrom */
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeTranslateRequest(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /* set listener for spinnerTo */
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeTranslateRequest(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /* build uri for translate request */
        yandexTranslateRequestBuilder = new Uri.Builder();
        yandexTranslateRequestBuilder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("translate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;

    }

    @Override
    public void onDestroyView() {
        /* Save choosen languages */
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(APP_PREFERENCES_LANG_FROM, spinnerFrom.getSelectedItemPosition());
        editor.putInt(APP_PREFERENCES_LANG_TO, spinnerTo.getSelectedItemPosition());
        editor.commit();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, R.id.translateItem, 0, R.string.translateProgress).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        this.itemProgress = menu.findItem(R.id.translateItem);
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setScaleX((float)0.5);
        progressBar.setScaleY((float)0.5);
        this.itemProgress.setActionView(progressBar);
        this.itemProgress.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void makeTranslateRequest(final boolean action) {
        /* Request to api translate text */
        if (lastRecordId == 0 || !langFrom.equals(hmLang.get(spinnerFrom.getSelectedItem())) || !langTo.equals(hmLang.get(spinnerTo.getSelectedItem()))  || lastResponse == null || !textField.getText().toString().equals(textFrom)) {
            final String textFr = textField.getText().toString();
            if (!textFr.equals("")) {
                buttonSpeak.setEnabled(false);
                buttonFav.setEnabled(false);
                textTranslated.setText("...");
                this.itemProgress.setVisible(true);
                final String from = hmLang.get(spinnerFrom.getSelectedItem());
                final String to = hmLang.get(spinnerTo.getSelectedItem());
                yandexTranslateRequestBuilder.clearQuery();

                String yandexTranslateRequestURL = yandexTranslateRequestBuilder.build().toString();
                StringRequest stringReq = new StringRequest
                        (Request.Method.POST, yandexTranslateRequestURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                buttonSpeak.setEnabled(true);
                                buttonFav.setEnabled(true);
                                smoothShowView(constraintLayout);
                                buttonFav.setImageResource(R.drawable.ic_favicon);
                                /* Create java object from json */
                                Gson gson = new Gson();
                                GsonTemplates.ResponseTranslate responseTranslate = gson.fromJson(response, GsonTemplates.ResponseTranslate.class);
                                textTranslated.setText(responseTranslate.text.get(0));
                                /* scroll to translateText */
                                if (action) {
                                    Utils.hideKeyboard(getActivity());
                                    constraintLayout.requestFocus();
                                }
                                lastResponse = responseTranslate;
                                itemProgress.setVisible(false);
                                /* save values to the database */
                                if ((!textField.getText().toString().equals(textFrom) || !langFrom.equals(hmLang.get(spinnerFrom.getSelectedItem())) || !langTo.equals(hmLang.get(spinnerTo.getSelectedItem()))) && action) {
                                    langFrom = responseTranslate.lang.substring(0, responseTranslate.lang.indexOf('-'));
                                    langTo = responseTranslate.lang.substring(responseTranslate.lang.indexOf('-') + 1);
                                    textFrom = textFr;
                                    String fromLang;
                                    fromLang = lastResponse.lang.substring(0, lastResponse.lang.indexOf('-'));
                                    ContentValues values = new ContentValues();
                                    /* Add record to history table*/
                                    values.put(TranslateContract.History.COLUMN_NAME_LANG_FROM, langFrom);
                                    values.put(TranslateContract.History.COLUMN_NAME_LANG_TO, langTo);
                                    values.put(TranslateContract.History.COLUMN_NAME_TEXT_FROM, textFrom);
                                    values.put(TranslateContract.History.COLUMN_NAME_TEXT_TO, lastResponse.text.get(0));
                                    values.put(TranslateContract.History.COLUMN_NAME_FAVOURITE, 0);
                                    lastRecordId = MainActivity.getDb().insert(TranslateContract.History.TABLE_NAME, null, values);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Log.d("volley", error.getMessage());
                                itemProgress.setVisible(false);
                                String message = getString(R.string.errorTranslate);
                                if (error.networkResponse != null) {
                                    if (error.networkResponse.statusCode == 413)
                                        message = getString(R.string.errorTranslateLength);
                                    if (error.networkResponse.statusCode == 403)
                                        message = getString(R.string.errorTranslateCount);
                                    if (error.networkResponse.statusCode == 422)
                                        message = getString(R.string.errorTranslateCant);
                                    if (error.networkResponse.statusCode == 501)
                                        message = getString(R.string.errorTranslateDirection);
                                }
                                Toast.makeText(getActivity(), message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("key", getString(R.string.api_key));
                        params.put("lang", from + '-' + to);
                        params.put("text", textFr);
                        return params;
                    }
                };
                stringReq.setRetryPolicy(new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingleton.getInstance(getActivity()).addToRequestQueue(stringReq);
            } else {}
                //Toast.makeText(getContext(),R.string.emptyField, Toast.LENGTH_SHORT).show();
        }

    }

    public void updateFav(){
        /* check change on favorite column */
        if (lastRecordId != 0) {
            String selection = TranslateContract.History._ID + " = ?";
            String[] selectionArgs = {lastRecordId + ""};
            Cursor cursor = MainActivity.getDb().query(TranslateContract.History.TABLE_NAME,
                    TranslateContract.History.allColumns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cursor.getCount() == 1) {
                cursor.moveToNext();
                int fav = cursor.getInt(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_FAVOURITE));
                if (fav == 1) {
                    buttonFav.setImageResource(R.drawable.ic_favicon_active);
                } else {
                    buttonFav.setImageResource(R.drawable.ic_favicon);
                }
            } else {
                lastRecordId = 0;
                constraintLayout.setVisibility(View.GONE);
            }
        }
    }

    public void setValues(long id, String from, String to, String langFrom, String langTo, boolean fav){
        lastRecordId = id;
        textFrom = from;
        textField.setText(from);
        textTranslated.setText(to);
        int prefFrom = keys.indexOf(hmLangInverse.get(langFrom));
        int prefTo = keys.indexOf(hmLangInverse.get(langTo));
        this.langFrom = langFrom;
        this.langTo = langTo;
        buttonSpeak.setEnabled(true);
        buttonFav.setEnabled(true);
        if (fav)
            buttonFav.setImageResource(R.drawable.ic_favicon_active);
        else
            buttonFav.setImageResource(R.drawable.ic_favicon);
        spinnerFrom.setSelection(prefFrom);
        spinnerTo.setSelection(prefTo);

        smoothShowView(constraintLayout);
    }

    public void smoothShowView (View v) {
        if (v != null) {
            if (v.getVisibility() == View.GONE)
                constraintLayout.setVisibility(View.VISIBLE);
            if (v.getAlpha() == 0)
                constraintLayout.animate().alpha(1);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface OnFragmentInteractionListener {
    }
}