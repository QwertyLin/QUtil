package q.view.list;

import q.util.Q;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 为ListView增加“自动加载更多”功能
 */
public class QAutoLoadMoreListView implements OnScrollListener {
	
	public interface OnLoadMoreListener {

		void onStart();
		/**
		 * 在非UI线程运行
		 * @return 是否还有更多
		 */
		boolean onBackground();
		void onFinish();
		void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}
	
	private ListView mListView;
	private BaseAdapter mAdapter;
	private OnLoadMoreListener mListener;
	private boolean mIsLoadingMore; //是否正在加载更多
	private boolean mHasMore = true; //是否还有更多，若没则不再显示底部提示
	private Runnable mTaskMore; //异步执行的任务
	private View mViewFooter; //底部正在加载View
	private Handler mHandler;

	public QAutoLoadMoreListView(Context ctx, ListView listView, BaseAdapter adapter, View viewFooter, OnLoadMoreListener listener){
		this.mListView = listView;
		this.mAdapter = adapter;
		this.mListener = listener;
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
		//
		listView.setAdapter(adapter);
	}
	
	public boolean isLoadingMore(){
		return mIsLoadingMore;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		if(!mIsLoadingMore && mHasMore && totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount ){
			Q.log.log(this, "onMore");
			mIsLoadingMore = true;
			//
			mListener.onStart();
			if(mTaskMore == null){
				mTaskMore = new Runnable() {
					@Override
					public void run() {
						mHasMore = mListener.onBackground();
						mHandler.sendEmptyMessage(0);
					}
				};
				mHandler = new Handler(){
					public void handleMessage(android.os.Message msg) {
						mListener.onFinish();
						mAdapter.notifyDataSetChanged();
						mIsLoadingMore = false;
						if(!mHasMore){
							mListView.removeFooterView(mViewFooter);
						}
					};
				};
			}
			Q.thread.manager().execute(mTaskMore);
		}
	}
}
