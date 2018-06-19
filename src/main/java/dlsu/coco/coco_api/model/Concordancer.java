package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.*;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusWriter;
import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.TagContent;
import dlsu.coco.coco_api.variables.WordContent;
import org.eclipse.emf.common.util.EList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Concordancer {

    private Corpus corpus;
    private ArrayList<ConcordanceContent> concordanceContents;

    private ArrayList<String> concept;
    private ArrayList<String> concepts = new ArrayList<String>();

    public Concordancer(ArrayList<String> concepts, Corpus corpus)
    {
        this.concepts = concepts;
        this.corpus = corpus;

    }

//    public JSONObject getWordNetResult()
//    {
//        JSONObject jsonObject = new JSONObject();
//
//        jsonObject.put("NOUN_SYNONYM", wordNet.getNounSynonymJSONObject());
//        jsonObject.put("NOUN_HYPONYM", wordNet.getNounHyponymJSONObject());
//        jsonObject.put("NOUN_HYPERNYM", wordNet.getNounHypernymJSONObject());
//        jsonObject.put("VERB_SYNONYM", wordNet.getVerbSynonymJSONObject());
//        jsonObject.put("VERB_HYPONYM", wordNet.getVerbHyponymJSONObject());
//        jsonObject.put("VERB_TROPONYM", wordNet.getVerbTroponymJSONObject());
//        jsonObject.put("ADJECTIVE_SYNONYM", wordNet.getAdjectiveSynonymJSONObject());
//        jsonObject.put("ADVERB_SYNONYM", wordNet.getAdverbSynonymJSONObject());
//
//        return jsonObject;
//    }
//
//    public JSONObject getConceptNetResult()
//    {
//        JSONObject jsonObject = new JSONObject();
//
////        jsonObject.put("RELATED_TO", conceptNet.getRelatedToJSONObject());
//        jsonObject.put("FORM_OF", conceptNet.getFormOfJSONObject());
////        jsonObject.put("IS_A", conceptNet.getIsAJSONObject());
////        jsonObject.put("PART_OF", conceptNet.getPartOfJSONObject());
////        jsonObject.put("CREATED_BY", conceptNet.getCreatedByJSONObject());
//
//        return jsonObject;
//    }

    public JSONObject getConcordanceResult() throws JSONException {
        this.concepts = new ArrayList<String>(new LinkedHashSet<String>(this.concepts));
      
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        concordanceContents = new ArrayList<>();
        ArrayList<WordContent> wordContent = new ArrayList<>();

        //CHECK ALL KEYWORDS
        for(String keyword : this.concepts)
        {

            //SEGMENT || CONTENT
            for(Segment content : corpus.getSegments())
            {
                //GRAPH || SENTENCE
                for(Graph sentence : content.getGraphs())
                {
                    ConcordanceContent item = new ConcordanceContent();
                    item.setKeyword(keyword);
                    item.setSentenceId(sentence.getId());
                    boolean keywordExist = false;
                    String completeSentence = "";

                    //TERMINAL || WORD
                    for(int ctr = 0; ctr < sentence.getTerminals().size(); ctr++)
                    {
                        completeSentence += sentence.getTerminals().get(ctr).getWord() + " ";
                        ArrayList<TagContent> tagContents = new ArrayList<>();

                        //TAGS
                        for(Annotation tag : sentence.getTerminals().get(ctr).getAnnotations()) {
                            tagContents.add(new TagContent(tag.getName(), tag.getValue()));
                        }
                        wordContent.add(new WordContent(sentence.getTerminals().get(ctr).getWord(), tagContents, sentence.getTerminals().get(ctr).getId()));

                        keyword = keyword.replaceAll("\\s","");
                        if(sentence.getTerminals().get(ctr).getWord().equalsIgnoreCase(keyword))
                        {

                            keywordExist = true;
                            item.setKeyword_Index(ctr);
                        }
                    }

                    if(keywordExist)
                    {
                        item.setWords(wordContent);
                        item.setCompleteSentence(completeSentence);
                        concordanceContents.add(item);
                    }
                    wordContent = new ArrayList<>();
                }
            }
        }

        System.out.println(concordanceContents.size());

        for(ConcordanceContent concordanceContent : concordanceContents)
        {
            jsonArray.put(concordanceContent.getJSON());
        }
        jsonObject.put("CONCORDANCE", jsonArray);

        return jsonObject;
    }

    public JSONObject getAdvancedResults(ArrayList<String> tags) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        concordanceContents = new ArrayList<>();
        ArrayList<WordContent> wordContent = new ArrayList<>();
        //CHECK ALL KEYWORDS
        for(String keyword : this.concepts)
        {

            //SEGMENT || CONTENT
            for(Segment content : corpus.getSegments())
            {
                //GRAPH || SENTENCE
                for(Graph sentence : content.getGraphs())
                {
                    ConcordanceContent item = new ConcordanceContent();
                    item.setKeyword(keyword);

                    boolean keywordExist = false;
                    String completeSentence = "";

                    //TERMINAL || WORD
                    for(int ctr = 0; ctr < sentence.getTerminals().size(); ctr++)
                    {
                        completeSentence += sentence.getTerminals().get(ctr).getWord() + " ";
                        ArrayList<TagContent> tagContents = new ArrayList<>();

                        //TAGS
                        for(Annotation tag : sentence.getTerminals().get(ctr).getAnnotations())
                        {
                            tagContents.add(new TagContent(tag.getName(), tag.getValue()));
                        }



                        wordContent.add(new WordContent(sentence.getTerminals().get(ctr).getWord(), tagContents,sentence.getTerminals().get(ctr).getId()));

                        if(sentence.getTerminals().get(ctr).getWord().equals(keyword))
                        {
                            int cnfrm = 0;

                            loop1:   for(Annotation a : sentence.getTerminals().get(ctr).getAnnotations()) {
                                for (String annotation : tags)
                                    if (a.getValue().equals(annotation)) {
                                        cnfrm++;
                                        tags.remove(annotation);
                                        continue loop1;
                                }
                            }
                                if(cnfrm == tags.size()) {
                                System.out.println(wordContent.size());
                                    keywordExist = true;
                                    item.setKeyword_Index(ctr);
                                }

                        }
                    }

                    if(keywordExist)
                    {
                        item.setWords(wordContent);
                        item.setCompleteSentence(completeSentence);
                        concordanceContents.add(item);
                    }
                    wordContent = new ArrayList<>();

                }
            }

        }

        for(ConcordanceContent concordanceContent : concordanceContents)
        {
            jsonArray.put(concordanceContent.getJSON());
        }
        jsonObject.put("CONCORDANCE", jsonArray);

        System.out.println(jsonObject.toString());
        return jsonObject;
    }
}
