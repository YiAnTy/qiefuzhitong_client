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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MonitorDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView tv_user_name;
    Callback del_monitor_callback;
    Handler handler;
    TextView tv_detail_title;
    TextView tv_detail_description;
    TextView tv_detail_status;
    TextView tv_detail_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_monitor_detail);
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
        initDelMonitorCallback();
    }

    public void render(){
        tv_user_name.setText(getSharedPreferences("intel", Context.MODE_PRIVATE).getString("user_name", "Unknown"));
        tv_detail_title = findViewById(R.id.tv_detail_name);
        tv_detail_description = findViewById(R.id.tv_detail_description);
        tv_detail_status = findViewById(R.id.tv_detail_status);
        tv_detail_time = findViewById(R.id.tv_detail_time);
        tv_detail_description.setText(getIntent().getStringExtra("monitor_description"));
        tv_detail_title.setText(getIntent().getStringExtra("monitor_name"));
        tv_detail_time.setText(getIntent().getStringExtra("time"));
        if (getIntent().getStringExtra("trend").equals("up")) {
            tv_detail_status.setText("您的病情正在好转,注意观察");
        } else if (getIntent().getStringExtra("trend").equals("down")) {
            tv_detail_status.setText("您的病情正在恶化,建议就医");
        } else {
            tv_detail_status.setText("请更新您的病情");
        }

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

    public void del_monitor(View view) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", getSharedPreferences("intel", Context.MODE_PRIVATE).getString("token", ""));
        params.put("monitor_name", getIntent().getStringExtra("monitor_name"));
        async_http_post("http://123.56.28.84:8080/del_monitor", params, del_monitor_callback);
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

    public void initDelMonitorCallback() {
        del_monitor_callback = new Callback() {
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };

    }

    public void update_monitor(View view) {
        Toast.makeText(this, "Unfinished", Toast.LENGTH_SHORT).show();
    }
}
