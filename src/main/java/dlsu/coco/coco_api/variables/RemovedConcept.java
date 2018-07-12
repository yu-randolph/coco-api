package dlsu.coco.coco_api.variables;

public class RemovedConcept {

    private String key;
    private String sentenceid;

    public RemovedConcept(String key, String sentenceid){
        this.key = key;
        this.sentenceid = sentenceid;
    }

    public String getKey() {
        return key;
    }


    public String getSentenceid() {
        return sentenceid;
    }
}
