package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConcordanceContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatternSearcher {

    private int nThreshold;
    private int nLength;

    private JSONArray jsonConcordance;
    private JSONObject jsonSuper;

    private ArrayList<ConcordanceContent> listSentence;
    private ArrayList<ConcordanceContent> listPattern;
    private ArrayList<ConcordanceContent> listMatch;

    public PatternSearcher(JSONObject jsonSuper) throws JSONException {
        this.nThreshold = 1;
        this.nLength = 0;

        this.listSentence = new ArrayList<>();
        this.listPattern = new ArrayList<>();
        this.listMatch = new ArrayList<>();
        this.jsonSuper = jsonSuper;

        this.concordanceParser();
        this.findPattern();
    }

    @SuppressWarnings("Duplicates")
    private void concordanceParser() throws JSONException {
        jsonConcordance = jsonSuper.getJSONArray("CONCORDANCE");

        for(int conCtr = 0; conCtr < jsonConcordance.length(); conCtr++)
        {
            JSONObject jsonObject = jsonConcordance.getJSONObject(conCtr);
            listSentence.add(new ConcordanceContent());
            listSentence.get(listSentence.size()-1).readJSON(jsonObject);
        }

        JSONObject jsonObjectPatterns = new JSONObject(jsonSuper.get("patterns").toString());
        JSONArray jsonArrayPatterns = jsonObjectPatterns.getJSONArray("patterns");
        for (int patternCtr = 0; patternCtr < jsonArrayPatterns.length(); patternCtr++)
        {
            ConcordanceContent pattern = new ConcordanceContent();
            JSONObject jsonPattern = jsonArrayPatterns.getJSONObject(patternCtr);
            pattern.readPatternJSON(jsonPattern);

            listPattern.add(pattern);
            System.out.println("PATTERN CONTENT");
            pattern.printWordContents();
            System.out.println();
        }

        nThreshold = jsonSuper.getInt("THRESHOLD");
        nLength = jsonSuper.getInt("LENGTH");
    }

    private JSONObject findPattern() throws JSONException {
       for(ConcordanceContent concItem : listSentence)
       {
           if(concItem.compareConcordanceWithPattern(listPattern.get(0)))
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
