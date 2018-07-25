package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConceptNetContent;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ConceptNet {

    //RELATION
    private static String RELATED_TO = "RelatedTo";
    private static String FORM_OF = "FormOf";
//    private static String IS_A = "IsA";
//    private static String PART_OF = "PartOf";
//    private static String CREATED_BY = "CreatedBy";

    //LANGUAGE
    private static String ENGLISH = "en";

    private static String conceptnetAPIAddress = "http://api.conceptnet.io/c/en/";
    private static String conceptnetAPIQuery = "http://api.conceptnet.io/query?";

    private HttpURLConnection httpURLConnection;
    private InputStream inputStream;

    private ArrayList<ConceptNetContent> jsonRelatedTo;
    private ArrayList<ConceptNetContent> jsonFormOf;
    private ArrayList<ConceptNetContent> jsonIsA;
    private ArrayList<ConceptNetContent> jsonPartOf;
    private ArrayList<ConceptNetContent> jsonCreatedBy;
        int ctr = 0;
    public ConceptNet(String word)
    {
        //http://api.conceptnet.io/query?node=/c/en/book&rel=/r/RelatedTo
        jsonRelatedTo = new ArrayList<>();
        jsonFormOf = new ArrayList<>();
//        jsonIsA = new ArrayList<>();
//        jsonPartOf = new ArrayList<>();
//        jsonCreatedBy = new ArrayList<>();

        jsonRelatedTo = this.httpRequest(this.relationQueryBuilder(word, RELATED_TO, ENGLISH));
        jsonFormOf = this.httpRequest(this.relationQueryBuilder(word, FORM_OF, ENGLISH));
//        jsonIsA = this.httpRequest(this.relationQueryBuilder(word, IS_A, ENGLISH) );
//        jsonPartOf = this.httpRequest(this.relationQueryBuilder(word, PART_OF, ENGLISH));
//        jsonCreatedBy = this.httpRequest(this.relationQueryBuilder(word, CREATED_BY, ENGLISH));

    }

    public String relationQueryBuilder(String word, String relation, String language)
    {
        //node=/c/en/book&rel=/r/RelatedTo

        String queryWord = "node=/c/en/" + word;
        String queryRelation = "rel=/r/" + relation;
        String queryLanguage = "other=/c/" + language;

        System.out.println(conceptnetAPIQuery + queryWord + "&" + queryRelation + "&" + queryLanguage);
        return conceptnetAPIQuery + queryWord + "&" + queryRelation + "&" + queryLanguage;
    }

    public ArrayList<ConceptNetContent> httpRequest(String address)
    {

        try {

            ArrayList<ConceptNetContent> content = new ArrayList<>();
            httpURLConnection = (HttpURLConnection) new URL(address).openConnection();
            httpURLConnection.setConnectTimeout(5000 *24);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            inputStream = httpURLConnection.getInputStream();
            String jsonText = IOUtils.toString(inputStream, String.valueOf(Charset.forName("UTF-8")));
            JSONObject json = new JSONObject(jsonText);
            JSONArray edges = json.getJSONArray("edges");

            System.out.println(edges.length());
            for(int i = 0; i < edges.length(); i++)
            {
                //System.out.println(edges.getJSONObject(i));
                JSONObject rel = edges.getJSONObject(i).getJSONObject("rel");
                JSONObject start = edges.getJSONObject(i).getJSONObject("start");
                Float weight = Float.parseFloat(edges.getJSONObject(i).get("weight").toString());
                JSONObject end = edges.getJSONObject(i).getJSONObject("end");
                String surfaceText = edges.getJSONObject(i).get("surfaceText").toString();

                ConceptNetContent item = new ConceptNetContent(rel.getString("label"), start.getString("label"), end.getString("label"), surfaceText, weight);
                System.out.println(item.toString());
                content.add(item);
            }


            inputStream.close();
            return content;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public ArrayList<ConceptNetContent> getJsonRelatedTo() {
//        return jsonRelatedTo;
//    }

    public ArrayList<ConceptNetContent> getJsonFormOf() {
        return jsonFormOf;
    }

//    public ArrayList<ConceptNetContent> getJsonIsA() {
//        return jsonIsA;
//    }
//
//    public ArrayList<ConceptNetContent> getJsonPartOf() {
//        return jsonPartOf;
//    }
//
//    public ArrayList<ConceptNetContent> getJsonCreatedBy() {
//        return jsonCreatedBy;
//    }

//    public JSONArray getRelatedToJSONObject()
//    {
//        JSONArray jsonArray = new JSONArray();
//
//        for(ConceptNetContent item : jsonRelatedTo)
//        {
//            jsonArray.put(item.toJSON());
//        }
//
//        return jsonArray;
//    }

    public JSONArray getFormOfJSONObject() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(ConceptNetContent item : jsonFormOf)
        {
            jsonArray.put(item.getStartWord());
        }
        return jsonArray;
    }


    public ArrayList<String> getContents(){
        ArrayList<String> content = new ArrayList<String>();
        for(ConceptNetContent item : jsonFormOf)
        {
            content.add(item.getStartWord());

        }
        return content;
    }

    public ArrayList<String> getRelatedContents(){
        ArrayList<String> content = new ArrayList<String>();
        for(ConceptNetContent item : jsonRelatedTo)
        {
            content.add(item.getStartWord().toLowerCase());
        }
        return content;
    }


//    public JSONArray getIsAJSONObject()
//    {
//        JSONArray jsonArray = new JSONArray();
//        for(ConceptNetContent item : jsonIsA)
//        {
//            jsonArray.put(item.toJSON());
//        }
//        return jsonArray;
//    }
//
//    public JSONArray getPartOfJSONObject()
//    {
//        JSONArray jsonArray = new JSONArray();
//        for(ConceptNetContent item : jsonPartOf)
//        {
//            jsonArray.put(item.toJSON());
//        }
//        return jsonArray;
//    }
//
//    public JSONArray getCreatedByJSONObject()
//    {
//        JSONArray jsonArray = new JSONArray();
//        for(ConceptNetContent item : jsonCreatedBy)
//        {
//            jsonArray.put(item.toJSON());
//        }
//        return jsonArray;
//    }
}