package com.knucapstone.tripjuvo.hotelAPI;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.knucapstone.tripjuvo.CityGuideApplication;
import com.knucapstone.tripjuvo.CityGuideConfig;
import com.knucapstone.tripjuvo.database.data.Data;
import com.knucapstone.tripjuvo.database.model.PoiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HotelDataJsonParser{

    ImageView imView;
    TextView txtView;
    public int flag = 0;

    private static final String DATABASE_NAME = CityGuideConfig.DATABASE_NAME;
    private static final String DATABASE_PATH = "/data/data/" + CityGuideApplication.getContext().getPackageName() + "/databases/";
    private static final int DATABASE_VERSION = CityGuideConfig.DATABASE_VERSION;

    public phpDown task; //웹서버의 php를 실행하기 위한 인스턴스
    ArrayList<ListItem> listitem = new ArrayList<ListItem>();
    Data<List<PoiModel>> DataListPoiModel = new Data<>();

    public List<PoiModel> getPoiDateaList() {
        try
        {//도메인을 실행하게되면 php가 실행되서 데이터전달이 일어난다.
            synchronized (task)
            {
                task.get();
            }
            Log.i("getPoiDateaList", "Success");
            return poiDateaList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.i("getPoiDateaList","Err");
        return null;
    }
    public void waitForReady()
    {
        try
        {
            synchronized(this)
            {
                task.wait();
                task.get();
            }
             //도메인을 실행하게되면 php가 실행되서 데이터전달이 일어난다.
            Log.i("getPoiDateaList", "Ready");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setPoiDateaList(List<PoiModel> poiDateaList) {
        this.poiDateaList = poiDateaList;
    }

    public List<PoiModel> poiDateaList= new ArrayList<PoiModel>();;
    ListItem Item;

    public void start() {
        task = new phpDown();
        task.execute("http://partners.api.skyscanner.net/apiservices/hotels/liveprices/v2/UK/EUR/en-GB/35.837804,128.5542113-latlong/2016-06-22/2016-06-27/2/1?apiKey=kn611914167123204522255254589261");
        try
        {
            task.wait(); //도메인을 실행하게되면 php가 실행되서 데이터전달이 일어난다.
        }
        catch (Exception e)
        {

        }
    }

    public class phpDown extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... urls) {
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
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
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

        /*
        protected void onPostExecute(String str){
            txtView.setText(str);
        }
        */

        public void onPostExecute(String str){
            HotelData data = new HotelData();
            JSONObject jb = new JSONObject();
            JSONObject jo = new JSONObject();



            try{
                JSONObject root = new JSONObject(str);
                JSONArray imageUrl;
                JSONArray ja = root.getJSONArray("hotels"); //get the JSONArray which I made in the php file. the name of JSONArray is "results"

                listitem.add(new ListItem(ja.getString(0)));

                SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
                int cnt = ja.length();
                String update_sql = "UPDATE sqlite_sequence set seq = "+Integer.toString(cnt)+" where name = 'pois';";
                db.execSQL(update_sql);


                for(int i=0;i<ja.length();i++){

                    jo = ja.getJSONObject(i);
                    Log.i("DataSetting", jo.getString("name"));
                    PoiModel poimodel = new PoiModel();
                    poimodel.setName(jo.getString("name"));
                    poimodel.setLatitude(Double.parseDouble(jo.getString("latitude")));
                    poimodel.setLongitude(Double.parseDouble(jo.getString("longitude")));
                    data.imageUrl = jo.getString("images");
                    String convertedImageURL="";
                    try
                    {
                        Log.i("DataSetting", "http://d16eaqi26omzol.cloudfront.net/available/" + data.imageUrl.substring(4,12) + "/rmt.jpg");
                        convertedImageURL = "http://d16eaqi26omzol.cloudfront.net/available/" + data.imageUrl.substring(4,12) + "/rmt.jpg";
                        poimodel.setImage("http://d16eaqi26omzol.cloudfront.net/available/" + data.imageUrl.substring(4,12) + "/rmt.jpg");
                    }
                   catch (Exception e)
                   {
                       poimodel.setImage("");
                       convertedImageURL = "";
                       Log.i("DataSetting", "Image-Err");
                   }
                    poiDateaList.add(poimodel);
                    data.hotel_name = jo.getString("name");
                    data.hotel_id = jo.getString("hotel_id");
                    data.latitude = jo.getString("latitude");
                    data.longitute = jo.getString("longitude");

                    String sql = "INSERT INTO pois (id, category_id, name, intro, description, image, link, " +
                            "latitude, longitude, address, phone, email, favorite, city) values (" + data.hotel_id + "," + 2 +
                            ",'" + data.hotel_name + "','" + "intro" + "','" + "Description" + "','" + convertedImageURL
                            + "','" + "http://goo.gl/VBaUeq" + "'," + data.latitude + "," +
                            data.longitute + ",'" + "address " + "','" + "phone" + "','" + "email" + "', 0 ,'" + "Daegu" + "');";
                    try {
                        db.execSQL(sql);
                    }
                    catch (Exception e)
                    {
                        Log.i("DB","execSQL ERROR");
                    }
                    //imageUrl = jo.getJSONArray("images");
                }
                Log.i("poiDateaListSIZE", poiDateaList.size()+"");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            String result = "";

            result = jo.toString() + "\n";
            result += "name : " + data.hotel_name + "\n";
            result += "id : " + data.hotel_id + "\n";
            result += "lat,long : " + data.latitude +" "+data.longitute + "\n";
            //result += "imageUrl : " + data.imageUrl.substring(4,12) + "\n";
            result += "imageURL seperate";
        }
    }

    public Data<List<PoiModel>> readByCategory()
    {

        DataListPoiModel.setDataObject(poiDateaList);
//            public static final String COLUMN_ID = "id";
//            public static final String COLUMN_CATEGORY_ID = "category_id";
//            public static final String COLUMN_NAME = "name";
//            public static final String COLUMN_INTRO = "intro";
//            public static final String COLUMN_DESCRIPTION = "description";
//            public static final String COLUMN_IMAGE = "image";
//            public static final String COLUMN_LINK = "link";
//            public static final String COLUMN_LATITUDE = "latitude";
//            public static final String COLUMN_LONGITUDE = "longitude";
//            public static final String COLUMN_ADDRESS = "address";
//            public static final String COLUMN_PHONE = "phone";
//            public static final String COLUMN_EMAIL = "email";
//            public static final String COLUMN_FAVORITE = "favorite";
        return DataListPoiModel;
    }
}
