package com.knucapstone.tripjuvo.view;

import android.content.Context;
import android.util.AttributeSet;

import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;


// code taken from: http://stackoverflow.com/questions/3948934/synchronise-scrollview-scroll-positions-android
public class ObservableStickyScrollView extends StickyScrollView
{
	private ScrollViewListener scrollViewListener = null;


	public interface ScrollViewListener
	{
		void onScrollChanged(ObservableStickyScrollView scrollView, int x, int y, int oldx, int oldy);
	}


	public ObservableStickyScrollView(Context context)
	{
		super(context);
	}


	public ObservableStickyScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	public ObservableStickyScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}


	public void setOnScrollViewListener(ScrollViewListener scrollViewListener)
	{
		this.scrollViewListener = scrollViewListener;
	}


	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy)
	{
		super.onScrollChanged(x, y, oldx, oldy);
		if(scrollViewListener != null)
		{
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}
}
