package top.webb_l.automatic.acitivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.adapter.AutoMaticAdapter;
import top.webb_l.automatic.data.ScriptInfo;
import top.webb_l.automatic.model.Scripts;

public class MainActivity extends AppCompatActivity {
    private static RecyclerView rvData;
    private FloatingActionButton fabAdd;
    private LinearLayout rootNotData;
    public ArrayList<ScriptInfo> scripts;
    private AutoMaticAdapter autoMaticAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        initView();
        initClicks();
        initRecyclerViewItems();

    }

    private void getDataBase() {
        scripts = new ArrayList<>();
        for (Scripts script : LitePal.order("id desc").find(Scripts.class)) {
            Drawable icon = null;
            try {
                icon = getPackageManager().getApplicationIcon(script.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            ScriptInfo scriptInfo = new ScriptInfo(script.getTitle(), script.getDescription(), script.getPackageName(), script.getActivity(), icon, null);
            scriptInfo.setId(script.getId());
            scripts.add(scriptInfo);
        }
        if (scripts.size() > 0) {
            rootNotData.setVisibility(View.GONE);
        }
        autoMaticAdapter.scripts = scripts;
        autoMaticAdapter.notifyDataSetChanged();
    }

    private void initClicks() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectPackageActivity.class));
        });
        BottomAppBar bar = findViewById(R.id.bar);
        bar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.search_automatic:
                    CoordinatorLayout root = findViewById(R.id.root);
                    Snackbar.make(root, "不要点我啦，我还小暂时不能帮到你。", Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    private void initRecyclerViewItems() {
        rvData.setLayoutManager(new LinearLayoutManager(this));
        autoMaticAdapter = new AutoMaticAdapter(this);
        getDataBase();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intent = new Intent(this, OpenWebActivity.class);
                intent.putExtra("url", "http://121.4.250.193/");
                startActivity(intent);
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    protected void onRestart() {
        getDataBase();
        super.onRestart();
    }
}