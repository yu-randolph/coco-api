package dlsu.coco.coco_api.model;


import dlsu.coco.coco_api.variables.CoCoNetContent;



import org.json.JSONException;
import org.json.JSONObject;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;

import java.util.ArrayList;

import java.util.LinkedHashSet;

public class CoCoNet {


    private ArrayList<CoCoNetContent> cocoNetList;
    private ArrayList<String> conceptList;
    private JSONArray arrayConceptList;

    public CoCoNet() throws IOException, ParseException, JSONException {

        conceptList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        this.arrayConceptList = (JSONArray) parser.parse(new FileReader(System.getProperty("user.dir") + "/WordNet-3.0/coconet.json"));
        System.out.println("ARRAYCONCEPLIST" + this.arrayConceptList.toString());
        this.cocoNetList = loadCoCoNetContentList(this.arrayConceptList.toString());
    }

    public ArrayList<CoCoNetContent> loadCoCoNetContentList(String JSONConcepts) throws JSONException {
        org.json.JSONArray ann = new org.json.JSONArray(JSONConcepts);
        ArrayList<CoCoNetContent> listContent = new ArrayList<>();

        for (int i = 0; i < ann.length(); i++) {
            CoCoNetContent cnItem = new CoCoNetContent();
            JSONObject object = ann.getJSONObject(i);
            cnItem.setConcept(object.get("keyword").toString());
            ArrayList<String> cnList = new ArrayList<>();
            org.json.JSONArray jsonConceptList = object.getJSONArray("relatedWords");

                for (int j = 0; j < jsonConceptList.length(); j++)
                    cnList.add(jsonConceptList.get(j).toString());

            cnItem.setConceptList(cnList);

            listContent.add(cnItem);
        }
        System.out.println("LIST CONTENT SEIZE" + listContent.size());
        return listContent;

    }

//    public void overwriteConcepts(String newConcepts) throws JSONException, IOException {
//
//        ArrayList<CoCoNetContent> newContent = this.loadCoCoNetContentList(newConcepts);
//
//        loop1:
//        for (CoCoNetContent curNewContent : newContent) {
//            System.out.println("new Content" + curNewContent.getConcept());
//            for (int i = 0; i < this.cocoNetList.size(); i++) {
//                System.out.println("coconet List Content" + this.cocoNetList.get(i).getConcept());
//                if (curNewContent.getConcept().equalsIgnoreCase(this.cocoNetList.get(i).getConcept())) {
//                    this.cocoNetList.get(i).getConceptList().addAll(curNewContent.getConceptList());
//                    this.cocoNetList.get(i).setConceptList(new ArrayList<>(new LinkedHashSet<>(this.cocoNetList.get(i).getConceptList())));
//                    continue loop1;
//                }
//
//            }
//            this.cocoNetList.add(curNewContent);
//
//        }
//            this.saveConceptsAsJSONFile();
//
//    }

    public void overwriteConcepts(String newConcepts) throws JSONException, IOException {

        ArrayList<CoCoNetContent> newContent = this.loadCoCoNetContentList(newConcepts);
        this.cocoNetList.clear();
        loop1:
        for (CoCoNetContent curNewContent : newContent) {

            this.cocoNetList.add(curNewContent);

        }
        this.saveConceptsAsJSONFile();

    }
    public void saveConceptsAsJSONFile() throws JSONException, IOException {
        org.json.JSONArray finalJSON = new org.json.JSONArray();

        for(CoCoNetContent item : this.cocoNetList)
        {
            org.json.JSONArray jsonArray = new org.json.JSONArray();
            JSONObject jsonObject = new JSONObject();

            System.out.println(" ITEM " + item.getConcept());
           jsonObject.put("keyword",item.getConcept());

           for(String concept: item.getConceptList()){
               System.out.println(" concept  " + concept);
               jsonArray.put(concept);
           }
           jsonObject.put("relatedWords",jsonArray);
           finalJSON.put(jsonObject);
        }

        System.out.println("FINAL JSON" + finalJSON.toString());

         File file = new File(System.getProperty("user.dir") + "/WordNet-3.0/coconet.json");

        FileOutputStream fooStream = new FileOutputStream(file, false); // true to append
        // false to overwrite.
        byte[] myBytes = finalJSON.toString().getBytes();
        fooStream.write(myBytes);
        fooStream.close();
            System.out.println("Successfully Copied JSON Object to File...");

    }

    public  ArrayList<String> getConceptList(String concept) throws IOException, ParseException, JSONException {


        org.json.JSONArray ann = new org.json.JSONArray(this.arrayConceptList.toString());

        for (int i = 0; i < ann.length(); i++) {
            JSONObject object = ann.getJSONObject(i);
            String jsonConcept = object.get("keyword").toString();

            if (jsonConcept.equalsIgnoreCase(concept)) {
                org.json.JSONArray jsonConceptList = object.getJSONArray("relatedWords");
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
