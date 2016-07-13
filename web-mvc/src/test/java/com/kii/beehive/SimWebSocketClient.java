package com.kii.beehive;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by hdchen on 7/12/16.
 */
public class SimWebSocketClient {
	public static void main(String[] argu) throws Exception {
		//Step 1: create socket connection
		Socket socket = new Socket("echo.websocket.org", 80);

		//Step 2: Send request to switch protocol from HTTP to WebSocket
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		pw.print("GET ws://echo.websocket.org/?encoding=text HTTP/1.1\r\n");
		pw.print("Host: echo.websocket.org\r\n");
		pw.print("Connection: Upgrade\r\n");
		pw.print("Upgrade: websocket\r\n");
		pw.print("Origin: http://www.websocket.org\r\n");
		pw.print("Sec-WebSocket-Version: 13\r\n");
		pw.print("Sec-WebSocket-Key: qf1exVnNFZ8PIqnlEgQLYw==\r\n");
		pw.print("Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits\r\n");
		pw.print("\r\n");
		pw.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		//Step 3: Receive response which indicates the HTTP protocol is replaced with WebSocket
		String t;
		while ((t = br.readLine()) != null && !t.isEmpty()) System.out.println(t);

		//Step 4: Generate the first data frame
		ByteBuffer buffer = ByteBuffer.allocate(32);
		buffer.put((byte) 0x81);
		String message = "AKB";
		buffer.put((byte) (0b10000000 | message.length()));
		byte[] key = new byte[]{(byte) 0x12, 0x31, 0x62, 0x77};
		for (byte b : key) {
			buffer.put(b);
		}
		byte[] byteData = message.getBytes();
		for (int i = 0; i < byteData.length; ++i) {
			int j = i % 4;
			buffer.put((byte) (byteData[i] ^ key[j]));
		}

		//Step 5: Send the data frame
		socket.getOutputStream().write(buffer.array());
		socket.getOutputStream().flush();

		//Step 6: Receive data frame from server
		byte[] receive = new byte[1024];
		int len = socket.getInputStream().read(receive);
		System.out.println("receive " + len + " bytes");

		//Step 7: decode the data frame
		if (1 == (receive[0] & 0xFF) >> 7) {
			System.out.println("This is the last frame");
		}
		int payLoadLen = receive[1] & 0x7F;
		boolean hasMask = 1 == ((receive[1] & 0xFF) >> 7);
		System.out.println("has mask = " + hasMask);
		if (!hasMask) {
			System.out.println("receive = " + new String(receive, 2, payLoadLen));
		}
		br.close();
		System.out.println("done");
	}
}