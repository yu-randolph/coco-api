package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.TagContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatternSearcher {

    private JSONArray jsonConcordance;
    private JSONObject jsonSuper;

    private ArrayList<ConcordanceContent> listSentence;
    private ArrayList<ConcordanceContent> listMatch;
    private String pattern;

    public PatternSearcher(JSONObject jsonSuper) throws JSONException {

        this.listSentence = new ArrayList<>();
        this.listMatch = new ArrayList<>();
        this.jsonSuper = jsonSuper;
        this.pattern = "";

        this.concordanceParser();
    }

    @SuppressWarnings("Duplicates")
    private void concordanceParser() throws JSONException {
        jsonConcordance = jsonSuper.getJSONArray("concordance");

        for(int conCtr = 0; conCtr < jsonConcordance.length(); conCtr++)
        {
            JSONObject jsonObject = jsonConcordance.getJSONObject(conCtr);
            listSentence.add(new ConcordanceContent());
            listSentence.get(listSentence.size()-1).readJSON(jsonObject);
        }

        pattern = jsonSuper.getString("patternString");
    }

    @SuppressWarnings("Duplicates")
    public JSONObject findPattern() throws JSONException {

       for(ConcordanceContent concItem : listSentence)
       {
           String conc = "";
           for(int concCtr = 0; concCtr < concItem.getWords().size(); concCtr++)
           {
                for(int tagCtr = 0; tagCtr < concItem.getWords().get(concCtr).getTags().size(); tagCtr++)
                {
                    if(concItem.getWords().get(concCtr).getTags().get(tagCtr).getTagName().equals("pos"))
                    {
                        if(concCtr == concItem.getWords().size())
                        {
                            conc += concItem.getWords().get(concCtr).getTags().get(tagCtr).getTagValue();
                        }
                        else
                        {
                            conc += concItem.getWords().get(concCtr).getTags().get(tagCtr).getTagValue() + "-";
                        }
                    }
                }
           }

           if(conc.contains(pattern))
           {
               listMatch.add(concItem);
           }
       }

       JSONArray jsonArrayMatch = new JSONArray();
       JSONObject jsonObjectMatch = new JSONObject();

        for(ConcordanceContent concordanceContent : listMatch)
        {
            jsonArrayMatch.put(concordanceContent.getJSON());
        }
        jsonObjectMatch.put("CONCORDANCE", jsonArrayMatch);

        System.out.println(jsonObjectMatch.toString());
        return jsonObjectMatch;
    }
}
