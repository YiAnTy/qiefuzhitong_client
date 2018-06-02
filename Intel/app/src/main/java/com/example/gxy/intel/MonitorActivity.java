package com.example.gxy.intel;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxy.intel.adapter.RVAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MonitorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    RecyclerView rv_monitor;
    ArrayList<JSONObject> mData;
    TextView tv_user_name;
    Callback get_monitors_callback;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        handler = new Handler();
        rv_monitor = findViewById(R.id.rv_monitor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "呼叫客服", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        tv_user_name = navigationView.inflateHeaderView(R.layout.nav_header_main).findViewById(R.id.tv_user_name);
        navigationView.setNavigationItemSelectedListener(this);
        render();
        initGetMonitorsCallback();
        HashMap<String, String> params = new HashMap<>();
        params.put("token", getSharedPreferences("intel", Context.MODE_PRIVATE).getString("token", ""));
        async_http_post("http://123.56.28.84:8080/get_monitors", params, get_monitors_callback);
    }

    public void render(){
        tv_user_name.setText(getSharedPreferences("intel", Context.MODE_PRIVATE).getString("user_name", "Unknown"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_diagnose) {
            finish();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_monitor) {
            finish();
            Intent intent = new Intent(getApplicationContext(), MonitorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            finish();
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void add_monitor(View view) {
        Intent intent = new Intent(getApplicationContext(), AddMonitorActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void async_http_post(String url, HashMap<String, String> params, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = builder.build();
        final Request request = new Request
                .Builder()
                .post(requestBody)
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void initGetMonitorsCallback() {
        get_monitors_callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Ignore
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray jsonArr = new JSONArray(response.body().string());
                    mData = new ArrayList<>(jsonArr.length());
                    for(int i = 0; i < jsonArr.length(); i++) {
                        mData.add(i, jsonArr.getJSONObject(i));
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            rv_monitor.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                            RVAdapter rvAdapter = new RVAdapter(getApplicationContext(), mData);
                            rv_monitor.setAdapter(rvAdapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

    }
}
