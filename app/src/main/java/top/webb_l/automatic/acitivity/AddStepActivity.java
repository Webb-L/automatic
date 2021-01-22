package top.webb_l.automatic.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AddStepAdapter;
import top.webb_l.automatic.adapter.PackageNameAdapter;

public class AddStepActivity extends AppCompatActivity {

    private Button addStep;
    private CoordinatorLayout addStepBackdrop;
    private ExtendedFloatingActionButton saveStep;
    private static int position;

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            AddStepActivity.position = msg.what;
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_step);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();

    }

    private void initView() {
        setTitle("添加步骤");

        RecyclerView recyclerView = findViewById(R.id.rv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddStepAdapter adapter = new AddStepAdapter();
        recyclerView.setAdapter(adapter);

        addStep = findViewById(R.id.add_step);
        saveStep = findViewById(R.id.save_step);
        addStepBackdrop = findViewById(R.id.add_step_backdrop);
        LinearLayout backdropBackground = findViewById(R.id.backdrop_background);
        backdropBackground.setOnClickListener(v -> {
            addStepBackdrop.setVisibility(View.VISIBLE);
        });
        View addStepBackground = findViewById(R.id.add_step_background);
        addStepBackground.setOnClickListener(v -> {
            addStepBackdrop.setVisibility(View.GONE);
        });

        addStep.setOnClickListener(v -> {
            addStepBackdrop.setVisibility(View.VISIBLE);
        });

        saveStep.setOnClickListener(v -> {
            adapter.addStep();
            adapter.notifyDataSetChanged();
            addStepBackdrop.setVisibility(View.GONE);
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}