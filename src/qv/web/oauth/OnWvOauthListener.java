package qv.web.oauth;

public interface OnWvOauthListener {
	
	void onWvOauthLoadingFinish(WvOauthEntity en);
	void onWvOauthAuthing(WvOauthEntity en);
	void onWvOauthSuccess(WvOauthEntity en);
	void onWvOauthError(WvOauthEntity en);

}
