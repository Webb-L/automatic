package top.webb_l.automatic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.litepal.LitePal;

import java.util.ArrayList;

import top.webb_l.automatic.R;
import top.webb_l.automatic.activity.EditScriptActivity;
import top.webb_l.automatic.activity.MainActivity;
import top.webb_l.automatic.activity.UseScriptActivity;
import top.webb_l.automatic.data.ScriptInfo;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;

/**
 * @author Webb
 */
public class ScriptsAdapter extends RecyclerView.Adapter<ScriptsAdapter.ViewHolder> {
    public ArrayList<ScriptInfo> scripts;
    private final Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_item, parent, false);
        return new ViewHolder(view);
    }

    public ScriptsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScriptInfo scriptInfo = scripts.get(position);
        holder.appIcon.setImageDrawable(scriptInfo.getIcon());
        holder.scriptTitle.setText(scriptInfo.getTitle());
        holder.scriptDescription.setText(scriptInfo.getDescription());
        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(mContext)
                    .setTitle(R.string.danger)
                    .setMessage("是否需要删除" + scriptInfo.getTitle() + "，点击确定后不可挽回。")
                    .setNegativeButton(mContext.getResources().getString(android.R.string.cancel), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(mContext.getResources().getString(android.R.string.ok), (dialog, which) -> {
                        LitePal.deleteAll(Steps.class, "scripts_id = ?", String.valueOf(scriptInfo.getId()));
                        LitePal.delete(Scripts.class, scriptInfo.getId());
                        scripts.remove(position);
                        notifyItemChanged(position);
                        Toast.makeText(mContext, mContext.getString(R.string.delete) + mContext.getString(R.string.successfully) + "!", Toast.LENGTH_SHORT).show();
                        if (scripts.size()<=0) {
                            MainActivity.rootNotData.setVisibility(View.VISIBLE);
                        }
                    })
                    .show();
        });
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditScriptActivity.class);
            intent.putExtra("scriptId", scriptInfo.getId());
            intent.putExtra("status", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
        holder.use.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, UseScriptActivity.class);
            intent.putExtra("scriptId", scriptInfo.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scripts.size();
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
