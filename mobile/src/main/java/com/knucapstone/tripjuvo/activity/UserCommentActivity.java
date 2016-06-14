package com.knucapstone.tripjuvo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.adapter.UserCommentAdapter;
import com.knucapstone.tripjuvo.model.UserCommentsDummyModel;
import com.knucapstone.tripjuvo.view.pzv.PullToZoomListViewEx;

import java.util.ArrayList;

public class UserCommentActivity extends AppCompatActivity {

	public static final String TAG = "Parallax social";

	ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_comment);

		iv = (ImageView) findViewById(R.id.header_parallax_social_new_image);

		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(UserCommentActivity.this, "Photo of user",
						Toast.LENGTH_SHORT).show();
			}
		});

		PullToZoomListViewEx listView = (PullToZoomListViewEx) findViewById(R.id.paralax_social_list_view);
		listView.setShowDividers(0);
		ArrayList<UserCommentsDummyModel> list = new ArrayList<>();
		String comment = "너무너무 재밌고 좋은 곳이에요 재밌었습니다 개굿 강추 합니다";
		list.add(new UserCommentsDummyModel(0, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "정영민", R.string.fontello_heart_empty,1,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(1, "http://pengaja.com/uiapptemplate/newphotos/listviews/googlecards/travel/1.jpg", "신기정", R.string.fontello_heart_empty,1,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(2, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "이동희", R.string.fontello_heart_empty,2,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(3, "https://scontent.cdninstagram.com/t51.2885-15/s480x480/e35/13402167_234073196976638_1286476873_n.jpg", "세아", R.string.fontello_heart_empty,2,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(4, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "백기정", R.string.fontello_heart_empty,2,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(5, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "권성현", R.string.fontello_heart_empty,3,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(6, "https://scontent.cdninstagram.com/t51.2885-15/e35/13298041_1759290414342226_1792720584_n.jpg", "조형렬", R.string.fontello_heart_empty,4,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
		list.add(new UserCommentsDummyModel(7, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "김아무개", R.string.fontello_heart_empty,5,"https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));

		listView.setAdapter(new UserCommentAdapter(this, list, false));

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("User Comments");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
