package com.randioo.doudizhu_server.util;

import java.io.FileWriter;
import java.io.IOException;

import com.randioo.randioo_server_base.utils.StringUtils;

public class Write2File {

	public static void main(String[] args) {
		write2File(1, "hello");
		write2File(1, "world");
	}

	public static void write2File(int roleId, String str) {
		str += StringUtils.lineSplit;
		FileWriter writer = null;

		try {
			writer = new FileWriter("log" + StringUtils.fileSplit + roleId + ".txt", true);
			writer.write(str);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
