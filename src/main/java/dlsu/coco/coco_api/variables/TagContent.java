package dlsu.coco.coco_api.variables;

public class TagContent {
    private String TagName;
    private String TagValue;

    public TagContent(String tagName, String tagValue) {
        TagName = tagName;
        TagValue = tagValue;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public String getTagValue() {
        return TagValue;
    }

    public void setTagValue(String tagValue) {
        TagValue = tagValue;
    }
}
