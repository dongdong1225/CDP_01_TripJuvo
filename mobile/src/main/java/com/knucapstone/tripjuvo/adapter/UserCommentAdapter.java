package com.knucapstone.tripjuvo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.model.UserCommentsDummyModel;
import com.knucapstone.tripjuvo.util.ImageUtil;
import com.nhaarman.listviewanimations.util.Swappable;

import java.util.ArrayList;
import java.util.Collections;

public class UserCommentAdapter extends BaseAdapter implements Swappable,
		OnClickListener {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<UserCommentsDummyModel> mDummyModelList;

	public UserCommentAdapter(Context context, ArrayList<UserCommentsDummyModel> dummyModelList, boolean shouldShowDragAndDropIcon) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDummyModelList = dummyModelList;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return mDummyModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDummyModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDummyModelList.get(position).getId();
	}

	@Override
	public void swapItems(int positionOne, int positionTwo) {
		Collections.swap(mDummyModelList, positionOne, positionTwo);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_user_coment, parent, false);
			holder = new ViewHolder();

			holder.layout = (LinearLayout) convertView.findViewById(R.id.layout_header_of_item);
			holder.friends = (LinearLayout) convertView.findViewById(R.id.friends);
			holder.photo = (ImageView) convertView.findViewById(R.id.lvis_photo);
			holder.image = (ImageView) convertView.findViewById(R.id.lvis_image);
			holder.name = (TextView) convertView.findViewById(R.id.lvis_name);
			holder.hours = (TextView) convertView.findViewById(R.id.lvis_hours);
			holder.like = (TextView) convertView.findViewById(R.id.lvis_like);
			holder.comment = (TextView) convertView.findViewById(R.id.lvis_comment);
			holder.share = (TextView) convertView.findViewById(R.id.lvis_share);
			holder.Usercomments = (TextView) convertView.findViewById(R.id.lvis_comments);
			holder.image.setOnClickListener(this);
			holder.like.setOnClickListener(this);
			holder.comment.setOnClickListener(this);
			holder.share.setOnClickListener(this);
			holder.friends.setOnClickListener(this);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position != 0)
			holder.layout.setVisibility(View.GONE);
		else
			holder.layout.setVisibility(View.VISIBLE);

		UserCommentsDummyModel dm = mDummyModelList.get(position);

		//"http://pengaja.com/uiapptemplate/newphotos/profileimages/2.jpg"
		ImageUtil.displayRoundImage(holder.photo, dm.getmProfileImageURL(), null);
		ImageUtil.displayImage(holder.image, dm.getImageURL(), null);
		holder.hours.setText(dm.getmTime());
		holder.name.setText(dm.getText());
		holder.image.setTag(position);
		holder.like.setTag(position);
		holder.comment.setTag(position);
		holder.share.setTag(position);
		holder.friends.setTag(position);
		holder.Usercomments.setText(dm.getmUserComment());
		return convertView;
	}

	private static class ViewHolder {
		public LinearLayout layout;
		public LinearLayout friends;
		public ImageView photo;
		public ImageView image;
		public TextView name;
		public TextView hours;
		public TextView like;
		public TextView comment;
		public TextView share;
		public TextView Usercomments;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();
		switch (v.getId()) {
		case R.id.lvis_image:
			Toast.makeText(mContext, "Image: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.lvis_like:
			Toast.makeText(mContext, "Like: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.lvis_comment:
			Toast.makeText(mContext, "Comment: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.lvis_share:
			Toast.makeText(mContext, "Share: " + position, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.friends:
			Toast.makeText(mContext, "Friends", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
