package qv.list.more;

import q.QLog;
import android.content.Context;
import android.os.Message;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 为ListView增加“自动加载更多”功能
 */
public class LvAutoMoreUtil {
	
	/**
	 * 在setAdapter之前调用
	 */
	public static void init(final LvAutoMoreEntity en){		
		if(en.getViewFooter() == null){
			TextView tv = new TextView((Context)en.getListener());
			tv.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(0, 10, 0, 10);
			tv.setText("正在加载更多...");
			en.setViewFooter(tv);
		}
		en.getListView().addFooterView(en.getViewFooter());
		//
		en.getListView().setOnScrollListener(new OnScrollListener() { 
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) { }
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(en.isEnable() && !en.isDoing() && totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount ){
					QLog.log(this, "onMore");
					en.setDoing(true);
					en.getListener().onLvAutoMoreStart(en);
					if(en.isEnable()){
						LvAutoMoreInstance.getInstance().getThreadPool().execute(new Runnable() {
							@Override
							public void run() {
								en.getListener().onLvAutoMoreBackground(en);
								Message msg = LvAutoMoreInstance.getInstance().getHandler().obtainMessage();
								msg.what = LvAutoMoreInstance.MSG_SUCCESS;
								msg.obj = en;
								LvAutoMoreInstance.getInstance().getHandler().sendMessage(msg);
							}
						});
					}
				}
			}
		});
		//
	}
	
}
