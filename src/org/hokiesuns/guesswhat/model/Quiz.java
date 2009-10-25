package org.hokiesuns.guesswhat.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Quiz
{
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)    
    private Long id;
    
    @Persistent
    private int correctlyGuessed;
    @Persistent
    private List<Long> quizQuestions = new LinkedList<Long>();

//    public static long newQuiz(int pMaxQuizSize)
//    {
//        PersistenceManager pm = PMF.get().getPersistenceManager();
//        Quiz quiz = new Quiz();
//        long lReturn = 0;
//        Set<Integer> mQuestionSet = new HashSet<Integer>();
//        List<SimpleGuessable> mPossibleQuestions;
//        System.out.println("CREATING NEW QUIZ OF MAX LENGTH " + pMaxQuizSize);
//        Random r = new Random(System.currentTimeMillis());
//        int iMaxQuizSize = pMaxQuizSize;
//        Query q = pm.newQuery(SimpleGuessable.class);
//        mPossibleQuestions = (List<SimpleGuessable>)q.execute();
//
//        if(mPossibleQuestions.size() < iMaxQuizSize)
//        {
//            iMaxQuizSize = mPossibleQuestions.size();
//        }
//        int iCurrentQuestion = 0;
//        SimpleGuessable question = null;
//        while(mQuestionSet.size() < iMaxQuizSize)
//        {
//            int i = r.nextInt(mPossibleQuestions.size());
//            if(!mQuestionSet.contains(i))
//            {
//                mQuestionSet.add(i);
//                question = mPossibleQuestions.get(i);
//                quiz.addQuestion(question);
//                iCurrentQuestion++;
//            }
//        }
//        pm.makePersistent(quiz);
//        lReturn = quiz.id;
//        return lReturn;
//    }

    public static long[] newQuiz(int pMaxQuizSize)
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Set<Integer> mQuestionSet = new HashSet<Integer>();
        List<SimpleGuessable> mPossibleQuestions;
        System.out.println("CREATING NEW QUIZ OF MAX LENGTH " + pMaxQuizSize);
        List<Long> lQuiz = new ArrayList<Long>();
        Random r = new Random(System.currentTimeMillis());
        int iMaxQuizSize = pMaxQuizSize;
        Query q = pm.newQuery(SimpleGuessable.class);
        mPossibleQuestions = (List<SimpleGuessable>)q.execute();

        if(mPossibleQuestions.size() < iMaxQuizSize)
        {
            iMaxQuizSize = mPossibleQuestions.size();
        }
        long[] lReturn = new long[iMaxQuizSize];
        int iCurrentQuestion = 0;
        SimpleGuessable question = null;
        while(mQuestionSet.size() < iMaxQuizSize)
        {
            int i = r.nextInt(mPossibleQuestions.size());
            if(!mQuestionSet.contains(i))
            {
                mQuestionSet.add(i);
                question = mPossibleQuestions.get(i);
                lReturn[iCurrentQuestion]=question.getId();
                iCurrentQuestion++;
            }
        }
        return lReturn;
    }
    
    public Long getId()
    {
        return id;
    }

    public int getNumberOfQuestions()
    {
        return quizQuestions.size();
    }

    public int getCorrectlyGuessed()
    {
        return correctlyGuessed;
    }
    
    public void addQuestion(SimpleGuessable pQuestion)
    {
        quizQuestions.add(pQuestion.getId());
    }
    
    public SimpleGuessable getQuestion(PersistenceManager pStore, int pQuestionNumber)
    {
        if(quizQuestions.size() > 0 && pQuestionNumber < quizQuestions.size())
        {
            Long lQuestionID = quizQuestions.get(pQuestionNumber);
            return pStore.getObjectById(SimpleGuessable.class, lQuestionID);
        }
        return null;
    }
    
    public static List<String[]> getAnswers(long[] pQuizQuestions)
    {
        List<String[]> returnList = new ArrayList<String[]>();
        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
        List<Key> lKeys = new ArrayList<Key>();
        for(long l:pQuizQuestions)
        {
            lKeys.add(KeyFactory.createKey(SimpleGuessable.class.getSimpleName(), l));
        }
        Map<Key, Entity> results = service.get(lKeys);
        for(Key l:lKeys)
        {
            Entity e = results.get(l);
            String[] answerAndImage = new String[2];
            List<String> answers = (List<String>)e.getProperty("answers");
            int correctAnswer = ((Long)e.getProperty("correctAnswer")).intValue();
            answerAndImage[0]=answers.get(correctAnswer);
            answerAndImage[1]=e.getProperty("answerImageLocation").toString();
            returnList.add(answerAndImage);
        }
        return returnList;
    }
    
    //Return a map of "MD5(quizXquestionY)=>questionImagelocation
    public static Map<String,String> getQuestionImageHash(long[] pQuizQuestions)
    {
        Map<String, String> returnMap = new HashMap<String, String>();
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DatastoreService service = DatastoreServiceFactory.getDatastoreService();
            List<Key> lKeys = new ArrayList<Key>();
            for(long l:pQuizQuestions)
            {
                lKeys.add(KeyFactory.createKey(SimpleGuessable.class.getSimpleName(), l));
            }
            Map<Key, Entity> results = service.get(lKeys);
            for(int i=0; i < lKeys.size();i++)
            {
                Entity e = results.get(lKeys.get(i));
                String questionImage = e.getProperty("imageLocation").toString();
                byte[] hash = md.digest(("question" + pQuizQuestions[i]).getBytes());
                BigInteger bigInt = new BigInteger(1,hash);
                String hashtext = bigInt.toString(16);
                // Now we need to zero pad it if you actually want the full 32 chars.
                while(hashtext.length() < 32 ){
                  hashtext = "0"+hashtext;
                }
                returnMap.put(hashtext, questionImage);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnMap;
    }
}
