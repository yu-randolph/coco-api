package dlsu.coco.coco_api.variables;

import java.util.ArrayList;

public class WordContent {
    private String word;
    private ArrayList<TagContent> tags;

    public WordContent(String word, ArrayList<TagContent> tags) {
        this.word = word;
        this.tags = tags;
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
}
