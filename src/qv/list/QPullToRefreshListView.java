package qv.list;
import q.thread.QThreadManager;
import qv.list.pulltorefresh.PullToRefreshBase;
import qv.list.pulltorefresh.PullToRefreshListView;
import android.content.Context;
import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;



public class QPullToRefreshListView extends PullToRefreshListView {
	
	public interface OnRefreshListener{
		void onStart();
		void onBackground();
		void onFinish();
	}
	
	private Runnable mRun;
	private Handler mHandler;
	
	/**
	 * @param context
	 */
	/*public QPullToRefreshListView(Context context) {
		super(context);
		mCtx = context;
	}*/
	
	/**
	 * 下拉模式
	 * @param mode Mode.PULL_DOWN_TO_REFRESH: 下拉、Mode.PULL_UP_TO_REFRESH: 上拉、Mode.BOTH: 上下拉、Mode.DISABLED: 无
	 */
	@Override
	public void setMode(QPullToRefreshListView.Mode mode) {
		super.setMode(mode);
	}
	
	/**
	 * 手动刷新
	 */
	@Override
	public void setRefreshing() {
		super.setRefreshing(false);
	}
	
	public ListView getListView(){
		return getRefreshableView();
	}
	
	public QPullToRefreshListView(final Context ctx, final BaseAdapter adapter, final OnRefreshListener listener){
		super(ctx);
		getRefreshableView().setAdapter(adapter);
		setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				listener.onStart();
				if(mRun == null){
					mRun = new Runnable() {
						@Override
						public void run() {
							listener.onBackground();
							mHandler.sendEmptyMessage(0);
						}
					};
					mHandler = new Handler(){
						public void handleMessage(android.os.Message msg) {
							adapter.notifyDataSetChanged();
							onRefreshComplete();
							setLastUpdatedLabel(DateUtils.formatDateTime(ctx,
									System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL));
							listener.onFinish();
						};
					};
				}
				QThreadManager.getInstance().execute(mRun);
			}
		});
	}
	
	
}
