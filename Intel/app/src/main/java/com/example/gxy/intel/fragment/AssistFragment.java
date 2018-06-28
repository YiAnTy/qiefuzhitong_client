package com.example.gxy.intel.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxy.intel.MainActivity;
import com.example.gxy.intel.R;

import org.json.JSONArray;
import org.json.JSONException;

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


public class AssistFragment extends android.support.v4.app.Fragment {

    ArrayList<View> item_view_list = new ArrayList<>();
    Callback get_questions_callback;
    Handler handler;
    LayoutInflater inflater;
    ViewGroup container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        this.container = container;
        handler = new Handler();

        View assist = inflater.inflate(R.layout.assist, container, false);

        initGetQuestionsCallback();

        assist.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit(view);
            }
        });

        return assist;
    }

    public void submit(View view) {

        if (((Button)view).getText().equals("已完成")) {
            return;
        }
        int yes_counter = 0;
        int no_counter = 0;

        for (View item_view : item_view_list) {
            RadioGroup radioGroup = item_view.findViewById(R.id.assist_item_radio_group);
            if (radioGroup.getCheckedRadioButtonId() == R.id.choice_yes) {
                yes_counter++;
            }
            else {
                no_counter++;
            }
            item_view.findViewById(R.id.choice_yes).setClickable(false);
            item_view.findViewById(R.id.choice_no).setClickable(false);
        }

        ((Button)view).setText("已完成");

        if (yes_counter < no_counter) {
            Toast.makeText(getContext(), "根据问卷结果,本次识别结果不可靠,请重新识别", Toast.LENGTH_SHORT).show();
        }
    }

    public void refresh() {
        final String GET_QUESTIONS_URL = "http://123.56.28.84:8080/get_questions";
        HashMap<String, String> params = new HashMap<>();
        if (MainActivity.result_title == null) {
           return;
        }
        params.put("desease_name", MainActivity.result_title);
        async_http_post(GET_QUESTIONS_URL, params, get_questions_callback);
    }

    public void initGetQuestionsCallback() {
        get_questions_callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Ignore
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        JSONArray jsonArr = null;
                        try {
                            jsonArr = new JSONArray(response.body().string());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                        LinearLayout ll_assist_item_list = null;
                        try {
                            ll_assist_item_list = getView().findViewById(R.id.ll_assist_item_list);

                            for (View view : item_view_list) {
                                ll_assist_item_list.removeView(view);
                            }
                            item_view_list.clear();
                        }
                        catch (NullPointerException e) {
                            e.printStackTrace();
                            return;
                        }

                        if (jsonArr != null) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                View view = inflater.inflate(R.layout.assist_item, container, false);
                                ((Button)getView().findViewById(R.id.btn_submit)).setText(R.string.submit);
                                item_view_list.add(view);
                                TextView tv_assist_item_title = view.findViewById(R.id.assist_item_title);
                                try {
                                    tv_assist_item_title.setText(jsonArr.getJSONObject(i).getString("question"));
                                    ll_assist_item_list.addView(view);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

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

}
