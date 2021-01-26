package top.webb_l.automatic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.data.StepInfo;


public class AddStepAdapter extends RecyclerView.Adapter<AddStepAdapter.ViewHolder> {
    ArrayList<StepInfo> stepInfos = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitle;
        MaterialCardView stepDetailsStatus;
        ImageButton stepDetailEdit;
        LinearLayout stepDetails, stepControl;
        ChipGroup stepEvent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepDetailsStatus = itemView.findViewById(R.id.step_details_status);
            stepDetailEdit = itemView.findViewById(R.id.step_edit);
            stepDetails = itemView.findViewById(R.id.ll_step_details);
            stepControl = itemView.findViewById(R.id.step_control);
            stepEvent = itemView.findViewById(R.id.step_event);
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
        holder.stepTitle.setText("步骤" + (position + 1) + ":");
        Chip checkEvent = (Chip) holder.stepEvent.getChildAt(stepInfo.getEvent());
        if (checkEvent != null) {
            checkEvent.setChecked(true);
        }
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
                        CheckBox checkBox = new CheckBox(holder.stepControl.getContext());
                        checkBox.setText(R.string.chip_checkbox);
                        holder.stepControl.addView(checkBox);
                        break;
                    case 5:
                        RadioButton radioButton = new RadioButton(holder.stepControl.getContext());
                        radioButton.setText(R.string.chip_radiobutton);
                        holder.stepControl.addView(radioButton);
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

    @Override
    public int getItemCount() {
        return stepInfos.size();
    }

    public void addStep(ArrayList<StepInfo> stepInfos) {
        this.stepInfos = stepInfos;
    }
}
