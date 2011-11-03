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
        String cv = r.getParameter("oauth_header");

        try {
            cv = java.net.URLDecoder.decode(cv, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
        }

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
    
}
