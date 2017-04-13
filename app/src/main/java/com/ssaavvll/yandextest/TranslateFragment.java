package com.ssaavvll.yandextest;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private ConstraintLayout constraintLayout;
    private CoordinatorLayout rootLayout;
    private BottomNavigationView bottomNavigation;
    private HashMap<String, String> hmLang;
    private HashMap<String, String> hmLangInverse;
    private ArrayAdapter<CharSequence> adapter;
    private Uri.Builder yandexTranslateRequestBuilder;
    private TextToSpeech textToSpeech;
    private String langTo;
    private String textFrom;


    private OnFragmentInteractionListener listener;

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* get shared preferences */
        Activity activity = getActivity();
        sPref = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        bottomNavigation = (BottomNavigationView) activity.findViewById(R.id.navigation);
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
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
        textField = (EditText) view.findViewById(R.id.editTextTrans);
        textTranslated = (TextView) view.findViewById(R.id.translatedText);
        constraintLayout = (ConstraintLayout) view.findViewById(R.id.translateResult);
        rootLayout = (CoordinatorLayout) getActivity().findViewById(R.id.rootLayout);
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
        Log.d("Test", yandexTranslateURL);

        /* get Json from http request, parse them */
        JsonObjectRequest jsObjReq = new JsonObjectRequest
                (Request.Method.GET, yandexTranslateURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        GsonTemplates.ResponseLangs responseLangs = gson.fromJson(response.toString(), GsonTemplates.ResponseLangs.class);
                        if (responseLangs.langs != null) {
                            /* filling adapter and hash map*/
                            HashMap<String, String> langs = responseLangs.langs;
                            List<String> keys = new ArrayList<String>(langs.values());
                            Collections.sort(keys);

                            for (Map.Entry<String, String> entry : langs.entrySet()) {
                                hmLang.put(entry.getValue(), entry.getKey());
                                hmLangInverse.put(entry.getKey(), entry.getValue());
                            }
                            for (int i = 0; i < keys.size(); i++) {
                                adapter.add(keys.get(i));
                            }
                            spinnerFrom.setSelection(sPref.getInt(APP_PREFERENCES_LANG_FROM, 0));
                            spinnerTo.setSelection(sPref.getInt(APP_PREFERENCES_LANG_TO, 0));
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
                                    .show();
                        }
                    }
                }, new Response.ErrorListener() {
                    /* show alert dialog if can't get response from yandex api*/
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                                .show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjReq);
        constraintLayout.setAlpha(0);
        //constraintLayout.setVisibility(View.INVISIBLE);
        /* listener for clear button*/
        buttonClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textField.getText().clear();
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
                Log.d("speak", langTo);
                int result = textToSpeech.setLanguage(new Locale(langTo));
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getActivity(), R.string.langNotSupported, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), hmLangInverse.get(langTo), Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(textTranslated.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                }
            }
        });
        /* listener for favourite button */
        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(TranslateContract.History.COLUMN_NAME_LANG_FROM, "ru");
                values.put(TranslateContract.History.COLUMN_NAME_LANG_TO, "en");
                values.put(TranslateContract.History.COLUMN_NAME_TEXT_FROM, "olol");
                values.put(TranslateContract.History.COLUMN_NAME_TEXT_TO, "help");
                values.put(TranslateContract.History.COLUMN_NAME_FAVOURITE, 0);
                MainActivity act = (MainActivity)getActivity();
                long newRowId = act.getDb().insert(TranslateContract.History.TABLE_NAME, null, values);
            }
        });
        /* listener of keyboard opens */
        KeyboardVisibilityEvent.setEventListener(getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen)
                            bottomNavigation.setVisibility(View.GONE);
                        else
                            bottomNavigation.setVisibility(View.VISIBLE);
                    }
                }
        );
        /* settings adapter on spinners*/
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        /* build uri for translate request */
        yandexTranslateRequestBuilder = new Uri.Builder();
        yandexTranslateRequestBuilder.scheme("https")
                .authority("translate.yandex.net")
                .appendPath("api")
                .appendPath("v1.5")
                .appendPath("tr.json")
                .appendPath("translate");
        /* Text changed listener for main textField */
        textField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    /* make translate request to yandex API */
                    String from = hmLang.get(spinnerFrom.getItemAtPosition(spinnerFrom.getSelectedItemPosition()));
                    String to = hmLang.get(spinnerTo.getItemAtPosition(spinnerTo.getSelectedItemPosition()));
                    yandexTranslateRequestBuilder.clearQuery();
                    textFrom = s.toString();
                    yandexTranslateRequestBuilder.appendQueryParameter("key", getString(R.string.api_key))
                            .appendQueryParameter("text", s.toString())
                            .appendQueryParameter("lang", from + '-' + to);

                    String yandexTranslateRequestURL = yandexTranslateRequestBuilder.build().toString();
                    JsonObjectRequest jsObjReq = new JsonObjectRequest
                            (Request.Method.GET, yandexTranslateRequestURL, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("textFrom", textFrom);
                                    Log.d("resultJson", response.toString());
                                    constraintLayout.animate().alpha(1);
                                    //constraintLayout.setVisibility(View.VISIBLE);
                                    Gson gson = new Gson();
                                    GsonTemplates.ResponseTranslate responseTranslate = gson.fromJson(response.toString(), GsonTemplates.ResponseTranslate.class);
                                    textTranslated.setText(responseTranslate.text.get(0));
                                    langTo = responseTranslate.lang.substring(responseTranslate.lang.indexOf('-') + 1);
                                    //Log.d("translate", responseTranslate.text.toString());

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), "Error on translate",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjReq);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(APP_PREFERENCES_LANG_FROM, spinnerFrom.getSelectedItemPosition());
        editor.putInt(APP_PREFERENCES_LANG_TO, spinnerTo.getSelectedItemPosition());
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public interface OnFragmentInteractionListener {
    }
}