package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.Corpus;
import de.hu_berlin.german.korpling.tiger2.Graph;
import de.hu_berlin.german.korpling.tiger2.Segment;
import de.hu_berlin.german.korpling.tiger2.Terminal;
import de.hu_berlin.german.korpling.tiger2.samples.CorpusWriter;
import org.eclipse.emf.common.util.EList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Concordancer {

    private Corpus corpus;
    private Corpus concordances;
    private ArrayList<String> results;

    public Concordancer(ArrayList<String> results, Corpus corpus){
        this.results = results;
        this.corpus = corpus;
    }

    public ArrayList<String> getConcordances(){
        Segment s = this.corpus.getSegments().get(0);
        String sentence = "";
        for(String words : this.results){
            for(Graph g : s.getGraphs()){
                if(g.getTerminals().contains(words)) {
                    for (Terminal t : g.getTerminals())
                         sentence += t.getWord();
                     this.results.add(sentence);
                     sentence = "";
                     s.getGraphs().remove(g);
                }
            }
        }
        return this.results;
    }

    public JSONObject getConcordancesJSON(){
        Corpus corpus = this.corpus;
        JSONObject jsonObject = new JSONObject();

        if(corpus != null)
        {

            JSONArray listGraph = new JSONArray();
            //GET SENTENCE
            for(String itemGraph : this.results)
            {
                JSONObject sentence = new JSONObject();

                sentence.put("Sentence", itemGraph);


                listGraph.put(sentence);
            }

            jsonObject.put("Concordances", listGraph);

            return jsonObject;
        }
        else
        {
            return null;
        }
    }
}
