package q.http;

public interface OnHttpListener {
	
	boolean onHttpVerify(HttpEntity entity);
	void onHttpFinish(HttpEntity entity);
	void onHttpError(HttpEntity entity);

}
