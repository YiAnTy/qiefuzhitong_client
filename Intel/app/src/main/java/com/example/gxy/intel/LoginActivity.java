package com.example.gxy.intel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private Handler handler;
    private Callback verify_callback;
    private Callback login_callback;
    private Callback token_login_callback;

    // UI references.
    private Button btn_verification_code;
    private Boolean btn_verification_code_state;
    private EditText et_phone_number;
    private EditText et_verification_code;
    private Integer time;

    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_verification_code = findViewById(R.id.btn_verification_code);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_verification_code = findViewById(R.id.et_verification_code);
        btn_verification_code_state = true;
        handler = new Handler();

        init_verify_callback();
        init_login_callback();
        init_token_login_callback();
        try_to_skip_login();

    }

    private void try_to_skip_login() {
        final String TOKEN_LOGIN_URL = "http://123.56.28.84:8080/check_login_state";
        SharedPreferences sp = getSharedPreferences("intel", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        if (token != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("token", token);
            async_http_post(TOKEN_LOGIN_URL, params, token_login_callback);
        }
    }

    private void init_verify_callback() {
        verify_callback = new Callback() {
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
                    s = json.getString("verification_code");
                    if (json.has("errno") && json.getInt("errno") == 0) {
                        //TODO None
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "服务器内部错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "服务器内部错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };
    }

    private void init_login_callback() {
        login_callback = new Callback() {
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
                        sp.edit().putString("token", json.getString("token")).apply();
                        sp.edit().putString("user_name", json.getString("user_name")).apply();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "服务器内部错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };
    }

    private void init_token_login_callback() {
        token_login_callback = new Callback() {
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


    public void login(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    1);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        final String LOGIN_URL = "http://123.56.28.84:8080/login";

        String phone_number = et_phone_number.getText().toString().trim();
        if (phone_number.length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        String verification_code = et_verification_code.getText().toString().trim();
        if (verification_code.length() != 6) {
            Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("serial_number", android.os.Build.getSerial());
        params.put("phone_number", phone_number);
        params.put("verification_code", verification_code);
        async_http_post(LOGIN_URL, params, login_callback);
    }

    public void get_verification_code(View v) {

        final String GET_VERIFICATION_CODE_URL = "http://123.56.28.84:8080/get_verification_code";

        String phone_number = et_phone_number.getText().toString().trim();
        if (phone_number.trim().length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (btn_verification_code_state) {
            btn_verification_code_state = false;
            time = 60;
            btn_verification_code.setBackgroundResource(R.drawable.ic_sent_verification_code);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (time < 0) {
                        btn_verification_code.setText(R.string.get_verification_code);
                        btn_verification_code.setBackgroundResource(R.drawable.ic_send_verification_code);
                        btn_verification_code_state = true;
                        return;
                    }
                    btn_verification_code.setText(String.valueOf(time));
                    handler.postDelayed(this, 1000);
                    time--;
                }

            };
            handler.removeCallbacks(runnable);
            handler.post(runnable);
            HashMap<String, String> params = new HashMap<>();
            params.put("phone_number", phone_number);
            async_http_post(GET_VERIFICATION_CODE_URL, params, verify_callback);
        }
    }
}

