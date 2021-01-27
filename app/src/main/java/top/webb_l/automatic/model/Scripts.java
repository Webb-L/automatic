package top.webb_l.automatic.model;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;

public class Scripts extends LitePalSupport {
    private int id;
    private String title, description, packageName, activity;
    private ArrayList<Steps> stepIds = new ArrayList<Steps>();

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

    public ArrayList<Steps> getStepIds() {
        return stepIds;
    }

    public void setStepIds(ArrayList<Steps> stepIds) {
        this.stepIds = stepIds;
    }
}
