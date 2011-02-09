/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.smartplatforms.client;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;

/**
 *
 * @author josh
 */
public class SMArtOAuthParser{
	
	private HttpParameters params;
	
    public SMArtOAuthParser(HttpServletRequest r) {
        String cn = r.getParameter("cookie_name");
        String cv = getCookieValue(r.getCookies(), cn, null);
        try {
            cv = java.net.URLDecoder.decode(cv, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
                // TODO Auto-generated catch block
                uee.printStackTrace();
        }

        cv = cv.split("Authorization: ")[1];
    	this.setParams(cv);   
    }

    public SMArtOAuthParser(String oauth_header_string) {
    	this.setParams(oauth_header_string);
    }
    
    private void setParams(String h) {
    	this.params  = OAuth.oauthHeaderToParamsMap(h);
    }

    public String getParam(String pname) {
    	return this.params.getFirst(pname);
    }
    
     public static String getCookieValue(Cookie[] cookies, String cookieName,
                    String defaultValue) {
            for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookieName.equals(cookie.getName()))
                            return (cookie.getValue());
            }
            return (defaultValue);
    }


}
