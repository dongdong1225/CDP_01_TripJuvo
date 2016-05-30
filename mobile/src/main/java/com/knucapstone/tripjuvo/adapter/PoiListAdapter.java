package com.knucapstone.tripjuvo.adapter;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.database.model.PoiModel;
import com.knucapstone.tripjuvo.listener.AnimateImageLoadingListener;
import com.knucapstone.tripjuvo.utility.LocationUtility;

import java.util.List;


public class PoiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private static final int VIEW_TYPE_POI = 1;
	private static final int VIEW_TYPE_FOOTER = 2;

	private List<PoiModel> mPoiList;
	private List<Object> mFooterList;
	private PoiViewHolder.OnItemClickListener mListener;
	private int mGridSpanCount;
	private boolean mAnimationEnabled = true;
	private int mAnimationPosition = -1;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoadingListener mImageLoadingListener;


	public PoiListAdapter(List<PoiModel> poiList, List<Object> footerList, PoiViewHolder.OnItemClickListener listener, int gridSpanCount)
	{
		mPoiList = poiList;
		mFooterList = footerList;
		mListener = listener;
		mGridSpanCount = gridSpanCount;
		setupImageLoader();
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		// inflate view and create view holder
		if(viewType== VIEW_TYPE_POI)
		{
			View view = inflater.inflate(R.layout.fragment_poi_list_item, parent, false);
			return new PoiViewHolder(view, mListener, mImageLoader, mDisplayImageOptions, mImageLoadingListener);
		}
		else if(viewType==VIEW_TYPE_FOOTER)
		{
			View view = inflater.inflate(R.layout.fragment_poi_list_footer, parent, false);
			return new FooterViewHolder(view);
		}
		else
		{
			throw new RuntimeException("There is no view type that matches the type " + viewType);
		}
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		// bind data
		if(viewHolder instanceof PoiViewHolder)
		{
			// entity
			PoiModel poi = mPoiList.get(getPoiPosition(position));

			// bind data
			if(poi != null)
			{
				((PoiViewHolder) viewHolder).bindData(poi);
			}
		}
		else if(viewHolder instanceof FooterViewHolder)
		{
			// entity
			Object object = mFooterList.get(getFooterPosition(position));

			// bind data
			if(object != null)
			{
				((FooterViewHolder) viewHolder).bindData(object);
			}
		}

		// set item margins
		setItemMargins(viewHolder.itemView, position);

		// set animation
		setAnimation(viewHolder.itemView, position);
	}


	@Override
	public int getItemCount()
	{
		int size = 0;
		if(mPoiList !=null) size += mPoiList.size();
		if(mFooterList!=null) size += mFooterList.size();
		return size;
	}


	@Override
	public int getItemViewType(int position)
	{
		int pois = mPoiList.size();
		int footers = mFooterList.size();

		if(position < pois) return VIEW_TYPE_POI;
		else if(position < pois+footers) return VIEW_TYPE_FOOTER;
		else return -1;
	}


	public int getPoiCount()
	{
		if(mPoiList !=null) return mPoiList.size();
		return 0;
	}


	public int getFooterCount()
	{
		if(mFooterList!=null) return mFooterList.size();
		return 0;
	}


	public int getPoiPosition(int recyclerPosition)
	{
		return recyclerPosition;
	}


	public int getFooterPosition(int recyclerPosition)
	{
		return recyclerPosition - getPoiCount();
	}


	public int getRecyclerPositionByPoi(int poiPosition)
	{
		return poiPosition;
	}


	public int getRecyclerPositionByFooter(int footerPosition)
	{
		return footerPosition + getPoiCount();
	}


	public void refill(List<PoiModel> poiList, List<Object> footerList, PoiViewHolder.OnItemClickListener listener, int gridSpanCount)
	{
		mPoiList = poiList;
		mFooterList = footerList;
		mListener = listener;
		mGridSpanCount = gridSpanCount;
		notifyDataSetChanged();
	}


	public void stop()
	{
	}


	public void setAnimationEnabled(boolean animationEnabled)
	{
		mAnimationEnabled = animationEnabled;
	}


	private void setupImageLoader()
	{
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(R.drawable.placeholder_photo)
				.showImageOnFail(R.drawable.placeholder_photo)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
		mImageLoadingListener = new AnimateImageLoadingListener();
	}


	private void setAnimation(final View view, int position)
	{
		if(mAnimationEnabled && position>mAnimationPosition)
		{
			view.setScaleX(0F);
			view.setScaleY(0F);
			view.animate()
					.scaleX(1F)
					.scaleY(1F)
					.setDuration(300)
					.setInterpolator(new DecelerateInterpolator());

			mAnimationPosition = position;
		}
	}


	private void setItemMargins(View view, int position)
	{
		int marginTop = 0;

		if(position<mGridSpanCount)
		{
			TypedArray a = CityGuideApplication.getContext().obtainStyledAttributes(null, new int[]{android.R.attr.actionBarSize}, 0, 0);
			marginTop = (int) a.getDimension(0, 0);
			a.recycle();
		}

		ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		marginParams.setMargins(0, marginTop, 0, 0);
	}


	public static final class PoiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private TextView mNameTextView;
		private TextView mDistanceTextView;
		private ImageView mImageView;
		private OnItemClickListener mListener;
		private ImageLoader mImageLoader;
		private DisplayImageOptions mDisplayImageOptions;
		private ImageLoadingListener mImageLoadingListener;


		public interface OnItemClickListener
		{
			void onItemClick(View view, int position, long id, int viewType);
		}


		public PoiViewHolder(View itemView, OnItemClickListener listener, ImageLoader imageLoader, DisplayImageOptions displayImageOptions, ImageLoadingListener imageLoadingListener)
		{
			super(itemView);
			mListener = listener;
			mImageLoader = imageLoader;
			mDisplayImageOptions = displayImageOptions;
			mImageLoadingListener = imageLoadingListener;

			// set listener
			itemView.setOnClickListener(this);

			// find views
			mNameTextView = (TextView) itemView.findViewById(R.id.fragment_poi_list_item_name);
			mDistanceTextView = (TextView) itemView.findViewById(R.id.fragment_poi_list_item_distance);
			mImageView = (ImageView) itemView.findViewById(R.id.fragment_poi_list_item_image);
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


		public void bindData(PoiModel poi)
		{
			mNameTextView.setText(poi.getName());
			mImageLoader.displayImage(poi.getImage(), mImageView, mDisplayImageOptions, mImageLoadingListener);

			if(poi.getDistance()>0)
			{
				String distance = LocationUtility.getDistanceString(poi.getDistance(), LocationUtility.isMetricSystem());
				mDistanceTextView.setText(distance);
				mDistanceTextView.setVisibility(View.VISIBLE);
			}
			else
			{
				mDistanceTextView.setVisibility(View.GONE);
			}
		}
	}


	public static final class FooterViewHolder extends RecyclerView.ViewHolder
	{
		public FooterViewHolder(View itemView)
		{
			super(itemView);
		}


		public void bindData(Object object)
		{
			// do nothing
		}
	}
}
