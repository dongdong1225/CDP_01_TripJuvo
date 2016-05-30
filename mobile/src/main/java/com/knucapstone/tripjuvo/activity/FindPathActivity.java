package com.knucapstone.tripjuvo.activity;

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
import com.knucapstone.tripjuvo.TSP.Spot;
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
    private ArrayList<String> cityNameList;
    private ArrayAdapter adapter;
    private ListView listview;
    private ArrayList<Spot> spotArrayList;
    private AdjMatrix adjMatrix;
    //private ArrayList<Spot> sortedList;
    //private ArrayList<Integer> checkedItemsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_path);

        //서버로 region 테이블 내용 읽어오기
        task = new phpDown();
        task.execute("http://tripjuvo.ivyro.net/selectAllFromRegion.php");//도메인을 실행하게되면 php가 실행되서 데이터전달이 일어난다.

        //listView 생성
        cityNameList = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, cityNameList)
        {
        };
        //adapter = new ArrayAdapter(this,R.layout.list_item_donghee ,cityNameList);
        listview = (ListView) findViewById(R.id.listView) ;

        //floating action button 누르면 다음 activity로 이동
        final FloatingActionButton floatingActionButton1 = (FloatingActionButton)findViewById(R.id.fab_findPath);
        floatingActionButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SparseBooleanArray checkedItemsBooleanArray = listview.getCheckedItemPositions();
                ArrayList<Integer> checkedItemsIndexArrayList = new ArrayList<>();

                if(checkedItemsBooleanArray.size() == 0)
                    return;

                //int count = adapter.getCount();
                //체크한 항목이 몇번 항목인지를 checkedItemsIndexArrayList에 저장
                for(int i = 0 ; i<adapter.getCount() ; i++)
                {
                    if(checkedItemsBooleanArray.get(i))
                        checkedItemsIndexArrayList.add(i);
                    //checkedItemsIndexArrayList.add(i);
                }

                //체크된 항목의 Spot 클래스 정보를 checkedSpotList에 저장
                ArrayList<Spot> checkedSpotList = new ArrayList<>();
                for(int i=0 ; i<checkedItemsIndexArrayList.size() ; i++)
                {
                    checkedSpotList.add(spotArrayList.get(checkedItemsIndexArrayList.get(i)));
                }

                ArrayList<Spot> sortedList = doTSP(checkedSpotList);

                ArrayList<Location> locationArrayList = new ArrayList<>();
                ArrayList<String> pictureArrayList = new ArrayList<>();
                for(int i=0 ; i<sortedList.size() ; i++)
                {
                    Location temp = new Location(sortedList.get(i).getProvider());
                    temp.setLatitude(sortedList.get(i).getLatitude());
                    temp.setLongitude(sortedList.get(i).getLongitude());

                    locationArrayList.add(temp);
                    pictureArrayList.add(sortedList.get(i).getPicture());
                }

                Intent intent = new Intent(FindPathActivity.this, DragAndDropTravelActivity.class);
                //Intent intent = new Intent(FindPathActivity.this, ExpandableListViewActivity.class);
                intent.putExtra("locationArrayList", locationArrayList);
                intent.putExtra("pictureArrayList", pictureArrayList);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // action bar menu behavior
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

            spotArrayList = new ArrayList<>();
            Spot spot;

            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"

                for(int i=0;i<ja.length();i++){
                    JSONObject jo = ja.getJSONObject(i);
                    cityName = jo.getString("city");
                    spot = new Spot(cityName);
                    spot.setLatitude(Double.parseDouble(jo.getString("latitude")));
                    spot.setLongitude(Double.parseDouble(jo.getString("longitude")));
                    spot.setPicture(jo.getString("picture"));

                    spotArrayList.add(spot);
                    cityNameList.add(cityName);
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            listview.setAdapter(adapter);
        }
    }//end of private class

    private ArrayList<Spot> doTSP(ArrayList<Spot> checkedSpotList)
    {
        adjMatrix = new AdjMatrix(checkedSpotList);
        return adjMatrix.makeAdjMatrix();

    }

}