package com.example.wzh.test1;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private NotificationManager mNotificationManager = null;
    private Context mContext;

    private DatagramSocket s=null;
    private DatagramPacket recvdata;
    public boolean flag=true;


    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    warning();
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * receive data
     */
    public Runnable socket=new Runnable() {
        @Override
        public void run() {
            try {
                s = new DatagramSocket(8881);
                byte data[] = new byte[1024];
                while(flag) {
                    recvdata = new DatagramPacket(data, data.length);
                    s.receive(recvdata);
                    byte string[] = recvdata.getData();
                    if (string[0] == '1') {
                        handler.sendEmptyMessage(1);
                    }
                }
            } catch (IOException e) {
                handler.sendEmptyMessage(2);
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=getApplicationContext();

        mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        View v=findViewById(R.id.back);
        v.getBackground().setAlpha(220);

        TextView v1 = (TextView) findViewById(R.id.textView);
        TextView v2 = (TextView) findViewById(R.id.textView2);
        v1.setText(getClickableSpan("无法登录"));
        v2.setText(getClickableSpan("新用户"));
        v1.setMovementMethod(LinkMovementMethod.getInstance());
        v2.setMovementMethod(LinkMovementMethod.getInstance());
        removeHyperLinkUnderline(v1);
        removeHyperLinkUnderline(v2);
        Button mbutton=(Button)findViewById(R.id.ok);
        Button button5=(Button)findViewById(R.id.button5);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning();
            }
        });

        button5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.qq);
                builder.setTitle("请输入用户名和密码");
                final String[] choices={"SOURCE1 ONLY","SOURCE2 ONLY","SOURCE1 MAIN","SOURCE2 MAIN"};
                builder.setSingleChoiceItems(choices, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText username = (EditText)view.findViewById(R.id.username);
                final EditText password = (EditText)view.findViewById(R.id.password);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String a = username.getText().toString().trim();
                        String b = password.getText().toString().trim();
                        //    将输入的用户名和密码打印出来
                        Toast.makeText(MainActivity.this, "用户名: " + a + "  密码: " + b, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.show();
            }
        });

        Thread thread=new Thread(socket);
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void removeHyperLinkUnderline(TextView tv) {
        CharSequence text = tv.getText();
        if(text instanceof Spannable){
            Log.i("test","true");
            Spannable spannable = (Spannable) tv.getText();
            NoUnderlineSpan noUnderlineSpan = new NoUnderlineSpan();
            spannable.setSpan(noUnderlineSpan,0,text.length(), Spanned.SPAN_MARK_MARK);
        }
        else{
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private SpannableString getClickableSpan(final String s) {
        SpannableString spannableString = new SpannableString(s);
        int size=s.length();
        final String msg;
        //设置下划线文字
        spannableString.setSpan(new UnderlineSpan(), 0, size, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(new URLSpan("http://www.baidu.com"),0, size,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                byte bytes[]=new byte[]{0x3F};
//                if (bytes[0]==0x3F)
//                    Toast.makeText(MainActivity.this,Integer.toHexString((int)bytes[0]),Toast.LENGTH_SHORT).show();
                //mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            }
        }, 0, size, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, size, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    //警告
    private void warning(){
        PendingIntent pi = PendingIntent.getActivity(
                MainActivity.this,
                100,
                new Intent(MainActivity.this, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        Bitmap largeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setContentTitle("警告：")
                .setContentText("最高温度超过50℃")
                .setTicker("New Warning!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeBitmap)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(false)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi);
        Notification mNotification = mBuilder.build();
        int notifyId = 123;
        mNotificationManager.notify(notifyId, mNotification);
        writetext();
    }

    //温度超过50℃时写入DJI_Download的Warning Log文件中
    private void writetext(){
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日  HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String str2="温度超过50度，时间为"+str+"\n";
        str=Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/Warning Log.txt";
        File txtfile =
                new File(str);
        try {
            if (!txtfile.exists()){txtfile.createNewFile();}
            RandomAccessFile raf = new RandomAccessFile(txtfile, "rwd");
            raf.seek(txtfile.length());
            raf.write(str2.getBytes());  //将String字符串以字节流的形式写入到输出流中
            raf.close();         //关闭输出流
        }catch (Exception e){
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this,"警报！最高温度超过50摄氏度！",Toast.LENGTH_SHORT).show();

        String str1=Environment.getExternalStorageDirectory().getPath() + "/DJI_Download/Warning Log.db";
        SQLiteDatabase db= SQLiteDatabase.openOrCreateDatabase(str1,null);
        String TemperatureLog="create table if not exists TemperatureLog(_id integer primary key autoincrement,time text,longitude double,latitude double,height double,temperature text)";
        db.execSQL(TemperatureLog);
        String insertStr = "insert into TemperatureLog(time,longitude,latitude,height,temperature) values(?,?,?,?,?)";
        Object[] value=new Object[]{formatter.format(curDate),113.95890513446503,22.542813620045102,0.0,"温度升高到50摄氏度以上"};
        db.execSQL(insertStr,value);
        db.close();
    }
}
