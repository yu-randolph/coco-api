package dlsu.coco.coco_api.variables;

import edu.smu.tspell.wordnet.SynsetType;
import org.json.JSONArray;
import org.json.JSONObject;

public class WordNetContent {
    private SynsetType POStype;
    private String[] wordForm;
    private String definition;

    public WordNetContent(SynsetType POStype, String[] wordForm, String definition) {
        this.POStype = POStype;
        this.wordForm = wordForm;
        this.definition = definition;
    }

    public SynsetType getPOStype() {
        return POStype;
    }

    public void setPOStype(SynsetType POStype) {
        this.POStype = POStype;
    }

    public String[] getWordForm() {
        return wordForm;
    }

    public void setWordForm(String[] wordForm) {
        this.wordForm = wordForm;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    private String wordFormToString()
    {
        String toString = "";

        for(int ctr = 0; ctr < wordForm.length; ctr++)
            toString += wordForm[ctr] + " ";

        return toString;
    }

    public String getString()
    {
        return POStype.toString() + " : " + this.wordFormToString() + " || " + definition;
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject_concept = new JSONObject();
        jsonObject_concept.put("POStype", POStype.toString());

        JSONArray jsonArray_wordForm = new JSONArray();
        for(String word : this.wordForm)
        {
            jsonArray_wordForm.put(word);
        }
        jsonObject_concept.put("wordForm", jsonArray_wordForm);

        jsonObject_concept.put("definition", definition);

        return jsonObject_concept;
    }
}
