package q.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import q.view.adapter.QBaseAdapter;
import q.view.list.QListAutoLoadMore;

public class QView {
	
	/* Adapter */
	
	public static abstract class adapter<T> extends QBaseAdapter<T>{
		public adapter(Context ctx, List<T> datas) {
			super(ctx, datas);
		}
	}
	
	/* ListView */
	
	public static class list {
		
		public static class autoLoadMore extends QListAutoLoadMore {
			public interface Callback extends QListAutoLoadMore.Callback{}
			public autoLoadMore(Context ctx, ListView listView, View viewFooter, Callback callback) {
				super(ctx, listView, viewFooter, callback);
			}
		}
	}

	
	
}
