package org.hokiesuns.guesswhat.flickr;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hokiesuns.guesswhat.model.PMF;
import org.hokiesuns.guesswhat.model.SimpleGuessable;
import org.hokiesuns.guesswhat.model.User;

import com.aetrion.flickr.REST;
import com.aetrion.flickr.Transport;
import com.aetrion.flickr.interestingness.InterestingnessInterface;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.tags.Tag;

public class InterestingPicServlet extends HttpServlet
{
    private static final Set<String> peopleTags = new HashSet<String>()
    {
        {
            add("portrait");
            add("girl");
            add("boy");
            add("child");
            add("children");
            add("man");
            add("woman");
            add("people");
            add("kid");
        }
    };
    
    private static final String API_KEY = "4b1b63d999f57d3d8bc3510f9b781b6c";
    private static final String SHARED_SECRET = "1989fd52ac3844c4";
    private PersistenceManager pm = PMF.get().getPersistenceManager();
    private static final int PICS_TO_ADD=200; //Maximum number of pics to add from Flickr
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
//        Random r = new Random(System.currentTimeMillis());
        long lStart = System.currentTimeMillis(),lEnd = 0;
        Set<String> extraFields = new HashSet<String>();
        extraFields.add("tags");
        try
        {
            Transport t = new REST();
            InterestingnessInterface iPics = new InterestingnessInterface(API_KEY,SHARED_SECRET,t);
            PhotosInterface photoInt = new PhotosInterface(API_KEY,SHARED_SECRET,t);
            int iPicsAdded = 0;
            int iPage = 1;
            
            while(iPicsAdded < PICS_TO_ADD)
            {
                PhotoList pList = iPics.getList(getYesterday(),extraFields,200,iPage);
                for(int i=0; i < pList.size() && i < PICS_TO_ADD; i++)
                {
                    Photo p = (Photo)pList.get(i);
//                    p = photoInt.getInfo(p.getId(), p.getSecret());
                    if(!isPersonPic(p.getTags()))
                    {
//                        System.out.println(p.getUrl() + ":" + p.getTitle() + " " + p.getMediumUrl());
                        addPicture(p);
                        iPicsAdded++;
                    }
                }
                iPage++;
            }
        }
        catch(Exception e)
        {
            throw new ServletException(e);
        }
        lEnd = System.currentTimeMillis();
        resp.getOutputStream().println("Added " + PICS_TO_ADD + " pictures in " + (lEnd - lStart)/1000 + " seconds");
    }

    private void addPicture(Photo pic)
    {
        SimpleGuessable guess = new SimpleGuessable();
        guess.setImageLocation(pic.getMediumUrl());
        guess.setAnswerImageLocation(pic.getSmallUrl());
        guess.setCorrectAnswer(-1);
        guess.setCreator(new User(-1l));
        pm.makePersistent(guess);        
    }
    
    private boolean isPersonPic(Collection<Tag> tags)
    {
        for(Tag tag:tags)
        {
            String sTag = tag.getValue().toLowerCase();
            if(peopleTags.contains(sTag.toLowerCase()))
                return true;
        }
        return false;
    }
    
    private Date getYesterday()
    {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, -2);
        return c.getTime();
    }
    public static void main(String[] args) throws Exception
    {
        InterestingPicServlet servlet = new InterestingPicServlet();
        servlet.doGet(null, null);
    }
}
