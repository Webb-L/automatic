package top.webb_l.automatic.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import top.webb_l.automatic.R;


public class AddStepAdapter extends RecyclerView.Adapter<AddStepAdapter.ViewHolder> {
    private final static String TAG = AddStepAdapter.class.getName();
    private int number = 5;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitle;
        TextView stepDescription;
        ImageButton stepDetailsStatus;
        LinearLayout stepDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepDescription = itemView.findViewById(R.id.step_description);
            stepDetailsStatus = itemView.findViewById(R.id.step_details_status);
            stepDetails = itemView.findViewById(R.id.ll_step_details);
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
        holder.stepTitle.setText("步骤" + position + ":");
        holder.stepDescription.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam ut lectus arcu. Nulla sed elit sapien. Maecenas luctus libero ut feugiat lobortis. Ut sed enim at nisl porttitor posuere et ut mauris. Nulla tincidunt nisl quis ipsum iaculis, at feugiat felis auctor. Aliquam sit amet dolor at eros mollis ultrices. Quisque nec consectetur justo, eu placerat arcu. Integer auctor dui et mauris blandit, ac mattis neque interdum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nullam dapibus lacinia neque. Vestibulum malesuada maximus arcu. In hac habitasse platea dictumst. Sed molestie volutpat tortor vitae tincidunt. Vestibulum sit amet venenatis sapien.");

        holder.stepDetailsStatus.setOnClickListener(v -> {
            if (holder.stepDetails.getVisibility() == View.GONE) {
                holder.stepDetails.setVisibility(View.VISIBLE);
                holder.stepDetailsStatus.setImageResource(R.drawable.ic_keyboard_arrow_up_24dp);
                holder.stepDescription.setEllipsize(null);
                holder.stepDescription.setSingleLine(false);
            } else {
                holder.stepDetails.setVisibility(View.GONE);
                holder.stepDetailsStatus.setImageResource(R.drawable.ic_keyboard_arrow_down_24dp);
                holder.stepDescription.setEllipsize(TextUtils.TruncateAt.END);
                holder.stepDescription.setSingleLine(false);
                holder.stepDescription.setLines(3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return number;
    }

    public void addStep(){
        number++;
    }
}
