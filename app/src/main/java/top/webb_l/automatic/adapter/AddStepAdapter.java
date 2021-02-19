package top.webb_l.automatic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import top.webb_l.automatic.utils.StepSearchInfoUtils;


public class AddStepAdapter extends RecyclerView.Adapter<AddStepAdapter.ViewHolder> {
    ArrayList<StepInfo> stepInfoList = new ArrayList<>();
    private View root;

    public AddStepAdapter(View root) {
        this.root = root;
    }

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
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepInfo stepInfo = stepInfoList.get(position);
        String name = holder.context.getString(R.string.step) + (position + 1);
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
                        stepInfoList.remove(position);
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
                holder.stepControl.addView(StepSearchInfoUtils.getControlView(holder.stepControl.getContext(), stepInfo.getSearchContent(), stepInfo.getControl()));
            } else {
                holder.stepDetails.setVisibility(View.GONE);
                if (holder.stepControl.getChildCount() > 1) {
                    holder.stepControl.removeViewAt(1);
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
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
                .setTitle(context.getString(R.string.edit) + stepName)
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

            searchType.setOnCheckedChangeListener((group, checkedId) -> type.set(StepSearchInfoUtils.getSearchType(root, context, checkedId)));
            controlGroup.setOnCheckedChangeListener((group, checkedId) -> control.set(StepSearchInfoUtils.getControl(root, context, checkedId)));
            eventGroup.setOnCheckedChangeListener((group, checkedId) -> {
                view.findViewById(R.id.paste_card).setVisibility(View.GONE);
                event.set(StepSearchInfoUtils.getEvent(root, view, context, checkedId));
            });

            builder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                String content = searchContent.getText().toString();
                String paste = pasteContent.getText().toString();
                if (type.get() == 0 || control.get() == 0 || event.get() == 0 || TextUtils.isEmpty(content)) {
                    Toast.makeText(context, context.getString(R.string.form_null), Toast.LENGTH_SHORT).show();
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
        return stepInfoList.size();
    }

    public void setStep(ArrayList<StepInfo> stepInfoList) {
        this.stepInfoList = stepInfoList;
    }
}
