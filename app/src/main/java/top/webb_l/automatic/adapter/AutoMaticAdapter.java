package top.webb_l.automatic.adapter;

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
import top.webb_l.automatic.acitivity.MainActivity;
import top.webb_l.automatic.data.ScriptInfo;

public class AutoMaticAdapter extends RecyclerView.Adapter<AutoMaticAdapter.ViewHolder> {
    public ArrayList<ScriptInfo> scripts;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScriptInfo scriptInfo = MainActivity.scripts.get(position);
        holder.appIcon.setImageDrawable(scriptInfo.getIcon());
        holder.scriptTitle.setText(scriptInfo.getTitle());
        holder.scriptDescription.setText(scriptInfo.getDescription());
        holder.use.setOnClickListener(v -> MainActivity.scripts.remove(position));
        holder.delete.setOnClickListener(v -> {
            MainActivity.scripts.remove(position);
            MainActivity.mHandler.sendEmptyMessage(1);
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.scripts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView scriptTitle, scriptDescription;
        MaterialButton use, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            scriptTitle = itemView.findViewById(R.id.script_title);
            scriptDescription = itemView.findViewById(R.id.script_description);
            use = itemView.findViewById(R.id.use_button);
            delete = itemView.findViewById(R.id.delete_button);
        }
    }

    public void setData() {

    }

}
