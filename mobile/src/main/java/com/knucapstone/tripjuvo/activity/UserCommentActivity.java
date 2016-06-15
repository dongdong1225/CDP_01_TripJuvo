package com.knucapstone.tripjuvo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.adapter.UserCommentAdapter;
import com.knucapstone.tripjuvo.font.RobotoTextView;
import com.knucapstone.tripjuvo.model.UserCommentsDummyModel;
import com.knucapstone.tripjuvo.util.ImageUtil;
import com.knucapstone.tripjuvo.view.pzv.PullToZoomListViewEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class UserCommentActivity extends AppCompatActivity {

	public static final String TAG = "Parallax social";
	private long poi_id;
	private String poi_name;
	private String poi_address;
	private String poi_picture;

	phpDown task;
	ImageView iv;
	ArrayList<UserCommentsDummyModel> list = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_comment);
		Intent intent = getIntent();
		poi_id = intent.getLongExtra("poi_id", 2);
		poi_name = intent.getStringExtra("poi_name");
		poi_address = intent.getStringExtra("poi_address");
		poi_picture = intent.getStringExtra("poi_picture");

		iv = (ImageView) findViewById(R.id.header_parallax_social_new_image);
		ImageUtil.displayImage(iv, poi_picture, null);

		RobotoTextView cityName = (RobotoTextView) findViewById(R.id.cityName);
		RobotoTextView cityAddress = (RobotoTextView) findViewById(R.id.cityAddress);
		cityName.setText(poi_name);
		cityAddress.setText(poi_address);

		if(poi_id == 4) {
			task = new phpDown();
			task.execute(Long.toString(poi_id));
		}
		else
		{
			PullToZoomListViewEx listView = (PullToZoomListViewEx) findViewById(R.id.paralax_social_list_view);
			listView.setShowDividers(0);

			String comment = "너무너무 재밌고 좋은 곳이에요 재밌었습니다 개굿 강추 합니다";
			list.add(new UserCommentsDummyModel(0, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "정영민", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(1, "http://pengaja.com/uiapptemplate/newphotos/listviews/googlecards/travel/1.jpg", "신기정", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(2, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "이동희", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(3, "https://scontent.cdninstagram.com/t51.2885-15/s480x480/e35/13402167_234073196976638_1286476873_n.jpg", "세아", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(4, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "백기정", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(5, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "권성현", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(6, "https://scontent.cdninstagram.com/t51.2885-15/e35/13298041_1759290414342226_1792720584_n.jpg", "조형렬", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));
			list.add(new UserCommentsDummyModel(7, "https://scontent.cdninstagram.com/t51.2885-15/e35/13285423_224246607961310_1489135086_n.jpg", "김아무개", R.string.fontello_heart_empty,"2016-06-14 07:44:55","https://scontent.cdninstagram.com/t51.2885-15/e35/12822532_1689989874577293_712071163_n.jpg",comment));

			listView.setAdapter(new UserCommentAdapter(this, list, false));

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle("User Comments");
		}
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(UserCommentActivity.this, "Photo of user",
						Toast.LENGTH_SHORT).show();
			}
		});


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class phpDown extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... poi_id) {
			StringBuilder jsonHtml = new StringBuilder();
			try {
				// 연결 url 설정String minorid = (String) params[0];

				String link = "http://tripjuvo.ivyro.net/searchCommentFromPoi.php";
				String data = URLEncoder.encode("poi_id", "UTF-8") + "=" + URLEncoder.encode(poi_id[0], "UTF-8");
				URL url = new URL(link);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// 커넥션 객체 생성
				// 연결되었으면.
				if (conn != null) {
					conn.setConnectTimeout(10000);
					// 연결되었음 코드가 리턴되면.
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						for (; ; ) {
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
							String line = br.readLine();
							if (line == null) break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}
					conn.disconnect();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jsonHtml.toString();
		}

		protected void onPostExecute(String str) {
			Log.i("PHPTEST","onPost1");
			try {
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"
				Log.i("PHPTEST",Integer.toString(ja.length()));
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);

					String user_id = jo.getString("user_id");
					String contant = jo.getString("contant");
					String time = jo.getString("time");
					String profile = jo.getString("profile");
					String picture = jo.getString("picture");
					Log.i("FUCK",profile);

					list.add(new UserCommentsDummyModel(i, picture, user_id, R.string.fontello_heart_empty, time, profile, contant));

				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			inputList();
		}
	}
	void inputList()
	{
		PullToZoomListViewEx listView = (PullToZoomListViewEx) findViewById(R.id.paralax_social_list_view);
		listView.setShowDividers(0);

		listView.setAdapter(new UserCommentAdapter(this, list, false));

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("User Comments");
	}
}
