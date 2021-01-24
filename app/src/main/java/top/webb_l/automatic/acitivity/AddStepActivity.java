package top.webb_l.automatic.acitivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AddStepAdapter;
import top.webb_l.automatic.data.StepInfo;

public class AddStepActivity extends AppCompatActivity {

    private Button addStep;
    private CoordinatorLayout addStepBackdrop;
    private ExtendedFloatingActionButton saveStep;
    private static LinearLayout rootNotData;
    private ArrayList<StepInfo> stepInfos = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    AddStepActivity.rootNotData.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }

        }

    };
    private boolean searchType;
    private int checkEvent = -1,
            searchControl = -1;

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
        Intent intent = getIntent();
//        Toast.makeText(this, intent.getStringExtra("packageName"), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, intent.getStringExtra("activity"), Toast.LENGTH_SHORT).show();

    }

    private void initView() {
        setTitle("添加步骤");
        rootNotData = findViewById(R.id.root_not_data);

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

        CoordinatorLayout root = findViewById(R.id.root);
        // 判断搜索类型
        ChipGroup chipSearch = findViewById(R.id.chip_search);
        chipSearch.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId <= 0) {
                Snackbar.make(root, "请选择搜索类型！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Chip check = findViewById(checkedId);
            searchType = check.getText() == getResources().getString(R.string.chip_id);
        });
        // 判断控件
        ChipGroup chipControl = findViewById(R.id.chip_control);
        chipControl.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId <= 0) {
                Snackbar.make(root, "请选择控件！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Chip check = findViewById(checkedId);
            if (getResources().getString(R.string.chip_button) == check.getText()) {
                searchControl = 1;
            }
            if (getResources().getString(R.string.chip_image) == check.getText()) {
                searchControl = 2;
            }
            if (getResources().getString(R.string.chip_text) == check.getText()) {
                searchControl = 3;
            }
            if (getResources().getString(R.string.chip_checkbox) == check.getText()) {
                searchControl = 4;
            }
            if (getResources().getString(R.string.chip_radiobutton) == check.getText()) {
                searchControl = 5;
            }
            if (getResources().getString(R.string.chip_editText) == check.getText()) {
                searchControl = 6;
            }
            Log.d("TAG", "initView: "+searchControl);
        });
        ChipGroup chipEvent = findViewById(R.id.chip_event);
        chipEvent.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId <= 0) {
                Snackbar.make(root, "请选择事件！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Chip check = findViewById(checkedId);
            if (getResources().getString(R.string.chip_click) == check.getText()) {
                checkEvent = 0;
            }
            if (getResources().getString(R.string.chip_press) == check.getText()) {
                checkEvent = 1;
            }
            if (getResources().getString(R.string.chip_copy) == check.getText()) {
                checkEvent = 2;
            }
            if (getResources().getString(R.string.chip_paste) == check.getText()) {
                checkEvent = 3;
            }
        });

        EditText searchContent = findViewById(R.id.search_content);
        saveStep.setOnClickListener(v -> {
            String search = searchContent.getText().toString().trim();
            if (TextUtils.isEmpty(search) ||
                    searchControl < 0
                    || searchControl > 6 ||
                    checkEvent < 0
                    || checkEvent > 3) {
                Snackbar.make(root, "请确保所有内容都填写完成！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            stepInfos.add(new StepInfo(searchType, search, checkEvent, searchControl));
            adapter.addStep(stepInfos);
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