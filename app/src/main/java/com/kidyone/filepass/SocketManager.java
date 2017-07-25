package com.kidyone.filepass;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketManager {
	private ServerSocket server;
	private Handler handler = null;
	public SocketManager(Handler handler){
		this.handler = handler;
		int port = 9999;
		while(port > 9000){
			try {
				server = new ServerSocket(port);
				break;
			} catch (Exception e) {
				port--;
			}
		}
		SendMessage(1, port);
		Thread receiveFileThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					ReceiveFile();
				}
			}
		});
		receiveFileThread.start();
	}
	void SendMessage(int what, Object obj){
		if (handler != null){
			Message.obtain(handler, what, obj).sendToTarget();
		}
	}

	void ReceiveFile(){
		try{

//			Socket name = server.accept();
//			InputStream nameStream = name.getInputStream();
//			InputStreamReader streamReader = new InputStreamReader(nameStream);
//			BufferedReader br = new BufferedReader(streamReader);
//			String fileName = br.readLine();
//			br.close();
//			streamReader.close();
//			nameStream.close();
//			name.close();
//			SendMessage(0, "正在接收:" + fileName);

			Socket data = server.accept();
			InputStream dataStream = data.getInputStream();
			String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis();
			System.out.println(savePath);
			SendMessage(0,"路径:"+savePath);
			FileOutputStream file = new FileOutputStream(savePath, false);
			byte[] buffer = new byte[1024];
			int size = -1;
			while ((size = dataStream.read(buffer)) != -1){
				file.write(buffer, 0 ,size);
			}
			file.close();
			dataStream.close();
			data.close();
			SendMessage(0, "接收完成");
		}catch(Exception e){
			SendMessage(0, "接收错误:\n" + e.getMessage());
		}
	}

	public void sendFile(String ipAddress, String path, int port) {
		FileUtils.sendFile(path, ipAddress, port);
	}
}
