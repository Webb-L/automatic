package top.webb_l.automatic.model;

import android.content.ContentValues;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.HashMap;

public class Steps extends LitePalSupport {
    private String searchContent;
    private int id, searchType, event, control;
    private Scripts script;

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }

    public Scripts getScript() {
        return script;
    }

    public void setScript(Scripts script) {
        this.script = script;
    }

    public Steps find(int id) {
        return LitePal.find(Steps.class, id);
    }

    public int update(HashMap<String, String> updateData) {
        ContentValues values = new ContentValues();
        for (String key : updateData.keySet()) {
            values.put(key, updateData.get(key));
        }
        return LitePal.update(Steps.class, values, getId());
    }
}
