package top.webb_l.automatic.acitivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AutoMaticAdapter;
import top.webb_l.automatic.data.ScriptInfo;
import top.webb_l.automatic.model.Scripts;

public class MainActivity extends AppCompatActivity {
    private static RecyclerView rvData;
    private FloatingActionButton fabAdd;
    private static AutoMaticAdapter autoMaticAdapter;
    private static LinearLayout rootNotData;

    public static ArrayList<ScriptInfo> scripts = new ArrayList<>();
    private static CoordinatorLayout root;


    @SuppressLint("HandlerLeak")
    public static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    if (scripts.size() > 0) {
                        rootNotData.setVisibility(View.GONE);
                    } else {
                        rootNotData.setVisibility(View.INVISIBLE);
                    }
                    autoMaticAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Snackbar.make(root, "参数错误！", Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initClicks();
        initRecyclerViewItems();
    }

    private void initData() {
        Connector.getDatabase();
        for (Scripts script : LitePal.order("id desc").limit(5).find(Scripts.class)) {
            Drawable icon = null;
            try {
                icon = getPackageManager().getApplicationIcon(script.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            ScriptInfo scriptInfo = new ScriptInfo(script.getTitle(), script.getDescription(), script.getPackageName(), script.getActivity(), icon, null);
            scriptInfo.setId(script.getId());
            scripts.add(scriptInfo);
        }
    }

    private void initClicks() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectPackageActivity.class));
        });
    }

    private void initRecyclerViewItems() {
        rvData.setLayoutManager(new LinearLayoutManager(this));
        autoMaticAdapter = new AutoMaticAdapter(getApplicationContext());
        rvData.setAdapter(autoMaticAdapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rootNotData = findViewById(R.id.root_not_data);
        root = findViewById(R.id.root);
        rvData = findViewById(R.id.rv_data);
        fabAdd = findViewById(R.id.fab_add);
    }

}