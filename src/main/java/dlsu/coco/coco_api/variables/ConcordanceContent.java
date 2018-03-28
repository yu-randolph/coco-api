package dlsu.coco.coco_api.variables;

import java.util.ArrayList;

public class ConcordanceContent {
    private String keyword;
    private String keyword_ID;
    private ArrayList<String> sentence;
    private ArrayList<ArrayList<TagContent>> tags;

    public ConcordanceContent(String keyword, String keyword_ID, ArrayList<String> sentence, ArrayList<ArrayList<TagContent>> tags) {
        this.keyword = keyword;
        this.keyword_ID = keyword_ID;
        this.sentence = sentence;
        this.tags = tags;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword_ID() {
        return keyword_ID;
    }

    public void setKeyword_ID(String keyword_ID) {
        this.keyword_ID = keyword_ID;
    }

    public ArrayList<String> getSentence() {
        return sentence;
    }

    public void setSentence(ArrayList<String> sentence) {
        this.sentence = sentence;
    }

    public ArrayList<ArrayList<TagContent>> getTags() {
        return tags;
    }

    public void setTags(ArrayList<ArrayList<TagContent>> tags) {
        this.tags = tags;
    }
}
