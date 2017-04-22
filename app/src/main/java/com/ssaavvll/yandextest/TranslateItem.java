package com.ssaavvll.yandextest;

/**
 * Created by SSAAV on 12.04.2017.
 */
/* class for hold history item */
    public class TranslateItem {
        private long id;
        private String textFrom;
        private String textTo;
        private String langFrom;
        private String langTo;
        private int fav;

        TranslateItem(){}
        TranslateItem(long id, String textFrom, String textTo, String langFrom, String langTo, int fav) {
            this.id = id;
            this.textFrom = textFrom;
            this.textTo = textTo;
            this.langFrom = langFrom;
            this.langTo = langTo;
            this.fav = fav;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public String getTextFrom() {
            return textFrom;
        }

        public String getTextTo() {
            return textTo;
        }

        public String getLangFrom() {
            return langFrom;
        }

        public String getLangTo() {
            return langTo;
        }

        public boolean getFav() {
            return (fav == 0) ? false : true;
        }
    }
