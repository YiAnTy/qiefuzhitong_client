package com.example.gxy.intel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gxy.intel.fragment.AssistFragment;
import com.example.gxy.intel.fragment.DiagnoseFragment;
import com.example.gxy.intel.fragment.ResultFragment;
import com.example.gxy.intel.fragment.TreatFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<android.support.v4.app.Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TextView tv_user_name;
    public static String result_title = null;
    public static String result_content = null;
    public static String treat_content = null;
    public static Bitmap bitmap = null;
    public static boolean questions = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        init_data();
        register_events();
        render();
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    private void register_events() {

        TextView diagnose = findViewById(R.id.diagnose);
        TextView assist = findViewById(R.id.assist);
        TextView result = findViewById(R.id.result);
        TextView treat = findViewById(R.id.treat);

        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                selectTab(view);
            }
        };

        diagnose.setOnClickListener(clickListener);
        assist.setOnClickListener(clickListener);
        result.setOnClickListener(clickListener);
        treat.setOnClickListener(clickListener);
    }

    private void selectTab(View view){

        ImageView diagnose_img = findViewById(R.id.diagnose_img);
        ImageView result_img = findViewById(R.id.result_img);
        ImageView treat_img = findViewById(R.id.treat_img);
        ImageView assist_img = findViewById(R.id.assist_img);

        switch (view.getId()) {
            case R.id.diagnose:
                diagnose_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                assist_img.setBackgroundColor(Color.alpha((0)));
                treat_img.setBackgroundColor(Color.alpha(0));
                result_img.setBackgroundColor(Color.alpha(0));
                mViewPager.setCurrentItem(0);
                break;
            case R.id.assist:
                assist_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                diagnose_img.setBackgroundColor(Color.alpha(0));
                treat_img.setBackgroundColor(Color.alpha(0));
                result_img.setBackgroundColor(Color.alpha(0));
                mViewPager.setCurrentItem(1);
                break;
            case R.id.result:
                diagnose_img.setBackgroundColor(Color.alpha(0));
                assist_img.setBackgroundColor(Color.alpha((0)));
                treat_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                result_img.setBackgroundColor(Color.alpha(0));
                mViewPager.setCurrentItem(2);
                break;
            case R.id.treat:
                diagnose_img.setBackgroundColor(Color.alpha(0));
                assist_img.setBackgroundColor(Color.alpha((0)));
                treat_img.setBackgroundColor(Color.alpha(0));
                result_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                mViewPager.setCurrentItem(3);
                break;
            default:
                break;
        }
    }

    private void selectTabByPosition(int position){

        ImageView diagnose_img = findViewById(R.id.diagnose_img);
        ImageView treat_img = findViewById(R.id.treat_img);
        ImageView result_img = findViewById(R.id.result_img);
        ImageView assist_img = findViewById(R.id.assist_img);

        switch (position) {
            case 0:
                diagnose_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                treat_img.setBackgroundColor(Color.alpha(0));
                result_img.setBackgroundColor(Color.alpha(0));
                assist_img.setBackgroundColor(Color.alpha(0));
                break;
            case 1:
                diagnose_img.setBackgroundColor(Color.alpha(0));
                treat_img.setBackgroundColor(Color.alpha(0));
                result_img.setBackgroundColor(Color.alpha(0));
                assist_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                if(MainActivity.questions) {
                    ((AssistFragment)mFragments.get(1)).refresh();
                    MainActivity.questions = false;
                }
                break;
            case 2:
                diagnose_img.setBackgroundColor(Color.alpha(0));
                assist_img.setBackgroundColor(Color.alpha(0));
                treat_img.setBackgroundColor(Color.alpha(0));
                result_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                if (result_title != null) {
                    ((TextView)findViewById(R.id.tv_result_title)).setText(result_title);
                }
                if (result_content != null) {
                    ((TextView)findViewById(R.id.tv_result_content)).setText(result_content);
                }
                if (bitmap != null) {
                    ((ImageView)findViewById(R.id.iv_result_image)).setImageBitmap(bitmap);
                }
                break;
            case 3:
                diagnose_img.setBackgroundColor(Color.alpha(0));
                treat_img.setBackgroundColor(Color.parseColor("#FFFF8C"));
                result_img.setBackgroundColor(Color.alpha(0));
                if (treat_content != null) {
                    ((TextView)findViewById(R.id.tv_treat_content)).setText(treat_content);
                }
                break;
            default:
                break;
        }
    }

    private void init_data() {

        mViewPager = findViewById(R.id.viewpager);
        //将四个Fragment加入集合中
        mFragments.add(new DiagnoseFragment());
        mFragments.add(new AssistFragment());
        mFragments.add(new ResultFragment());
        mFragments.add(new TreatFragment());


        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 3) {
                    if (treat_content != null) {
                        ((TextView)findViewById(R.id.tv_treat_content)).setText(treat_content);
                    }
                }
                else if (position == 2) {
                    if (result_title != null) {
                        ((TextView)findViewById(R.id.tv_result_title)).setText(result_title);
                    }
                    if (result_content != null) {
                        ((TextView)findViewById(R.id.tv_result_content)).setText(result_content);
                    }
                    if (bitmap != null) {
                        ((ImageView)findViewById(R.id.iv_result_image)).setImageBitmap(bitmap);
                    }

                }
                else if (position == 1) {
                    if(MainActivity.questions) {
                        ((AssistFragment)mFragments.get(1)).refresh();
                        MainActivity.questions = false;
                    }

                }
            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                mViewPager.setCurrentItem(position);
                selectTabByPosition(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
