package org.smartplatforms.client;

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

    public TokenSecret(SMArtOAuthParser p) {
        this.token = p.getParam("smart_oauth_token");
        this.tokenSecret = p.getParam("smart_oauth_token_secret");
    }

    public String getToken() { return token; }
    public String getTokenSecret() { return tokenSecret; }

}
