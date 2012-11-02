package qv.web.oauth;

public interface OnOauthListener {
	
	void onWvOauthLoadingFinish();
	void onWvOauthAuthing();
	void onWvOauthSuccess(Token token);
	void onWvOauthError();

}