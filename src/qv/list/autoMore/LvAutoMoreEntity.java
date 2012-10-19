package qv.list.autoMore;

import android.view.View;
import android.widget.ListView;

public class LvAutoMoreEntity {

	private int id;
	private OnLvAutoMoreListener listener;
	private ListView listView;
	private View viewFooter; //底部正在加载View
	private boolean isDoing; //是否正在执行
	private boolean isEnable = true;//是否有效，即是否自动加载更多
	
	public LvAutoMoreEntity(int id, OnLvAutoMoreListener listener, ListView listView, View viewFooter){
		this.id = id;
		this.listener = listener;
		this.listView = listView;
		this.viewFooter = viewFooter;
	}
	
	protected void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	protected void setViewFooter(View viewFooter) {
		this.viewFooter = viewFooter;
	}
	protected void setDoing(boolean isDoing) {
		this.isDoing = isDoing;
	}
	
	public int getId() {
		return id;
	}
	public OnLvAutoMoreListener getListener() {
		return listener;
	}
	public ListView getListView() {
		return listView;
	}
	public View getViewFooter() {
		return viewFooter;
	}
	public boolean isDoing() {
		return isDoing;
	}
	public boolean isEnable() {
		return isEnable;
	}
	
	
	
}
