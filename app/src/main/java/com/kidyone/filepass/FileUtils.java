package com.kidyone.filepass;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**文件工具类*/
public class FileUtils {

	/**传输文件 */
	public static void sendFile(String filePath, String ip, int port){
		Socket socket = null;
		BufferedInputStream bis = null;
		try {
			//创建socket
			socket = new Socket(ip, port);
			socket.setSoTimeout(3000);
			//socket输入流
			InputStream is = socket.getInputStream();
			//socket输出流
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			
			//获取文件名
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			File file = new File(filePath);
			
			//读取文件输入流
			bis = new BufferedInputStream(new FileInputStream(file));
			
			//输出文件名
			bos.write(fileName.getBytes());
			bos.flush();
			
			//接受标志
			is.read();
			
			int len = 0;
			byte[] bys = new byte[1024 * 8];
			while((len = bis.read(bys)) != -1){
				bos.write(bys, 0, len);
				bos.flush();
			}
			socket.shutdownOutput();
			is.close();
			bos.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bis != null){
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
