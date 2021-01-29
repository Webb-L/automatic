package top.webb_l.automatic.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AppInfoUtils {
    private final PackageManager packageManager;

    public AppInfoUtils(Context mContext) {
        packageManager = mContext.getPackageManager();
    }


    public String[] getActivities(String packageName) {
        ArrayList<String> strings = new ArrayList<>();
        try {
            PackageInfo applicationInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            for (ActivityInfo activity : applicationInfo.activities) {
                strings.add(activity.name.replace(packageName, ""));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return strings.toArray(new String[strings.size()]);
    }

    public String[] getPackageInfoList() {
        ArrayList<String> strings = new ArrayList<>();
        try {
            List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            for (int i = 0; i < apps.size() - 1; i++) {
                ApplicationInfo info = apps.get(i).applicationInfo;
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    strings.add(info.loadLabel(packageManager).toString() + "/r/" + info.packageName);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return strings.toArray(new String[strings.size()]);
    }
}
