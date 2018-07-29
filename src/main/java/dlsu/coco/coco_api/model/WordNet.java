package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.WordNetContent;
import edu.smu.tspell.wordnet.*;
import edu.smu.tspell.wordnet.impl.file.PropertyNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

public class WordNet {

    static WordNetDatabase database;
    private final static String[] SYNSET_TYPES = {"", "noun","verb","adjective","adverb"};

    private String word;

    private ArrayList<WordNetContent> nounSynonym;
    private ArrayList<WordNetContent> nounHyponym;
    private ArrayList<WordNetContent> nounHypernym;

    private ArrayList<WordNetContent> verbSynonym;
    private ArrayList<WordNetContent> verbHyponym;
    private ArrayList<WordNetContent> verbTroponym;

    private ArrayList<WordNetContent> adjectiveSynonym;

    private ArrayList<WordNetContent> adverbSynonym;

    public WordNet(String dictLocation, String word)
    {
        System.setProperty(PropertyNames.DATABASE_DIRECTORY,  dictLocation);
        System.out.println(System.getProperty(PropertyNames.DATABASE_DIRECTORY));

        database = WordNetDatabase.getFileInstance();

        this.word = word;

        this.getNoun();
        this.getVerb();
        this.getAdjective();
        this.getAdverb();

        this.printResult();
    }

    public ArrayList<String> relatedNouns(){
        ArrayList<String> relatedNouns = new ArrayList<String>();
        for(int i = 0; i < nounSynonym.size(); i++)
        {
            if(!relatedNouns.contains(nounSynonym.get(i).getWordForm()[0]))
            relatedNouns.add(nounSynonym.get(i).getWordForm()[0]);
        }
        for(int i = 0; i < nounHyponym.size(); i++)
        {
            if(!relatedNouns.contains(nounHyponym.get(i).getWordForm()[0]))
            relatedNouns.add(nounHyponym.get(i).getWordForm()[0]);
        }
        for(int i = 0; i < nounHypernym.size(); i++)
        {
            if(!relatedNouns.contains(nounHypernym.get(i).getWordForm()[0]))
            relatedNouns.add(nounHypernym.get(i).getWordForm()[0]);
        }

        relatedNouns = new ArrayList<String>(new LinkedHashSet<String>(relatedNouns));
        return relatedNouns;
    }

    public ArrayList<String> relatedVerbs(){

        ArrayList<String> relatedVerbs = new ArrayList<String>();
        for(int i = 0; i < verbSynonym.size(); i++)
        {
            if(!relatedVerbs.contains(verbSynonym.get(i).getWordForm()[0]))
            relatedVerbs.add(verbSynonym.get(i).getString());
        }
        for(int i = 0; i < verbHyponym.size(); i++)
        {
            if(!relatedVerbs.contains(verbHyponym.get(i).getWordForm()[0]))
            relatedVerbs.add(verbHyponym.get(i).getString());
        }
        for(int i = 0; i < verbTroponym.size(); i++)
        {
            if(!relatedVerbs.contains(verbTroponym.get(i).getWordForm()[0]))
            relatedVerbs.add(verbTroponym.get(i).getString());
        }
        return relatedVerbs;
    }

    public ArrayList<String> relatedAdverbs(){

        ArrayList<String> relatedAdverbs = new ArrayList<String>();
        for(int i = 0; i < adverbSynonym.size(); i++)
        {
            if(!relatedAdverbs.contains(adverbSynonym.get(i).getWordForm()[0]))
            relatedAdverbs.add(adverbSynonym.get(i).getString());
        }

        return relatedAdverbs;
    }

    public ArrayList<String> relatedAdjectives(){

        ArrayList<String> relatedAdjectives = new ArrayList<String>();
        for(int i = 0; i < adjectiveSynonym.size(); i++)
        {
            if(!relatedAdjectives.contains(adjectiveSynonym.get(i).getWordForm()[0]))
            relatedAdjectives.add(adjectiveSynonym.get(i).getString());
        }

        return relatedAdjectives;
    }


    public void printResult()
    {
        System.out.println("noun synonym");
        for(int i = 0; i < nounSynonym.size(); i++)
        {
            System.out.println(nounSynonym.get(i).getString());
        }

        System.out.println();
        System.out.println("noun hyponym");
        for(int i = 0; i < nounHyponym.size(); i++)
        {
            System.out.println(nounHyponym.get(i).getString());
        }

        System.out.println();
        System.out.println("noun hypernym");
        for(int i = 0; i < nounHypernym.size(); i++)
        {
            System.out.println(nounHypernym.get(i).getString());
        }

        System.out.println();
        System.out.println("verb synonym");
        for(int i = 0; i < verbSynonym.size(); i++)
        {
            System.out.println(verbSynonym.get(i).getString());
        }

        System.out.println();
        System.out.println("verb hyponym");
        for(int i = 0; i < verbHyponym.size(); i++)
        {
            System.out.println(verbHyponym.get(i).getString());
        }

        System.out.println();
        System.out.println("verb troponym");
        for(int i = 0; i < verbTroponym.size(); i++)
        {
            System.out.println(verbTroponym.get(i).getString());
        }

        System.out.println();
        System.out.println("adjective synonym");
        for(int i = 0; i < adjectiveSynonym.size(); i++)
        {
            System.out.println(adjectiveSynonym.get(i).getString());
        }

        System.out.println();
        System.out.println("adverb synonym");
        for(int i = 0; i < adverbSynonym.size(); i++)
        {
            System.out.println(adverbSynonym.get(i).getString());
        }
        System.out.println();
    }

    public ArrayList<WordNetContent> extractValues_NounSynset(ArrayList<NounSynset> synset)
    {
        ArrayList<WordNetContent> content = new ArrayList<>();

        for(int ctr = 0; ctr < synset.size(); ctr++)
        {
            String[] split = synset.get(ctr).toString().split("@|\\[|-");

            WordNetContent item = new WordNetContent(SynsetType.NOUN, split[2].replaceFirst("]", "").split(","), split[3].replaceFirst(" ", ""));
            content.add(item);
        }

        return content;
    }

    public ArrayList<WordNetContent> extractValues_VerbSynset(ArrayList<VerbSynset> synset)
    {
        ArrayList<WordNetContent> content = new ArrayList<>();

        for(int ctr = 0; ctr < synset.size(); ctr++)
        {
            String[] split = synset.get(ctr).toString().split("@|\\[|-");

            WordNetContent item = new WordNetContent(SynsetType.VERB, split[2].replaceFirst("]", "").split(","), split[3].replaceFirst(" ", ""));
            content.add(item);
        }

        return content;
    }

    public void getNoun()
    {
        nounSynonym = new ArrayList<>();
        nounHyponym = new ArrayList<>();
        nounHypernym = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<String>();
        ArrayList<NounSynset> synsetHyponym = new ArrayList<>();
        ArrayList<NounSynset> synsetHypernym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.NOUN))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            if(!duplicates.contains(item.getWordForm()[0])) {
                nounSynonym.add(item);
                duplicates.add(item.getWordForm()[0]);
            }
        }

        for(Synset synset : database.getSynsets(word, SynsetType.NOUN))
        {
            Collections.addAll(synsetHyponym, ((NounSynset) synset).getHypernyms());
            Collections.addAll(synsetHypernym, ((NounSynset) synset).getHyponyms());
        }

        nounHyponym = this.extractValues_NounSynset(synsetHyponym);
        nounHypernym = this.extractValues_NounSynset(synsetHypernym);
    }

    public void getVerb()
    {
        verbSynonym = new ArrayList<>();
        verbHyponym = new ArrayList<>();
        verbTroponym = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<String>();
        ArrayList<VerbSynset> synsetHyponym = new ArrayList<>();
        ArrayList<VerbSynset> synsetTroponym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.VERB))
        {

            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            if(!duplicates.contains(item.getWordForm()[0])) {
                verbSynonym.add(item);
                duplicates.add(item.getWordForm()[0]);
            }
        }

        for(Synset synset : database.getSynsets(word, SynsetType.VERB))
        {
            Collections.addAll(synsetHyponym, ((VerbSynset) synset).getHypernyms());
            Collections.addAll(synsetTroponym, ((VerbSynset) synset).getTroponyms());
        }

        verbHyponym = this.extractValues_VerbSynset(synsetHyponym);
        verbTroponym = this.extractValues_VerbSynset(synsetTroponym);
    }

    public void getAdjective()
    {
        adjectiveSynonym = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<String>();

        for(Synset synset : database.getSynsets(word, SynsetType.ADJECTIVE))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            if(!duplicates.contains(item.getWordForm()[0])) {
                adjectiveSynonym.add(item);
                duplicates.add(item.getWordForm()[0]);
            }
        }
    }

    public void getAdverb()
    {
        adverbSynonym = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<String>();
        for(Synset synset : database.getSynsets(word, SynsetType.ADVERB))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            if(!duplicates.contains(item.getWordForm()[0])) {
            adverbSynonym.add(item);
            }
        }
    }

    public ArrayList<WordNetContent> getNounSynonym() {
        return nounSynonym;
    }

    public ArrayList<WordNetContent> getNounHyponym() {
        return nounHyponym;
    }

    public ArrayList<WordNetContent> getNounHypernym() {
        return nounHypernym;
    }

    public ArrayList<WordNetContent> getVerbSynonym() {
        return verbSynonym;
    }

    public ArrayList<WordNetContent> getVerbHyponym() {
        return verbHyponym;
    }

    public ArrayList<WordNetContent> getVerbTroponym() {
        return verbTroponym;
    }

    public ArrayList<WordNetContent> getAdjectiveSynonym() { return adjectiveSynonym; }

    public ArrayList<WordNetContent> getAdverbSynonym() {
        return adverbSynonym;
    }

    public JSONArray getNounSynonymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : nounSynonym)
        {
            jsonArray.put(item.getWordForm()[0]);
        }

        return jsonArray;
    }

    public JSONArray getNounHyponymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : nounHyponym)
        {
            jsonArray.put(item.getWordForm()[0]);
        }

        return jsonArray;
    }

    public JSONArray getNounHypernymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : nounHypernym)
        {
            jsonArray.put(item.getWordForm()[0]);
        }

        return jsonArray;
    }

    public JSONArray getVerbSynonymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : verbSynonym)
        {
            jsonArray.put(item.getWordForm()[0]);
        }

        return jsonArray;
    }

    public JSONArray getVerbHyponymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : verbHyponym)
        {
            jsonArray.put(item.getWordForm()[0]);
        }

        return jsonArray;
    }

    public JSONArray getVerbTroponymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : verbTroponym)
        {
            jsonArray.put(item.getWordForm()[0]);
        }

        return jsonArray;
    }

    public JSONArray getAdjectiveSynonymJSONObject() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : adjectiveSynonym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;}

    public JSONArray getAdverbSynonymJSONObject() throws JSONException {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : adverbSynonym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }
}