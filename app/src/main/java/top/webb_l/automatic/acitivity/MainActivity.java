package top.webb_l.automatic.acitivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AutoMaticAdapter;
import top.webb_l.automatic.data.ScriptInfo;

public class MainActivity extends AppCompatActivity {
    private static RecyclerView rvData;
    private FloatingActionButton fabAdd;
    private static AutoMaticAdapter autoMaticAdapter;
    private static LinearLayout rootNotData;

    public static ArrayList<ScriptInfo> scripts = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    public static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    rootNotData.setVisibility(View.GONE);
                    autoMaticAdapter.notifyDataSetChanged();
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
        initView();
        initClicks();
        initRecyclerViewItems();
    }

    private void initClicks() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectPackageActivity.class));
        });
    }

    private void initRecyclerViewItems() {
        rvData.setLayoutManager(new LinearLayoutManager(this));
        autoMaticAdapter = new AutoMaticAdapter();
        rvData.setAdapter(autoMaticAdapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rootNotData = findViewById(R.id.root_not_data);
        rvData = findViewById(R.id.rv_data);
        fabAdd = findViewById(R.id.fab_add);
    }
}