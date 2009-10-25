package org.hokiesuns.guesswhat.model;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User
{
    @PrimaryKey
    @Persistent
    private Long userID;
    @Persistent
    private int currentPointTotal;
    @Persistent
    private List<Long> createdGuessables= new LinkedList<Long>();
    @Persistent
    private List<Long> correctlyGuessed= new LinkedList<Long>();
    
    public User(long pUserID)
    {
        userID = pUserID;
        currentPointTotal=0;
    }
    
    public Long getUserID()
    {
        return userID;
    }
    public void setUserID(Long userID)
    {
        this.userID = userID;
    }
    public int getCurrentPointTotal()
    {
        return currentPointTotal;
    }
    public void increaseCurrentPointTotal(int currentPointTotal)
    {
        this.currentPointTotal += currentPointTotal;
    }

    public List<Long> getCreatedGuessables()
    {
        return createdGuessables;
    }

    public List<Long> getCorrectlyGuessed()
    {
        return correctlyGuessed;
    }
}
