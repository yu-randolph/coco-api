package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.Corpus;
import de.hu_berlin.german.korpling.tiger2.Graph;
import de.hu_berlin.german.korpling.tiger2.Segment;
import de.hu_berlin.german.korpling.tiger2.Terminal;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusWriter;
import dlsu.coco.coco_api.variables.ConcordanceContent;
import org.eclipse.emf.common.util.EList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Concordancer {

    private Corpus corpus;
    private ArrayList<ConcordanceContent> concordanceContents;
    private ConceptNet conceptNet;
    private WordNet wordNet;
    private String keyword;

    public Concordancer(String keyword, String dictLocation, Corpus corpus)
    {
        this.keyword = keyword;
        this.corpus = corpus;

        concordanceContents = new ArrayList<>();
        conceptNet = new ConceptNet(keyword);
        wordNet = new WordNet(dictLocation, keyword);
    }

    public JSONObject getWordNetResult()
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("NOUN_SYNONYM", wordNet.getNounSynonymJSONObject());
        jsonObject.put("NOUN_HYPONYM", wordNet.getNounHyponymJSONObject());
        jsonObject.put("NOUN_HYPERNYM", wordNet.getNounHypernymJSONObject());
        jsonObject.put("VERB_SYNONYM", wordNet.getVerbSynonymJSONObject());
        jsonObject.put("VERB_HYPONYM", wordNet.getVerbHyponymJSONObject());
        jsonObject.put("VERB_TROPONYM", wordNet.getVerbTroponymJSONObject());
        jsonObject.put("ADJECTIVE_SYNONYM", wordNet.getAdjectiveSynonymJSONObject());
        jsonObject.put("ADVERB_SYNONYM", wordNet.getAdverbSynonymJSONObject());

        return jsonObject;
    }

    public JSONObject getConceptNetResult()
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("RELATED_TO", conceptNet.getRelatedToJSONObject());
        jsonObject.put("FORM_OF", conceptNet.getFormOfJSONObject());
        jsonObject.put("IS_A", conceptNet.getIsAJSONObject());
        jsonObject.put("PART_OF", conceptNet.getPartOfJSONObject());
        jsonObject.put("CREATED_BY", conceptNet.getCreatedByJSONObject());

        return jsonObject;
    }

    public JSONObject getConcordanceResult(String[] keywords)
    {
        //CHECK ALL KEYWORDS
        for(String item : keywords)
        {
            //SEGMENT || CONTENT
            for(Segment content : corpus.getSegments())
            {
                //GRAPH || SENTENCE
                for(Graph sentence : content.getGraphs())
                {
                    //TERMINAL || WORD
                    for(Terminal word : sentence.getTerminals())
                    {
                        if(item.equals(word.getWord()))
                        {
                            
                        }
                    }
                }
            }
        }

        return null;
    }
}
