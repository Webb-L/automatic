package top.webb_l.automatic.acitivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;

import java.util.List;

import top.webb_l.automatic.R;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;
import top.webb_l.automatic.service.AutoAccessibilityService;

public class UseScriptActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    public static Scripts script;
    public static List<Steps> steps;
    public static EditText scriptLog;
    public Switch serviceStatus;

    @SuppressLint("UseSwitchCompatOrMaterialCode")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        setContentView(R.layout.activity_use_script);
        initData();
        if (script == null || steps == null || steps.size() == 0) {
            Toast.makeText(this, "参数错误！", Toast.LENGTH_SHORT).show();
            return;
        }
        initView();
    }

    private void initView() {
        setTitle(script.getTitle());
        serviceStatus = findViewById(R.id.status);
        serviceStatus.setChecked(AutoAccessibilityService.serviceStatus);
        serviceStatus.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        });

        scriptLog = findViewById(R.id.scriptLog);
    }

    private void initData() {
        Intent intent = getIntent();
        int scriptId = intent.getIntExtra("scriptId", 0);
        script = LitePal.find(Scripts.class, scriptId);
        steps = LitePal.where("scripts_id = ?", String.valueOf(script.getId())).find(Steps.class);
    }

    @Override
    protected void onRestart() {
        serviceStatus.setChecked(AutoAccessibilityService.serviceStatus);
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.use_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.open:
                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage(script.getPackageName()));
                } catch (Exception e) {
                    Toast.makeText(this, "跳转异常！", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}