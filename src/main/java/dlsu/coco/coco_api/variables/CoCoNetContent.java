package dlsu.coco.coco_api.variables;

import java.util.ArrayList;

public class CoCoNetContent {


    private String concept;
    private ArrayList<String> conceptList;

    public void CoCoNetContent(){

        conceptList = new ArrayList<String>();
    }


    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public ArrayList<String> getConceptList() {
        return conceptList;
    }

    public void setConceptList(ArrayList<String> conceptList) {
        this.conceptList = conceptList;
    }




}
