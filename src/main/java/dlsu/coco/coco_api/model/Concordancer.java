package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.*;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusWriter;
import dlsu.coco.coco_api.variables.*;
import org.eclipse.emf.common.util.EList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Concordancer {

    private Corpus corpus;
    private ArrayList<ConcordanceContent> concordanceContents;

    private ArrayList<String> concept;
    private ArrayList<String> concepts = new ArrayList<String>();
    private ArrayList<String> tagsList;
    private String pos;
    private ArrayList<RemovedConcept> removedWordsList;
    public Concordancer(Corpus corpus)
    {

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


    public void JSONtoArrayConceptLIst(String concepts) throws JSONException {


        JSONObject jsonObject = new JSONObject(concepts);
        JSONObject arr = new JSONObject(jsonObject.get("conceptlist").toString());
        JSONObject wn = new JSONObject(arr.get("WORDNET").toString());
        JSONObject tags = new JSONObject(jsonObject.get("tags").toString());
        JSONObject anno = new JSONObject(jsonObject.get("annotations").toString());
        JSONArray addedwords,removedWords;
        ArrayList<String> addedWordsList = null;
        removedWordsList = new ArrayList<>();
        Iterator<?> keys;

        if(jsonObject.has("addedwords")) {
            addedwords = new JSONArray(jsonObject.get("addedwords").toString());
            addedWordsList = addToList(addedwords);
        }

        if(jsonObject.has("removedwords")) {
            removedWords = new JSONArray(jsonObject.get("removedwords").toString());
            removedWordsList = addToRemovedWords(removedWords);
        }

     
        keys = wn.keys();

        ArrayList<String> conceptList = addToConceptList(keys,wn,addedWordsList);

        //create corpus Object
//        CorpusCreater cc = new CorpusCreater(tags,anno);
//        cc.annotationsToArrayList();
//        cc.sentencesToArrayList();
//        this.corpus = cc.getCorpus();


        if(wn.toString().contains("NOUN")){
            pos = "NN";
        }
        else
            pos = "VB";

        JSONArray conceptNet = new JSONArray(arr.get("FORM_OF").toString());

        for (int x = 0; x < conceptNet.length(); x++) {
            conceptList.add(conceptNet.get(x).toString());
        }
        this.concepts = new ArrayList<String>(new LinkedHashSet<String>(this.concepts));
        this.concepts = conceptList;
    }

    public ArrayList<String> addToConceptList( Iterator<?> keys , JSONObject words, ArrayList<String> addedWords) throws JSONException{

       ArrayList<String> list = new ArrayList<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            JSONArray contents = null;
            try {
                contents = new JSONArray(words.get(key).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < contents.length(); j++) {
                try {
                    list.add(contents.get(j).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if(addedWords != null)
        list.addAll(addedWords);

        return list;
    }

    public ArrayList<String> addToList(JSONArray words) throws JSONException{

        ArrayList<String> list = new ArrayList<>();

            for (int j = 0; j < words.length(); j++) {
                try {
                    list.add(words.get(j).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        return list;
    }

    public ArrayList<RemovedConcept> addToRemovedWords(JSONArray words) throws JSONException {

        ArrayList<RemovedConcept> list = new ArrayList<>();

        for (int j = 0; j < words.length(); j++) {
            JSONObject curObJ = null;
            try {
                curObJ = words.getJSONObject(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                list.add(new RemovedConcept(curObJ.getString("concept").toString(),curObJ.getString("sentence_id").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }


    public void JSONToArrayAdvancedConceptList(String concepts) throws JSONException {

        JSONObject jsonObject = new JSONObject(concepts);
        JSONArray ann = new JSONArray(jsonObject.get("AnnotationsList").toString());
        Iterator<String> annKeys;
        this.tagsList = new ArrayList<>();


        for (int i = 0; i < ann.length(); i++) {
            JSONObject object = ann.getJSONObject(i);
            annKeys = object.keys();
        while (annKeys.hasNext()) {
            String keyValue = (String) annKeys.next();
            String valueString = object.getString(keyValue);
            tagsList.add(valueString);
            if(i == 0)
                if(valueString.toString().contains("Noun")){
                    pos = "NN";
                }
                else if (valueString.toString().contains("Verb"))
                    pos = "VB";
                else if(valueString.toString().contains("Adjective"))
                    pos = "JJ";
                else if(valueString.toString().contains("Adverb"))
                    pos = "RB";
        }

    }
        ArrayList<String> conceptList = new ArrayList<String>();


//        JSONObject arr = new JSONObject(jsonObject.get("conceptlist").toString());
//        JSONObject wn = new JSONObject(arr.get("WORDNET").toString());

        JSONObject wn = new JSONObject(jsonObject.get("WORDNET").toString());
        JSONObject tags = new JSONObject(jsonObject.get("tags").toString());
        JSONObject anno = new JSONObject(jsonObject.get("annotations").toString());
        JSONArray addedwords,removedWords;
        ArrayList<String> addedWordsList = null;
        removedWordsList = new ArrayList<>();
        Iterator<?> keys = wn.keys();
//
//       while (keys.hasNext()) {
//            String key = (String) keys.next();
//            System.out.println("KEYS" + key);
//            switch (key) {
//                case "NOUN_SYNONYM":
//                    if (rels.contains("Synonym")) {
//                        JSONArray contents = new JSONArray(wn.get(key).toString());
//                        for (int j = 0; j < contents.length(); j++) {
//                            conceptList.add(contents.get(j).toString());
//                        }
//                    }
//                    break;
//                case "NOUN_HYPERNYM":
//                    if (rels.contains("Hypernym")) {
//                        JSONArray contents = new JSONArray(wn.get(key).toString());
//                        for (int j = 0; j < contents.length(); j++) {
//                            conceptList.add(contents.get(j).toString());
//                        }
//                    }
//                    break;
//                case "NOUN_HYPONYM":
//                    if (rels.contains("Hyponym")) {
//                        JSONArray contents = new JSONArray(wn.get(key).toString());
//                        for (int j = 0; j < contents.length(); j++) {
//                            conceptList.add(contents.get(j).toString());
//                        }
//                    }
//                    break;
//                case "VERB_SYNONYM":
//                    if (rels.contains("Synonym")) {
//                        JSONArray contents = new JSONArray(wn.get(key).toString());
//                        for (int j = 0; j < contents.length(); j++) {
//                            conceptList.add(contents.get(j).toString());
//                        }
//                    }
//                    break;
//                case "VERB_TROPONYM":
//                    if (rels.contains("Troponym")) {
//                        JSONArray contents = new JSONArray(wn.get(key).toString());
//                        for (int j = 0; j < contents.length(); j++) {
//                            conceptList.add(contents.get(j).toString());
//                        }
//
//                    }
//                    break;
//                case "VERB_HYPONYM":
//                    if (rels.contains("Hyponym")) {
//                        JSONArray contents = new JSONArray(wn.get(key).toString());
//                        for (int j = 0; j < contents.length(); j++) {
//                            conceptList.add(contents.get(j).toString());
//                        }
//
//                    }
//                    break;
//
//            }
//
//        }

        if(jsonObject.has("addedwords")) {
            addedwords = new JSONArray(jsonObject.get("addedwords").toString());
            addedWordsList = addToList(addedwords);
        }

        if(jsonObject.has("removedwords")) {
            removedWords = new JSONArray(jsonObject.get("removedwords").toString());
            removedWordsList = addToRemovedWords(removedWords);
        }


        keys = wn.keys();

        conceptList.addAll(addToConceptList(keys,wn,addedWordsList));
        //create corpus Object
//        CorpusCreater cc = new CorpusCreater(tags,anno);
//        cc.annotationsToArrayList();
//        cc.sentencesToArrayList();
//        this.corpus = cc.getCorpus();


        if(wn.toString().contains("NOUN")){
            pos = "NN";
        }
        else
            pos = "VB";

        JSONArray conceptNet = new JSONArray(jsonObject.get("FORM_OF").toString());

        for (int x = 0; x < conceptNet.length(); x++) {
            conceptList.add(conceptNet.get(x).toString());
        }
        this.concepts = new ArrayList<String>(new LinkedHashSet<String>(this.concepts));
        this.concepts = conceptList;
    }


    public JSONObject getConcordanceResult() throws JSONException {
        this.concepts = new ArrayList<String>(new LinkedHashSet<String>(this.concepts));

//        Set<String> cns = new LinkedHashSet<String>(this.concepts);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        concordanceContents = new ArrayList<>();
        ArrayList<WordContent> wordContent = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();
//        String lemma ="";
        System.out.println(" this.concepts Size " + this.concepts.size());
        int i = 0;

        //CHECK ALL KEYWORDS
        for(String keyword : this.concepts)
        {
        i++;
            System.out.println("CONC KEY " + keyword + " " + i);
            //SEGMENT || CONTENT
          for(Segment content : corpus.getSegments())
            {
                //GRAPH || SENTENCE
                loop1:   for(Graph sentence : content.getGraphs())
                {
                    ConcordanceContent item = new ConcordanceContent();
                    item.setKeyword(keyword);
                    item.setSentenceId(sentence.getId());
                    boolean keywordExist = false, posVal = false;
                    String completeSentence = "";
//                    lemma = "";
                    //TERMINAL || WORD
                    for(int ctr = 0; ctr < sentence.getTerminals().size(); ctr++) {
                        posVal = false;
                        completeSentence += sentence.getTerminals().get(ctr).getWord() + " ";
                        ArrayList<TagContent> tagContents = new ArrayList<>();

                        //TAGS
                        for (Annotation tag : sentence.getTerminals().get(ctr).getAnnotations()) {
                            tagContents.add(new TagContent(tag.getName(), tag.getValue()));
                        }
                        wordContent.add(new WordContent(sentence.getTerminals().get(ctr).getWord(), tagContents, sentence.getTerminals().get(ctr).getId()));

                        keyword = keyword.replaceAll("\\s", "");

                        for (TagContent tag : tagContents) {
//                            if (tag.getTagName().contains("lemma")) {
//                                lemma = tag.getTagValue();
//                            }
                            if (tag.getTagName().contains("pos") && tag.getTagValue().contains(pos))
                                posVal = true;
                        }

                        if (sentence.getTerminals().get(ctr).getWord().equalsIgnoreCase(keyword)) {

                            for (RemovedConcept rc : removedWordsList) {
                                if (rc.getKey().equalsIgnoreCase(keyword) && rc.getSentenceid().equalsIgnoreCase(sentence.getId()))
                                    continue loop1;
                            }

                            if (posVal && !duplicates.contains(keyword)) {
                                keywordExist = true;
                                item.setKeyword_Index(ctr);
                            }

                        }

                    }
                    if (keywordExist) {
                        item.setWords(wordContent);
                        item.setCompleteSentence(completeSentence);
                        if (!concordanceContents.contains(item))
                            concordanceContents.add(item);
                        keywordExist = false;
                        posVal = false;

                    }
                    wordContent = new ArrayList<>();
                }

            }
            duplicates.add(keyword);
        }

        System.out.println(concordanceContents.size());

        for(ConcordanceContent concordanceContent : concordanceContents)
        {
            jsonArray.put(concordanceContent.getJSON());
        }
        jsonObject.put("CONCORDANCE", jsonArray);

        return jsonObject;
    }

        public JSONObject getAdvancedResults() throws JSONException {
            this.concepts = new ArrayList<String>(new LinkedHashSet<String>(this.concepts));

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            ArrayList<String> duplicates = new ArrayList<>();

            concordanceContents = new ArrayList<>();
            ArrayList<WordContent> wordContent = new ArrayList<>();
            System.out.println(" this.concepts Size " + this.concepts.size());

            int i = 0;

            //CHECK ALL KEYWORDS
        for(String keyword : this.concepts)
        {
            i++;
            System.out.println("CONC KEY ADVANCED " + keyword + " " + i);

            //SEGMENT || CONTENT
            for(Segment content : corpus.getSegments())
            {
                //GRAPH || SENTENCE
                for(Graph sentence : content.getGraphs())
                {
                    ConcordanceContent item = new ConcordanceContent();

                    item.setSentenceId(sentence.getId());


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
                        keyword = keyword.replaceAll("\\s", "");

                        item.setKeyword(keyword);
                        if(sentence.getTerminals().get(ctr).getWord().equalsIgnoreCase(keyword))
                        {
                            int cnfrm = 0;
                            loop1:   for(Annotation a : sentence.getTerminals().get(ctr).getAnnotations()) {
                                for (String annotation : tagsList) {
                                    if (a.getValue().equals(annotation) || (a.getName().equals("pos") && a.getValue().contains(this.pos))) {
                                        cnfrm++;
                                        continue loop1;
                                    }
                                }
                            }
                                if(cnfrm == tagsList.size()) {
                                    keywordExist = true;
                                    item.setKeyword_Index(ctr);
                                }

                        }
                    }

                    if(keywordExist)
                    {
                        item.setWords(wordContent);
                        item.setCompleteSentence(completeSentence);
                        if (!concordanceContents.contains(item) && !duplicates.contains(item.getSentenceId())) {
                            concordanceContents.add(item);
                            duplicates.add(item.getSentenceId());
                        }
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

        System.out.println(jsonObject.toString());
        return jsonObject;
    }

}
