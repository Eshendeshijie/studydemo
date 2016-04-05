package com.example.error;


import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

public class MainApplication extends Application{  
      
    ArrayList<Activity> list = new ArrayList<Activity>();  
      
    public void init(){  
        //设置该CrashHandler为程序的默认处理器    
        CrashHanler catchExcep = new CrashHanler(this);  
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);   
    }  
      
    /** 
     * Activity关闭时，删除Activity列表中的Activity对象*/  
    public void removeActivity(Activity a){  
        list.remove(a);  
    }  
      
    /** 
     * 向Activity列表中添加Activity对象*/  
    public void addActivity(Activity a){  
        list.add(a);  
    }  
      
    /** 
     * 关闭Activity列表中的所有Activity*/  
    public void finishActivity(){  
        for (Activity activity : list) {    
            if (null != activity) {    
                activity.finish();    
            }    
        }  
    }  
}  