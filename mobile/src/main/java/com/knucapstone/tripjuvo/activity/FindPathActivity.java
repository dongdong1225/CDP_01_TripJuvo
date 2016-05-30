package com.knucapstone.tripjuvo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.knucapstone.tripjuvo.R;
import com.knucapstone.tripjuvo.TSP.AdjMatrix;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FindPathActivity extends AppCompatActivity {

    private CharSequence mTitle;
    private phpDown task; //웹서버의 php를 실행하기 위한 인스턴스
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView listview;
    private ArrayList<Location> cityArrayList;
    private AdjMatrix adjMatrix;
    private ArrayList<Location> sortedList;
    private ArrayList<Integer> checkedItemsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        //서버로 region 테이블 내용 읽어오기
        task = new phpDown();
        task.execute("http://tripjuvo.ivyro.net/selectAllFromRegion.php");//도메인을 실행하게되면 php가 실행되서 데이터전달이 일어난다.

        //listView 생성
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, cityList);
        listview = (ListView) findViewById(R.id.listView) ;

        //floating action button 누르면 다음 activity로 이동
        final FloatingActionButton floatingActionButton1 = (FloatingActionButton)findViewById(R.id.fab_findPath);
        floatingActionButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SparseBooleanArray checkedItemsBooleanArray = listview.getCheckedItemPositions();
                //ArrayList<Integer> checkedItemsIndexArrayList = new ArrayList<Integer>();
                checkedItemsArrayList = new ArrayList<Integer>();

                int count = adapter.getCount();
                for(int i = 0 ; i<count ; i++)
                {
                    if(checkedItemsBooleanArray.get(i))
                        checkedItemsArrayList.add(i);
                    //checkedItemsIndexArrayList.add(i);
                }

                doTSP();

                ArrayList<String> tspList = new ArrayList<String>();
                for(int i=0 ; i<sortedList.size() ; i++)
                {
                    tspList.add(sortedList.get(i).getProvider());
                }


                ComponentName compName = new ComponentName("com.csform.android.uiapptemplate","com.csform.android.uiapptemplate.MainActivity");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(compName);
                intent=new Intent("com.csform.android.uiapptemplate.DragAndDropTravelActivity");
                intent.putExtra("CHECKED_ITEMS", tspList);
                startActivity(intent);
            }
        });


        setupActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_path, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private void setupActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }

    private class phpDown extends AsyncTask<String, Integer,String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str)
        {
            String cityName;
            String cityLatitude;
            String cityLongitude;
            Location city;
            cityArrayList = new ArrayList<>();

            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"

                for(int i=0;i<ja.length();i++){
                    JSONObject jo = ja.getJSONObject(i);
                    cityName = jo.getString("city");
                    cityLatitude = jo.getString("latitude");
                    cityLongitude = jo.getString("longitude");

                    city = new Location(cityName);
                    city.setLatitude(Double.parseDouble(cityLatitude));
                    city.setLongitude(Double.parseDouble(cityLongitude));
                    cityArrayList.add(city);
                    cityList.add(cityName);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
            listview.setAdapter(adapter);
        }
    }//end of private class

    private void doTSP()
    {
        ArrayList<Location> tspList = new ArrayList<>();

        for(int i=0 ; i<checkedItemsArrayList.size() ; i++)
        {
            tspList.add(cityArrayList.get(checkedItemsArrayList.get(i)));
        }

        adjMatrix = new AdjMatrix(tspList);
        sortedList = adjMatrix.makeAdjMatrix();

//        String sss = "";
//        for(int i=0 ; i<sortedList.size() ; i++)
//        {
//            sss+=sortedList.get(i).getProvider() + "\n";
//        }
//
//        TextView textView = (TextView)findViewById(R.id.temp);
//        textView.setText(sss);
    }

}