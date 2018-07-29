package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.CorpusCreater;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class PatternFilter {

    private ArrayList<ConcordanceContent> patternList = new ArrayList<>();
    private ArrayList<ConcordanceContent> sentenceList = new ArrayList<>();
    private ArrayList<String> listID = new ArrayList<>();

    @SuppressWarnings("Duplicates")
    public void JSONparser(String sJsonContent)
    {
        try
        {
            System.out.println("PARSING JSON!");
            JSONObject jsonObject = new JSONObject(sJsonContent);

            JSONArray jsonConcordance = jsonObject.getJSONArray("concordances");

            for(int conCtr = 0; conCtr < jsonConcordance.length(); conCtr++)
            {
                JSONObject jsonConc = jsonConcordance.getJSONObject(conCtr);
                sentenceList.add(new ConcordanceContent());
                sentenceList.get(sentenceList.size()-1).readJSON(jsonConc);
            }

            //PATTERN IDs
            JSONArray jsonArray = jsonObject.getJSONArray("patternFilters");
            if (jsonArray != null)
            {
                for (int i=0; i<jsonArray.length(); i++)
                {
                    listID.add(jsonArray.getString(i));
                }
            }

            //PATTERN LIST
            JSONObject jsonObjectPatterns = jsonObject.getJSONObject("patterns");
            JSONArray jsonArrayPatterns = jsonObjectPatterns.getJSONArray("patterns");
            for (int patternCtr = 0; patternCtr < jsonArrayPatterns.length(); patternCtr++)
            {
                ConcordanceContent pattern = new ConcordanceContent();
                JSONObject jsonPattern = jsonArrayPatterns.getJSONObject(patternCtr);
                pattern.readPatternJSON(jsonPattern);

                patternList.add(pattern);
                System.out.println("PATTERN CONTENT");
                pattern.printWordContents();
                System.out.println();
            }

            System.out.println("PATTERN LIST SIZE : " + patternList.size());
        } catch(JSONException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("CONC SIZE : " + sentenceList.size());
        System.out.println("PAT SIZE :  " + patternList.size());
        System.out.println("ID SIZE : " + listID.size());
        System.out.println("");
    }

    public JSONObject getFilteredByID()  {
        try
        {
            JSONArray patterns = new JSONArray();

            for(int ctr = 0; ctr < patternList.size(); ctr++)
            {
                if(listID.contains(patternList.get(ctr).getSentenceId()))
                {
                    System.out.println("");
                    System.out.println("CONTAINS!");
                    System.out.println("");

                    JSONObject pattern = new JSONObject();
                    pattern.put("pattern", patternList.get(ctr).getSummaryJSON());
                    pattern.put("id", patternList.get(ctr).getSentenceId());

                    JSONArray patternOrigin = new JSONArray();
                    for(String originID : patternList.get(ctr).getPatternOrigin())
                    {
                        System.out.println("ORIGIN!");
                        for(int originfinder = 0; originfinder < sentenceList.size(); originfinder++)
                        {
                            System.out.println("ORIGIN ID : " + originID);
                            System.out.println("SENTENCE ID : " + sentenceList.get(originfinder).getSentenceId());
                            if(originID.equals(sentenceList.get(originfinder).getSentenceId()))
                            {
                                patternOrigin.put(sentenceList.get(originfinder).getSummaryJSON());
                            }
                        }
                    }

                    pattern.put("originSentences", patternOrigin);
                    pattern.put("frequency", patternList.get(ctr).getFreq());
                    patterns.put(pattern);
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("patterns", patterns);
            System.out.println(jsonObject.toString());
            return jsonObject;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
