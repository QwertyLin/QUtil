package a.view.list;

import java.util.ArrayList;
import java.util.List;

import q.util.a.view.QBaseAdapter;

import android.content.Context;
import android.view.View;
import android.widget.Gallery;

public class QGallery<T> extends Gallery {

	public QGallery(Context ctx, ArrayList<T> data, int layoutResId) {
		super(ctx);
		setPadding(0, 0, 20, 0);
		setAdapter(new Adapter(ctx, data));
	}
	
	private class Adapter extends QBaseAdapter<T>{

		public Adapter(Context ctx, ArrayList<T> data) {
			super(ctx, data);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Object getViewHolder(View v) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onInitItem(int position, T data, Object viewHolder) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected int getLayoutId() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	private class Holder {
		
	}

}
