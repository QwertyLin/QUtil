package q.view.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class  QAdapterBase<T> extends BaseAdapter {
	
	protected List<T> mDatas;
	protected Context mCtx;
	protected LayoutInflater mInflater;
	
	public QAdapterBase(Context ctx, List<T> datas) {
		this.mCtx = ctx;
		this.mDatas =  datas;
		this.mInflater = ((Activity)ctx).getLayoutInflater();
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		Object h = null;
		if(v == null){
			v = mInflater.inflate(getLayoutId(), null);
			h = getViewHolder(v);
			v.setTag(h);
		}else{
			h = v.getTag();
		}
		onInitItem(position, mDatas.get(position), h);
		return v;
	}
	
	protected abstract int getLayoutId();
	protected abstract Object getViewHolder(View v);
	protected abstract void onInitItem(int position, T data, Object viewHolder);
}
