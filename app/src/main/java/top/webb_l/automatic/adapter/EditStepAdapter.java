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

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import top.webb_l.automatic.R;
import top.webb_l.automatic.model.Steps;
import top.webb_l.automatic.utils.StepSearchInfoUtils;


/**
 * @author Webb
 */
public class EditStepAdapter extends RecyclerView.Adapter<EditStepAdapter.ViewHolder> {
    ArrayList<Steps> stepInfoList = new ArrayList<>();
    private final String TAG = getClass().getName();
    private View root;

    public EditStepAdapter(View root) {
        this.root = root;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitle;
        MaterialCardView stepDetailsStatus;
        LinearLayout stepDetails, stepControl;
        ChipGroup stepEvent;
        ImageButton menu;
        Context context;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.title);
            stepDetailsStatus = itemView.findViewById(R.id.details_status);
            stepDetails = itemView.findViewById(R.id.ll_step_details);
            stepControl = itemView.findViewById(R.id.control);
            stepEvent = itemView.findViewById(R.id.event);
            menu = itemView.findViewById(R.id.menu);
            context = itemView.getContext();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_step_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Steps stepInfo = stepInfoList.get(position);
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
                        showEditDialog(holder.context, name, stepInfo.getId(), position);
                        return true;
                    case R.id.delete:
                        LitePal.delete(Steps.class, stepInfo.getId());
                        stepInfoList.remove(position);
                        notifyDataSetChanged();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
        int childCount = holder.stepControl.getChildCount();
        if (childCount > 1) {
            holder.stepControl.removeViewAt(1);
        }
        holder.stepControl.addView(StepSearchInfoUtils.getControlView(holder.stepControl.getContext(), stepInfo.getSearchContent(), stepInfo.getControl()));

    }

    @SuppressLint("NonConstantResourceId")
    private void showEditDialog(Context context, String stepName, int id, int position) {
        View view = View.inflate(context, R.layout.add_step_dialog, null);
        Steps data = LitePal.find(Steps.class, id);
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
        eventChip.setChecked(true);
        pasteContent.setText(data.getPasteContent());

        if (data.getEvent() == 4) {
            view.findViewById(R.id.paste_card).setVisibility(View.VISIBLE);
        }

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
                HashMap<String, String> stringHashMap = new HashMap<>();
                stringHashMap.put("searchType", String.valueOf(type.get()));
                stringHashMap.put("searchContent", content);
                stringHashMap.put("control", String.valueOf(control.get()));
                stringHashMap.put("event", String.valueOf(event.get()));
                stringHashMap.put("pasteContent", paste);
                data.update(stringHashMap);

                stepInfoList.set(position, LitePal.find(Steps.class, id));
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

    public void setStep(ArrayList<Steps> stepInfoList) {
        this.stepInfoList = stepInfoList;
    }
}
