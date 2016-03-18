package com.example.eiditortext;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsRSS extends Activity {
    public ListView newslist = null;
    private List<Map<String, Object>> Newsdata = new ArrayList<Map<String,Object>>();
    public ArrayList<News> NewsInfo = new ArrayList<News>();
    public MyAdapter listadapter = null;
    public String url_guonei = "http://news.baidu.com/n?cmd=4&class=civilnews&tn=rss";
    public String url_guoji = "http://news.baidu.com/n?cmd=4&class=internews&tn=rss";
    public String url_hulianwang = "http://news.baidu.com/n?cmd=4&class=internet&tn=rss";
    public String url_keji = "http://news.baidu.com/n?cmd=4&class=technnews&tn=rss";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsrss);
        LayoutInflater inflater = getLayoutInflater();
        newslist = (ListView) findViewById(R.id.newslist);
        //newslist.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        Log.v("zsw_edit", "newslist ==== "+newslist.getDescendantFocusability());
        Button bt_guonei = (Button) findViewById(R.id.button1);
        Button bt_guoji = (Button) findViewById(R.id.button2);
        Button bt_hulianwang = (Button) findViewById(R.id.button3);
        Button bt_keji = (Button) findViewById(R.id.button4);
        bt_guonei.setOnClickListener(ShowNews);
        bt_guoji.setOnClickListener(ShowNews);
        bt_hulianwang.setOnClickListener(ShowNews);
        bt_keji.setOnClickListener(ShowNews);
        Button bt_load = (Button) findViewById(R.id.button5);
        bt_load.setOnClickListener(ShowNews);
    }
    
    private boolean isloadimage = false;
    public OnClickListener ShowNews = new OnClickListener() {  
        @Override  
        public void onClick(View v) {
            String url = "";
            switch(v.getId()){
                case R.id.button1 :
                    url = url_guonei;
                    break;
                case R.id.button2 :
                    url = url_guoji;
                    break;
                case R.id.button3 :
                    url = url_hulianwang;
                    break;
                case R.id.button4 :
                    url = url_keji;
                    break;
                case R.id.button5 :
                    isloadimage = true;
                    toShowImage.removeMessages(0);
                    toShowImage.sendEmptyMessage(0);
                    break;
            }
            if(!isloadimage){
                if (!NewsInfo.isEmpty()) {
                    NewsInfo.clear();
                }
                Log.v("zsww", "url===" + url);
                GetNewsTextandImageTask task = new GetNewsTextandImageTask();
                task.execute(url);
            }
            
        }  
    };
    
    private Handler toShowNews = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            
            switch(msg.what)
            {
            case 0:
                    if (NewsInfo == null) {
                        Toast.makeText(NewsRSS.this, "��ȡ����ʧ��", Toast.LENGTH_LONG).show();
                    }
                    else {
                        MyAdapter listadapter = new MyAdapter(Newsdata.size(), NewsRSS.this, NewsInfo);
                        newslist.setAdapter(listadapter);
                    }
                break;
            }
        }
    };
    
    private Handler toShowImage = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            
            switch(msg.what)
            {
            case 0:
                    if (Newsdata == null) {
                        Toast.makeText(NewsRSS.this, "��ȡ����ʧ��", Toast.LENGTH_LONG).show();
                    }
                    else {
                            newslist.setAdapter(new SimpleAdapter(NewsRSS.this, Newsdata,
                                    R.layout.listitem, new String[]{"title", "description", "newsimage"},
                                    new int[]{R.id.newstitle, R.id.newsdes, R.id.newsimage}));
                            isloadimage = false;
                    }
                break;
            }
        }
    };
    
    private ArrayList<String> News2String(ArrayList<News> news)
    {
        ArrayList<String> stringlist = new ArrayList<String>();
        for(int i = 0; i < news.size(); i++){
            stringlist.add(news.get(i).title +"\\n"+news.get(i).description);
        }
        return stringlist;
    }
    
    private List<Map<String, Object>> News2ListMap(ArrayList<News> news)
    {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < news.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", news.get(i).title);
            map.put("description", news.get(i).description);
            if (news.get(i).newsurl != "" || true) {
//                Log.v("zswww","newsurl===="+news.get(i).newsurl);
//                URL imageurl;
//                try {
//                    imageurl = new URL(news.get(i).newsurl);
//                    bm = bmpFromURL(imageurl);
//                } catch (MalformedURLException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                 GetNewsImageTask task = new GetNewsImageTask();
                 task.execute(news.get(i).newsurl);
            }
            map.put("image", bm);
            data.add(map);
        }
        return data;
    }
    
    public static Bitmap bmpFromURL(URL imageURL){
        Bitmap result = null;
        try {
            HttpURLConnection connection = (HttpURLConnection)imageURL .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            if (is == null){
                throw new RuntimeException("stream is null");
            }else{
                try {
                    byte[] data=readStream(is);
                    if(data!=null){
                        result = BitmapFactory.decodeByteArray(data, 0, data.length);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }               
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /*
     * �õ�ͼƬ�ֽ��� �����С
     * */
    public static byte[] readStream(InputStream inStream) throws Exception{      
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();      
        byte[] buffer = new byte[1024];      
        int len = 0;      
        while( (len=inStream.read(buffer)) != -1){      
            outStream.write(buffer, 0, len);      
        }      
        outStream.close();      
        inStream.close();      
        return outStream.toByteArray();      
    }
    
    public Bitmap bm;
    
    class GetNewsImageTask extends AsyncTask<String,Integer,Bitmap> {//�̳�AsyncTask  
        
        @Override  
        protected Bitmap doInBackground(String... params) {//�����ִ̨�е������ں�̨�߳�ִ��  
            publishProgress(0);//�������onProgressUpdate(Integer... progress)����  
            //final Bitmap bm;  
            try {  
                URL imageurl = new URL(params[0]);
                bm = bmpFromURL(imageurl);
                Log.v("zswww","return bm");
            } catch (Exception e) {
                Log.v("zswww","return null");
                return null;  
            }  
            publishProgress(100);  
            //mImageView.setImageBitmap(result); �����ں�̨�̲߳���ui  
            return bm;  
        }  
          
        protected void onProgressUpdate(Integer... progress) {//�ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��  
         }  
  
         protected void onPostExecute(Bitmap result) {//��̨����ִ����֮�󱻵��ã���ui�߳�ִ��   
         }  
           
         protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��  
         }  
           
         protected void onCancelled () {//��ui�߳�ִ��  
         }  
    }
    
    public String NewsText = null;
    
    class GetNewsTextandImageTask extends AsyncTask<String, Integer, ArrayList<News>> {// �̳�AsyncTask
        @Override
        protected ArrayList<News> doInBackground(String... params) {// �����ִ̨�е������ں�̨�߳�ִ��
            try {
                NewsText = getStringByUrl(params[0]);
                // ���幤�� XmlPullParserFactory
                if(NewsText != null){
                    XmlPullParserFactory factory;
                    factory = XmlPullParserFactory.newInstance();
                    // ��������� XmlPullParser
                    XmlPullParser parser = factory.newPullParser();
                    // ��ȡxml��string���
                    parser.setInput(new StringReader(NewsText));
                    NewsInfo = ParseXml(parser);
                }                
                Log.v("zswww", "return news");
            } catch (Exception e) {
                Log.v("zswww", "return null");
                return null;
            }
            Log.v("zsww","NewsText==="+NewsText);
            return NewsInfo;
        }

        protected void onPostExecute(ArrayList<News> result) {// ��̨����ִ����֮�󱻵��ã���ui�߳�ִ��
            if (result != null) {
                Toast.makeText(NewsRSS.this, "���³ɹ�", Toast.LENGTH_LONG).show();
                Log.v("zswww","result.size()=="+result.size());
                listadapter = new MyAdapter(result.size(), NewsRSS.this, NewsInfo);
                newslist.setAdapter(listadapter);
                newslist.setOnScrollListener(mScrollListener);
            } else {
                Toast.makeText(NewsRSS.this, "����ʧ��", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    OnScrollListener mScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_FLING:
                listadapter.setFlagBusy(true);
                break;
            case OnScrollListener.SCROLL_STATE_IDLE:
                listadapter.setFlagBusy(false);
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                listadapter.setFlagBusy(false);
                break;
            default:
                break;
            }
            listadapter.notifyDataSetChanged();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

        }
    };

    /**
     * ��ȡurl��xml��Դ ת��String
     * @param url
     * @return ���� ��ȡurl��xml�ַ�
     */
    public String getStringByUrl(String url) {
        String outputString = "";
        // DefaultHttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //HttpClient httpclient = AndroidHttpClient.newInstance("Android-ime", NewsRSS.this);
        // HttpGet
        HttpGet httpget = new HttpGet(url);
        // ResponseHandler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            outputString = httpclient.execute(httpget, responseHandler);        
            outputString.replace("<![CDATA[", "");
            outputString.replace("]]>", "");
            outputString.replace("![CDATA[", "");
            outputString.replace("]]", "");
            outputString = new String(outputString.getBytes("ISO-8859-1"), "gb2312");    // �����������
            Log.i("HttpClientConnector", "���ӳɹ�");
        } catch (Exception e) {
            Log.i("HttpClientConnector", "����ʧ��");
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return outputString;
    }
    
    public static class News{
        public News(){
            title = "";
            description = "";
            newsurl = "";
        }
        public String title = "";
        public String description = "";
        public String newsurl = "";
        public void setDescription(String tp){
            description = tp;
        }
        public void setTitle(String st){
            title = st;
        }
        public void setNewsurl(String tp){
            newsurl = tp;
        }
    }
    
    public static class NewsUrl{
        public NewsUrl(){
            imageurl = "";
            newsurl = "";
        }
        public String imageurl = "";
        public String newsurl = "";
        public void setImageurl(String tp){
            imageurl = tp;
        }
        public void setNewsurl(String st){
            newsurl = st;
        }
    }
    
    public static boolean isNews = false;
    
    public static ArrayList<News> ParseXml(XmlPullParser parser){
        ArrayList<News> NewsArray = new ArrayList<News>();
        News NewsTemp = null;
        String newsDescription = null;
        String newsTitle = null;
        String imageurl = null;
        try {
            //��ʼ�����¼�
            int eventType = parser.getEventType();

            //�����¼����������ĵ������һֱ����
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //��Ϊ������һ�Ѿ�̬�������������������switch
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        imageurl = "";
                        //��ǰ��ǩ�������
                        String tagName = parser.getName();
                        Log.d("zsww", "====XmlPullParser.START_TAG=== tagName: " + tagName);

                        if(tagName.equals("item")){
                            NewsTemp = new News();
                            isNews = true;
                        }else if(tagName.equals("title")){
                            newsTitle = parser.nextText();
                            
                        }else if(tagName.equals("description")){
                            newsDescription = parser.nextText();              
                            
                            Log.v("zsww", "title getText: "+newsTitle);
                            Log.v("zsww", "des getText: "+newsDescription);                    
                            Log.e("zsww", "=========================");
                            if (isNews) {
                                if (newsDescription != null && newsTitle != null) {
//                                    newsDescription.replace("<![CDATA[", "");
//                                    newsDescription.replace("]]", "");
//                                    newsTitle.replace("<![CDATA[", "");
//                                    newsTitle.replace("]]", "");
                                    //����ʽȥ��html��ʽ
                                    Pattern regex = Pattern.compile("<(.*)>");  
                                    Matcher matcher = regex.matcher(newsDescription); 
                                    boolean isMatched = matcher.find();  //matchs()ƥ������ַ�find()ƥ���ַ��е��Ӵ�
                                    //Log.v("zswww","ismatch==="+isMatched);
                                    if(isMatched){
                                        for(int j = 0; j < matcher.groupCount(); j++){
                                            
                                            newsDescription = newsDescription.replace(matcher.group(j), ""); 
                                            regex = Pattern.compile("http:([^\"]{1,})");
                                            Matcher matcherforurl = regex.matcher(matcher.group(j));   //����ʶ�𵽵�һ��url֮��Ͳ�����ʶ���ˣ�������
                                            if(matcherforurl.find()){
//                                                for(int k = 0; k < matcherforurl.groupCount(); k++){
//                                                    Log.v("zswww",k+"=="+matcherforurl.group(k));
//                                                }
                                                matcherforurl = regex.matcher(matcher.group(j).replace(matcherforurl.group(), ""));
                                                if(matcherforurl.find()){
                                                    //Log.v("zswww","=="+matcherforurl.group());
                                                    imageurl = matcherforurl.group();
                                                }
                                                else{
                                                    imageurl = "";
                                                }
                                            }
                                        }
                                       
                                    }
                                    //Log.v("zswwww","newsDescription==="+newsDescription);
                                    NewsTemp.setDescription(newsDescription);
                                    NewsTemp.setTitle(newsTitle);
                                    NewsTemp.setNewsurl(imageurl);
                                    NewsArray.add(NewsTemp);
                                }
                            }
                            isNews = false;
                            
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }

                //��������next����������һ���¼������˵Ľ��ͳ���ѭ��#_#
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return NewsArray;
    }
}
