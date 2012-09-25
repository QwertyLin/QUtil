package q.view;

import java.util.List;

import android.content.Context;
import q.view.adapter.QBaseAdapter;

public class QView {
	
	public static abstract class adapter<T> extends QBaseAdapter<T>{
		public adapter(Context ctx, List<T> datas) {
			super(ctx, datas);
		}
	}

}
