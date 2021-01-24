package top.webb_l.automatic.data;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * @author Webb
 */
public class ScriptInfo {
    private int id;
    private String title, description, packageName, activity;
    private Drawable icon;
    private ArrayList<StepInfo> steps;

    public ScriptInfo(int id, String title, String description, String packageName, String activity, Drawable icon, ArrayList<StepInfo> steps) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.packageName = packageName;
        this.activity = activity;
        this.icon = icon;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public ArrayList<StepInfo> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<StepInfo> steps) {
        this.steps = steps;
    }


}
