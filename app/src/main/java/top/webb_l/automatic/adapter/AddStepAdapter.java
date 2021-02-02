package top.webb_l.automatic.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import top.webb_l.automatic.R;
import top.webb_l.automatic.data.StepInfo;


public class AddStepAdapter extends RecyclerView.Adapter<AddStepAdapter.ViewHolder> {
    ArrayList<StepInfo> stepInfos = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitle;
        MaterialCardView stepDetailsStatus;
        ImageButton menu;
        LinearLayout stepDetails, stepControl;
        ChipGroup stepEvent;
        Context context;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepDetailsStatus = itemView.findViewById(R.id.step_details_status);
            menu = itemView.findViewById(R.id.menu);
            stepDetails = itemView.findViewById(R.id.ll_step_details);
            stepControl = itemView.findViewById(R.id.step_control);
            stepEvent = itemView.findViewById(R.id.step_event);
            context = itemView.getContext();
            this.itemView = itemView;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.acitivity_add_step_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepInfo stepInfo = stepInfos.get(position);
        String name = "步骤" + (position + 1);
        holder.stepTitle.setText(name + ":");
        Chip checkEvent = (Chip) holder.stepEvent.getChildAt(stepInfo.getEvent() - 1);
        if (checkEvent != null) {
            checkEvent.setChecked(true);
        }

        holder.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.context, holder.menu);
            popupMenu.inflate(R.menu.step_manager);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit:
                        showEditDialog(holder.context, name, stepInfo, position);
                        return true;
                    case R.id.delete:
                        stepInfos.remove(position);
                        notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });


        holder.stepDetailsStatus.setOnClickListener(v -> {
            if (holder.stepDetails.getVisibility() == View.GONE) {
                holder.stepDetails.setVisibility(View.VISIBLE);
                switch (stepInfo.getControl()) {
                    case 1:
                        Button button = new Button(holder.stepControl.getContext());
                        button.setText(R.string.chip_button);
                        holder.stepControl.addView(button);
                        break;
                    case 2:
                        ImageView image = new ImageView(holder.stepControl.getContext());
                        image.setImageResource(R.drawable.ic_extension_off_64dp);
                        holder.stepControl.addView(image);
                        break;
                    case 3:
                        TextView text = new TextView(holder.stepControl.getContext());
                        text.setText(R.string.chip_text);
                        holder.stepControl.addView(text);
                        break;
                    case 4:
                        RadioButton radioButton = new RadioButton(holder.stepControl.getContext());
                        radioButton.setText(R.string.chip_radiobutton);
                        holder.stepControl.addView(radioButton);
                        break;
                    case 5:
                        CheckBox checkBox = new CheckBox(holder.stepControl.getContext());
                        checkBox.setText(R.string.chip_checkbox);
                        holder.stepControl.addView(checkBox);
                        break;
                    case 6:
                        EditText editText = new EditText(holder.stepControl.getContext());
                        editText.setText(R.string.chip_editText);
                        holder.stepControl.addView(editText);
                        break;
                    default:
                        break;
                }
            } else {
                holder.stepDetails.setVisibility(View.GONE);
                if (holder.stepControl.getChildCount() > 1) {
                    holder.stepControl.removeViewAt(1);
                }
            }
        });
    }

    private void showEditDialog(Context context, String stepName, StepInfo data, int position) {
        View view = View.inflate(context, R.layout.add_step_dialog, null);
        ChipGroup searchType = view.findViewById(R.id.chip_search);
        EditText searchContent = view.findViewById(R.id.search_content);
        ChipGroup controlGroup = view.findViewById(R.id.chip_control);
        ChipGroup eventGroup = view.findViewById(R.id.chip_event);
        EditText pasteContent = view.findViewById(R.id.paste_content);

        Chip typeChip = (Chip) searchType.getChildAt(data.getSearchType() - 1);
        typeChip.setChecked(true);
        searchContent.setText(data.getSearchContent());
        Chip controlChip = (Chip) controlGroup.getChildAt(data.getControl() - 1);
        controlChip.setChecked(true);
        Chip eventChip = (Chip) eventGroup.getChildAt(data.getEvent() - 1);
        if (data.getEvent() == 4) {
            view.findViewById(R.id.paste_card).setVisibility(View.VISIBLE);
        }
        eventChip.setChecked(true);
        pasteContent.setText(data.getPasteContent());

        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setTitle("编辑" + stepName)
                .setView(view)
                .setNegativeButton(context.getResources().getString(android.R.string.cancel), (dialog, which) -> dialog.cancel())
                .setPositiveButton(context.getResources().getString(android.R.string.ok), null)
                .setCancelable(false)
                .create();
        builder.setOnShowListener(dialog -> {
            AtomicInteger type = new AtomicInteger(), control = new AtomicInteger(), event = new AtomicInteger();
            type.set(data.getSearchType());
            control.set(data.getControl());
            event.set(data.getEvent());

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
                        Toast.makeText(context, "请选择搜索类型！", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "请选择控件！", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
            eventGroup.setOnCheckedChangeListener((group, checkedId) -> {
                view.findViewById(R.id.paste_card).setVisibility(View.GONE);
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
                        view.findViewById(R.id.paste_card).setVisibility(View.VISIBLE);
                        break;
                    default:
                        event.set(0);
                        Toast.makeText(context, "请选择事件！", Toast.LENGTH_SHORT).show();
                        break;
                }
            });

            builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                String content = searchContent.getText().toString();
                String paste = pasteContent.getText().toString();
                if (type.get() == 0 || control.get() == 0 || event.get() == 0 || TextUtils.isEmpty(content)) {
                    Toast.makeText(context, "请确保所有内容都填写完成！", Toast.LENGTH_SHORT).show();
                    return;
                }
                data.setSearchType(type.get());
                data.setSearchContent(content);
                data.setControl(control.get());
                data.setEvent(event.get());
                data.setPasteContent(paste);
                notifyItemChanged(position);
                dialog.cancel();
            });
        });
        builder.show();
    }


    @Override
    public int getItemCount() {
        return stepInfos.size();
    }

    public void setStep(ArrayList<StepInfo> stepInfos) {
        this.stepInfos = stepInfos;
    }
}
