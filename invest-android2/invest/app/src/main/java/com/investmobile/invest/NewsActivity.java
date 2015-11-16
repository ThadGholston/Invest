package com.investmobile.invest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class NewsActivity extends AppCompatActivity {
    Intent intent;
    String title;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        title = intent.getStringExtra("title");
        link = intent.getStringExtra("link");
        setContentView(R.layout.activity_news);
        setTitle("Invest");
        TextView textView = (TextView) findViewById(R.id.newsTitle);
        textView.setLines(1);
        textView.setText(title);
        WebView webview = new WebView(this);
        webview.setWebViewClient(new MyWebViewClient());
        setContentView(webview);
        webview.loadUrl(link);
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sendIntent = Intent.createChooser(sendIntent, "Send stock news via");
                startActivity(sendIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
