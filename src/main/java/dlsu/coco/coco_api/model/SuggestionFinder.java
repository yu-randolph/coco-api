package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.*;
import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.CorpusCreater;
import dlsu.coco.coco_api.variables.TagContent;
import dlsu.coco.coco_api.variables.WordContent;
import org.eclipse.emf.common.util.EList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SuggestionFinder {

    private Corpus corpus;
    private ArrayList<ConcordanceContent> concordanceContents;

    private ArrayList<String> concept;
    private ArrayList<String> concepts = new ArrayList<String>();
    private ArrayList<String> tags;
    private String pos;

    private ArrayList<ConcordanceContent> patternList = new ArrayList<>();

    @SuppressWarnings("Duplicates")
    public void JSONparser(String sJsonContent) throws JSONException
    {
        //CORPUS
        JSONObject jsonObject = new JSONObject(concepts);
        JSONObject jsonConceptlist = new JSONObject(jsonObject.get("conceptlist").toString());
        JSONObject jsonWordnet = new JSONObject(jsonConceptlist.get("WORDNET").toString());
        JSONObject jsonTags = new JSONObject(jsonObject.get("tags").toString());
        JSONObject jsonAnnotations = new JSONObject(jsonObject.get("annotations").toString());

        Iterator<?> wordnetKeys = jsonWordnet.keys();
        ArrayList<String> conceptList = new ArrayList<>();

        CorpusCreater cc = new CorpusCreater(jsonTags, jsonAnnotations);
        cc.annotationsToArrayList();
        cc.sentencesToArrayList();
        this.corpus = cc.getCorpus();

        if(jsonWordnet.toString().contains("NOUN"))
        {
            pos = "NN";
        }
        else
        {
            pos = "VB";
        }


        while (wordnetKeys.hasNext())
        {
            String sKey = (String) wordnetKeys.next();
            JSONArray jsonContents = new JSONArray(jsonWordnet.get(sKey).toString());

            for (int ctr = 0; ctr < jsonContents.length(); ctr++)
            {
                conceptList.add(jsonContents.get(ctr).toString());
            }
        }

        //CONCEPT LIST
        JSONArray conceptNet = new JSONArray(jsonConceptlist.get("FORM_OF").toString());

        for (int conceptCtr = 0; conceptCtr < conceptNet.length(); conceptCtr++)
        {
            conceptList.add(conceptNet.get(conceptCtr).toString());
        }
        this.concepts = conceptList;


        //PATTERN LIST
        JSONArray jsonPatterns = new JSONArray(jsonObject.get("patterns").toString());
        for (int patternCtr = 0; patternCtr < jsonPatterns.length(); patternCtr++)
        {
            ConcordanceContent pattern = new ConcordanceContent();
            JSONObject jsonPattern = jsonPatterns.getJSONObject(patternCtr);
            pattern.readPatternJSON(jsonPattern);

            patternList.add(pattern);
        }
    }

    public JSONObject getSuggestions(String sJsonContent) throws JSONException
    {
        System.out.println("GET SUGGESTION!");
        this.JSONparser(sJsonContent);

        ArrayList<String> finalSuggestions = new ArrayList<>();
        for(ConcordanceContent pattern : patternList)
        {
            finalSuggestions.addAll(this.boyerMoore(pattern));
        }

        System.out.println("SUGGESTION FINAL");
        for(int i = 0; i < finalSuggestions.size(); i++)
        {
            System.out.println(finalSuggestions.get(i));
        }
        System.out.println();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonSuggestions = new JSONArray();

        Set<String> uniqueSet = new HashSet<String>(finalSuggestions);
        for (String sKey : uniqueSet)
        {
            System.out.println("Suggest : " + sKey + " : " + Collections.frequency(finalSuggestions, sKey));

            JSONObject jsonSuggestion = new JSONObject();
            jsonSuggestion.put("word", sKey);
            jsonSuggestion.put("frequency", Collections.frequency(finalSuggestions, sKey));
            jsonSuggestions.put(jsonSuggestion);
        }

        jsonObject.put("suggestions", jsonSuggestions);
        System.out.println("SUGGESTIONS : " + jsonObject);
        return jsonObject;
    }

    public ArrayList<String> boyerMoore(ConcordanceContent pattern)
    {
        //COMPARE PATTERN WITH EACH SENTENCE IN THE CORPUS
        ArrayList<String> suggestions = new ArrayList<>();

        //EXTRACT EACH SENTENCE FROM THE CORPUS
        Segment corpusContent = corpus.getSegments().get(0);
        for(Graph sentence : corpusContent.getGraphs())
        {
            //BOYER MOORE ALGORITHM APPLICATION
            //BAD CHARACTER RULE
            EList<Terminal> sentenceWords = sentence.getTerminals();

            //TEXT = sentence
            //PATTERN = patternWords
            ArrayList<WordContent> patternWords = pattern.getWords();
            ArrayList<Integer> results = new ArrayList<>();

            Integer textCtr = patternWords.size() - 1;
            Integer patternCtr = patternWords.size() - 1;

            do
            {
                if(this.compareWords(patternWords.get(patternCtr), sentenceWords.get(textCtr)))
                {
                    if(patternCtr == 0)
                        results.add(textCtr);
                    else
                    {
                        textCtr -= 1;
                        patternCtr -= 1;
                    }
                }
                else
                {
                    textCtr = textCtr + patternWords.size() - Math.min(patternCtr, 1 + boyerMooreLast(sentenceWords.get(textCtr), patternWords));
                    patternCtr = patternWords.size() - 1;
                }
            }
            while(textCtr > sentence.getTerminals().size());

            //GET THE SUGGESTED WORD
            for(int resultCtr = 0; resultCtr < results.size(); resultCtr++)
            {
                suggestions.add(sentenceWords.get(results.get(resultCtr) + pattern.getKeyword_Index()).getWord().toLowerCase());
            }
        }

        return suggestions;
    }

    private Integer boyerMooreLast(Terminal sentenceWord, ArrayList<WordContent> patternWords)
    {
        for(int ctr = patternWords.size() - 1; ctr >= 0; ctr--)
        {
            if(compareWords(patternWords.get(ctr), sentenceWord))
                return ctr;
        }

        return -1;
    }

    private Boolean compareWords(WordContent patternWord, Terminal corpusWord)
    {
        EList<Annotation> corpusTags = corpusWord.getAnnotations();
        ArrayList<TagContent> patternTags = patternWord.getTags();

        for(Annotation corpusTag : corpusTags)
        {
            for(TagContent patternTag : patternTags)
            {
                if(corpusTag.getName().equals(patternTag.getTagName()) && corpusTag.getValue().equals(patternTag.getTagValue()) && !corpusTag.getValue().equals("O") && !patternTag.getTagValue().equals("O"))
                        return true;
            }
        }

        return false;
    }
}
