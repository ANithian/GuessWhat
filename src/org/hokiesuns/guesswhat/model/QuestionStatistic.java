package org.hokiesuns.guesswhat.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class QuestionStatistic
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)    
    private Key id;
    
    @Persistent
    private Date timeStamp;
    
    @Persistent
    private int answerChoice;
    
    @Persistent
    private String answerText;

    public QuestionStatistic(int answerChoice)
    {
        this.timeStamp = new Date();
        this.answerChoice = answerChoice;
    }
    
    public QuestionStatistic(String answerText)
    {
        this.timeStamp = new Date();
        this.answerText = answerText;
        this.answerChoice = -1;
    }
}
