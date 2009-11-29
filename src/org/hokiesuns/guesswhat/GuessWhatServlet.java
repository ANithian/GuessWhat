package org.hokiesuns.guesswhat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.hokiesuns.guesswhat.model.PMF;
import org.hokiesuns.guesswhat.model.Quiz;
import org.hokiesuns.guesswhat.model.SimpleGuessable;
import org.hokiesuns.guesswhat.model.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class GuessWhatServlet extends HttpServlet
{
    private List<SimpleGuessable> results = null;
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        
        String action = req.getParameter("action");
        System.out.println("ACTION = " + action);
        if(action.equals("imageadmin"))
        {
            InputStream is = this.getClass().getResourceAsStream("/org/hokiesuns/guesswhat/model/images.xml");
            try
            {
                Query q = pm.newQuery(SimpleGuessable.class);
                Collection<SimpleGuessable> c = (Collection<SimpleGuessable>)q.execute();
                pm.deletePersistentAll(c);
                q = pm.newQuery(User.class);
                pm.deletePersistentAll((Collection<User>)q.execute());
                
                Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList images = (NodeList)xPath.evaluate("/images/image", d,XPathConstants.NODESET);
//                User u = new User(100000289421407l);
                User u = new User(-1l);
                pm.makePersistent(u);
                for(int i=0; i < images.getLength(); i++)
                {
                    Element eImage = (Element)images.item(i);
                    SimpleGuessable guess = new SimpleGuessable();
                    guess.setImageLocation(eImage.getAttribute("name"));
                    guess.setAnswerImageLocation(eImage.getAttribute("answerImage"));
                    guess.setCorrectAnswer(Integer.parseInt(eImage.getAttribute("correctAnswer")));
                    guess.setCreator(u);
                    NodeList answers = (NodeList)xPath.evaluate("answer", eImage,XPathConstants.NODESET);
                    for(int j=0; j < answers.getLength(); j++)
                    {
                        Element eAnswer = (Element)answers.item(j);
                        guess.addAnswer(eAnswer.getTextContent());
                    }
                    pm.makePersistent(guess);
                }
                is.close();
            }
            catch (SAXException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (ParserConfigurationException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (XPathExpressionException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                pm.close();
            }
        }
//        else if(action.equals("newQuiz"))
//        {
//            long id = Quiz.newQuiz(10);
//            System.out.println("QUIZ ID = " + id);
//            pm = PMF.get().getPersistenceManager();
//            Quiz quiz = pm.getObjectById(Quiz.class,id);
//            pm.makePersistent(quiz);
//            System.out.println(quiz);
//        }
        else if(action.equals("questionStats"))
        {
            String id = req.getParameter("user_id");
            long userId = Long.parseLong(id);
            Query q = pm.newQuery("select from " + SimpleGuessable.class.getName() + " where creator=="+ userId);
            List<SimpleGuessable> results = (List<SimpleGuessable>)q.execute();
            for(SimpleGuessable s:results)
            {
                System.out.println("Image: " + s.getAnswerImageLocation() + " has been viewed " + s.getNumberTimesShown() + " times.");
                System.out.println("Answer Distribution:");
                List<Integer> answerDist = s.getAnswerDistributions();
                List<String> answers = s.getAnswers();
                for(int i=0; i < answers.size(); i++)
                {
                    System.out.println(answers.get(i) + " " + answerDist.get(i));
                }
                System.out.println("===========================");
            }
        }
        else if(action.equals("test"))
        {
//            MessageDigest md;
//            try
//            {
//                md = MessageDigest.getInstance("MD5");
//                byte[] md5 = md.digest("Hello World".getBytes());
////                StringBuffer hexString = new StringBuffer();
////                for (int i=0;i<md5.length;i++) {
////                    hexString.append(Integer.toHexString(0xFF & md5[i]));
////                }
////                System.out.println(hexString.toString());
//                BigInteger bigInt = new BigInteger(1,md5);
//                String hashtext = bigInt.toString(16);
//                // Now we need to zero pad it if you actually want the full 32 chars.
//                while(hashtext.length() < 32 ){
//                  hashtext = "0"+hashtext;
//                }
//                System.out.println(hashtext);
//            }
//            catch (NoSuchAlgorithmException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            Quiz.getNumberImages();
        }
        else if(action.equals("bulkget"))
        {
            DatastoreService service = DatastoreServiceFactory.getDatastoreService();

            List<Key> keys = new ArrayList<Key>();
            //...
            keys.add(KeyFactory.createKey(SimpleGuessable.class.getSimpleName(), 49));
            keys.add(KeyFactory.createKey(SimpleGuessable.class.getSimpleName(), 50));
            Map<Key,Entity> entities = service.get(keys);
            System.out.println(entities.size());
            for(Entry<Key, Entity> e:entities.entrySet())
            {
                System.out.println(e.getValue().getProperty("answers").getClass());
            }
        }
        else if(action.equals("getuser"))
        {
            Query q = pm.newQuery(User.class);
            List<User> l = (List<User>)q.execute();
            System.out.println(l.size());
        }
        else if(action.equals("getguess"))
        {
//            SimpleGuessable g = pm.getObjectById(SimpleGuessable.class,41);
//            System.out.println(g.getCorrectAnswer());
//            Query query = pm.newQuery("select from org.hokiesuns.guesswhat.model.Employee");
            Query query = pm.newQuery("select from org.hokiesuns.guesswhat.model.SimpleGuessable");
//            Query query = pm.newQuery(Employee.class);
//            query.setFilter("lastName == lastNameParam");
//            query.setOrdering("hireDate desc");
//            query.declareParameters("String lastNameParam");
            try {
//                List<Employee> results = (List<Employee>) query.execute();
//                System.out.println(results.size());
//                if (results.iterator().hasNext()) {
//                    for (Employee e : results) {
//                        // ...
//                        System.out.println(e.getFirstName());
//                    }
//                } else {
//                    // ... no results ...
//                    System.out.println("no results");
//                }
                results = (List<SimpleGuessable>) query.execute();
                System.out.println(results.size());
                for (SimpleGuessable e : results) {
                    // ...
                    System.out.println(e.getId());
                    System.out.println(e.getCreator());
                    System.out.println(e.getImageLocation());
                    System.out.println(e.getAnswerImageLocation());
                    System.out.println(e.getCorrectAnswer());
                    for(String s:e.getAnswers())
                    {
                        System.out.println(s);
                    }
                    System.out.println("--------------");
                }                 
            } finally {
                query.closeAll();
//                pm.close();
            }
           
        }
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, worlddd");
    }
}
