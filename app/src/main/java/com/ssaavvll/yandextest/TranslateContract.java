package com.ssaavvll.yandextest;

import android.provider.BaseColumns;

/**
 * Created by SSAAV on 10.04.2017.
 */

/* defining struct of table */
public final class TranslateContract {
    private TranslateContract(){};

    public static class History implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_TEXT_FROM = "text_from";
        public static final String COLUMN_NAME_TEXT_TO = "text_to";
        public static final String COLUMN_NAME_LANG_FROM = "lang_from";
        public static final String COLUMN_NAME_LANG_TO = "lang_to";
        public static final String COLUMN_NAME_FAVOURITE = "favourite";
        public static final String[] allColumns = {_ID,
                COLUMN_NAME_TEXT_FROM,
                COLUMN_NAME_TEXT_TO,
                COLUMN_NAME_LANG_TO,
                COLUMN_NAME_LANG_FROM,
                COLUMN_NAME_FAVOURITE
        };
    }
}
