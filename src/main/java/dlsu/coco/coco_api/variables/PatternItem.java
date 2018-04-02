package dlsu.coco.coco_api.variables;

public class PatternItem {
    private String sWord;
    private Integer nFreq;

    public PatternItem(String sWord) {
        this.sWord = sWord;
        this.nFreq = 1;
    }

    public void increaseFreq()
    {
        this.nFreq++;
    }

    public String getsWord() {
        return sWord;
    }

    public void setsWord(String sWord) {
        this.sWord = sWord;
    }

    public Integer getnFreq() {
        return nFreq;
    }

    public void setnFreq(Integer nFreq) {
        this.nFreq = nFreq;
    }
}
