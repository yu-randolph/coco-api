package dlsu.coco.coco_api.variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.text.html.HTML;
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

    public JSONObject getJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyword", this.keyword);
        jsonObject.put("keyword_index", this.keyword_Index);
        jsonObject.put("completeSentence", this.completeSentence);

        JSONArray listGraph = new JSONArray();

            for(WordContent word: this.words) {
                    JSONObject wordContent = new JSONObject();
                      JSONObject tagContent = new JSONObject();
                    wordContent.put("word",word.getWord());
                    for(TagContent tag : word.getTags()) {

                        tagContent.put("name",tag.getTagName());
                        tagContent.put("value",tag.getTagValue());
                    }
                    wordContent.put("tags",tagContent);
                    listGraph.put(wordContent);
            }
        jsonObject.put("WordContent", listGraph);
        System.out.println(jsonObject);
        return jsonObject;



    }
}
