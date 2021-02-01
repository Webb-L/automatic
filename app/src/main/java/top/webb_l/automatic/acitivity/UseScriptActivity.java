package top.webb_l.automatic.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;

import top.webb_l.automatic.R;
import top.webb_l.automatic.model.Scripts;

public class UseScriptActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    private Scripts script;

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
        if (script == null) {
            Toast.makeText(this, "参数错误！", Toast.LENGTH_SHORT).show();
            return;
        }
        initView();
    }

    private void initView() {
        setTitle("使用" + script.getTitle());
        Switch status = findViewById(R.id.status);
        EditText scriptLog = findViewById(R.id.scriptLog);
    }

    private void initData() {
        Intent intent = getIntent();
        int scriptId = intent.getIntExtra("scriptId", 0);
        script = LitePal.find(Scripts.class, scriptId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}