package top.webb_l.automatic.acitivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.PackageNameAdapter;
import top.webb_l.automatic.data.AppInfo;

public class SelectPackageActivity extends AppCompatActivity {
    private boolean status = false;
    private RecyclerView activityList;
    private RecyclerView appInfos;
    private CollapsingToolbarLayout toolBarLayout;
    private List<AppInfo> packageInfoList;
    private LinearLayout loading;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            loading.setVisibility(View.GONE);
            appInfos.setLayoutManager(new LinearLayoutManager(SelectPackageActivity.this));
            PackageNameAdapter packageNameAdapter = new PackageNameAdapter(SelectPackageActivity.this);
            packageInfoList = getPackageInfoList();
            packageNameAdapter.setApps(packageInfoList);
            appInfos.setAdapter(packageNameAdapter);
            toolBarLayout.setTitle(getResources().getString(R.string.select_package) + "(" + packageInfoList.size() + ")");
            packageNameAdapter.setOnItemClickListener(packageInfo -> {
                status = true;
                ArrayList<AppInfo> activities = getActivitiesByApplication(packageInfo);
                toolBarLayout.setTitle(getResources().getString(R.string.select_activity) + "(" + activities.size() + ")");
                showActivityList(activities);
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_package);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        appInfos = findViewById(R.id.app_list);
        activityList = findViewById(R.id.activity_list);

        loading = findViewById(R.id.loading);
        loading.setOnTouchListener((v, event) -> false);

        mHandler.sendEmptyMessage(0);
    }

    /**
     * 某个应用给点击后就展示这个应用activity列表。
     *
     * @param activities activity数据 ArrayList<AppInfo>
     */
    private void showActivityList(ArrayList<AppInfo> activities) {
        appInfos.setVisibility(View.GONE);
        activityList.setVisibility(View.VISIBLE);
        activityList.setLayoutManager(new LinearLayoutManager(this));
        PackageNameAdapter packageNameAdapter = new PackageNameAdapter(this);
        packageNameAdapter.setApps(activities);
        activityList.setAdapter(packageNameAdapter);
        packageNameAdapter.setOnItemClickListener(appInfo -> {
            Intent intent = new Intent(this, AddStepActivity.class);
            intent.putExtra("packageName", appInfo.getApplicationInfo().packageName);
            intent.putExtra("activity", appInfo.getPackageName());
            startActivity(intent);
        });
    }

    /**
     * 获取某个软件全部activity
     *
     * @param appInfo 软件信息 AppInfo
     * @return 返回全部activity ArrayList<AppInfo>
     */
    private ArrayList<AppInfo> getActivitiesByApplication(AppInfo appInfo) {
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        for (ActivityInfo activity : appInfo.getApplicationInfo().activities) {
            appInfos.add(
                    new AppInfo(
                            activity.loadLabel(getPackageManager()).toString(),
                            activity.name.replace(appInfo.getPackageName(), ""),
                            activity.loadIcon(getPackageManager()),
                            appInfo.getApplicationInfo()
                    ));
        }
        return appInfos;
    }


    /**
     * 获取用户应用列表
     *
     * @return 应用列表
     */
    private List<AppInfo> getPackageInfoList() {
        List<AppInfo> packageInfos = new ArrayList<>();
        try {
            List<PackageInfo> apps = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
            for (PackageInfo app : apps) {
                ApplicationInfo info = app.applicationInfo;
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    packageInfos.add(new AppInfo(info.loadLabel(getPackageManager()).toString(),
                            app.packageName,
                            info.loadIcon(getPackageManager()),
                            app));
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return packageInfos;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return showPrevious();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 返回上一个列表
     *
     * @return 是否可以结束页面
     */
    private boolean showPrevious() {
        if (status) {
            activityList.setVisibility(View.GONE);
            appInfos.setVisibility(View.VISIBLE);
            toolBarLayout.setTitle(getResources().getString(R.string.select_package) + "(" + packageInfoList.size() + ")");
            status = false;
            return false;
        }
        finish();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return showPrevious();
        }
        return super.onKeyDown(keyCode, event);
    }
}