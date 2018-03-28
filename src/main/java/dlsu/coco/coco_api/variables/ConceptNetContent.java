package dlsu.coco.coco_api.variables;

import org.json.JSONObject;

public class ConceptNetContent {
    private String relation;
    private String startWord;
    private String endWord;
    private String surfaceText;
    private Float weight;

    public ConceptNetContent(String relation, String startWord, String endWord, String surfaceText, Float weight) {
        this.relation = relation;
        this.startWord = startWord;
        this.endWord = endWord;
        this.surfaceText = surfaceText;
        this.weight = weight;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getStartWord() {
        return startWord;
    }

    public void setStartWord(String startWord) {
        this.startWord = startWord;
    }

    public String getEndWord() {
        return endWord;
    }

    public void setEndWord(String endWord) {
        this.endWord = endWord;
    }

    public String getSurfaceText() {
        return surfaceText;
    }

    public void setSurfaceText(String surfaceText) {
        this.surfaceText = surfaceText;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String toString()
    {
        return relation + " : " + startWord + " ~ " + endWord + " | " + surfaceText  + " = " + weight;
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("relation", relation);
        jsonObject.put("startWord", startWord);
        jsonObject.put("endWord", endWord);
        jsonObject.put("surfaceText", surfaceText);
        jsonObject.put("weight", weight);

        return jsonObject;
    }
}


