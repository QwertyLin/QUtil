package q.view.list;

import q.util.QUtil;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 为ListView增加“自动加载更多”功能
 * 需要在setAdapter之前调用
 */
public class QAutoLoadMoreListView implements OnScrollListener {
	
	public interface Callback {
		/**
		 * @return true继续执行onMoreOnThread
		 */
		boolean onMoreStart();
		/**
		 * 在非UI线程运行
		 * @return 是否还有更多
		 */
		boolean onMoreOnThread();
		void onMoreFinish();
		void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}
	
	private ListView mListView;
	private Callback mCallback;
	private boolean mIsLoadingMore; //是否正在加载更多
	private boolean mHasMore = true; //是否还有更多，若没则不再显示底部提示
	private Runnable mTaskMore; //异步执行的任务
	private View mViewFooter; //底部正在加载View
	private Handler mHandler;

	public QAutoLoadMoreListView(Context ctx, ListView listView, View viewFooter, Callback callback){
		this.mListView = listView;
		this.mCallback = callback;
		//
		if(viewFooter == null){
			TextView tv = new TextView(ctx);
			tv.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(0, 10, 0, 10);
			tv.setText("正在加载更多...");
			viewFooter = tv;
		}
		this.mViewFooter = viewFooter;
		listView.addFooterView(this.mViewFooter);
		//
		listView.setOnScrollListener(this);
	}
	
	public boolean isLoadingMore(){
		return mIsLoadingMore;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mCallback.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		if(!mIsLoadingMore && mHasMore && totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount ){
			QUtil.log.log(this, "onMore");
			mIsLoadingMore = true;
			//
			if(mTaskMore == null){
				mTaskMore = new Runnable() {
					@Override
					public void run() {
						mHasMore = mCallback.onMoreOnThread();
						mHandler.sendEmptyMessage(0);
					}
				};
				mHandler = new Handler(){
					public void handleMessage(android.os.Message msg) {
						mCallback.onMoreFinish();
						mIsLoadingMore = false;
						if(!mHasMore){
							mListView.removeFooterView(mViewFooter);
						}
					};
				};
			}
			if(!mCallback.onMoreStart()){
				mIsLoadingMore = false;
				return;
			}
			new Thread(mTaskMore).start();
		}
	}
}
