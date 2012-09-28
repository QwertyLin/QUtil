package q.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import q.view.adapter.QAdapterBase;
import q.view.list.QAutoLoadMoreListView;
import q.view.list.QListViewUtil;
import q.view.list.QPullToRefreshListView;
import q.view.tab.QTabActivityBase;
import q.view.text.QRotateTextView;
import q.view.text.QTextViewUtil;

public class QView {
	
	/* Adapter */
	
	public static class adapter {
		
		public static abstract class base<T> extends QAdapterBase<T>{
			public base(Context ctx, List<T> datas) {
				super(ctx, datas);
			}
		}
	}
	
	/* ListView */
	
	public static class list {
		
		public static class util extends QListViewUtil{}
		
		/**
		 * 自动加载更多
		 */
		public static class autoLoadMore extends QAutoLoadMoreListView {
			public interface OnLoadMoreListener extends QAutoLoadMoreListView.OnLoadMoreListener{}
			public autoLoadMore(Context ctx, ListView listView, BaseAdapter adapter, View viewFooter, OnLoadMoreListener listener) {
				super(ctx, listView, adapter, viewFooter, listener);
			}
		}
		
		/**
		 * 下拉刷新
		 */
		public static class pullToRefresh extends QPullToRefreshListView {
			public interface OnRefreshListener extends QPullToRefreshListView.OnRefreshListener{}
			public pullToRefresh(Context ctx, BaseAdapter adapter, OnRefreshListener listener) {
				super(ctx, adapter, listener);
			}
		}
	}
	
	/* Tab */
	
	public static class tab {
		
		public abstract static class base extends QTabActivityBase {}
	}
	
	/* TextView */
	
	public static class text {
		
		/**
		 * 旋转文字
		 */
		public static class rotate extends QRotateTextView {
			public rotate(Context context, float degree) {
				super(context, degree);
			}
		}
		
		public static class util extends QTextViewUtil{}
	}
	
	
}
