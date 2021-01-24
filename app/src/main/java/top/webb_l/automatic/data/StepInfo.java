package top.webb_l.automatic.data;

/**
 * @author Webb
 */
public class StepInfo {
    private boolean searchType;
    private String searchContent;
    private int event, control;

    public StepInfo(boolean searchType, String searchContent, int event, int control) {
        this.searchType = searchType;
        this.searchContent = searchContent;
        this.event = event;
        this.control = control;
    }

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

    public int getControl() { return control; }

    public void setControl(int control) { this.control = control; }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
