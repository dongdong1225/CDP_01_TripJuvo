package com.knucapstone.tripjuvo.fragment;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by aassw on 2016-05-17.
 */
public class preferenceToServer {

    public void insert(long p_Id) {
        String p_id = Long.toString(p_Id);
        SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.knucapstone.tripjuvo/databases/tripjuvo.db",null,0);
        String sql = "SELECT * from logins where checks = 1;";
        Cursor c = db.rawQuery(sql,null);
        c.moveToFirst();
        String u_id = c.getString(2);
        String age = Integer.toString(c.getInt(3));
        insertToDatabase(u_id, age, p_id);
    }

    private void insertToDatabase(String u_id, String age, String p_id) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //    loading = ProgressDialog.show(SignupActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
            //    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String u_id = (String) params[0];
                    String age = (String) params[1];
                    String poi_id = (String) params[2];

                    Log.i("uid,age,poi",u_id+age+poi_id);
                    String link = "http://tripjuvo.ivyro.net/preference.php";
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");
                    data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8");
                    data += "&" + URLEncoder.encode("poi_id", "UTF-8") + "=" + URLEncoder.encode(poi_id, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(u_id, age, p_id);
    }
}
