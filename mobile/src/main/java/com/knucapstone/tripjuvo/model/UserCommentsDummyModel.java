package com.knucapstone.tripjuvo.model;

public class UserCommentsDummyModel {

	private long mId;
	private String mImageURL;
	private String mText;
	private int mIconRes;
	private String mTime;
	private String mProfileImageURL;
	private String mUserComment;





	public UserCommentsDummyModel() {
	}

	public UserCommentsDummyModel(long id, String imageURL, String text, int iconRes, String time, String ProfileImageURL,String Comments) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mIconRes = iconRes;
		mTime = time;
		mProfileImageURL = ProfileImageURL;
		mUserComment = Comments;

	}

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
	public String getmProfileImageURL() {return mProfileImageURL;}

	public void setmProfileImageURL(String mProfileImageURL) {this.mProfileImageURL = mProfileImageURL;}

	public String getmTime() {return mTime;}

	public void setmTime(String mTime) {this.mTime = mTime;}

	public String getmUserComment() {return mUserComment;}

	public void setmUserComment(String mUserComment) {this.mUserComment = mUserComment;}

	@Override
	public String toString() {
		return mText;
	}
}
