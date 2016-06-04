package com.knucapstone.tripjuvo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;


public class DrawerDividerItemDecoration extends RecyclerView.ItemDecoration
{
	private Drawable mDivider;
	private List<Integer> mDividerPositions;
	private int mSpacing;


	public DrawerDividerItemDecoration(Context context, AttributeSet attrs, List<Integer> dividerPositions, int spacingPixelSize)
	{
		final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
		mDivider = a.getDrawable(0);
		a.recycle();

		mDividerPositions = dividerPositions;
		mSpacing = spacingPixelSize;
	}


	public DrawerDividerItemDecoration(Drawable divider, List<Integer> dividerPositions, int spacingPixelSize)
	{
		mDivider = divider;
		mDividerPositions = dividerPositions;
		mSpacing = spacingPixelSize;
	}


	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state)
	{
		super.getItemOffsets(outRect, view, recyclerView, state);

		if(mDivider==null)
		{
			return;
		}

		int position = recyclerView.getChildLayoutPosition(view);
		int itemCount = recyclerView.getAdapter().getItemCount();

		// first item after header offset
		if(position==1)
		{
			outRect.top = mSpacing;
		}

		// last item offset
		if(position==itemCount-1)
		{
			outRect.bottom = mSpacing;
		}

		// divider offset
		if(mDividerPositions.contains(position))
		{
			outRect.top = mSpacing*2;
		}
	}


	@Override
	public void onDrawOver(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state)
	{
		if(mDivider==null)
		{
			super.onDrawOver(canvas, recyclerView, state);
			return;
		}

		// initialization
		int left = 0, right = 0, top = 0, bottom = 0, size;
		int childCount = recyclerView.getChildCount();

		size = mDivider.getIntrinsicHeight();
		left = recyclerView.getPaddingLeft();
		right = recyclerView.getWidth() - recyclerView.getPaddingRight();

		for(int i=0; i<childCount; i++)
		{
			View child = recyclerView.getChildAt(i);

			// check if draw divider or not
			int position = recyclerView.getChildLayoutPosition(child);
			boolean drawDivider = mDividerPositions.contains(position);
			if(!drawDivider) continue;

			// calculate bounds
			RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
			top = child.getTop() - params.topMargin - size - mSpacing;
			bottom = top + size;

			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(canvas);
		}
	}
}
