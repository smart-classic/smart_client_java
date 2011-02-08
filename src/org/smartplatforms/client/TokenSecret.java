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
 * @author nate
 */
public class TokenSecret {

    private final String token;
    private final String tokenSecret;

    public TokenSecret(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
    }

    public TokenSecret(HttpServletRequest request) {
        String cn = request.getParameter("cookie_name");
        String cv = getCookieValue(request.getCookies(), cn, null);
        try {
            cv = java.net.URLDecoder.decode(cv, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
                // TODO Auto-generated catch block
                uee.printStackTrace();
        }

        cv = cv.split("Authorization: ")[1];
        HttpParameters ohp = OAuth.oauthHeaderToParamsMap(cv);
        this.token = ohp.getFirst("smart_oauth_token");
        this.tokenSecret = ohp.getFirst("smart_oauth_token_secret");
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

    public String getToken() { return token; }
    public String getTokenSecret() { return tokenSecret; }

}
