package top.webb_l.automatic.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import top.webb_l.automatic.R;
import top.webb_l.automatic.activity.EditScriptActivity;
import top.webb_l.automatic.activity.MainActivity;
import top.webb_l.automatic.activity.UseScriptActivity;
import top.webb_l.automatic.data.ScriptInfo;
import top.webb_l.automatic.encryption.ShareEncryption;
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

    @SuppressLint({"ResourceType", "NonConstantResourceId"})
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

    /**
     * 分享脚本到剪切板
     *
     * @param context    上下文用于获取字符串
     * @param scriptInfo 脚本信息
     */
    private void shareScript(Context context, ScriptInfo scriptInfo) {
        JSONObject script = new JSONObject();
        try {
            script.put("title", scriptInfo.getTitle());
            script.put("description", scriptInfo.getDescription());
            script.put("packageName", scriptInfo.getPackageName());
            script.put("activity", scriptInfo.getActivity());
            JSONArray steps = new JSONArray();
            List<Steps> stepList = LitePal.where("scripts_id = ?", String.valueOf(scriptInfo.getId())).find(Steps.class);
            for (Steps step : stepList) {
                JSONObject stepInfo = new JSONObject();
                stepInfo.put("searchType", step.getSearchType());
                stepInfo.put("searchContent", step.getSearchContent());
                stepInfo.put("control", step.getControl());
                stepInfo.put("event", step.getEvent());
                stepInfo.put("pasteContent", step.getPasteContent());
                steps.put(stepInfo);
            }
            script.put("steps", steps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String encryption = ShareEncryption.encryption(script.toString());
        if (TextUtils.isEmpty(encryption)) {
            Toast.makeText(context, context.getString(R.string.share) + context.getString(R.string.failure), Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(ClipData.newPlainText("Label", "AUTO://" + encryption));
        Toast.makeText(context, "已保存到剪切板！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除脚本
     *
     * @param position   第几个item
     * @param scriptInfo 脚本信息
     */
    private void removeScript(int position, ScriptInfo scriptInfo) {
        new MaterialAlertDialogBuilder(mContext)
                .setTitle(R.string.danger)
                .setMessage("是否需要删除【" + scriptInfo.getTitle() + "】，点击确定后不可挽回。")
                .setNegativeButton(mContext.getResources().getString(android.R.string.cancel), (dialog, which) -> dialog.cancel())
                .setPositiveButton(mContext.getResources().getString(android.R.string.ok), (dialog, which) -> {
                    LitePal.deleteAll(Steps.class, "scripts_id = ?", String.valueOf(scriptInfo.getId()));
                    LitePal.delete(Scripts.class, scriptInfo.getId());
                    scripts.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, mContext.getString(R.string.delete) + mContext.getString(R.string.successfully) + "!", Toast.LENGTH_SHORT).show();
                    if (scripts.size() <= 0) {
                        MainActivity.rootNotData.setVisibility(View.VISIBLE);
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return scripts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView scriptTitle, scriptDescription;
        MaterialButton use, menu, editButton;
        Context context;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editButton = itemView.findViewById(R.id.edit_button);
            menu = itemView.findViewById(R.id.menu);
            appIcon = itemView.findViewById(R.id.app_icon);
            scriptTitle = itemView.findViewById(R.id.script_title);
            scriptDescription = itemView.findViewById(R.id.script_description);
            use = itemView.findViewById(R.id.use_button);
            context = itemView.getContext();
        }
    }
}
