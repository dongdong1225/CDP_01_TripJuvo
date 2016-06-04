package com.knucapstone.tripjuvo.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.SingleSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.database.model.CategoryModel;
import com.knucapstone.tripjuvo.listener.AnimateImageLoadingListener;

import java.util.List;


public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private static final int VIEW_TYPE_HEADER = 0;
	private static final int VIEW_TYPE_CATEGORY = 1;

	private List<CategoryModel> mCategoryList;
	private CategoryViewHolder.OnItemClickListener mListener;
	private SingleSelector mSingleSelector = new SingleSelector();
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoadingListener mImageLoadingListener;


	public DrawerAdapter(List<CategoryModel> categoryList, CategoryViewHolder.OnItemClickListener listener)
	{
		mCategoryList = categoryList;
		mListener = listener;
		mSingleSelector.setSelectable(true);
		setupImageLoader();
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		// inflate view and create view holder
		if(viewType==VIEW_TYPE_HEADER)
		{
			View view = inflater.inflate(R.layout.drawer_header, parent, false);
			return new HeaderViewHolder(view);
		}
		else if(viewType==VIEW_TYPE_CATEGORY)
		{
			View view = inflater.inflate(R.layout.drawer_item, parent, false);
			return new CategoryViewHolder(view, mListener, mSingleSelector, mImageLoader, mDisplayImageOptions, mImageLoadingListener);
		}
		else
		{
			throw new RuntimeException("There is no view type that matches the type " + viewType);
		}
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		if(viewHolder instanceof HeaderViewHolder)
		{
			// bind data
			((HeaderViewHolder) viewHolder).bindData();
		}
		else if(viewHolder instanceof CategoryViewHolder)
		{
			// entity
			CategoryModel category = mCategoryList.get(getCategoryPosition(position));

			// bind data
			if(category != null)
			{
				((CategoryViewHolder) viewHolder).bindData(category);
			}
		}
	}


	@Override
	public int getItemCount()
	{
		int size = getHeaderCount();
		size += getCategoryCount();
		return size;
	}


	@Override
	public int getItemViewType(int position)
	{
		int headers = getHeaderCount();
		int categories = getCategoryCount();

		if(position < headers) return VIEW_TYPE_HEADER;
		else if(position < headers+categories) return VIEW_TYPE_CATEGORY;
		else return -1;
	}


	public int getHeaderCount()
	{
		return 1;
	}


	public int getCategoryCount()
	{
		if(mCategoryList!=null) return mCategoryList.size();
		return 0;
	}


	public int getHeaderPosition(int recyclerPosition)
	{
		return recyclerPosition;
	}


	public int getCategoryPosition(int recyclerPosition)
	{
		return recyclerPosition - getHeaderCount();
	}


	public int getRecyclerPositionByHeader(int headerPosition)
	{
		return headerPosition;
	}


	public int getRecyclerPositionByCategory(int categoryPosition)
	{
		return categoryPosition + getHeaderCount();
	}


	public void refill(List<CategoryModel> categoryList, CategoryViewHolder.OnItemClickListener listener)
	{
		mCategoryList = categoryList;
		mListener = listener;
		notifyDataSetChanged();
	}


	public void stop()
	{
	}


	public void setSelected(int position)
	{
		mSingleSelector.setSelected(position, 0, true);
	}


	private void setupImageLoader()
	{
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(android.R.color.transparent)
				.showImageOnFail(android.R.color.transparent)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
		mImageLoadingListener = new AnimateImageLoadingListener();
	}


	public static final class HeaderViewHolder extends RecyclerView.ViewHolder
	{
		public HeaderViewHolder(View itemView)
		{
			super(itemView);
		}


		public void bindData()
		{
			// do nothing
		}
	}


	public static final class CategoryViewHolder extends SwappingHolder implements View.OnClickListener
	{
		private TextView mNameTextView;
		private TextView mCountTextView;
		private ImageView mIconImageView;
		private OnItemClickListener mListener;
		private SingleSelector mSingleSelector;
		private ImageLoader mImageLoader;
		private DisplayImageOptions mDisplayImageOptions;
		private ImageLoadingListener mImageLoadingListener;


		public interface OnItemClickListener
		{
			void onItemClick(View view, int position, long id, int viewType);
		}


		public CategoryViewHolder(View itemView, OnItemClickListener listener, SingleSelector singleSelector, ImageLoader imageLoader, DisplayImageOptions displayImageOptions, ImageLoadingListener imageLoadingListener)
		{
			super(itemView, singleSelector);
			mListener = listener;
			mSingleSelector = singleSelector;
			mImageLoader = imageLoader;
			mDisplayImageOptions = displayImageOptions;
			mImageLoadingListener = imageLoadingListener;

			// set selection background
			setSelectionModeBackgroundDrawable(ContextCompat.getDrawable(CityGuideApplication.getContext(), R.drawable.selector_selectable_item_bg));
			setSelectionModeStateListAnimator(null);

			// set listener
			itemView.setOnClickListener(this);

			// find views
			mNameTextView = (TextView) itemView.findViewById(R.id.drawer_item_name);
			mCountTextView = (TextView) itemView.findViewById(R.id.drawer_item_count);
			mIconImageView = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
		}


		@Override
		public void onClick(View view)
		{
			int position = getAdapterPosition();
			if(position != RecyclerView.NO_POSITION)
			{
				mListener.onItemClick(view, position, getItemId(), getItemViewType());
			}
		}


		public void bindData(CategoryModel category)
		{
			mNameTextView.setText(category.getName());
			mCountTextView.setVisibility(View.GONE); // not implemented
			mImageLoader.displayImage(category.getImage(), mIconImageView, mDisplayImageOptions, mImageLoadingListener);
		}
	}
}
