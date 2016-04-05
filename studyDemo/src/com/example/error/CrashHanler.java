package com.example.error;
import java.lang.Thread.UncaughtExceptionHandler;

import com.example.studydemo.MainActivity;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class CrashHanler implements UncaughtExceptionHandler {

	private Thread.UncaughtExceptionHandler mDefaultHandler;
	public static final String TAG = "CatchExcep";
	MainApplication application;

	public CrashHanler(MainApplication application) {
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		this.application = application;
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}

		}
		// finish activity
		application.finishActivity();
		// restart activity
        Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);  
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  
        Intent.FLAG_ACTIVITY_NEW_TASK);  
        application.getApplicationContext().startActivity(intent);
        System.exit(0); 
		/*Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
		// MainActivity 这个Activity在Manifest 文件中将它的启动模式修改为
		// android:launchMode="singleTask"
		PendingIntent restartIntent = PendingIntent.getActivity(application.getApplicationContext(), 0, intent,
				Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		// 退出程序
		AlarmManager mgr = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
		// 退出程序
		String packName = application.getApplicationContext().getPackageName();
		ActivityManager activityMgr = (ActivityManager) application.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		// 四种不同关闭本程序 最好都写上 因为在不同的版本中有些方法不能很好的执行
		activityMgr.restartPackage(packName);// 重新启动本程序
		activityMgr.killBackgroundProcesses(packName);//
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);*/
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
		return true;
	}
}