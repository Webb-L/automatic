package top.webb_l.automatic.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.EditStepAdapter;
import top.webb_l.automatic.encryption.ShareEncryption;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;
import top.webb_l.automatic.utils.AppInfoUtils;
import top.webb_l.automatic.utils.StepSearchInfoUtils;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

/**
 * @author Webb
 */
public class EditScriptActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private CoordinatorLayout root;
    private Scripts script;
    private PackageManager packageManager;
    private TextView tvPackageName, tvActivityName, appName, tvTitle, tvDescription;
    private ImageView appIcon;
    private EditStepAdapter adapter;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            initView();
        }
    };

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
        mHandler.sendEmptyMessage(1);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        int scriptId = intent.getIntExtra("scriptId", 0);
        if (scriptId < 0) {
            Snackbar.make(root, getString(R.string.parameter_error), Snackbar.LENGTH_LONG).show();
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


        RecyclerView stepList = findViewById(R.id.stepList);
        stepList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditStepAdapter(root);
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
     * 分享脚本到剪切板
     *
     * @param context    上下文用于获取字符串
     * @param scriptInfo 脚本信息
     */
    private void shareScript(Context context, Scripts scriptInfo) {
        JSONObject script = new JSONObject();
        try {
            script.put("title", scriptInfo.getTitle());
            script.put("description", scriptInfo.getDescription());
            script.put("packageName", scriptInfo.getPackageName());
            script.put("activity", scriptInfo.getActivity());
            JSONArray steps = new JSONArray();
            List<Steps> stepList = LitePal.where("scripts_id = ?", String.valueOf(scriptInfo.getId())).find(Steps.class);
            for (Steps step : stepList) {
                JSONObject stepInfo = new JSONObject();
                stepInfo.put("searchType", step.getSearchType());
                stepInfo.put("searchContent", step.getSearchContent());
                stepInfo.put("control", step.getControl());
                stepInfo.put("event", step.getEvent());
                stepInfo.put("pasteContent", step.getPasteContent());
                steps.put(stepInfo);
            }
            script.put("steps", steps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encryption = ShareEncryption.encryption(script.toString());
        if (TextUtils.isEmpty(encryption)) {
            Toast.makeText(context, context.getString(R.string.share) + context.getString(R.string.failure), Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(ClipData.newPlainText("Label", "AUTO://" + encryption));
        Toast.makeText(context, "已保存到剪切板！", Toast.LENGTH_SHORT).show();
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
            EditText pasteContent = view.findViewById(R.id.paste_content);

            searchType.setOnCheckedChangeListener((group, checkedId) -> {
                type.set(StepSearchInfoUtils.getSearchType(root, this, checkedId));
            });
            controlGroup.setOnCheckedChangeListener((group, checkedId) -> {
                control.set(StepSearchInfoUtils.getControl(root, this, checkedId));
            });
            eventGroup.setOnCheckedChangeListener((group, checkedId) -> {
                view.findViewById(R.id.paste_card).setVisibility(View.GONE);
                event.set(StepSearchInfoUtils.getEvent(root, view, this, checkedId));
            });

            AlertDialog builder = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.add_step)
                    .setView(view)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getResources().getString(android.R.string.ok), null)
                    .setCancelable(false)
                    .create();
            builder.setOnShowListener(dialog -> {
                builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    String content = searchContent.getText().toString();
                    String paste = pasteContent.getText().toString();
                    if (type.get() == 0 || control.get() == 0 || event.get() == 0 || TextUtils.isEmpty(content)) {
                        Snackbar.make(root, getString(R.string.form_null), LENGTH_SHORT).show();
                        return;
                    }

                    Steps step = new Steps();
                    step.setScript(script);
                    step.setSearchType(type.get());
                    step.setSearchContent(content);
                    step.setControl(control.get());
                    step.setEvent(event.get());
                    step.setPasteContent(paste);
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
                    .setTitle(getString(R.string.save_step))
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
                        Snackbar.make(root, getString(R.string.save_step_null), Snackbar.LENGTH_SHORT).show();
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
        ArrayList<String> appName = new ArrayList<>();
        for (String s : packageInfoList) {
            String[] info = s.split("/r/");
            appName.add(info[0]);
        }
        ImageView editPackageName = findViewById(R.id.editPackageName);
        editPackageName.setOnClickListener(v -> {
            AtomicInteger index = new AtomicInteger();
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getResources().getString(R.string.select_package) + "(" + packageInfoList.length + ")")
                    .setSingleChoiceItems(appName.toArray(new String[appName.size()]), 0, (dialog, which) -> index.set(which))
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
                        Snackbar.make(root, getString(R.string.package_update_prompt), Snackbar.LENGTH_LONG).show();
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
        ArrayList<Steps> stepArray = new ArrayList<>(steps);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_script, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                shareScript(this, script);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
