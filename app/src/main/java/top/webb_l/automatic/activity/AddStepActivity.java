package top.webb_l.automatic.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AddStepAdapter;
import top.webb_l.automatic.data.StepInfo;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

/**
 * @author Webb
 */
public class AddStepActivity extends AppCompatActivity {

    private Button addStep;
    private CoordinatorLayout addStepBackdrop;
    private ExtendedFloatingActionButton saveStep;
    private final ArrayList<StepInfo> stepInfo = new ArrayList<>();
    private final AtomicInteger searchType = new AtomicInteger(),
            checkEvent = new AtomicInteger(),
            searchControl = new AtomicInteger();
    private String packageName, activityName;
    private CoordinatorLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_step);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initData();
        initView();
/*        NewbieGuide.with(this)
                .setLabel("addStep")
                .alwaysShow(true)
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(addStep, new RelativeGuide(R.layout.newbieguide_add_step_prompt, Gravity.BOTTOM, 100))
                        .setLayoutRes(R.layout.activity_add_step, R.id.add_step)
                        .setEverywhereCancelable(false))
                .show();*/
    }

    private void initData() {
        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");
        activityName = intent.getStringExtra("activity");
    }

    private void initView() {
        setTitle("添加步骤");
        root = findViewById(R.id.root);
        RecyclerView recyclerView = findViewById(R.id.rv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddStepAdapter adapter = new AddStepAdapter(root);
        recyclerView.setAdapter(adapter);
        addStep = findViewById(R.id.add_step);
        saveStep = findViewById(R.id.save_step);
        addStepBackdrop = findViewById(R.id.add_step_backdrop);
        // 保存全部步骤。
        saveAllStep(root);
        // 处理添加步骤控件显示可以隐藏
        addBackdropShowStatus();
        // 选择搜索类型
        selectSearchType(root);
        // 选择控件
        selectControl(root);
        // 选择事件
        selectEvents(root);
        // 添加步骤
        addStep(root, adapter);
    }

    /**
     * 校验数据并添加数据
     *
     * @param root    给Snackbar用来提示。
     * @param adapter 用来更新数据。
     */
    private void addStep(CoordinatorLayout root, AddStepAdapter adapter) {
        EditText searchContent = findViewById(R.id.search_content);
        saveStep.setOnClickListener(v -> {
            String search = searchContent.getText().toString();
            EditText pasteContent = findViewById(R.id.paste_content);
            String paste = pasteContent.getText().toString();
            if (searchType.get() == 0 || searchControl.get() == 0 || checkEvent.get() == 0 || TextUtils.isEmpty(search)) {
                Snackbar.make(root, getString(R.string.form_null), LENGTH_SHORT).show();
                return;
            }
            stepInfo.add(new StepInfo(search, paste, searchType.get(), checkEvent.get(), searchControl.get()));
            adapter.setStep(stepInfo);
            adapter.notifyDataSetChanged();
            searchContent.setText("");
            addStepBackdrop.setVisibility(View.GONE);
        });
    }

    /**
     * 选择事件
     *
     * @param root 页面根控件用来提供给Snackbar CoordinatorLayout
     */
    @SuppressLint("NonConstantResourceId")
    private void selectEvents(CoordinatorLayout root) {
        ChipGroup chipEvent = findViewById(R.id.chip_event);
        chipEvent.setOnCheckedChangeListener((group, checkedId) -> {
            findViewById(R.id.paste_content_card).setVisibility(View.GONE);
            switch (checkedId) {
                case R.id.chip_check:
                    checkEvent.set(1);
                    break;
                case R.id.chip_press:
                    checkEvent.set(2);
                    break;
                case R.id.chip_copy:
                    checkEvent.set(3);
                    break;
                case R.id.chip_paste:
                    checkEvent.set(4);
                    findViewById(R.id.paste_content_card).setVisibility(View.VISIBLE);
                    break;
                default:
                    checkEvent.set(0);
                    Snackbar.make(root, getString(R.string.select_event), LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * 选择需要搜索的控件。
     *
     * @param root 页面根控件用来提供给Snackbar CoordinatorLayout
     */
    @SuppressLint("NonConstantResourceId")
    private void selectControl(CoordinatorLayout root) {
        ChipGroup chipControl = findViewById(R.id.chip_control);
        chipControl.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chip_button:
                    searchControl.set(1);
                    break;
                case R.id.chip_image:
                    searchControl.set(2);
                    break;
                case R.id.chip_text:
                    searchControl.set(3);
                    break;
                case R.id.chip_radiobutton:
                    searchControl.set(4);
                    break;
                case R.id.chip_checkbox:
                    searchControl.set(5);
                    break;
                case R.id.chip_editText:
                    searchControl.set(6);
                    break;
                default:
                    searchControl.set(0);
                    Snackbar.make(root, getString(R.string.select_control_prompt), LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * 选择搜索类型
     *
     * @param root 页面根控件用来提供给Snackbar CoordinatorLayout
     */
    @SuppressLint("NonConstantResourceId")
    private void selectSearchType(CoordinatorLayout root) {
        ChipGroup chipSearch = findViewById(R.id.chip_search);
        chipSearch.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chip_content:
                    searchType.set(1);
                    break;
                case R.id.chip_id:
                    searchType.set(2);
                    break;
                default:
                    searchType.set(0);
                    Snackbar.make(root, getString(R.string.select_search_type_prompt), LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * 添加数据表单是否显示
     */
    private void addBackdropShowStatus() {
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
    }

    /**
     * 保存全部步骤
     *
     * @param root 页面根控件用来提供给Snackbar CoordinatorLayout
     */
    private void saveAllStep(CoordinatorLayout root) {
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(v -> {
            if (stepInfo.size() == 0) {
                Snackbar.make(root, getString(R.string.no_add_step), LENGTH_SHORT).show();
                return;
            }
            View layout = View.inflate(v.getContext(), R.layout.save_all_step, null);
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(AddStepActivity.this)
                    .setTitle(R.string.save_step)
                    .setView(layout)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    })
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            alertDialog.setOnShowListener(dialog -> {
                EditText title = layout.findViewById(R.id.title_edit);
                EditText description = layout.findViewById(R.id.description_edit);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    String saveTitle = title.getText().toString().trim();
                    String saveDescription = description.getText().toString().trim();
                    if (TextUtils.isEmpty(saveTitle) || TextUtils.isEmpty(saveDescription)) {
                        Snackbar.make(root, getString(R.string.save_step_null), LENGTH_SHORT).show();
                        return;
                    }
                    Drawable icon = null;
                    try {
                        icon = getPackageManager().getApplicationIcon(packageName);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Scripts script = new Scripts();
                    script.setActivity(activityName);
                    script.setDescription(saveDescription);
                    script.setPackageName(packageName);
                    script.setTitle(saveTitle);
                    for (StepInfo stepInfo : stepInfo) {
                        Steps step = new Steps();
                        step.setControl(stepInfo.getControl());
                        step.setEvent(stepInfo.getEvent());
                        step.setSearchContent(stepInfo.getSearchContent());
                        step.setPasteContent(stepInfo.getPasteContent());
                        step.setScript(script);
                        step.setSearchType(stepInfo.getSearchType());
                        step.save();
                        script.getStepIds().add(step);
                    }
                    script.save();

                    alertDialog.cancel();
                    alertDialog.dismiss();
                    finish();
                });
            });
            alertDialog.show();
        });
    }


    private void returnPrompt() {
        if (stepInfo.size() > 0) {
            Snackbar.make(root, getString(R.string.data_no_save_cancel), LENGTH_SHORT).setAction(android.R.string.ok, v -> finish()).show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnPrompt();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnPrompt();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}