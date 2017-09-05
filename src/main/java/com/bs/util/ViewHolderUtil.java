package com.bs.util;

import android.util.SparseArray;
import android.view.View;

@SuppressWarnings("unchecked")
public class ViewHolderUtil {
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHoler = (SparseArray<View>) view.getTag();
		if (viewHoler == null) {
			viewHoler = new SparseArray<View>();
			view.setTag(viewHoler);
		}
		View childrenView = viewHoler.get(id);
		if (childrenView == null) {
			childrenView = view.findViewById(id);
			viewHoler.put(id, childrenView);
		}
		return (T) childrenView;
	}
}
