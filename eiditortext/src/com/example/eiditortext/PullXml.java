package com.example.eiditortext;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eiditortext.MainActivity.GetCSDNLogoTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class PullXml extends Activity {
    public TextView textxml = null;
    public ArrayList<City> WeatherInfo = new ArrayList<City>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showxml);
        LayoutInflater inflater = getLayoutInflater();
        textxml = (TextView) findViewById(R.id.cityweather);
        Button bt_geturl = (Button) findViewById(R.id.pullmethod);
        bt_geturl.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                GetCityWeatherTask task = new GetCityWeatherTask();  
                task.execute("http://php.weather.sina.com.cn/xml.php?city=%B1%B1%BE%A9&password=DJOYnieT8234jlsK&day=0");  
            }  
        });
        Button  bt_pull = (Button) findViewById(R.id.pullxml);
        bt_pull.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                  if(CityWeather == null){
                      
                  }
                else {
                    // 定义工厂 XmlPullParserFactory
                    XmlPullParserFactory factory;
                    try {
                        factory = XmlPullParserFactory.newInstance();
                        // 定义解析器 XmlPullParser
                        XmlPullParser parser = factory.newPullParser();
                        //获取xml的string数据
                        parser.setInput(new StringReader(CityWeather));
                        WeatherInfo = ParseXml(parser);
                        if(WeatherInfo == null){
                            Log.v("zsww","pull wrong!!");
                            return;
                        }
                        Log.v("zsww","WeatherInfo==="+WeatherInfo);
                        Log.v("zsww","WeatherInfo==="+WeatherInfo.get(0).status);
                        for(int i = 0; i < WeatherInfo.size(); i++){
                            textxml.setText(WeatherInfo.get(i).cityName+"  "+WeatherInfo.get(i).status
                                    +"  "+WeatherInfo.get(i).temperature);
                        }
                        
                    } catch (XmlPullParserException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }  
        });
    }
    
    public String CityWeather = null;
    
    class GetCityWeatherTask extends AsyncTask<String, Integer, String> {// 继承AsyncTask
        @Override
        protected String doInBackground(String... params) {// 处理后台执行的任务，在后台线程执行
            try {
                CityWeather = getStringByUrl(params[0]);
                Log.v("zswww", "return weather");
            } catch (Exception e) {
                Log.v("zswww", "return null");
                return null;
            }
            return CityWeather;
        }

        protected void onPostExecute(String result) {// 后台任务执行完之后被调用，在ui线程执行
            if (result != null) {
                Toast.makeText(PullXml.this, "成功获取天气", Toast.LENGTH_LONG).show();
                textxml.setText(CityWeather);
            } else {
                Toast.makeText(PullXml.this, "获取天气失败", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * 同样删除首行，才能解析成功，
     * @param fileName
     * @return 返回xml文件的inputStream
     */     
    public InputStream getInputStreamFromAssets(String fileName){
        try {
            InputStream inputStream = getResources().getAssets().open(fileName);
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取XML文件，xml文件放到res/xml文件夹中，若XML为本地文件，则推荐该方法
     * 
     * @param fileName
     * @return : 读取到res/xml文件夹下的xml文件，返回XmlResourceParser对象（XmlPullParser的子类）
     */
    public XmlResourceParser getXMLFromResXml(String fileName){
        XmlResourceParser xmlParser = null;
        try {
            //*/
            //  xmlParser = this.getResources().getAssets().openXmlResourceParser("assets/"+fileName);        // 失败,找不到文件
            //xmlParser = this.getResources().getXml(R.xml.provinceandcity);
            /*/
            // xml文件在res目录下 也可以用此方法返回inputStream
            InputStream inputStream = this.getResources().openRawResource(R.xml.provinceandcity);
            /*/
            return xmlParser;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return xmlParser;
    }

    /**
     * 读取url的xml资源 转成String
     * @param url
     * @return 返回 读取url的xml字符串
     */
    public String getStringByUrl(String url) {
        String outputString = "";
        // DefaultHttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();
        // HttpGet
        HttpGet httpget = new HttpGet(url);
        // ResponseHandler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            outputString = httpclient.execute(httpget, responseHandler);            
            outputString = new String(outputString.getBytes("ISO-8859-1"), "utf-8");    // 解决中文乱码
            Log.i("HttpClientConnector", "连接成功");
        } catch (Exception e) {
            Log.i("HttpClientConnector", "连接失败");
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return outputString;
    }

    /**
     * 解析SDcard xml文件
     * @param fileName
     * @return 返回xml文件的inputStream
     */     
    public InputStream getInputStreamFromSDcard(String fileName){
        try {
            // 路径根据实际项目修改
            String path = Environment.getExternalStorageDirectory().toString() + "/test_xml/";

            Log.v("", "path : " + path);

            File xmlFlie = new File(path+fileName);

            InputStream inputStream = new FileInputStream(xmlFlie);

            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static class City{
        public City(){
            temperature = "";
            status = "";
            cityName = "";
        }
        public String temperature = "";
        public String status = "";
        public String cityName = "";
        public void setTemperature(String tp){
            temperature = tp;
        }
        public void setStatus(String st){
            status = st;
        }
        public void setCityName(String name){
            cityName = name;
        }
    }
    
    public static class News{
        public News(){
            title = "";
            description = "";
        }
        public String title = "";
        public String description = "";
        public void setDescription(String tp){
            description = tp;
        }
        public void setTitle(String st){
            title = st;
        }
    }
    
    public static ArrayList<City> ParseXml(XmlPullParser parser){
        ArrayList<City> CityArray = new ArrayList<City>();
        City CityTemp = null;
        String temperature = null;
        String cityName = null;
        String status = null;
        CityTemp = new City();
        try {
            //开始解析事件
            int eventType = parser.getEventType();

            //处理事件，不碰到文档结束就一直处理
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //因为定义了一堆静态常量，所以这里可以用switch
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:

                        //给当前标签起个名字
                        String tagName = parser.getName();
                        Log.d("zsww", "====XmlPullParser.START_TAG=== tagName: " + tagName);

                        if(tagName.equals("city")){
                           Log.v("zsww","parser=="+parser.getName()+"=="+"=="+"=="+parser.getAttributeCount()+"=="+parser.getAttributeValue(tagName, tagName));
                            cityName = parser.nextText().toString();//Integer.parseInt(parser.getAttributeValue(0));
                            Log.v("zsww","cityname==="+cityName);
                        }else if(tagName.equals("status1")){
                            status = parser.nextText();
                        }else if(tagName.equals("temperature1")){
                            temperature = parser.nextText() + "度";                          
                            parser.next();
                           
                            Log.v("zsww", "status getText: "+status);
                            Log.v("zsww", "name getText: "+cityName);                            
                            Log.e("zsww", "=========================");
                            
                            CityTemp.setTemperature(temperature);
                            CityTemp.setStatus(status);
                            CityTemp.setCityName(cityName);
                            CityArray.add(CityTemp);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }

                //别忘了用next方法处理下一个事件，忘了的结果就成死循环#_#
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return CityArray;
    }
}
