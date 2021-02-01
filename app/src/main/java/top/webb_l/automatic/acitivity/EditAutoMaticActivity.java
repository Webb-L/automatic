package top.webb_l.automatic.acitivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.EditStepAdapter;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;
import top.webb_l.automatic.utils.AppInfoUtils;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

public class EditAutoMaticActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private ImageView editPackageName;
    private CoordinatorLayout root;
    private Scripts script;
    private PackageManager packageManager;
    private RecyclerView stepList;
    private TextView tvPackageName, tvActivityName, appName, tvTitle, tvDescription;
    private ImageView appIcon;
    private EditStepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        setContentView(R.layout.activity_edit_auto_matic);
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        int scriptId = intent.getIntExtra("scriptId", 0);
        if (scriptId < 0) {
            Snackbar.make(root, "参数错误", Snackbar.LENGTH_LONG).show();
            return;
        }
        script = LitePal.find(Scripts.class, scriptId);
        packageManager = getPackageManager();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        root = findViewById(R.id.root);
        tvPackageName = findViewById(R.id.packageName);
        tvActivityName = findViewById(R.id.activityName);
        appIcon = findViewById(R.id.appIcon);
        appName = findViewById(R.id.appName);
        tvTitle = findViewById(R.id.title);
        tvDescription = findViewById(R.id.description);
        setTitle(getResources().getString(R.string.edit) + script.getTitle());
        setAppIcon(script.getPackageName());
        setAppName(script.getPackageName());
        setPackageName(script.getPackageName());
        setActivityName(script.getActivity());
        setScriptTitle(script.getTitle());
        setScriptDescription(script.getDescription());

        AppInfoUtils appInfoUtils = new AppInfoUtils(this);
        String[] packageInfoList = appInfoUtils.getPackageInfoList();
        editPackageName(packageInfoList);
        editScriptInfo();
        addStepInfo();

        ImageView selectActivityName = findViewById(R.id.editActivityName);
        selectActivity(appInfoUtils, selectActivityName);


        stepList = findViewById(R.id.stepList);
        stepList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditStepAdapter();
        stepList.setAdapter(adapter);
        initStepList(LitePal.where("scripts_id = ?", String.valueOf(script.getId())).find(Steps.class), adapter);

        findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void selectActivity(AppInfoUtils appInfoUtils, ImageView selectActivityName) {
        AtomicInteger index = new AtomicInteger();
        selectActivityName.setOnClickListener(v -> {
            String[] activities = appInfoUtils.getActivities((String) this.tvPackageName.getText());
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getResources().getString(R.string.select_activity) + "(" + activities.length + ")")
                    .setSingleChoiceItems(activities, 0, (dialog, which) -> index.set(which))
                    .setNegativeButton(getResources().getString(android.R.string.cancel), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getResources().getString(android.R.string.ok), (dialog, which) -> {
                        String activity = activities[index.get()];
                        HashMap<String, String> stringHashMap = new HashMap<>();
                        stringHashMap.put("activity", activity);
                        script.update(stringHashMap);
                        setActivityName(activity);
                        tvActivityName.setTextColor(this.tvPackageName.getTextColors());
                    })
                    .show();
        });
    }

    private void setScriptDescription(String description) {
        tvDescription.setText(description);
    }

    private void setScriptTitle(String title) {
        tvTitle.setText(title);
    }

    private void setActivityName(String activityName) {
        tvActivityName.setText(activityName);
    }

    /**
     * 设置图标
     *
     * @param packageName String 软件包名用来获取图标。
     */
    private void setAppName(String packageName) {
        try {
            appName.setText(packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加步骤。
     */
    @SuppressLint("NonConstantResourceId")
    private void addStepInfo() {
        MaterialButton addStep = findViewById(R.id.add_step);
        addStep.setOnClickListener(v -> {
            View view = View.inflate(this, R.layout.add_step_dialog, null);
            AtomicInteger type = new AtomicInteger(), control = new AtomicInteger(), event = new AtomicInteger();
            ChipGroup searchType = view.findViewById(R.id.chip_search);
            EditText searchContent = view.findViewById(R.id.search_content);
            ChipGroup controlGroup = view.findViewById(R.id.chip_control);
            ChipGroup eventGroup = view.findViewById(R.id.chip_event);

            searchType.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.chip_content:
                        type.set(1);
                        break;
                    case R.id.chip_id:
                        type.set(2);
                        break;
                    default:
                        type.set(0);
                        Snackbar.make(root, "请选择搜索类型！", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            });
            controlGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.chip_button:
                        control.set(1);
                        break;
                    case R.id.chip_image:
                        control.set(2);
                        break;
                    case R.id.chip_text:
                        control.set(3);
                        break;
                    case R.id.chip_radiobutton:
                        control.set(4);
                        break;
                    case R.id.chip_checkbox:
                        control.set(5);
                        break;
                    case R.id.chip_editText:
                        control.set(6);
                        break;
                    default:
                        control.set(0);
                        Snackbar.make(root, "请选择控件！", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            });
            eventGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.chip_check:
                        event.set(1);
                        break;
                    case R.id.chip_press:
                        event.set(2);
                        break;
                    case R.id.chip_copy:
                        event.set(3);
                        break;
                    case R.id.chip_paste:
                        event.set(4);
                        break;
                    default:
                        event.set(0);
                        Snackbar.make(root, "请选择事件！", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            });

            AlertDialog builder = new MaterialAlertDialogBuilder(this)
                    .setTitle("添加步骤")
                    .setView(view)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getResources().getString(android.R.string.ok), null)
                    .setCancelable(false)
                    .create();
            builder.setOnShowListener(dialog -> {
                builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    String content = searchContent.getText().toString();
                    if (type.get() == 0 || control.get() == 0 || event.get() == 0 || TextUtils.isEmpty(content)) {
                        Snackbar.make(root, "请确保所有内容都填写完成！", LENGTH_SHORT).show();
                        return;
                    }

                    Steps step = new Steps();
                    step.setScript(script);
                    step.setSearchType(type.get());
                    step.setSearchContent(content);
                    step.setControl(control.get());
                    step.setEvent(event.get());
                    step.save();
                    List<Steps> steps = LitePal.where("scripts_id = ?", String.valueOf(script.getId())).find(Steps.class);
                    initStepList(steps, adapter);
                    adapter.notifyItemChanged(steps.size());
                    dialog.cancel();
                });
            });
            builder.show();
        });
    }

    /**
     * 脚本标题和介绍。
     */
    private void editScriptInfo() {
        MaterialButton editAutoMatic = findViewById(R.id.editAutoMatic);
        editAutoMatic.setOnClickListener(v -> {
            View layout = View.inflate(v.getContext(), R.layout.save_all_step, null);
            EditText titleScript = layout.findViewById(R.id.title_edit);
            EditText descriptionScript = layout.findViewById(R.id.description_edit);
            titleScript.setText(script.getTitle());
            descriptionScript.setText(script.getDescription());
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("保存步骤")
                    .setView(layout)
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .setPositiveButton(android.R.string.ok, null)
                    .create();

            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    String saveTitle = titleScript.getText().toString().trim();
                    String saveDescription = descriptionScript.getText().toString().trim();
                    if (TextUtils.isEmpty(saveTitle) || TextUtils.isEmpty(saveDescription)) {
                        Snackbar.make(root, "请确保保存步骤所有内容都填写完成！", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    HashMap<String, String> stringHashMap = new HashMap<>();
                    stringHashMap.put("title", saveTitle);
                    stringHashMap.put("description", saveDescription);
                    script.update(stringHashMap);
                    setScriptTitle(saveTitle);
                    setScriptDescription(saveDescription);
                    alertDialog.cancel();
                    alertDialog.dismiss();
                });
            });
            alertDialog.show();
        });
    }

    /**
     * 编辑包名
     *
     * @param packageInfoList 包名列表 String[]
     */
    private void editPackageName(String[] packageInfoList) {
        ArrayList<String> AppName = new ArrayList<>();
        for (String s : packageInfoList) {
            String[] info = s.split("/r/");
            AppName.add(info[0]);
        }
        editPackageName = findViewById(R.id.editPackageName);
        editPackageName.setOnClickListener(v -> {
            AtomicInteger index = new AtomicInteger();
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getResources().getString(R.string.select_package) + "(" + packageInfoList.length + ")")
                    .setSingleChoiceItems(AppName.toArray(new String[AppName.size()]), 0, (dialog, which) -> index.set(which))
                    .setNegativeButton(getResources().getString(android.R.string.cancel), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getResources().getString(android.R.string.ok), (dialog, which) -> {
                        String packageName = packageInfoList[index.get()].split("/r/")[1];
                        HashMap<String, String> stringHashMap = new HashMap<>(1);
                        stringHashMap.put("packageName", packageName);
                        script.update(stringHashMap);
                        setAppIcon(packageName);
                        setAppName(packageName);
                        setPackageName(packageName);
                        tvActivityName.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        Snackbar.make(root, "请更新红色字体的内容，如果不更新会导致无法使用。", Snackbar.LENGTH_LONG).show();
                    })
                    .create()
                    .show();
        });
    }

    /**
     * 初始化步骤列表。
     *
     * @param steps   步骤列表 List<Steps>
     * @param adapter
     */
    private void initStepList(List<Steps> steps, EditStepAdapter adapter) {
        ArrayList<Steps> stepArray = new ArrayList<>();
        stepArray.addAll(steps);
        adapter.setStep(stepArray);
    }


    /**
     * 根据包名修改某个ImageView图片
     *
     * @param packageName 软件包名 String
     */
    private void setAppIcon(String packageName) {
        try {
            Drawable applicationIcon = packageManager.getApplicationIcon(packageName);
            appIcon.setImageDrawable(applicationIcon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setPackageName(String packageName) {
        tvPackageName.setText(packageName);
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
