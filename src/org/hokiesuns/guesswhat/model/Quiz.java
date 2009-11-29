package org.hokiesuns.guesswhat.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jdo.JDOCanRetryException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;



public class Quiz
{
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
            if(correctAnswer >=0)
                answerAndImage[0]=answers.get(correctAnswer);
            answerAndImage[1]=e.getProperty("answerImageLocation").toString();
            returnList.add(answerAndImage);
        }
        return returnList;
    }
    
    public static String[] getUserAnswers(long[] pQuizQuestions, int[] pUserAnswers)
    {
        String[] returnList = new String[pUserAnswers.length];
        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
        List<Key> lKeys = new ArrayList<Key>();
        for(long l:pQuizQuestions)
        {
            lKeys.add(KeyFactory.createKey(SimpleGuessable.class.getSimpleName(), l));
        }
        Map<Key, Entity> results = service.get(lKeys);
        int i=0;
        for(Key l:lKeys)
        {
            Entity e = results.get(l);
            List<String> answers = (List<String>)e.getProperty("answers");
            if(pUserAnswers[i] >=0)
                returnList[i]=answers.get(pUserAnswers[i]);
            else
                returnList[i]=null;
            i++;
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
    
    public static void updateQuestionStatistics(PersistenceManager pManager, long pQuestion, int pAnswerChoice, String pFreeChoice)
    {
        final int NUM_RETRIES=5;
        for (int i = 0; i < NUM_RETRIES; i++) {
            pManager.currentTransaction().begin();

            SimpleGuessable question = pManager.getObjectById(SimpleGuessable.class, pQuestion);
            question.updateStatistics(pAnswerChoice, pFreeChoice);
            try {
                pManager.currentTransaction().commit();
                break;

            } catch (JDOCanRetryException ex) {
                if (i == (NUM_RETRIES - 1)) { 
                    throw ex;
                }
            }
        }
    }
    
    public static int getNumberImages()
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("__Stat_Kind__");
        
        Entity e = datastore.prepare(q).asSingleEntity();
//        Iterable<Entity> it = datastore.prepare(new com.google.appengine.api.datastore.Query("__Stat_Kind__")).asIterable();
//        for(Entity e:it)
//        {
//            String s = e.getProperty("kind_name").toString();
//            System.out.println(s);
//        }
        return 10;
    }
}
