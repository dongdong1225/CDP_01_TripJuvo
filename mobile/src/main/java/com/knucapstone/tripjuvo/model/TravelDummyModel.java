package com.knucapstone.tripjuvo.model;

public class TravelDummyModel {

	private long mId;
	private String mImageURL;
	private String mText;
	private String mSubText;
	private String mRating;
	private int mIconRes;

	public TravelDummyModel() {
	}


	public TravelDummyModel(long id, String imageURL, String text, int iconRes) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
	}

	public TravelDummyModel(long id, String imageURL, String text, int iconRes, String SubText, String Rating) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
		mSubText = SubText;
		mRating = Rating;
	}
	public String getRating() { return mRating; }

	public String getSubText() { return mSubText; }

	public void setSubText(String mSubText) { this.mSubText = mSubText; }

	public void setRating(String mRating) { this.mRating = mRating; }

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		mImageURL = imageURL;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public int getIconRes() {
		return mIconRes;
	}

	public void setIconRes(int iconRes) {
		mIconRes = iconRes;
	}

	@Override
	public String toString() {
		return mText;
	}
}
