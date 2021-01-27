package top.webb_l.automatic.model;

import org.litepal.crud.LitePalSupport;

public class Steps extends LitePalSupport {
    private boolean searchType;
    private String searchContent;
    private int id, event, control;
    private Scripts script;

    public boolean isSearchType() {
        return searchType;
    }

    public void setSearchType(boolean searchType) {
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
}
