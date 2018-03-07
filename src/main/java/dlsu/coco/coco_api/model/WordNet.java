package dlsu.coco.coco_api.model;

import edu.smu.tspell.wordnet.*;
import edu.smu.tspell.wordnet.impl.file.PropertyNames;

import java.util.ArrayList;
import java.util.Collections;

public class WordNet {

    static WordNetDatabase database;
    private final static String[] SYNSET_TYPES = {"", "noun","verb","adjective","adverb"};

    private String word;

    private ArrayList<String> nounSynonym;
    private ArrayList<NounSynset> nounHyponym;
    private ArrayList<NounSynset> nounHypernym;

    private ArrayList<String> verbSynonym;
    private ArrayList<VerbSynset> verbHyponym;
    private ArrayList<VerbSynset> verbTroponym;

    private ArrayList<String> adjectiveSynonym;

    private ArrayList<String> adverbSynonym;

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
    }

    public void getNoun()
    {
        nounSynonym = new ArrayList<>();
        nounHyponym = new ArrayList<>();
        nounHypernym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.NOUN))
        {
            Collections.addAll(nounSynonym, synset.getWordForms());
            Collections.addAll(nounHyponym, ((NounSynset) synset).getHypernyms());
            Collections.addAll(nounHypernym, ((NounSynset) synset).getHyponyms());
        }
    }

    public void getVerb()
    {
        verbSynonym = new ArrayList<>();
        verbHyponym = new ArrayList<>();
        verbTroponym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.VERB))
        {
            Collections.addAll(verbSynonym, synset.getWordForms());
            Collections.addAll(verbHyponym, ((VerbSynset) synset).getHypernyms());
            Collections.addAll(verbTroponym, ((VerbSynset) synset).getTroponyms());
        }
    }

    public void getAdjective()
    {
        adjectiveSynonym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.ADJECTIVE))
        {
            Collections.addAll(adjectiveSynonym, synset.getWordForms());
        }
    }

    public void getAdverb()
    {
        adverbSynonym = new ArrayList<>();

        for(Synset synset : database.getSynsets(word, SynsetType.ADVERB))
        {
            Collections.addAll(adverbSynonym, synset.getWordForms());
        }
    }

    public ArrayList<String> getNounSynonym() {
        return nounSynonym;
    }

    public ArrayList<NounSynset> getNounHyponym() {
        return nounHyponym;
    }

    public ArrayList<NounSynset> getNounHypernym() {
        return nounHypernym;
    }

    public ArrayList<String> getVerbSynonym() {
        return verbSynonym;
    }

    public ArrayList<VerbSynset> getVerbHyponym() {
        return verbHyponym;
    }

    public ArrayList<VerbSynset> getVerbTroponym() {
        return verbTroponym;
    }

    public ArrayList<String> getAdjectiveSynonym() { return adjectiveSynonym; }

    public ArrayList<String> getAdverbSynonym() {
        return adverbSynonym;
    }
}