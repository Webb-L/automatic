package top.webb_l.automatic.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.ScriptsAdapter;
import top.webb_l.automatic.data.ScriptInfo;
import top.webb_l.automatic.encryption.ShareEncryption;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;

/**
 * @author Webb
 */
public class MainActivity extends AppCompatActivity {
    private static RecyclerView rvData;
    private FloatingActionButton fabAdd;
    public static LinearLayout rootNotData;
    public ArrayList<ScriptInfo> scripts;
    private ScriptsAdapter scriptsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        initView();
        initClicks();
        initRecyclerViewItems();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getDataBase() {
        scripts = new ArrayList<>();
        for (Scripts script : LitePal.order("id desc").find(Scripts.class)) {
            Drawable icon = null;
            try {
                icon = getPackageManager().getApplicationIcon(script.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                icon = getDrawable(R.drawable.ic_extension_off_64dp);
                e.printStackTrace();
            }
            ScriptInfo scriptInfo = new ScriptInfo(script.getTitle(), script.getDescription(), script.getPackageName(), script.getActivity(), icon, null);
            scriptInfo.setId(script.getId());
            scripts.add(scriptInfo);
        }
        if (scripts.size() > 0) {
            rootNotData.setVisibility(View.GONE);
        }
        scriptsAdapter.scripts = scripts;
        scriptsAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    private void initClicks() {
        CoordinatorLayout root = findViewById(R.id.root);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, SelectPackageActivity.class)));
        BottomAppBar bar = findViewById(R.id.bar);
        bar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.search_automatic:
                    Snackbar.make(root, "不要点我啦，我还小暂时不能帮到你。", Snackbar.LENGTH_SHORT).show();
                    break;
                case R.id.help:
                    Intent intent = new Intent(this, OpenWebActivity.class);
                    intent.putExtra("url", "http://121.4.250.193/");
                    startActivity(intent);
                    break;
                case R.id.import_script:
                    importScript(root);
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    /**
     * 从剪切板导入数据
     *
     * @param root 用于Snackbar提示
     */
    private void importScript(CoordinatorLayout root) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipboardManager == null && clipData.getItemCount() <= 0) {
            return;
        }
        ClipData.Item dataItemAt = clipData.getItemAt(0);
        final String PROTOCOL = "AUTO://";
        if (dataItemAt == null) {
            Snackbar.make(root, getResources().getString(R.string.Import_failed), Snackbar.LENGTH_SHORT).show();
            return;
        }

        String content = getAllSatisfyStr(dataItemAt.getText().toString(), PROTOCOL + "\\w+").toString().replace(PROTOCOL, "");
        if (TextUtils.isEmpty(content)) {
            Snackbar.make(root, getResources().getString(R.string.wrong_format), Snackbar.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject script = new JSONObject(ShareEncryption.decrypt(content));
            Scripts scripts = new Scripts();
            scripts.setTitle(script.getString("title"));
            scripts.setDescription(script.getString("description"));
            scripts.setPackageName(script.getString("packageName"));
            scripts.setActivity(script.getString("activity"));
            scripts.save();
            for (int i = 0; i < script.getJSONArray("steps").length(); i++) {
                JSONObject step = script.getJSONArray("steps").getJSONObject(i);
                Steps steps = new Steps();
                steps.setPasteContent(step.getString("pasteContent"));
                steps.setSearchContent(step.getString("searchContent"));
                steps.setControl(step.getInt("control"));
                steps.setEvent(step.getInt("event"));
                steps.setSearchType(step.getInt("searchType"));
                steps.setScript(scripts);
                steps.save();
            }
            getDataBase();
            Snackbar.make(root, getResources().getString(R.string.Imported_successfully), Snackbar.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有满足正则表达式的字符串
     *
     * @param str   需要被获取的字符串
     * @param regex 正则表达式
     * @return 所有满足正则表达式的字符串
     */
    private StringBuilder getAllSatisfyStr(String str, String regex) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        StringBuilder allSatisfyStr = new StringBuilder();
        if (regex == null || regex.isEmpty()) {
            allSatisfyStr.append(str);
            return allSatisfyStr;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            allSatisfyStr.append(matcher.group());
        }
        return allSatisfyStr;
    }

    private void initRecyclerViewItems() {
        rvData.setLayoutManager(new LinearLayoutManager(this));
        scriptsAdapter = new ScriptsAdapter(this);
        getDataBase();
        rvData.setAdapter(scriptsAdapter);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rootNotData = findViewById(R.id.root_not_data);
        rvData = findViewById(R.id.rv_data);
        fabAdd = findViewById(R.id.fab_add);
    }

    @Override
    protected void onRestart() {
        getDataBase();
        super.onRestart();
    }
}