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
}
