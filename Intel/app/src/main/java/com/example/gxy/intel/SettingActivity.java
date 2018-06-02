package com.example.gxy.intel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView tv_user_name;
    private Callback update_user_callback;
    private Callback update_phone_number_callback;
    private Handler handler;
    private Callback verify_callback;
    private Boolean btn_old_verification_code_state;
    private Boolean btn_new_verification_code_state;
    private Button btn_old_verification_code;
    private Button btn_new_verification_code;
    private int time_old;
    private int time_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
        drawer.setDefaultFocusHighlightEnabled(false);

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
        handler = new Handler();
        initUpdateUserCallBack();
        initVerifyCallBack();
        initUpdatePhoneNumberCallBack();
        btn_old_verification_code_state = true;
        btn_old_verification_code = findViewById(R.id.btn_old_verification_code);
        btn_new_verification_code_state = true;
        btn_new_verification_code = findViewById(R.id.btn_new_verification_code);
        time_old = 60;
        time_new = 60;
        render();
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

    public void updateUserName(View view) {
        final String UPDATE_USER_NAME = "http://123.56.28.84:8080/update_user_name";
        HashMap<String, String> params = new HashMap<>();
        SharedPreferences sp = getSharedPreferences("intel", Context.MODE_PRIVATE);
        params.put("token", sp.getString("token", ""));
        EditText et_username = findViewById(R.id.et_username);
        params.put("new_user_name", et_username.getText().toString());
        async_http_post(UPDATE_USER_NAME, params, update_user_callback);
    }

    private void initVerifyCallBack() {
        verify_callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        };
    }

    private void initUpdateUserCallBack() {
        update_user_callback = new Callback() {
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
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.has("errno") && json.getInt("errno") == 0) {
                        SharedPreferences sp = getSharedPreferences("intel", Context.MODE_PRIVATE);
                        sp.edit().putString("user_name", json.getString("user_name")).apply();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    //TODO None
                }

            }
        };
    }

    private void initUpdatePhoneNumberCallBack() {
        update_phone_number_callback = new Callback() {
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
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.has("errno") && json.getInt("errno") == 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    else{
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "手机号已经被注册", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    //TODO None
                }

            }
        };
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

    public void get_old_verification_code(View v) {

        final String GET_VERIFICATION_CODE_URL = "http://123.56.28.84:8080/get_verification_code";
        EditText et_old_phone_number = findViewById(R.id.et_old_phone_number);

        String phone_number = et_old_phone_number.getText().toString().trim();
        if (phone_number.trim().length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (btn_old_verification_code_state) {
            btn_old_verification_code_state = false;
            time_old = 60;
            btn_old_verification_code.setBackgroundResource(R.drawable.ic_sent_verification_code);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (time_old < 0) {
                        btn_old_verification_code.setText(R.string.get_verification_code);
                        btn_old_verification_code.setBackgroundResource(R.drawable.ic_send_verification_code);
                        btn_old_verification_code_state = true;
                        return;
                    }
                    btn_old_verification_code.setText(String.valueOf(time_old));
                    handler.postDelayed(this, 1000);
                    time_old--;
                }

            };
            handler.removeCallbacks(runnable);
            handler.post(runnable);
            HashMap<String, String> params = new HashMap<>();
            params.put("phone_number", phone_number);
            async_http_post(GET_VERIFICATION_CODE_URL, params, verify_callback);
        }
    }

    public void get_new_verification_code(View v) {

        final String GET_VERIFICATION_CODE_URL = "http://123.56.28.84:8080/get_verification_code";
        EditText et_new_phone_number = findViewById(R.id.et_new_phone_number);

        String phone_number = et_new_phone_number.getText().toString().trim();
        if (phone_number.trim().length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (btn_new_verification_code_state) {
            btn_new_verification_code_state = false;
            time_new = 60;
            btn_new_verification_code.setBackgroundResource(R.drawable.ic_sent_verification_code);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (time_new < 0) {
                        btn_new_verification_code.setText(R.string.get_verification_code);
                        btn_new_verification_code.setBackgroundResource(R.drawable.ic_send_verification_code);
                        btn_new_verification_code_state = true;
                        return;
                    }
                    btn_new_verification_code.setText(String.valueOf(time_new));
                    handler.postDelayed(this, 1000);
                    time_new--;
                }

            };
            handler.removeCallbacks(runnable);
            handler.post(runnable);
            HashMap<String, String> params = new HashMap<>();
            params.put("phone_number", phone_number);
            async_http_post(GET_VERIFICATION_CODE_URL, params, verify_callback);
        }
    }

    public void updatePhoneNumber(View view) {

        final String UPDATE_PHONE_NUMBER_URL = "http://123.56.28.84:8080/update_phone_number";

        EditText et_old_phone_number = findViewById(R.id.et_old_phone_number);
        EditText et_new_phone_number = findViewById(R.id.et_new_phone_number);
        EditText et_old_verification_code = findViewById(R.id.et_old_verification_code);
        EditText et_new_verification_code = findViewById(R.id.et_new_verification_code);

        HashMap<String, String> params = new HashMap<>();
        params.put("old_phone_number", et_old_phone_number.getText().toString());
        params.put("new_phone_number", et_new_phone_number.getText().toString());
        params.put("old_verification_code", et_old_verification_code.getText().toString());
        params.put("new_verification_code", et_new_verification_code.getText().toString());
        async_http_post(UPDATE_PHONE_NUMBER_URL, params, update_phone_number_callback);

    }

    public void logout(View view) {

        final String LOGOUT_URL = "http://123.56.28.84:8080/logout";
        SharedPreferences sp = getSharedPreferences("intel", Context.MODE_PRIVATE);
        sp.edit().putString("token", "").apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}