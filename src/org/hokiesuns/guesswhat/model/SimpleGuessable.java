package org.hokiesuns.guesswhat.model;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SimpleGuessable
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)    
    private Long id;
    @Persistent
    private String imageLocation;
    @Persistent
    private String answerImageLocation;
    @Persistent
    private int correctAnswer; //0 based
    @Persistent
    private List<String> answers = new ArrayList<String>();
    @Persistent
    private Long creator;
    @Persistent
    private int numberTimesShown;
    @Persistent
    private List<Integer> answerDistributions = new ArrayList<Integer>();
    @Persistent
    private int badQuestionCount=0;
    @Persistent
    private List<QuestionStatistic> questionStatistics = new ArrayList<QuestionStatistic>();
    
    public Long getId()
    {
        return id;
    }
    public String getImageLocation()
    {
        return imageLocation;
    }
    public int getCorrectAnswer()
    {
        return correctAnswer;
    }
    public List<String> getAnswers()
    {
        return answers;
    }

    public void setImageLocation(String imageLocation)
    {
        this.imageLocation = imageLocation;
    }
    
    //0 based
    public void setCorrectAnswer(int correctAnswer)
    {
        this.correctAnswer = correctAnswer;
    }
    public void addAnswer(String pAnswer)
    {
        answers.add(pAnswer);
        answerDistributions.add(0);
    }
    public String getAnswerImageLocation()
    {
        return answerImageLocation;
    }

    public void setAnswerImageLocation(String answerImageLocation)
    {
        this.answerImageLocation =answerImageLocation;
    }
    public Long getCreator()
    {
        return creator;
    }
    public void setCreator(User creator)
    {
        this.creator = creator.getUserID();
    }
    public int getNumberTimesShown()
    {
        return numberTimesShown;
    }
    
//    public void incrementNumberTimesShown()
//    {
//        numberTimesShown++;
//    }
//    
//    public void incrementAnswerDistribution(int pAnswerChoice)
//    {
//        if(pAnswerChoice < answerDistributions.size())
//        {
//            int i = answerDistributions.get(pAnswerChoice);
//            i++;
//            answerDistributions.set(pAnswerChoice, i);
//        }
//    }
    
    public void updateStatistics(int pAnswerChoice, String pFreeChoice)
    {
        numberTimesShown++;
        if (answerDistributions != null && 
                pAnswerChoice < answerDistributions.size() && 
                pAnswerChoice >=0)
        {
            int i = answerDistributions.get(pAnswerChoice);
            i++;
            answerDistributions.set(pAnswerChoice, i);
            questionStatistics.add(new QuestionStatistic(pAnswerChoice));
        }
        else
        {
            //handle free choice here!
            questionStatistics.add(new QuestionStatistic(pFreeChoice));
        }
    }
    
    public void incrementBoringCount()
    {
        badQuestionCount++;
    }
    
    public List<Integer> getAnswerDistributions()
    {
        return answerDistributions;
    }
    
    public double getDifficulty()
    {
        return 1-((badQuestionCount*1.0d)/(1.0d*numberTimesShown));
    }
}
