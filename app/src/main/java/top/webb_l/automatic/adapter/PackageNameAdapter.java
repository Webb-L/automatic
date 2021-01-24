package top.webb_l.automatic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.webb_l.automatic.R;
import top.webb_l.automatic.data.AppInfo;

public class PackageNameAdapter extends RecyclerView.Adapter<PackageNameAdapter.ViewHolder> {
    private List<AppInfo> apps = null;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
    }

    public PackageNameAdapter(Context context) {
        this.context = context;
    }

    public PackageNameAdapter(List<AppInfo> apps, Context context) {
        this.apps = apps;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo appInfo = apps.get(position);
        holder.appName.setText(appInfo.getName());
        holder.appPackage.setText(appInfo.getPackageName());
        holder.appIcon.setImageDrawable(appInfo.getIcon());
        holder.root.setOnClickListener(v -> {
            if (onItemClickListener!=null) {
                onItemClickListener.onItemClick(appInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        CardView root;
        TextView appName, appPackage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.cv_root);
            appIcon = itemView.findViewById(R.id.iv_appIcon);
            appName = itemView.findViewById(R.id.tv_appName);
            appPackage = itemView.findViewById(R.id.tv_appPackage);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AppInfo appInfo);
    }
}
