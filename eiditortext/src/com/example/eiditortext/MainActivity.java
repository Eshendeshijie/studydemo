
package com.example.eiditortext;

import android.net.Uri;
import android.opengl.ETC1;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.EditText.*;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.inputmethodservice.*;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public TextView tv = null;
    public TextView ev2 = null;
    public EditText et = null;
    public ProgressBar pb = null;
    public int m_count = 0;
    //����������Ի���
    public ProgressDialog m_pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout mainxml = (RelativeLayout)inflater.inflate(R.layout.activity_main, null);
        lblImage = (ImageView)findViewById(R.id.image);
        lblImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, "����");
                intent.putExtra(Intent.EXTRA_TEXT, "�ö�����������?");
                startActivity(intent);
                //startActivity(Intent.createChooser(intent, getTitle()));
            }
         });
        tv = (TextView) findViewById(R.id.textView1);
        tv.setTextSize(50);
        ev2 = (EditText) findViewById(R.id.editText2);
        et = (EditText) findViewById(R.id.editText1);
        ev2.addTextChangedListener(textChangeWatcher);
        ev2.requestFocus();
        ev2.setText(R.string.changesize);
        //ev2.setSelection(8);
        et.setKeyListener(ContactNumberListener.getInstance());
        et.addTextChangedListener(textChangeWatcher);
        //ͨ��setsoftinputmode��������ģʽ
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Button bt = (Button) findViewById(R.id.button1);
        Button bt_camara = (Button) findViewById(R.id.button2);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        Button bt_async = (Button) findViewById(R.id.button3);
        //bt.setOnClickListener(clicklistener);
        bt_async.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                pb.setVisibility(View.VISIBLE);
                GetCSDNLogoTask task = new GetCSDNLogoTask();  
                task.execute("http://preview.mail.163.com/xdownload?filename=301-魏晨.jpg&mid=1tbiWAFGfVEAG2YKLQAAsp&part=5&sign=c36eb108aefc0280b9a915d3c231c39c&time=1394087544&uid=bbkteam1@163.com");
                //task.execute("http://t1.baidu.com/it/u=http%3A%2F%2Fwww.cenews.com.cn%2Ffzxw%2Ffzyw%2F201403%2FW020140305346571446884.jpg&fm=30");  
            }  
        });
        Button bt_pullxml = (Button) findViewById(R.id.pullxml);

        //����mButton02���¼�����
        bt_pullxml.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
               
                m_count = 0;
               
                // ����ProgressDialog����
                m_pDialog = new ProgressDialog(MainActivity.this);
               
                // ���ý������񣬷��Ϊ����
                m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
               
                // ����ProgressDialog ����
                m_pDialog.setTitle("��ʾ");
               
                // ����ProgressDialog ��ʾ��Ϣ
                m_pDialog.setMessage("����һ�����ζԻ�������");
               
                // ����ProgressDialog ����ͼ��
                //m_pDialog.setIcon(R.drawable.img2);
               
                // ����ProgressDialog ��������
                m_pDialog.setProgress(100);
               
                // ����ProgressDialog �Ľ�����Ƿ���ȷ
                m_pDialog.setIndeterminate(false);
               
                // ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
                m_pDialog.setCancelable(true);
               
                // ��ProgressDialog��ʾ
                m_pDialog.show();
               
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            while (m_count <= 100)
                            {
                                // ���߳������ƽ�ȡ�
                                m_pDialog.setProgress(m_count++);
                                Thread.sleep(100);
                            }
                            m_pDialog.cancel();
                        }
                        catch (InterruptedException e)
                        {
                            m_pDialog.cancel();
                        }
                    }
                }.start();
               
            }
        });
//        bt_pullxml.setOnClickListener(new OnClickListener() {  
//            @Override  
//            public void onClick(View v) {  
//                Intent intent = new Intent();
////                Bundle bundle = new Bundle();
////                bundle.putString("Key_height", editText1.getText().toString());
////                bundle.putString("Key_weight", editText2.getText().toString());
////                intent.putExtras(bundle);
//                intent.setClass(MainActivity.this, PullXml.class);
//                startActivity(intent);  
//            }  
//        });
        Button bt_rss = (Button) findViewById(R.id.newsrss);
        bt_rss.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, NewsRSS.class);
                startActivity(intent);  
            }  
        });
        Log.v("zsww",et.getText().toString());
        
        Button bt_share = (Button)findViewById(R.id.button4);
        bt_share.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v){
               Intent intent=new Intent(Intent.ACTION_SEND);
               intent.setType("text/x-vcard");
               intent.putExtra(Intent.EXTRA_SUBJECT, "����");
               intent.putExtra(Intent.EXTRA_TEXT, "�ö�����������?");
               Intent chooserintent = createChooser(MainActivity.this, intent, "hahahhah", "com.android.filemanager", "com.tencent.mobileqq");
               //intent.setComponent(new ComponentName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity"));
               startActivity(chooserintent);
               //startActivity(Intent.createChooser(intent, getTitle()));
           }
        });
    }

    /**
     * 过滤指定应用
     * @param removePackageName 需要过滤的应用的包名
     */
    public static Intent createChooser(Context context, Intent target, CharSequence title, String... removePackageName) {
        String action = target.getAction();
        String type = target.getType();
        Bundle extras = target.getExtras();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(target, 0);
        Parcelable[] pa = target.getParcelableArrayExtra(Intent.EXTRA_INITIAL_INTENTS);
        if (!resInfo.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            for (ResolveInfo info : resInfo) {
                Intent targeted = new Intent(action);
                targeted.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                targeted.setType(type);
                ActivityInfo activityInfo = info.activityInfo;
                activityInfo.labelRes = info.labelRes;
                boolean remove = false;
                for (int i = 0; i < removePackageName.length; i++) {
                    if (activityInfo.packageName.equals(removePackageName[i])) {
                        remove = true;
                        break;
                    }
                }
                if (remove) {
                    continue;
                }
                targeted.putExtras(extras);
                //targeted.setPackage(activityInfo.packageName);
                Log.v("zsww", "targeted.resolveActivity(pm)==="+info.loadLabel(pm));
                activityInfo.loadLabel(pm);
                //targeted.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name)); //(context, activityInfo.name);
                Log.v("zsww", "activityInfo.packageName==="+activityInfo.packageName);
                //Log.v("zsww", "info==="+info.labelRes+"=="+info.nonLocalizedLabel);
                //activityInfo.
                targeted.setPackage(activityInfo.packageName);
                targeted.setClassName(activityInfo.packageName, activityInfo.name);
                targeted.resolveActivityInfo(pm, 0).labelRes = info.labelRes;
                //targeted.putExtra("labelRes", info.labelRes);
                ResolveInfo ri = new ResolveInfo();
                ri.labelRes = info.labelRes;
                ri.activityInfo = targeted.resolveActivityInfo(pm, 0);
                //Log.v("zsww", "ri==="+ri.labelRes+"=="+ri.nonLocalizedLabel);
                Log.v("zsww", "ri.loadLabel(pm)==="+ri.loadLabel(pm));
                //targeted.
                Log.v("zsww", "targeted======"+targeted.resolveActivityInfo(pm, 0).labelRes);
                //targeted.putExtras(activityInfo.);
                targetedShareIntents.add(targeted);
            }
            int targetedShareIntentsSize = targetedShareIntents.size();
            if (targetedShareIntentsSize == 0) {
                return Intent.createChooser(target, title);
            }
            Intent remove = targetedShareIntents.remove(targetedShareIntentsSize - 1);
            Intent chooserIntent = Intent.createChooser(remove, title);
//            Intent chooserIntent = new Intent(Intent.ACTION_SEND);
//            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//            chooserIntent.setType(type);
//            chooserIntent.putExtras(extras);
//            chooserIntent.setClassName("android", "com.android.internal.app.ResolverActivity");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            return chooserIntent;
        }
        return Intent.createChooser(target, title);
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
    
    class GetCSDNLogoTask extends AsyncTask<String,Integer,Bitmap> {//�̳�AsyncTask  
        
        @Override  
        protected Bitmap doInBackground(String... params) {//�����ִ̨�е������ں�̨�߳�ִ��  
            publishProgress(0);//�������onProgressUpdate(Integer... progress)����  
            HttpClient hc = new DefaultHttpClient();  
            publishProgress(30);  
            HttpGet hg = new HttpGet(params[0]);//��ȡcsdn��logo  
            Log.v("zswww",hg.toString());
            final Bitmap bm;  
            try {  
                //HttpResponse hr = hc.execute(hg);
                //bm = BitmapFactory.decodeStream(hr.getEntity().getContent());
                URL imageurl = new URL(params[0]);
                bm = bmpFromURL(imageurl);
                Log.v("zswww","return bm");
                Log.v("zswww", "is bmp == "+bm);
            } catch (Exception e) {
                Log.v("zswww","return null");
                return null;  
            }  
            publishProgress(100);  
            //mImageView.setImageBitmap(result); �����ں�̨�̲߳���ui  
            return bm;  
        }  
          
        protected void onProgressUpdate(Integer... progress) {//�ڵ���publishProgress֮�󱻵��ã���ui�߳�ִ��  
            pb.setProgress(progress[0]);//���½�����Ľ��  
         }  
  
         protected void onPostExecute(Bitmap result) {//��̨����ִ����֮�󱻵��ã���ui�߳�ִ��  
             pb.setVisibility(View.GONE);
             if(result != null) {  
                 Toast.makeText(MainActivity.this, "�ɹ���ȡͼƬ", Toast.LENGTH_LONG).show();  
                 lblImage.setImageBitmap(result);  
             }else {  
                 Toast.makeText(MainActivity.this, "��ȡͼƬʧ��", Toast.LENGTH_LONG).show();  
             }  
         }  
           
         protected void onPreExecute () {//�� doInBackground(Params...)֮ǰ�����ã���ui�߳�ִ��  
             lblImage.setImageBitmap(null);  
             pb.setProgress(0);//�������λ  
         }  
           
         protected void onCancelled () {//��ui�߳�ִ��  
             pb.setProgress(0);//�������λ  
         }  
          
    }
    
    private TextWatcher textChangeWatcher = new TextWatcher(){

        public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
            Log.v("zsww", "beforeTextChanged");
            flag = !flag;
        }

        public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
            Log.v("zsww", "onTextChanged");
            insert = i;
            inputCount = k;
            if(charsequence != "" || charsequence != null){
                int a = 121 /2 ;
                Log.v("zsww","aaaaa=="+a);
            //tv.setText(ev2.getText().toString());
                tv.setText("བ");
                Typeface fontFace = Typeface.createFromAsset(MainActivity.this.getAssets(), "DDC_Uchen.ttf");
                //System.out.println(fontFace);
                // 字体文件必须是true type font的格式(ttf)；
                // 当使用外部字体却又发现字体没有变化的时候(以 Droid Sans代替)，
                // 通常是因为这个字体android没有支持,而非你的程序发生了错误
                tv.setTypeface(fontFace);

            if(et.getText().toString().length() == 0)return;
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.max(Integer.parseInt(et.getText().toString()), 1));
            }
            else{
                tv.setText(ev2.getText().toString());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            }
        }

        boolean flag = false;// �����Ƿ��ظ�ִ������sppanable
        int insert = 0;
        int inputCount = 0;

        public void afterTextChanged(Editable editable) {
            if (flag) {
                Log.v("zsww", "afterTextChanged");
                int temp = inputCount;
                int tempInsert = insert;
                // ChatMessage cm = new ChatMessage();
                // cm.setMsg(editable.toString());
                // //���Ҫ����������ݽ��д��?�����flag���ƣ��������ѭ��
                // ChatMessageDao.setFormaterEditText(mEtMsgEdit, cm,
                // mContext);
                // mEtMsgEdit.setSelection(tempInsert + temp);
            }
        }
    };
    
    @Override
    protected void onResume()
    {
        super.onResume();
        Log.v("zsww","onresume");
        if(ad != null && inputServer != null){
//            Log.v("zsww", "setfocus");
//            inputServer.setFocusable(true);
//            inputServer.requestFocus();
//            InputMethodManager im = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
//            Log.v("zsww","active=1=="+im.isActive(inputServer));
//            Log.v("zsww", "active=2=="+im.isActive());
//            View currenfocus = getCurrentFocus();
//            Log.v("zsww","currenfocus=1="+currenfocus);
//            if(!im.showSoftInput(inputServer,InputMethodManager.SHOW_FORCED)){
//                
//                if(currenfocus != null)
//                {
//                    Log.v("zsww","currenfocus=2="+currenfocus);
//                    currenfocus.clearFocus();
//                }
//                inputServer.setFocusable(true);
//                inputServer.requestFocus();
//                im.showSoftInputFromInputMethod(inputServer.getWindowToken(), InputMethodManager.SHOW_FORCED);
//                Log.v("zsww","im=1="+im.showSoftInput(inputServer,InputMethodManager.SHOW_FORCED));       
//            }
            onFocusChange(inputServer,true);
            
        }
    }
    
    public boolean isFocus = true;
    void onFocusChange(View v, boolean hasFocus)
    {
        isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        inputServer.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus)
                {
                    imm.showSoftInput(inputServer, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                else
                {
                    imm.hideSoftInputFromWindow(inputServer.getWindowToken(), 0);
                }
            }
        }, 500);
    }
    
    public EditText inputServer = null;
    public Dialog ad = null;
    public void clicklistener(View view) {
        Log.v("zsww", "onclick");
        inputServer = new EditText(this);
        Builder builder = new Builder(this);
        builder.setView(inputServer);
        builder.setPositiveButton("alertDialog",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String inputName = inputServer.getText().toString();
                             AlertDialog.Builder at = new
                             AlertDialog.Builder(MainActivity.this);
                             at.setMessage("�罻���������������������꿪�����ظ������޺��̷��˷�����񿪷����׿������밣˹���޿�����˿�ʦ��");
                             at.setPositiveButton("ok", null);
                             at.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        ad = (Dialog)builder.create(); 
        ad.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        
        try {
            Field field  =  ad.getClass().getDeclaredField( "mAlert" );
            field.setAccessible( true );
            //   ���mAlert������ֵ 
            Object obj  =  field.get(ad);
            field  =  obj.getClass().getDeclaredField( "mHandler" );
            field.setAccessible( true );
            //   �޸�mHandler������ֵ��ʹ���µ�ButtonHandler�� 
            field.set(obj, new ButtonHandler(ad));
            //dialog.dismiss();
            // String inputName =
            // inputServer.getText().toString();
            // AlertDialog.Builder at = new
            // AlertDialog.Builder(ad.getContext());
            // at.setPositiveButton("ok", null);
            // at.show();
        } catch (Exception e) {
            e.printStackTrace();
        }      
         ad.show();
         InputMethodManager im = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
         im.showSoftInput(inputServer,InputMethodManager.SHOW_FORCED);  


    }
    
    private static final int CAMERA_WITH_DATA = 0;
    private static final int PHOTO_PICKED_WITH_DATA =1;
    private ImageView lblImage = null;
    public void clicklistener_camera(View view) {
        Log.v("zsww", "onclick_camera");
        
//        Intent camera_intent = new Intent();  
//        camera_intent.setAction("android.media.action.IMAGE_CAPTURE");    
//        startActivityForResult(camera_intent,0);
        //�����
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_WITH_DATA);
        
//        Uri mSaveUri = Uri.parse("content://" + "media" + "/external/image/mdeia/1.jpg");
//        int REQUEST_CROP = 0;
//        Bundle newExtras = new Bundle();
//        newExtras.putParcelable(MediaStore.EXTRA_OUTPUT, mSaveUri);
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        Uri tempUri = Uri.parse("content://" + "media" + "/external/image/mdeia/1.jpg");
//        //cropIntent.setData(tempUri);
//        //cropIntent.putExtras(newExtras);
//        startActivityForResult(cropIntent, REQUEST_CROP);
    }
    
    protected void doCropPhoto(Bitmap data){
        Intent intent = getCropImageIntent(data);
        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
    }
    
    public static Intent getCropImageIntent(Bitmap data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.putExtra("data", data);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);
        return intent;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(resultCode != RESULT_OK)
            return;
        switch(requestCode){
        case CAMERA_WITH_DATA:
            final Bitmap photo = data.getParcelableExtra("data");
            if(photo!=null){
                doCropPhoto(photo);
            }
        case PHOTO_PICKED_WITH_DATA:
            Bitmap photo1 = data.getParcelableExtra("data");
            if(photo1!=null){
                lblImage.setImageBitmap(photo1);
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class  ButtonHandler  extends  Handler
    {

         private  WeakReference < DialogInterface >  mDialog;

         public  ButtonHandler(DialogInterface dialog)
        {
            mDialog  =   new  WeakReference < DialogInterface > (dialog);
        }

        @Override
         public   void  handleMessage(Message msg)
        {
             switch  (msg.what)
            {

                 case  DialogInterface.BUTTON_POSITIVE:
                 case  DialogInterface.BUTTON_NEGATIVE:
                 case  DialogInterface.BUTTON_NEUTRAL:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(mDialog
                            .get(), msg.what);
                     break ;
            }
        }
    }

}