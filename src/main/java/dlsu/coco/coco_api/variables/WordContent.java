package dlsu.coco.coco_api.variables;

import java.util.ArrayList;

public class WordContent {
    private String word;
    private String wordId;
    private ArrayList<TagContent> tags;

    public WordContent(String word, ArrayList<TagContent> tags, String wordId) {
        this.word = word;
        this.tags = tags;
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<TagContent> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagContent> tags) {
        this.tags = tags;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }


}
