package q.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import q.view.adapter.QBaseAdapter;
import q.view.list.QAutoLoadMoreListView;
import q.view.text.QRotateTextView;
import q.view.text.QTextViewUtil;

public class QView {
	
	/* Adapter */
	
	public static class adapter {
		
		public static abstract class base<T> extends QBaseAdapter<T>{
			public base(Context ctx, List<T> datas) {
				super(ctx, datas);
			}
		}
	}
	
	/* ListView */
	
	public static class list {
		
		/**
		 * 自动加载更多
		 */
		public static class autoLoadMore extends QAutoLoadMoreListView {
			public interface Callback extends QAutoLoadMoreListView.Callback{}
			public autoLoadMore(Context ctx, ListView listView, View viewFooter, Callback callback) {
				super(ctx, listView, viewFooter, callback);
			}
		}
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
