package top.webb_l.automatic.data;

/**
 * @author Webb
 */
public class StepInfo {
    private String searchContent,pasteContent;
    private int searchType, event, control;

    public StepInfo(String searchContent, String pasteContent, int searchType, int event, int control) {
        this.searchContent = searchContent;
        this.pasteContent = pasteContent;
        this.searchType = searchType;
        this.event = event;
        this.control = control;
    }

    public String getPasteContent() {
        return pasteContent;
    }

    public void setPasteContent(String pasteContent) {
        this.pasteContent = pasteContent;
    }

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

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }
}
