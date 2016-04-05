package com.example.service;

import com.example.service.IRemoteService.Stub;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

public class TestService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.v("zsw_show", "on bind");
		return mBinder;
	}
	
	private final IRemoteService.Stub mBinder = new Stub() {
		
		@Override
		public int getPid() throws RemoteException {
			// TODO Auto-generated method stub
			return Process.myPid();
		}
		
		@Override
		public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.v("zsw_show", "server basicTypes : "+anInt+"=="+aLong+"=="+aBoolean+"=="+aFloat+"=="+aDouble+"=="+aString);
			System.out.println("Thread: " + Thread.currentThread().getName());
            System.out.println("basicTypes aDouble: " + aDouble +" anInt: " + anInt+" aBoolean " + aBoolean+" aString " + aString);
		}
	};

}
