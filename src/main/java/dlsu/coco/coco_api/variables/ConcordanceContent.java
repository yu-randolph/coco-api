package dlsu.coco.coco_api.variables;

import org.json.JSONObject;

import java.util.ArrayList;

public class ConcordanceContent {
    private String keyword;
    private int keyword_Index;
    private String completeSentence;
    private ArrayList<WordContent> words;

    public ConcordanceContent(String keyword, int keyword_Index, String completeSentence, ArrayList<WordContent> words) {
        this.keyword = keyword;
        this.keyword_Index = keyword_Index;
        this.completeSentence = completeSentence;
        this.words = words;
    }

    public ConcordanceContent(){}

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getKeyword_Index() {
        return keyword_Index;
    }

    public void setKeyword_Index(int keyword_Index) {
        this.keyword_Index = keyword_Index;
    }

    public String getCompleteSentence() {
        return completeSentence;
    }

    public void setCompleteSentence(String completeSentence) {
        this.completeSentence = completeSentence;
    }

    public ArrayList<WordContent> getWords() {
        return words;
    }

    public void setWords(ArrayList<WordContent> words) {
        this.words = words;
    }

    public JSONObject getJSON()
    {
        return null;
    }
}
