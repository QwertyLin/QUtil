package qv.list;

import q.QLog;
import q.thread.QThreadManager;
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
public class QAutoLoadMoreListViewFilter implements OnScrollListener, Runnable {
	
	public interface OnLoadMoreListener {

		void onStart();
		/**
		 * 在非UI线程运行
		 * @return 赋值给setEnable
		 */
		boolean onBackground();
		void onFinish();
	}
	
	private ListView mListView;
	private BaseAdapter mAdapter;
	private OnLoadMoreListener mListener;
	private View mViewFooter; //底部正在加载View
	private boolean mIsDoing; //是否正在执行
	private boolean mEnable;//是否有效，即是否自动加载更多
	
	public QAutoLoadMoreListViewFilter(Context ctx, ListView listView, BaseAdapter adapter, View viewFooter, OnLoadMoreListener listener){
		this.mListView = listView;
		this.mAdapter = adapter;
		this.mListener = listener;
		this.mEnable = true;
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
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(mEnable && !mIsDoing && totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount ){
			QLog.log(this, "onMore");
			mIsDoing = true;
			mListener.onStart();
			QThreadManager.getInstance().execute(this);
		}
	}
	
	@Override
	public void run() {
		setEnable(mListener.onBackground());
		mHandler.sendEmptyMessage(0);
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				onFinish();
				break;
			case 1://enable true
				onSetEnable(true);
				break;
			case 2://enable false
				onSetEnable(false);
				break;
			}
		};
	};
	
	private void onFinish(){
		mListener.onFinish();
		mAdapter.notifyDataSetChanged();
		mIsDoing = false;
	}
	
	/**
	 * 改变状态
	 * @param enable
	 */
	private void onSetEnable(boolean enable){
		if(enable){
			mListView.removeFooterView(mViewFooter);
			mListView.addFooterView(mViewFooter);
		}else{
			mListView.removeFooterView(mViewFooter);
		}
	}
	
	/**
	 * 是否有效
	 * @param enable
	 */
	public void setEnable(boolean enable){
		if(mEnable ^ enable){
			this.mEnable = enable;
			if(enable){
				mHandler.sendEmptyMessage(1);
			}else{
				mHandler.sendEmptyMessage(2);
			}
		}
	}
	
	/**
	 * 是否正在执行
	 * @return
	 */
	public boolean isDoing(){
		return mIsDoing;
	}

	
}
