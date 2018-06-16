package dlsu.coco.coco_api.variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.text.html.HTML;
import java.util.ArrayList;

public class ConcordanceContent {
    private String keyword;
    private int keyword_Index;
    private String completeSentence;
    private ArrayList<WordContent> words;
    private String sentenceId;
    private int patternFrequency;


    public ConcordanceContent(String keyword, int keyword_Index, String completeSentence, ArrayList<WordContent> words,String sentenceId) {
        this.keyword = keyword;
        this.keyword_Index = keyword_Index;
        this.completeSentence = completeSentence;
        this.sentenceId = sentenceId;
        this.words = words;
        this.patternFrequency = 0;
    }

    public ConcordanceContent(){}

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getKeyword_Index() {
        return keyword_Index;
    }

    public void setKeyword_Index(int keyword_Index) {
        this.keyword_Index = keyword_Index;
    }

    public String getCompleteSentence() {
        return completeSentence;
    }

    public void setCompleteSentence(String completeSentence) {
        this.completeSentence = completeSentence;
    }

    public ArrayList<WordContent> getWords() {
        return words;
    }

    public void setWords(ArrayList<WordContent> words) {
        this.words = words;
    }

    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
    }

    public void increaseFreq() { this.patternFrequency++; }

    public void resetFreq() { patternFrequency = 0; }

    public int getFreq() {return patternFrequency;}

    public JSONObject getJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keyword", this.keyword);
        jsonObject.put("keyword_index", this.keyword_Index);
        jsonObject.put("completeSentence", this.completeSentence);
        jsonObject.put("sentenceId", this.sentenceId);
        JSONArray listGraph = new JSONArray();

            for(WordContent word: this.words) {
                    JSONObject wordContent = new JSONObject();
                    JSONArray allTags = new JSONArray();

                    wordContent.put("word",word.getWord());
                    for(TagContent tag : word.getTags()) {
                        JSONObject tagContent = new JSONObject();
                        tagContent.put("name",tag.getTagName());
                        tagContent.put("value",tag.getTagValue());
                        allTags.put(tagContent);
                    }
                    wordContent.put("tags",allTags);
                    wordContent.put("wordId",word.getWordId());
                    listGraph.put(wordContent);
            }
        jsonObject.put("WordContent", listGraph);
        System.out.println(jsonObject);
        return jsonObject;
    }

    public void readJSON(JSONObject concordance) throws JSONException {
        this.keyword = concordance.getString("keyword");
        this.keyword_Index = concordance.getInt("keyword_index");
        this.completeSentence = concordance.getString("completeSentence");
        this.sentenceId = concordance.getString("sentenceId");
        this.words = new ArrayList<>();

        JSONArray jsonWords = concordance.getJSONArray("WordContent");

        for(int wordCtr = 0; wordCtr < jsonWords.length(); wordCtr++)
        {
            JSONObject jsonWordContent = jsonWords.getJSONObject(wordCtr);
            JSONArray  jsonTagContents = jsonWordContent.getJSONArray("tags");
            ArrayList<TagContent> tagList = new ArrayList();

            String lemma = null;
            for(int tagCtr = 0; tagCtr < jsonTagContents.length(); tagCtr++)
            {
                JSONObject jsonTagContent = jsonTagContents.getJSONObject(tagCtr);
                if(jsonTagContent.getString("name").equals("lemma"))
                {
                    lemma = jsonTagContent.getString("value");
                }
                tagList.add(new TagContent(jsonTagContent.getString("name"), jsonTagContent.getString("value")));
            }

            words.add(new WordContent(jsonWordContent.getString("word"), tagList, jsonWordContent.getString("wordId"), lemma));
        }
    }

    public void compareConcorrdanceContent(ConcordanceContent originalSentence)
    {
        ArrayList<WordContent> sentence = originalSentence.getWords();

        int nLeftCtr = keyword_Index - 1;
        int nRightCtr = keyword_Index + 1;

        int nLeftSentenceCtr = originalSentence.keyword_Index - 1;
        int nRightSentenceCtr = originalSentence.keyword_Index + 1;

        boolean bLeftMatch = true;
        boolean bRightMatch = true;

        //CHECK LEFT SIDE
        while(nLeftCtr >= 0 && nLeftSentenceCtr >= 0 && bLeftMatch)
        {
            ArrayList<TagContent> patternTags = this.removeLemmaAndOthers(words.get(nLeftCtr).getTags());
            ArrayList<TagContent>  sentenceTags = this.removeLemmaAndOthers(sentence.get(nLeftSentenceCtr).getTags());
            boolean bEquals = false;

            System.out.println(words.get(nLeftCtr).getWord() + ":" + nLeftCtr + " || " + sentence.get(nLeftSentenceCtr).getWord() + ":" + nLeftSentenceCtr);

            for(TagContent patternTag : patternTags)
            {
                for(TagContent sentenceTag : sentenceTags)
                {
                    if(patternTag.getTagName().equals(sentenceTag.getTagName()) && patternTag.getTagValue().equals(sentenceTag.getTagValue()))
                        bEquals = true;
                }
            }

            if(!bEquals)
            {
                System.out.println("LEFT DOES NOT MATCH");
                bLeftMatch = false;
            }

            nLeftCtr--;
            nLeftSentenceCtr--;
        }

        //CHECK RIGHT SIDE
        while(nRightCtr < words.size() && nRightSentenceCtr < sentence.size() && bRightMatch)
        {
            ArrayList<TagContent> patternTags = this.removeLemmaAndOthers(words.get(nRightCtr).getTags());
            ArrayList<TagContent>  sentenceTags = this.removeLemmaAndOthers(sentence.get(nRightSentenceCtr).getTags());
            boolean bEquals = false;

            System.out.println(words.get(nRightCtr).getWord() + ":" + nRightCtr + " || " + sentence.get(nRightSentenceCtr).getWord() + ":" + nRightSentenceCtr);

            for(TagContent patternTag : patternTags)
            {
                for(TagContent sentenceTag : sentenceTags)
                {
                    if(patternTag.getTagName().equals(sentenceTag.getTagName()) && patternTag.getTagValue().equals(sentenceTag.getTagValue()))
                        bEquals = true;
                }
            }

            if(!bEquals)
            {
                System.out.println("RIGHT DOES NOT MATCH");
                bRightMatch = false;
            }

            nRightCtr++;
            nRightSentenceCtr++;
        }

        if(bLeftMatch && bRightMatch && nLeftCtr < 0 && nRightCtr == words.size())
        {
            System.out.println(completeSentence + " && " + originalSentence.getCompleteSentence());

            increaseFreq();
        }
    }

    private ArrayList<TagContent> removeLemmaAndOthers(ArrayList<TagContent> originalTags)
    {
        ArrayList<TagContent> tags = new ArrayList<>();
        tags.addAll(originalTags);

        for(int tagCtr = 0; tagCtr < tags.size(); tagCtr++)
        {
            if(tags.get(tagCtr).getTagName().equals("lemma"))
                tags.remove(tagCtr);

            if(tags.get(tagCtr).getTagName().equals("ner") &&  tags.get(tagCtr).getTagValue().equals("O"))
                tags.remove(tagCtr);
        }

        return tags;
    }
}
