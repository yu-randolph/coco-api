package dlsu.coco.coco_api.model;

import dlsu.coco.coco_api.variables.ConcordanceContent;
import dlsu.coco.coco_api.variables.WordContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatternFinder {

    private JSONArray jsonConcordance;
    private JSONObject jsonSuper;

    private ArrayList<ConcordanceContent> listSentence;
    private ArrayList<ConcordanceContent> listPattern;

    public PatternFinder(JSONObject jsonSuper) throws JSONException {
        this.listSentence = new ArrayList<>();
        this.listPattern = new ArrayList<>();
        this.jsonSuper = jsonSuper;

        this.concordanceParser();
        this.findPattern();
    }

    private void concordanceParser() throws JSONException {
        jsonConcordance = jsonSuper.getJSONArray("CONCORDANCE");

        for(int conCtr = 0; conCtr < jsonConcordance.length(); conCtr++)
        {
            JSONObject jsonObject = jsonConcordance.getJSONObject(conCtr);
            listSentence.add(new ConcordanceContent());
            listSentence.get(listSentence.size()-1).readJSON(jsonObject);
        }
    }

    private void findPattern()
    {
        //SETTING PATTERN CANDIDATE FROM EACH SENTENCE && COMPARING THE CANDIDATE WITH TABULAR CONCORDANCE RESULT
        for(ConcordanceContent candidateOrigin : listSentence) {
            System.out.println(candidateOrigin.getCompleteSentence());
            ArrayList<WordContent> candidate = new ArrayList<>();

            boolean ignoreLeft = false;
            boolean ignoreRight = false;
            boolean matchingLeft = false;
            boolean matchingRight = false;

            int nRightIndex = candidateOrigin.getKeyword_Index() + 1;
            int nLeftIndex = candidateOrigin.getKeyword_Index() - 1;
            int nKeywordIndexChaser = 0;

            candidate.add(candidateOrigin.getWords().get(candidateOrigin.getKeyword_Index()));

            while (!ignoreLeft || !ignoreRight)
            {
                for(int i = 0; i < candidate.size(); i++)
                {
                    System.out.print(candidate.get(i).getWord() + " ");
                }
                System.out.println("");

                //LEFT
                if(!ignoreLeft && nLeftIndex > -1)
                {
                    System.out.println("LEFT : " + candidateOrigin.getWords().get(nLeftIndex).getWord());
                    ConcordanceContent pattern = new ConcordanceContent();

                    candidate.add(0, candidateOrigin.getWords().get(nLeftIndex));

                    nKeywordIndexChaser++;

                    ArrayList<WordContent> insert = new ArrayList<>();
                    insert.addAll(candidate);
                    pattern.setWords(insert);

                    String completeSentence = "";
                    for(int ctr = 0; ctr < candidate.size(); ctr++)
                    {
                        completeSentence += candidate.get(ctr).getWord() + " ";
                    }
                    pattern.setCompleteSentence(completeSentence);

                    pattern.setKeyword(candidateOrigin.getKeyword());
                    pattern.setKeyword_Index(nKeywordIndexChaser);

                    pattern.setSentenceId("pattern");
                    pattern.resetFreq();
                    pattern.initPatternOrigin();

                    pattern.printWordContents();
                    System.out.println();

                    for(ConcordanceContent comparedSentence : listSentence)
                    {
                        pattern.compareConcorrdanceContent(comparedSentence);
                    }

                    System.out.println("FREQ : " + pattern.getFreq());
                    if(pattern.getFreq()  > 1)
                    {
                        matchingLeft = true;
                        System.out.print("SENTENCE : ");
                        for(int i = 0; i < pattern.getWords().size(); i++)
                        {
                            System.out.print(pattern.getWords().get(i).getWord() + " ");
                        }
                        System.out.println();

                        if(isPatternUnique(pattern))
                        {
                            listPattern.add(pattern);
                        }
                    }
                    else
                    {
                        ignoreLeft = true;
                    }

                    candidate.remove(0);
                    System.out.println("LEFT DONE");
                    nKeywordIndexChaser--;
                }
                else
                {
                    ignoreLeft = true;
                }

                //RIGHT
                if(!ignoreRight && nRightIndex < candidateOrigin.getWords().size())
                {
                    System.out.println("RIGHT : " + candidateOrigin.getWords().get(nRightIndex).getWord());
                    ConcordanceContent pattern = new ConcordanceContent();

                    candidate.add(candidateOrigin.getWords().get(nRightIndex));

                    ArrayList<WordContent> insert = new ArrayList<>();
                    insert.addAll(candidate);
                    pattern.setWords(insert);

                    String completeSentence = "";
                    for(int ctr = 0; ctr < candidate.size(); ctr++)
                    {
                        completeSentence += candidate.get(ctr).getWord() + " ";
                    }
                    pattern.setCompleteSentence(completeSentence);

                    pattern.setKeyword(candidateOrigin.getKeyword());
                    pattern.setKeyword_Index(nKeywordIndexChaser);
                    pattern.setSentenceId("pattern");
                    pattern.resetFreq();
                    pattern.initPatternOrigin();

                    pattern.printWordContents();
                    System.out.println();

                    for(ConcordanceContent comparedSentence : listSentence)
                    {
                        pattern.compareConcorrdanceContent(comparedSentence);
                    }

                    System.out.println("FREQ : " + pattern.getFreq());
                    if(pattern.getFreq()  > 1)
                    {
                        matchingRight = true;
                        if(isPatternUnique(pattern))
                        {
                            listPattern.add(pattern);
                        }
                        System.out.print("SENTENCE : ");
                        for(int i = 0; i < pattern.getWords().size(); i++)
                        {
                            System.out.print(pattern.getWords().get(i).getWord() + " ");
                        }
                        System.out.println();
                    }
                    else
                    {
                        ignoreRight = true;
                    }

                    candidate.remove(candidate.size()-1);
                    System.out.println("RIGHT DONE");
                }
                else
                {
                    ignoreRight = true;
                }

                if(matchingLeft)
                {
                    candidate.add(0, candidateOrigin.getWords().get(nLeftIndex));

                    nKeywordIndexChaser++;
                    nLeftIndex -= 1;
                    matchingLeft = false;
                }

                if(matchingRight)
                {
                    candidate.add(candidateOrigin.getWords().get(nRightIndex));

                    nRightIndex += 1;
                    matchingRight = false;
                }

                ArrayList<WordContent> temp = new ArrayList<>();
                temp.addAll(candidate);
                candidate = new ArrayList<>();
                candidate.addAll(temp);

                System.out.println("=======================================");
            }
        }

        for(int idCtr = 0; idCtr < listPattern.size(); idCtr++)
        {
            listPattern.get(idCtr).setSentenceId(idCtr+1 +"");
        }
    }

    private boolean isPatternUnique(ConcordanceContent pattern)
    {
        if(listPattern.size() == 0)
        {
            System.out.println("PATTERN CANDIDATE : " + pattern.getCompleteSentence());
            System.out.println("PATTERN CANDIDATE'S WORDCONTENTS PRINT : ");
            pattern.printWordContents();
            System.out.println();
            return true;
        }
        else
        {
            boolean isUnique = true;
            System.out.println("PATTERN CANDIDATE : " + pattern.getCompleteSentence());
            System.out.println("PATTERN CANDIDATE'S WORDCONTENTS PRINT : ");
            pattern.printWordContents();
            System.out.println();

            for(int ctr = 0; ctr < listPattern.size(); ctr++)
            {
                System.out.println("COMPARED PATTERN " + listPattern.get(ctr).getCompleteSentence());

                if(listPattern.get(ctr).getCompleteSentence().equalsIgnoreCase(pattern.getCompleteSentence()))
                    isUnique = false;
            }

            System.out.println("UNIQUE : " + isUnique);
            return isUnique;
        }
    }

    public void printPattern()
    {
        System.out.println("PATTERNS!");
        for(int ctr = 0; ctr < listPattern.size(); ctr++)
        {
            System.out.println("FREQUENCY : " + listPattern.get(ctr).getFreq());
            System.out.print("ORIGIN ID : ");
            for(int i = 0; i < listPattern.get(ctr).getPatternOrigin().size(); i++)
            {
                System.out.print(listPattern.get(ctr).getPatternOrigin().get(i) + " ");
            }
            System.out.println();
            System.out.println("COMP SENTENCE");
            System.out.println(listPattern.get(ctr).getCompleteSentence());
            System.out.println("PRINT WORDCONTENTS");
            listPattern.get(ctr).printWordContents();
            System.out.println();

            System.out.println();
        }
    }

    public JSONObject getJSONpattern() throws JSONException
    {
        JSONArray patterns = new JSONArray();

        for(int ctr = 0; ctr < listPattern.size(); ctr++)
        {
            JSONObject pattern = new JSONObject();
            pattern.put("id", listPattern.get(ctr).getSentenceId());
            pattern.put("pattern", listPattern.get(ctr).getSummaryJSON());

            JSONArray patternOrigin = new JSONArray();
            for(String originID : listPattern.get(ctr).getPatternOrigin())
            {
                for(int originfinder = 0; originfinder < listSentence.size(); originfinder++)
                {
                    if(originID.equals(listSentence.get(originfinder).getSentenceId()))
                    {
                        patternOrigin.put(listSentence.get(originfinder).getSummaryJSON());
                    }
                }
            }

            pattern.put("originSentences", patternOrigin);
            pattern.put("frequency", listPattern.get(ctr).getFreq());
            patterns.put(pattern);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("patterns", patterns);
        return jsonObject;
    }
}
