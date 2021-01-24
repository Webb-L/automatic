package top.webb_l.automatic.acitivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AutoMaticAdapter;

public class Main2Activity extends AppCompatActivity {

    private RecyclerView rvData;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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
        AutoMaticAdapter autoMaticAdapter = new AutoMaticAdapter();
        rvData.setAdapter(autoMaticAdapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rvData = findViewById(R.id.rv_data);
        fabAdd = findViewById(R.id.fab_add);
    }
}