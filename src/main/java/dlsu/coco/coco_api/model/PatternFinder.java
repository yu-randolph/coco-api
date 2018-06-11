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
                    pattern.setWords(candidate);

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
                    pattern.setWords(candidate);

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

                System.out.println("=======================================");
            }
        }
    }

    private boolean isPatternUnique(ConcordanceContent pattern)
    {
        if(listPattern.size() == 0)
        {
            System.out.println("PATTERN CANDIDATE : " + pattern.getCompleteSentence());
            return true;
        }
        else
        {
            boolean isUnique = true;
            System.out.println("PATTERN CANDIDATE : " + pattern.getCompleteSentence());
            for(int ctr = 0; ctr < listPattern.size(); ctr++)
            {
                System.out.println("COMPARED PATTERN " + listPattern.get(ctr).getCompleteSentence());

                if(listPattern.get(ctr).getCompleteSentence().equals(pattern.getCompleteSentence()))
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
            System.out.println(listPattern.get(ctr).getCompleteSentence());

            System.out.println();
        }
    }

    public JSONObject getJSONpattern()
    {
        return null;
    }
}
