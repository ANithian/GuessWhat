package org.hokiesuns.guesswhat.facebook;

import com.google.code.facebookapi.FacebookJsonRestClient;
import com.google.code.facebookapi.IFacebookRestClient;

public class FacebookWrapper
{
    private static IFacebookRestClient facebookClient;
    public static IFacebookRestClient getClient(String pSessionKey)
    {
//        if(facebookClient == null)
//        {
            System.out.println("Creating new FB Client with session key " + pSessionKey);
            facebookClient = new FacebookJsonRestClient("732394fdee1cd373b6e4898bfb59c16a", "5ac5cf83775a11041f64b476919c67b5", pSessionKey);
//        }
        return facebookClient;
    }
}
