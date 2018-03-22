package dlsu.coco.coco_api.model;

import de.hu_berlin.german.korpling.tiger2.main.Tiger2Converter;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
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
    private static String IS_A = "IsA";
    private static String PART_OF = "PartOf";
    private static String CREATED_BY = "CreatedBy";

    //LANGUAGE
    private static String ENGLISH = "en";

    private static String conceptnetAPIAddress = "http://api.conceptnet.io/c/en/";
    private static String conceptnetAPIQuery = "http://api.conceptnet.io/query?";

    private HttpURLConnection httpURLConnection;
    private InputStream inputStream;

    private JSONArray jsonRelatedTo;
    private JSONArray jsonFormOf;
    private JSONArray jsonIsA;
    private JSONArray jsonPartOf;
    private JSONArray jsonCreatedBy;

    private ArrayList<String> formOfWords;
    public ConceptNet(String word)
    {
        //http://api.conceptnet.io/query?node=/c/en/book&rel=/r/RelatedTo

//        jsonRelatedTo = this.httpRequest(this.relationQueryBuilder(word, RELATED_TO, ENGLISH));
        this.formOfWords = new ArrayList<String>();
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

    public JSONArray httpRequest(String address)
    {
        try {
            httpURLConnection = (HttpURLConnection) new URL(address).openConnection();
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
                JSONObject start = edges.getJSONObject(i).getJSONObject("start");
                System.out.print(start.getString("label") + " ");
                this.formOfWords.add(start.getString("label"));
            }
            System.out.println();

            inputStream.close();
            return edges;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getForms(){
        return this.formOfWords;
    }
}
