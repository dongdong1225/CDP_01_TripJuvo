package com.knucapstone.tripjuvo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.util.DialogUniversalInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class DialogUniversalInfoActivity extends Activity {
	private int poi_id;
	private phpDown task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		final int a;
		Intent intent = getIntent();
		a = intent.getIntExtra("asdf", 0);
		task = new phpDown();
		task.execute(Integer.toString(a));

		DialogUniversalInfoUtils dialog = new DialogUniversalInfoUtils(this);
		dialog.showDialog();
		dialog.mDialogOKButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DialogUniversalInfoActivity.this, MainActivity.class);
				Log.i("BeaconMinor", a + "");
				intent.putExtra("BeaconPoi", poi_id);
				startActivity(intent);
				finish();
			}
		});


		dialog.mDialogCANCELButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});


		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setTitle("Dialog universal info");
	}

	@Override
	public void onAttachedToWindow() {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
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
		protected String doInBackground(String... minorid) {
			StringBuilder jsonHtml = new StringBuilder();
			try {
				// 연결 url 설정String minorid = (String) params[0];

				String link = "http://tripjuvo.ivyro.net/searchPoiFromBeacon.php";
				String data = URLEncoder.encode("minor_id", "UTF-8") + "=" + URLEncoder.encode(minorid[0], "UTF-8");
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
			try {
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"
				JSONObject jo = ja.getJSONObject(0);
				String poiid = jo.getString("poi_id");
				poi_id = Integer.parseInt(poiid);
				Log.i("COUNT", "readE");
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
}
