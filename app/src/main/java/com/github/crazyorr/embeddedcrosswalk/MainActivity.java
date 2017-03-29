package com.github.crazyorr.embeddedcrosswalk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import static com.github.crazyorr.embeddedcrosswalk.Const.PREFERENCES_NAME;
import static com.github.crazyorr.embeddedcrosswalk.Const.PREFERENCE_KEY_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String INTERFACE_NAME_EXAMPLE = "example";
    private static final int REQUEST_CODE_URL = 1;

    private XWalkView mXWalkView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mXWalkView = (XWalkView) findViewById(R.id.webview);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        mXWalkView.getSettings().setJavaScriptEnabled(true);
        mXWalkView.addJavascriptInterface(new MyJavaScriptInterface(), INTERFACE_NAME_EXAMPLE);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String url = sharedPreferences.getString(PREFERENCE_KEY_URL, null);
        if (TextUtils.isEmpty(url)) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_URL);
        } else {
            mXWalkView.loadUrl(url);
        }

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android2Js("Hello from Android");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_URL);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_URL:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String url = data.getStringExtra(SettingsActivity.INTENT_EXTRA_URL);
                        mXWalkView.loadUrl(url);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mXWalkView.evaluateJavascript("onBackPressed()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(final String value) {
                if (!Boolean.valueOf(value)) {
                    MainActivity.super.onBackPressed();
                }

            }
        });
    }

    private void android2Js(final String content) {
        mXWalkView.evaluateJavascript(String.format("android2Js('%s')", content), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(final String value) {
                // TODO Question: the evaluated result String is quoted (surrounded by "")
                Log.i(TAG, "android2Js returns: " + value);
            }
        });
    }

    class MyJavaScriptInterface {

        @JavascriptInterface
        public String js2Android(final String content) {
            Log.i(TAG, "js2Android called with: " + content);
            return "Got it from Android";
        }
    }
}
