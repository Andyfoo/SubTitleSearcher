package zimu.util;

import java.io.File;

public class StringUtil {
	public static String getPrintSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			// 因为如果以MB为单位的话，要保留最后1位小数，
			// 因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
		}
	}
	/**
	 * 返回字符串的文件名，过滤目录字符
	 * 
	 * @param str
	 *                字符串
	 * 
	 * @return 字符串
	 */
	public static String basename(String str) {
		int pos = str.lastIndexOf('/');
		int pos2 = str.lastIndexOf('\\');
		if (str.length() == 0)
			return "";
		if (pos > pos2) {
			return str.substring(pos + 1);
		} else {
			return str.substring(pos2 + 1);
		}
	}

	/**
	 * 返回字符串的目录，过滤文件名字符
	 * 
	 * @param str
	 *                字符串
	 * 
	 * @return 字符串
	 */
	public static String dirname(String str) {
		int pos = str.lastIndexOf('/');
		int pos2 = str.lastIndexOf('\\');
		if (str.length() == 0)
			return "";
		if (pos > pos2) {
			return str.substring(0, pos + 1);
		} else {
			return str.substring(0, pos2 + 1);
		}
	}
	
	/**
	 * 获得文件的扩展名，扩展名不带“.”
	 * 
	 * @param fileName 文件名
	 * @return 扩展名
	 */
	public static String extName(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if (pos>-1) {
			return fileName.substring(pos + 1);
		} else {
			return "";
		}
	}
	public static String extName(File file) {
		return extName(file.getName());
	}

	public static void main(String[] args) {

	}

}
