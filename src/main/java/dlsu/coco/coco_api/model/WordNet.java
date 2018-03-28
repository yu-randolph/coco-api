package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.WordNetContent;
import edu.smu.tspell.wordnet.*;
import edu.smu.tspell.wordnet.impl.file.PropertyNames;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

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

        ArrayList<NounSynset> synsetHyponym = new ArrayList<>();
        ArrayList<NounSynset> synsetHypernym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.NOUN))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            nounSynonym.add(item);
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

        ArrayList<VerbSynset> synsetHyponym = new ArrayList<>();
        ArrayList<VerbSynset> synsetTroponym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.VERB))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            verbSynonym.add(item);
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

        for(Synset synset : database.getSynsets(word, SynsetType.ADJECTIVE))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            adjectiveSynonym.add(item);
        }
    }

    public void getAdverb()
    {
        adverbSynonym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.ADVERB))
        {
            WordNetContent item = new WordNetContent(synset.getType(), synset.getWordForms(), synset.getDefinition());
            adverbSynonym.add(item);
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

    public JSONArray getNounSynonymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : nounSynonym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }

    public JSONArray getNounHyponymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : nounHyponym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }

    public JSONArray getNounHypernymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : nounHypernym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }

    public JSONArray getVerbSynonymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : verbSynonym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }

    public JSONArray getVerbHyponymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : verbHyponym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }

    public JSONArray getVerbTroponymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : verbTroponym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }

    public JSONArray getAdjectiveSynonymJSONObject() {
        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : adjectiveSynonym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;}

    public JSONArray getAdverbSynonymJSONObject() {

        JSONArray jsonArray = new JSONArray();

        for(WordNetContent item : adverbSynonym)
        {
            jsonArray.put(item.toJSON());
        }

        return jsonArray;
    }
}