package org.hokiesuns.guesswhat.facebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.IFacebookRestClient;
import com.google.code.facebookapi.ProfileField;

public class FBUser implements Serializable
{
    private String name;
    private long id;
    private long[] friendids;
    private String[] friends;
    
    public FBUser(long pUserId, IFacebookRestClient<Object> pFacebookClient) throws FacebookException
    {
        id = pUserId;
        //Initialize the user
        List<ProfileField> fields=new ArrayList<ProfileField>();
        fields.add(ProfileField.NAME);
        List<Long> lIds = new ArrayList<Long>();
        lIds.add(pUserId);
        JSONArray friendArray = (JSONArray)pFacebookClient.friends_get();
        if(friendArray != null)
        {
            friendids = new long[friendArray.length()];
            friends = new String[friendids.length];
            
            for(int i=0; i < friendArray.length(); i++)
            {
                try
                {
                    friendids[i]=friendArray.getLong(i);
                    lIds.add(friendids[i]);
                }
                catch (JSONException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        JSONArray friendInfo = (JSONArray)pFacebookClient.users_getInfo(lIds, fields);
        int friendIdx = 0;
        for(int i=0; i < friendInfo.length(); i++)
        {
            try
            {
                JSONObject user  = (JSONObject)friendInfo.getJSONObject(i);
                long lId = user.getLong(ProfileField.UID.toString());
                String sName = user.getString(ProfileField.NAME.toString());
                if(lId == pUserId)
                {
                    name = sName;
                }
                else
                {
                    friends[friendIdx] = user.getString(ProfileField.NAME.toString());
                    friendIdx++;
                }
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public long getId()
    {
        return id;
    }

    public String[] getFriends()
    {
        return friends;
    }

    public long[] getFriendIds()
    {
        return friendids;
    }
}
