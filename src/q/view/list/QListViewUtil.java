package q.view.list;

import android.widget.ListView;

public class QListViewUtil {

	public static void init(ListView listView){
		listView.setCacheColorHint(0x00000000);
		//listView.setDivider(null);
		//listView.setDividerHeight(0);
		listView.setFadingEdgeLength(0);
	}
}
