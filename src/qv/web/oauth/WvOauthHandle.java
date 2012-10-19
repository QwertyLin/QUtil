package qv.web.oauth;

import java.util.regex.Matcher;

public interface WvOauthHandle {

	int getType();
	String getAuthUrl();
	String getUrlParsePattern();
	void parseUrl(final WvOauthEntity en, Matcher m);
	
}


