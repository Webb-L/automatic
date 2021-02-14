package top.webb_l.automatic;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    private Switch serviceStatus;
    private EditText etFindText;
    private Button btCheckInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        serviceStatus = findViewById(R.id.serviceStatus);
        etFindText = findViewById(R.id.et_findText);
        btCheckInput = findViewById(R.id.bt_checkInput);


        serviceStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                } catch (Exception e) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                    e.printStackTrace();
                }
            }
        });

        btCheckInput.setOnClickListener(v -> {
            String texts = etFindText.getText().toString().trim();
            if (TextUtils.isEmpty(texts)) {
                Toast.makeText(Main2Activity.this, "请输入要查找的内容！", Toast.LENGTH_SHORT).show();
                return;
            }
            serviceStatus.setClickable(true);
            serviceStatus.setTextColor(getColor(R.color.black));
//            AutoAccessibilityService.findText = texts.split("/");
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}