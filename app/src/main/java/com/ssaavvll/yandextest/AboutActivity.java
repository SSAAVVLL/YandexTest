package com.ssaavvll.yandextest;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    final Intent intentVK = new Intent(Intent.ACTION_VIEW, Uri.parse("http://vk.com/savl72rus"));
    final Intent intentMail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
            .putExtra(Intent.EXTRA_EMAIL, "ssaavvll.utmn@gmail.com")
            .putExtra(Intent.EXTRA_SUBJECT, "YandexTest");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        /* setting actionBar */
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView copyrights = (TextView) findViewById(R.id.copyright);
        copyrights.setMovementMethod(LinkMovementMethod.getInstance());

        /* textViews */
        TextView textVK = (TextView) findViewById(R.id.vk_page);
        TextView textMail = (TextView) findViewById(R.id.send_mail);

        /* set onClick Listeners */
        textVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentVK);
            }
        });
        textMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentMail);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
