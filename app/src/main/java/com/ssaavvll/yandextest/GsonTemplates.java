package com.ssaavvll.yandextest;

import java.util.HashMap;
import java.util.List;

/**
 * Created by SSAAV on 08.04.2017.
 */

/* templates for request from yandexTranslate API*/
public class GsonTemplates {
    public class ResponseLangs {
        String[] dirs;
        HashMap<String, String> langs;
    }

    public class ResponseTranslate {
        int code;
        String lang;
        List<String> text;
    }
}
