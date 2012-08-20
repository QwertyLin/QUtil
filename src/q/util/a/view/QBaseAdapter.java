package q.util.a.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class  QBaseAdapter<T> extends BaseAdapter {
	
	protected List<T> datas;
	protected Context ctx;
	protected LayoutInflater inflater;
	
	public QBaseAdapter(Context ctx, List<T> datas) {
		this.ctx = ctx;
		this.datas =  datas;
		this.inflater = ((Activity)ctx).getLayoutInflater();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public T getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		Object h = null;
		if(v == null){
			v = inflater.inflate(getLayoutId(), null);
			h = getViewHolder(v);
			v.setTag(h);
		}else{
			h = v.getTag();
		}
		onInitItem(position, datas.get(position), h);
		return v;
	}
	
	protected abstract int getLayoutId();
	protected abstract Object getViewHolder(View v);
	protected abstract void onInitItem(int position, T data, Object viewHolder);
}
