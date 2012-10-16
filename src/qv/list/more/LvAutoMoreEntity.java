package qv.list.more;

import android.os.Message;
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
	
	public void setEnable(boolean isEnable) {
		if(this.isEnable ^ isEnable){
			this.isEnable = isEnable;
			Message msg = LvAutoMoreInstance.getInstance().getHandler().obtainMessage();
			if(isEnable){
				msg.what = LvAutoMoreInstance.MSG_ENABLE;
			}else{
				msg.what = LvAutoMoreInstance.MSG_DISABLE;
			}
			msg.obj = this;
			LvAutoMoreInstance.getInstance().getHandler().sendMessage(msg);
		}
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
	public void setViewFooter(View viewFooter) {
		this.viewFooter = viewFooter;
	}
	public boolean isDoing() {
		return isDoing;
	}
	public void setDoing(boolean isDoing) {
		this.isDoing = isDoing;
	}
	public boolean isEnable() {
		return isEnable;
	}
	
	
	
}
