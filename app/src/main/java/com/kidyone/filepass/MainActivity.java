package com.kidyone.filepass;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int FILE_CODE = 0;
    private TextView tvMsg;
    private EditText txtIP, txtPort, txtEt;
    private Button btnSend;
    private Handler handler;
    private SocketManager socketManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = (TextView)findViewById(R.id.tvMsg);
        txtIP = (EditText)findViewById(R.id.txtIP);
        txtPort = (EditText)findViewById(R.id.txtPort);
        txtEt = (EditText)findViewById(R.id.et);
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, (String)SPUtil.get(MainActivity.this,lastPath,Environment.getExternalStorageDirectory().getPath()));

                startActivityForResult(i, FILE_CODE);
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 0:
                        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                        txtEt.append("\n[" + format.format(new Date()) + "]" + msg.obj.toString());
                        break;
                    case 1:
                        tvMsg.setText("本机IP：" + GetIpAddress() + " 监听端口:" + msg.obj.toString());
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
       // socketManager = new SocketManager(handler);

        Thread receiveFileThread = new Thread(new FileServer());
        receiveFileThread.start();

    }

    String lastPath = "lastPath_";


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            final String ipAddress = txtIP.getText().toString();
            final int port = Integer.parseInt(txtPort.getText().toString());
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    final ArrayList<String> fileNames = new ArrayList<>();
                    final ArrayList<String> paths = new ArrayList<>();
                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            paths.add(uri.getPath());
                            fileNames.add(uri.getLastPathSegment());
                        }
                        if(GenericUtil.isEmpty(paths) || GenericUtil.isEmpty(fileNames))return;
                        SPUtil.putAndApply(MainActivity.this,lastPath,paths.get(0).replace(fileNames.get(0),""));
                        Message.obtain(handler, 0, "正在发送至" + ipAddress + ":" +  port).sendToTarget();
                        Thread sendThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
//                                socketManager.SendFile(fileNames, paths, ipAddress, port);
                                FileUtils.sendFile(paths.get(0), ipAddress, port);
                            }
                        });
                        sendThread.start();
                    }
                } else {
                    final ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);
                    final ArrayList<String> fileNames = new ArrayList<>();
                    if (!GenericUtil.isEmpty(paths)) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            paths.add(uri.getPath());
                            fileNames.add(uri.getLastPathSegment());
//                            socketManager.SendFile(fileNames, paths, ipAddress, port);
                            FileUtils.sendFile(paths.get(0), ipAddress, port);
                        }
                        if(GenericUtil.isEmpty(paths) || GenericUtil.isEmpty(fileNames))return;
                        SPUtil.putAndApply(MainActivity.this,lastPath,paths.get(0).replace(fileNames.get(0),""));
                        Message.obtain(handler, 0, "正在发送至" + ipAddress + ":" +  port).sendToTarget();
                        Thread sendThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
//                                socketManager.SendFile(fileNames, paths, ipAddress, port);
                                FileUtils.sendFile(paths.get(0), ipAddress, port);
                            }
                        });
                        sendThread.start();
                    }
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
    public String GetIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }
}
