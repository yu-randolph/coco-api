package dlsu.coco.coco_api.model;


import dlsu.coco.coco_api.variables.CoCoNetContent;


import dlsu.coco.coco_api.variables.ConceptNetContent;
import dlsu.coco.coco_api.variables.WordNetContent;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import javax.json.Json;
import javax.json.JsonWriter;
import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class CoCoNet {

    private File cocoNet;
    private ArrayList<CoCoNetContent> cocoNetList;
    private ArrayList<String> conceptList;
    private JSONArray arrayConceptList;

    public CoCoNet() throws IOException, ParseException, JSONException {

        conceptList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        this.arrayConceptList = (JSONArray) parser.parse(new FileReader(System.getProperty("user.dir") + "/WordNet-3.0/coconet.json"));
        this.cocoNetList = loadCoCoNetContentList(this.arrayConceptList.toString());
    }

    public ArrayList<CoCoNetContent> loadCoCoNetContentList(String JSONConcepts) throws JSONException {
        org.json.JSONArray ann = new org.json.JSONArray(JSONConcepts);
        ArrayList<CoCoNetContent> listContent = new ArrayList<>();

        for (int i = 0; i < ann.length(); i++) {
            CoCoNetContent cnItem = new CoCoNetContent();
            JSONObject object = ann.getJSONObject(i);
            cnItem.setConcept(object.get("concept").toString());
            ArrayList<String> cnList = new ArrayList<>();
            org.json.JSONArray jsonConceptList = object.getJSONArray("conceptList");

                for (int j = 0; j < jsonConceptList.length(); j++)
                    cnList.add(jsonConceptList.get(j).toString());

            cnItem.setConceptList(cnList);

            listContent.add(cnItem);
        }
        return listContent;

    }

    public void overwriteConcepts(String newConcepts) throws JSONException {

        ArrayList<CoCoNetContent> newContent = this.loadCoCoNetContentList(newConcepts);

          loop1:for(CoCoNetContent curNewContent: newContent){

                 for(int i = 0; i < this.cocoNetList.size(); i++){
                     if(curNewContent.getConcept().equalsIgnoreCase(this.cocoNetList.get(i).getConcept())){
                         this.cocoNetList.get(i).getConceptList().addAll(curNewContent.getConceptList());
                         this.cocoNetList.get(i).setConceptList(new ArrayList<>(new LinkedHashSet<>(this.cocoNetList.get(i).getConceptList())));
                         continue loop1;
                     }

                 }
              this.cocoNetList.add(curNewContent);

            }

    }

    public void saveConceptsAsJSONFile() throws JSONException, IOException {
        org.json.JSONArray finalJSON = new org.json.JSONArray();

        org.json.JSONArray jsonArray = new org.json.JSONArray();
        JSONObject jsonObject = new JSONObject();


        for(CoCoNetContent item : this.cocoNetList)
        {
           jsonObject.put("concept",item.getConcept());
           for(String concept: item.getConceptList()){
               jsonArray.put(concept);
           }
           jsonObject.put("conceptList",jsonArray);
           finalJSON.put(jsonObject);
        }

         FileWriter file = new FileWriter(System.getProperty("user.dir") + "/WordNet-3.0/coconet.json");

            file.write(finalJSON.toString());
            System.out.println("Successfully Copied JSON Object to File...");

    }

    public  ArrayList<String> getConceptList(String concept) throws IOException, ParseException, JSONException {


        org.json.JSONArray ann = new org.json.JSONArray(this.arrayConceptList.toString());

        for (int i = 0; i < ann.length(); i++) {
            JSONObject object = ann.getJSONObject(i);
            String jsonConcept = object.get("concept").toString();

            if (jsonConcept.equalsIgnoreCase(concept)) {
                org.json.JSONArray jsonConceptList = object.getJSONArray("conceptList");
                for (int j = 0; j < jsonConceptList.length(); j++)
                    conceptList.add(jsonConceptList.get(j).toString());
            }
        }
        return conceptList;
    }

    public org.json.JSONArray getJSONConceptList() throws JSONException {

        org.json.JSONArray jsonArray = new org.json.JSONArray();

        for(String item : conceptList)
        {
            jsonArray.put(item);
        }

        return jsonArray;
    }
}
