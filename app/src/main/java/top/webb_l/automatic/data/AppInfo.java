package top.webb_l.automatic.data;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name, packageName;
    private Drawable icon;
    private PackageInfo applicationInfo;

    public AppInfo(String name, String packageName, Drawable icon, PackageInfo applicationInfo) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.applicationInfo = applicationInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public PackageInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(PackageInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }
}
