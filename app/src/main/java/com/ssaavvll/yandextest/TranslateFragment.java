package com.ssaavvll.yandextest;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class TranslateFragment extends Fragment {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LANG_FROM = "langFrom";
    public static final String APP_PREFERENCES_LANG_TO = "langTo";
    protected SharedPreferences sPref;
    protected Spinner spinnerFrom;
    protected Spinner spinnerTo;
    protected ImageButton buttonClear;
    protected HashMap<CharSequence, CharSequence> hmLang;
    protected ArrayAdapter<CharSequence> adapter;

    private OnFragmentInteractionListener listener;

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Log.d("Spref", Integer.toString(sPref.getInt(APP_PREFERENCES_LANG_FROM, 0)));
        Log.d("spRef", sPref.getAll().toString());

        //MySingleton.getInstance(getActivity()).setSpinnerFrom(10);


//        this.setRetainInstance(true);
//        Log.d("Hello", "I am created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        spinnerFrom = (Spinner) view.findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) view.findViewById(R.id.spinnerTo);
        buttonClear = (ImageButton) view.findViewById(R.id.clearButton);
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        hmLang = new HashMap<CharSequence, CharSequence>();
        /* Build uri for get list of langs from api */
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
        /*Log.d("Test", yandexTranslateURL);*/

        /* get Json from http request, parse them */
        JsonObjectRequest jsObjReq = new JsonObjectRequest
                (Request.Method.GET, yandexTranslateURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", System.currentTimeMillis() + "");
                        //Toast.makeText(getApplicationContext(),"Response", Toast.LENGTH_SHORT).show();
                        /*Log.d("resultJson", response.toString());*/
                        try {
                            JSONObject resp = response.getJSONObject("langs");
                            JSONArray respNames = resp.names();
                            for (int i = 0; i < respNames.length(); i++) {
                                Object lang = respNames.get(i);
                                String langStr = lang.toString();
                                String langName = resp.get(langStr).toString();
                                adapter.add(langName);
                                hmLang.put(langName, langStr);
                                /*Log.d("shortLang", hmLang.get(langName).toString());*/
                                /*Log.d("lang", resp.get(langStr).toString());*/
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsObjReq);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        buttonClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("buttonClear","click");
            }
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
        Log.d("resume", "translateFragment");
        Log.d("resume", System.currentTimeMillis() + "");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("pause", "translateFragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        Log.d("detach", "translateFragment");
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
        outState.putInt("spin", 1);
        //SharedPreferences mPrefs = getSharedPreferences("label",0);
        Log.d("spinnerFrom", Integer.toString(spinnerFrom.getSelectedItemPosition()));
        Log.d("spinnerFrom", "help me");
    }

    public interface OnFragmentInteractionListener {
    }
}