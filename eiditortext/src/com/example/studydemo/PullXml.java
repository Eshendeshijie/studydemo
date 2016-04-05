package com.example.studydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eiditortext.R;
import com.example.studydemo.MainActivity.GetCSDNLogoTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PullXml extends Activity {
    public TextView textxml = null;
    public static ArrayList<City> WeatherInfo = new ArrayList<City>();
    private final String WEATHER_URL_START = "http://php.weather.sina.com.cn/xml.php?city=";
    private final String WEATHER_URL_END = "&password=DJOYnieT8234jlsK&day=0";
    private String mCurrentCity = "北京";
    private Context mContext;
    private String domResult = "";
    private String saxResult = "";
    private final String INDENT_DEFAULT = "  ";
    private int mPraseNum = 0;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showxml);
        mContext = getApplicationContext();
        LayoutInflater inflater = getLayoutInflater();
        textxml = (TextView) findViewById(R.id.cityweather);
        Button locationBtn = (Button) findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 LocationManager locationManager;  
			        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
			        String provider = LocationManager.NETWORK_PROVIDER;  
			        Criteria criteria = new Criteria();  
			        criteria.setAccuracy(Criteria.ACCURACY_FINE);  
			        criteria.setAltitudeRequired(false);  
			        criteria.setBearingRequired(false);  
			        criteria.setCostAllowed(true);  
			        criteria.setPowerRequirement(Criteria.POWER_LOW);  
			        Location location = locationManager.getLastKnownLocation(provider);  
			        updateWithNewLocation(location);  
			        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);  
			        getEncode("深圳", "utf-8", INT_DEC);
			}
		});
        Button bt_geturl = (Button) findViewById(R.id.pullmethod);
        bt_geturl.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                GetCityWeatherTask task = new GetCityWeatherTask();  
                task.execute(getCityUrl(mCurrentCity));  
            }  
        });
        Button  pullBtn = (Button) findViewById(R.id.pull_xml);
        pullBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WeatherInfo = ParseXmlByPull(CityWeather);
				if (WeatherInfo == null) {
					Log.v("zsww", "pull wrong!!");
					return;
				}
				Log.v("zsww", "WeatherInfo===" + WeatherInfo);
				Log.v("zsww", "WeatherInfo===" + WeatherInfo.get(0).status);
				for (int i = 0; i < WeatherInfo.size(); i++) {
					textxml.setText(WeatherInfo.get(i).cityName + "  " + WeatherInfo.get(i).status + "  "
							+ WeatherInfo.get(i).temperature);
				}
			}
		});
		Button  domBtn = (Button) findViewById(R.id.dom_xml);
		domBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				domResult = "";
				mPraseNum = 0;
				ParseXmlByDOM(CityWeather);
				if (domResult == null || domResult == "") {
					Log.v("zsww", "pull wrong!!");
					return;
				}
				textxml.setText(domResult);
			}
		});
		Button  saxBtn = (Button) findViewById(R.id.sax_xml);
		saxBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saxResult = ParseXmlBySAX(CityWeather);
				if (saxResult == null || saxResult == "") {
					Log.v("zsww", "pull wrong!!");
					return;
				}
				textxml.setText(saxResult);
			}
		});
    }
    
    public String CityWeather = null;
    
    class GetCityWeatherTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                CityWeather = getStringByUrl(params[0]);
                Log.v("zswww", "return weather");
            } catch (Exception e) {
                Log.v("zswww", "return null");
                return null;
            }
            return CityWeather;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(PullXml.this, "加载成功", Toast.LENGTH_LONG).show();
                textxml.setText(CityWeather);
            } else {
                Toast.makeText(PullXml.this, "加载失败", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * ͬ��ɾ�����У����ܽ����ɹ���
     * @param fileName
     * @return ����xml�ļ���inputStream
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
     * ��ȡXML�ļ���xml�ļ��ŵ�res/xml�ļ����У���XMLΪ�����ļ������Ƽ��÷���
     * 
     * @param fileName
     * @return : ��ȡ��res/xml�ļ����µ�xml�ļ�������XmlResourceParser����XmlPullParser�����ࣩ
     */
    public XmlResourceParser getXMLFromResXml(String fileName){
        XmlResourceParser xmlParser = null;
        try {
            //*/
            //  xmlParser = this.getResources().getAssets().openXmlResourceParser("assets/"+fileName);        // ʧ��,�Ҳ����ļ�
            //xmlParser = this.getResources().getXml(R.xml.provinceandcity);
            /*/
            // xml�ļ���resĿ¼�� Ҳ�����ô˷�������inputStream
            InputStream inputStream = this.getResources().openRawResource(R.xml.provinceandcity);
            /*/
            return xmlParser;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return xmlParser;
    }

    /**
     * ��ȡurl��xml��Դ ת��String
     * @param url
     * @return ���� ��ȡurl��xml�ַ���
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
            outputString = new String(outputString.getBytes("ISO-8859-1"), "utf-8");    // �����������
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return outputString;
    }

    /**
     * ����SDcard xml�ļ�
     * @param fileName
     * @return ����xml�ļ���inputStream
     */     
    public InputStream getInputStreamFromSDcard(String fileName){
        try {
            // ·������ʵ����Ŀ�޸�
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
    
    
    //pull方式解析
    public static ArrayList<City> ParseXmlByPull(String CityWeather){
    	XmlPullParser parser = null;
    	XmlPullParserFactory factory;
		if (CityWeather == null) {
			return null;
		} else {
			// XmlPullParserFactory
			try {
				factory = XmlPullParserFactory.newInstance();
				// XmlPullParser
				parser = factory.newPullParser();
				// 解析节点数据
				parser.setInput(new StringReader(CityWeather));
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(parser == null) return null;
        ArrayList<City> CityArray = new ArrayList<City>();
        City CityTemp = null;
        String temperature = null;
        String cityName = null;
        String status = null;
        CityTemp = new City();
        try {
            //获取节点
            int eventType = parser.getEventType();

            //开始节点
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //解析不同节点
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:

                        //获取名字
                        String tagName = parser.getName();
                        Log.d("zsww", "====XmlPullParser.START_TAG=== tagName: " + tagName);

                        if(tagName.equals("city")){
                           Log.v("zsww","parser=="+parser.getName()+"=="+"=="+"=="+parser.getAttributeCount()+"=="+parser.getAttributeValue(tagName, tagName));
                            cityName = parser.nextText().toString();//Integer.parseInt(parser.getAttributeValue(0));
                            Log.v("zsww","cityname==="+cityName);
                        }else if(tagName.equals("status1")){
                            status = parser.nextText();
                        }else if(tagName.equals("temperature1")){
                            temperature = parser.nextText() + "℃";                          
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

                //获取下一个节点
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
    
    //DOM方式解析
    public String ParseXmlByDOM(String CityWeather){
    	if(TextUtils.isEmpty(CityWeather)) {
    		return null;
    	}
    	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder domBuilder = null;
    	Document document = null;
    	Element root = null;
    	NodeList nodes = null;
    	try {
    		//忽略空白的接口不起作用
    		domFactory.setIgnoringElementContentWhitespace(true);
    		domFactory.setIgnoringComments(true);
    		domBuilder = domFactory.newDocumentBuilder();
			document = domBuilder.parse(new InputSource(new StringReader(CityWeather)));
    		//document = domBuilder.parse(mContext.getResources().getAssets().open("newsrss.xml"));
    		root = document.getDocumentElement();
			nodes = root.getChildNodes();
			displayNodes(nodes);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return domResult;
    }
    
    //遍历DOM节点和属性
    //getNodeName():获取节点名字
    //getAttributes():获取节点的属性列表
    //getTextContent():获取当前节点内的所有文本值
    public String displayNodes(NodeList Nlist){
    	mPraseNum += 1;
    	String INDENT_ELEMENT = "";
    	String INDENT_ATTRS = "";
    	Log.v("zsw_show", "mPraseNum : "+mPraseNum);
    	for(int i = 0; i < mPraseNum - 1; i++) {
    		INDENT_ELEMENT += INDENT_DEFAULT;
    	}
    	INDENT_ATTRS = INDENT_ELEMENT + INDENT_DEFAULT;
    	String SPILIT = ": ";
    	String ENTER = "\n";
    	//遍历节点
        for (int i = 0; i <Nlist.getLength(); i++) {
            Node node = Nlist.item(i);
            if(node.hasChildNodes()){//判断该节点是否还有子节点
                NodeList list = node.getChildNodes();
                //如果节点仅有一个子节点，并且该节点为TEXT_NODE，则该子节点为节点的值
                if(list.getLength() == 1 && list.item(0).getNodeType() == Node.TEXT_NODE) {
                	domResult += INDENT_ELEMENT + node.getNodeName()+ SPILIT + node.getTextContent() + ENTER;
                }
                //否则为父节点
                else {
                	domResult += INDENT_ELEMENT + node.getNodeName() + ENTER;
                }
                //遍历属性
                NamedNodeMap map = node.getAttributes();
                for(int j = 0; j < map.getLength(); j++) {
                	domResult += INDENT_ATTRS + map.item(j).getNodeName() + SPILIT + map.item(j).getNodeValue() + ENTER;
                }
                displayNodes(list);  //调用方法本身
            } else {
            	//以‘/>’结尾的节点
            	if(node.getNodeType() == Node.ELEMENT_NODE) {
            		domResult += INDENT_ELEMENT + node.getNodeName() + ENTER;
            		NamedNodeMap map = node.getAttributes();
            		for(int j = 0; j < map.getLength(); j++) {
                    	domResult += INDENT_ATTRS + map.item(j).getNodeName() + SPILIT + map.item(j).getNodeValue() + ENTER;
                    }
            	}
            }
        }
        return domResult;
    }
    
    //SAX方式解析
    public String ParseXmlBySAX(String CityWeather){
    	CityWeather = CityWeather.replace("\n", "");
    	SAXParserFactory saxParserFactory =SAXParserFactory.newInstance();
    	String result = "";
    	try {
    		SAXPraseHandler praseHandler = new SAXPraseHandler();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(praseHandler);
			saxParser.parse(new InputSource(new StringReader(CityWeather)), praseHandler);
			result = praseHandler.getResult();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }
    
    //SAX方式解析
    public static ArrayList<City> ParseXmlByUtil(String CityWeather){
    	return null;
    }
    
    //定位位置
    private final LocationListener locationListener = new LocationListener() {  
        public void onLocationChanged(Location location) {  
        updateWithNewLocation(location);  
        }  
        public void onProviderDisabled(String provider){  
        updateWithNewLocation(null);  
        }  
        public void onProviderEnabled(String provider){ }  
        public void onStatusChanged(String provider, int status,  
        Bundle extras){ }  
    }; 
    
    private void updateWithNewLocation(Location location) {  
        String latLongString;  
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "纬度:" + lat + "\n经度:" + lng;
			List<Address> addList = null;  
	        Geocoder ge = new Geocoder(this);  
	        try {  
	            addList = ge.getFromLocation(lat, lng, 1);  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        if(addList!=null && addList.size()>0){  
	            for(int i=0; i<addList.size(); i++){  
	                Address ad = addList.get(i);  
	                latLongString += "\n";  
	                latLongString += ad.getCountryName() + ";" + ad.getLocality();  
	                mCurrentCity = ad.getLocality();
	            }  
	        }
	        mCurrentCity = mCurrentCity.substring(0, mCurrentCity.length() - 1);
		} else {
			latLongString = "无法获取地理信息";
		}  
        textxml.setText("您当前的位置是:\n" +latLongString);  
    }  
    
    private String getCityUrl(String cityname) {
    	if(TextUtils.isEmpty(cityname)) {
    		cityname = "北京";
    	}
    	String gbkOfCity = getEncode(cityname, "gb2312", INT_HEX);//Utf8Togb2312Encode(cityname);
		Log.v("zsw_show", "city url : "+cityname+"=="+gbkOfCity);
		StringBuilder cityUrl = new StringBuilder().append(WEATHER_URL_START).append(gbkOfCity).append(WEATHER_URL_END);
    	return cityUrl.toString();
    }
    
	public static String utf8Togb2312(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '+':
				sb.append(' ');
				break;
			case '%':
				try {
					sb.append((char) Integer.parseInt(
					str.substring(i + 1, i + 3), 16));
				}
				catch (NumberFormatException e) {
					throw new IllegalArgumentException();
				}
				i += 2;
				break;
			default:
				sb.append(c);
				break;
			}
		}
		String result = sb.toString();
		String res = null;
		try {
			byte[] inputBytes = result.getBytes("8859_1");
			res = new String(inputBytes, "UTF-8");
		}
		catch (Exception e) {
		}
		return res;
	}
	
	public static String Utf8Togb2312Encode(String str) {
		String urlEncode = "";
		try {
			urlEncode = URLEncoder.encode(str, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return urlEncode;
	}
	
	// form of output : %XX%XX%XX...
    private final int INT_BINARY = 0; //二进制
    private final int INT_OCTAL = 1;  //八进制
    private final int INT_HEX = 2;    //十六进制
    private final int INT_DEC = 3;    //十进制 
	private String getEncode(String str, String outputEncode, final int radix) {
		String result = "";
		String origne = "";
		try {
		    byte[] outputEncodeByte = str.getBytes(outputEncode);
		    for(int i = 0; i < outputEncodeByte.length; i++) {
		    	if(outputEncodeByte[i] < 0) {
		    		result += "%";
		    	}
				switch (radix) {
				case INT_BINARY:
					result += Integer.toBinaryString(outputEncodeByte[i] & 0x000000FF);
					origne += Integer.toBinaryString(outputEncodeByte[i]);
					break;
				case INT_OCTAL:
					result += Integer.toOctalString(outputEncodeByte[i] & 0x000000FF);
					origne += Integer.toOctalString(outputEncodeByte[i]);
					break;
				case INT_HEX:
					result += Integer.toHexString(outputEncodeByte[i] & 0x000000FF);
					origne += Integer.toHexString(outputEncodeByte[i]);
					break;
				case INT_DEC:
				default:
					result += outputEncodeByte[i] & 0x000000FF;
					origne += outputEncodeByte[i];
					break;
				}
		    }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
