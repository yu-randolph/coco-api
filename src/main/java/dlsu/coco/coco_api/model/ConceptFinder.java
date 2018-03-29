package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.Corpus;

import java.util.ArrayList;

public class ConceptFinder {

    private String corpusWords,concept;
    private ArrayList<String> relatedWords,conceptResults;

    public ConceptFinder(String corpusWords, String concept){

        this.corpusWords = corpusWords;
        this.concept = concept;
        this.relatedWords = new ArrayList<String>();
        this.conceptResults = new ArrayList<String>();
//        this.getConceptNet();
        this.getWordNet();
    }


    public void getConceptNet(){
        ConceptNet cp = new ConceptNet(this.concept);
//        for(String results : cp.getForms())
//             this.relatedWords.add(results);

    }


    public void getWordNet(){
        WordNet wn = new WordNet("C:\\Users\\Micoh F Alvarez\\Desktop\\System needs\\WordNet-3.0\\WordNet-3.0\\dict",this.concept);

//            for(String results : wn.getNounSynonym()){
//                this.relatedWords.add(results);
//            }
    }

    public void findConcepts(){

            for(String relatedWord : relatedWords){
                if(corpusWords.contains(relatedWord))
                    this.conceptResults.add(relatedWord);

            }
    }

    public ArrayList<String> getConceptResults() {
        return this.conceptResults;
    }
}
