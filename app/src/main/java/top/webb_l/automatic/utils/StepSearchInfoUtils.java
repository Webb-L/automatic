package top.webb_l.automatic.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import top.webb_l.automatic.R;

/**
 * @author Webb
 */
public class StepSearchInfoUtils {
    @SuppressLint("NonConstantResourceId")
    public static int getSearchType(View root, Context context, int index) {
        switch (index) {
            case R.id.chip_content:
                return 1;
            case R.id.chip_id:
                return 2;
            default:
                Snackbar.make(root, context.getString(R.string.select_search_type_prompt), Snackbar.LENGTH_SHORT).show();
                return 0;
        }
    }

    @SuppressLint("NonConstantResourceId")
    public static int getControl(View root, Context context, int index) {
        switch (index) {
            case R.id.chip_button:
                return 1;
            case R.id.chip_image:
                return 2;
            case R.id.chip_text:
                return 3;
            case R.id.chip_radiobutton:
                return 4;
            case R.id.chip_checkbox:
                return 5;
            case R.id.chip_editText:
                return 6;
            default:
                Snackbar.make(root, context.getString(R.string.select_control_prompt), Snackbar.LENGTH_SHORT).show();
                return 0;
        }
    }

    @SuppressLint("NonConstantResourceId")
    public static int getEvent(View root, View view, Context context, int index) {
        switch (index) {
            case R.id.chip_check:
                return 1;
            case R.id.chip_press:
                return 2;
            case R.id.chip_copy:
                return 3;
            case R.id.chip_paste:
                view.findViewById(R.id.paste_card).setVisibility(View.VISIBLE);
                return 4;
            default:
                Snackbar.make(root, context.getString(R.string.select_event), Snackbar.LENGTH_SHORT).show();
                return 0;
        }
    }

    public static View getControlView(Context context, String searchContent, int index) {
        switch (index) {
            case 1:
                Button button = new Button(context);
                button.setText(searchContent);
                return button;
            case 2:
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.ic_extension_off_64dp);
                return imageView;
            case 3:
                TextView text = new TextView(context);
                text.setText(searchContent);
                return text;
            case 4:
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(searchContent);
                return radioButton;
            case 5:
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(searchContent);
                return checkBox;
            case 6:
                EditText editText = new EditText(context);
                editText.setText(searchContent);
                return editText;
            default:
                return null;
        }
    }
}
