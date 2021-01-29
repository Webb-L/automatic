package top.webb_l.automatic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.acitivity.EditAutoMaticActivity;
import top.webb_l.automatic.acitivity.MainActivity;
import top.webb_l.automatic.data.ScriptInfo;

public class AutoMaticAdapter extends RecyclerView.Adapter<AutoMaticAdapter.ViewHolder> {
    public ArrayList<ScriptInfo> scripts;
    private Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_item, parent, false);
        return new ViewHolder(view);
    }

    public AutoMaticAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScriptInfo scriptInfo = MainActivity.scripts.get(position);
        holder.appIcon.setImageDrawable(scriptInfo.getIcon());
        holder.scriptTitle.setText(scriptInfo.getTitle());
        holder.scriptDescription.setText(scriptInfo.getDescription());
        holder.delete.setOnClickListener(v -> {
            // 删除脚本信息.
            MainActivity.scripts.remove(position);
            MainActivity.mHandler.sendEmptyMessage(1);
        });
//        holder.use.setOnClickListener(null);
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditAutoMaticActivity.class);
            intent.putExtra("scriptId", scriptInfo.getId());
            intent.putExtra("status", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.scripts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView scriptTitle, scriptDescription;
        MaterialButton use, delete, editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editButton = itemView.findViewById(R.id.edit_button);
            appIcon = itemView.findViewById(R.id.app_icon);
            scriptTitle = itemView.findViewById(R.id.script_title);
            scriptDescription = itemView.findViewById(R.id.script_description);
            use = itemView.findViewById(R.id.use_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }
}
