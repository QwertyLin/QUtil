package q.view.list;

import q.util.QLog;
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
public class QListViewUtilAutoLoadMore implements OnScrollListener {
	
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
	
	private ListView listView;
	private Callback callback;
	private boolean isLoadingMore; //是否正在加载更多
	private boolean hasMore = true; //是否还有更多，若没则不再显示底部提示
	private Runnable taskMore; //异步执行的任务
	private View viewFooter; //底部正在加载View
	private Handler handler;

	public QListViewUtilAutoLoadMore(Context ctx, ListView listView, View viewFooter, Callback callback){
		this.listView = listView;
		this.callback = callback;
		//
		if(viewFooter == null){
			TextView tv = new TextView(ctx);
			tv.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(0, 10, 0, 10);
			tv.setText("正在加载更多...");
			viewFooter = tv;
		}
		this.viewFooter = viewFooter;
		listView.addFooterView(this.viewFooter);
		//
		listView.setOnScrollListener(this);
	}
	
	public boolean isLoadingMore(){
		return isLoadingMore;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		callback.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		if(!isLoadingMore && hasMore && totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount ){
			QLog.log("onMore");
			isLoadingMore = true;
			//
			if(taskMore == null){
				taskMore = new Runnable() {
					@Override
					public void run() {
						hasMore = callback.onMoreOnThread();
						handler.sendEmptyMessage(0);
					}
				};
				handler = new Handler(){
					public void handleMessage(android.os.Message msg) {
						callback.onMoreFinish();
						isLoadingMore = false;
						if(!hasMore){
							listView.removeFooterView(viewFooter);
						}
					};
				};
			}
			if(!callback.onMoreStart()){
				isLoadingMore = false;
				return;
			}
			new Thread(taskMore).start();
		}
	}
}
