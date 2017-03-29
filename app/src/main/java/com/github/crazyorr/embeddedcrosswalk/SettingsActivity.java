package com.github.crazyorr.embeddedcrosswalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.github.crazyorr.embeddedcrosswalk.Const.PREFERENCES_NAME;
import static com.github.crazyorr.embeddedcrosswalk.Const.PREFERENCE_KEY_URL;

// TODO 修改/删除URL
public class SettingsActivity extends AppCompatActivity implements AddUrlDialogFragment.OnAddUrlItemListener {

    public static final String INTENT_EXTRA_URL = "INTENT_EXTRA_URL";
    public static final String PREFERENCE_KEY_URL_LIST = "PREFERENCE_KEY_URL_LIST";
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private ListView lvUrls;
    private List<UrlItem> urlItemList;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String json = sharedPreferences.getString(PREFERENCE_KEY_URL_LIST, null);
        if (TextUtils.isEmpty(json)) {
            urlItemList = new ArrayList<>();
//            urlItemList.add(new UrlItem("http://localhost:9001/", "test server"));
            urlItemList.add(new UrlItem("file:///android_asset/app/index.html", "test local"));
        } else {
            Type type = new TypeToken<List<UrlItem>>() {
            }.getType();
            urlItemList = gson.fromJson(json, type);
        }

        lvUrls = (ListView) findViewById(R.id.lv_urls);
        lvUrls.setAdapter(new UrlAdapter(urlItemList));
        lvUrls.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        String url = sharedPreferences.getString(PREFERENCE_KEY_URL, null);
        int checkedPosition = ListView.INVALID_POSITION;
        for (int i = 0; i < urlItemList.size(); i++) {
            if (TextUtils.equals(url, urlItemList.get(i).url)) {
                checkedPosition = i;
                break;
            }
        }
        if (checkedPosition != ListView.INVALID_POSITION) {
            lvUrls.setItemChecked(checkedPosition, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(PREFERENCE_KEY_URL_LIST, gson.toJson(urlItemList))
                .putString(PREFERENCE_KEY_URL, getCheckedUrl())
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                break;
            case R.id.action_add:
                new AddUrlDialogFragment().show(getSupportFragmentManager(), "dialog");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void finish() {
        if (lvUrls.getCheckedItemCount() == 1) {
            Intent data = new Intent();
            data.putExtra(INTENT_EXTRA_URL, getCheckedUrl());
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    @Override
    public void onAddUrlItem(String url, String description) {
        boolean exists = false;
        for (UrlItem urlItem : urlItemList) {
            if (TextUtils.equals(url, urlItem.url)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            Toast.makeText(this, R.string.url_already_exists, Toast.LENGTH_SHORT).show();
        } else {
            urlItemList.add(new UrlItem(url, description));
        }
    }

    private String getCheckedUrl() {
        int checkedItemPosition = lvUrls.getCheckedItemPosition();
        String checkedUrl;
        if (checkedItemPosition != ListView.INVALID_POSITION) {
            checkedUrl = urlItemList.get(checkedItemPosition).url;
        } else {
            checkedUrl = null;
        }
        return checkedUrl;
    }

    static class UrlAdapter extends BaseAdapter {

        private List<UrlItem> list;

        public UrlAdapter(List<UrlItem> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            int count;
            if (list == null) {
                count = 0;
            } else {
                count = list.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_url_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvDescription = (TextView) convertView.findViewById(
                        R.id.tv_description);
                viewHolder.tvUrl = (TextView) convertView.findViewById(
                        R.id.tv_url);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            UrlItem urlItem = list.get(position);
            viewHolder.tvDescription.setText(urlItem.description);
            viewHolder.tvUrl.setText(urlItem.url);
            return convertView;
        }

        class ViewHolder {
            TextView tvDescription;
            TextView tvUrl;
        }
    }

    class UrlItem {
        String url;
        String description;

        public UrlItem(String url, String description) {
            this.url = url;
            this.description = description;
        }
    }
}
