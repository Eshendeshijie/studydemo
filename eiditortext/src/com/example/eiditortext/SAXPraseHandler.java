package com.example.eiditortext;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;
import android.util.Log;

public class SAXPraseHandler extends DefaultHandler {
	
	private boolean mIgnoringElementContentWhitespace = false;
	private String mElementName = "";
	private String mElementContent = "";
	private String mResult = "";
	private final String SPILIT = ": ";
	private final String ENTER = "\n";
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		mResult = "";
		mElementName = "";
		mElementContent = "";
		super.startDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		Log.v("zsw_show", "startElement : "+uri+"=="+localName+"=="+qName);
		mElementName = localName;
		mResult += mElementName;
		super.startElement(uri, localName, qName, attributes);
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String res = new String(ch, start, length);
		mElementContent = res;
		if(!TextUtils.isEmpty(res)) {
			mResult += SPILIT + res;
		}
		Log.v("zsw_show", "result : "+res);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub
		mResult += ENTER;
		super.endElement(uri, localName, qName);
	}
	
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}
	
	public void setIgnoringElementContentWhitespace(boolean ignore) {
		mIgnoringElementContentWhitespace = ignore;
	}
	
	public String getResult() {
		return mResult;
	}

}
