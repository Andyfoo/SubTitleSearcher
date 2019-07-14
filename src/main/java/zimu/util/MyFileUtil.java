package zimu.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * 
 *         功能说明：请补充
 */
public class MyFileUtil {
	static final Log logger = LogFactory.get();

	/**
	 * 获取指定目录文件列表。
	 */
	public static ArrayList<File> getFileList(String filePath) {
		return getFileList(filePath, false);
	}
	public static ArrayList<File> getFileList(String filePath, boolean sub) {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		return getFileList(file, sub);
	}

	private static ArrayList<File> getFileList(File file, boolean sub) {
		File[] files = file.listFiles();
		ArrayList<File> r = new ArrayList<File>();
		for (File f : files) {
			if (f.isDirectory() && sub) {
				r.addAll(getFileList(f, sub));
			} else if (f.isFile()) {
				r.add(f);
			}
		}
		return r;
	}

	/**
	 * 获取指定目录子目录列表。
	 */
	public static ArrayList<File> getPathList(String filePath) {
		File file = new File(filePath);
		if (!file.exists())
			return null;
		return getPathList(file);
	}

	private static ArrayList<File> getPathList(File file) {
		File[] files = file.listFiles();
		ArrayList<File> r = new ArrayList<File>();
		for (File f : files) {
			if (f.isDirectory()) {
				r.add(f);
			} else if (f.isFile()) {

			}
		}
		return r;
	}

	/*
	 * isExists
	 */
	public static boolean isExists(String filename) {
		try {
			/**//* 查找文件，如果不存在，就创建 */
			File file = new File(filename);
			return file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除此路径名表示的文件或目录。 如果此路径名表示一个目录，则会先删除目录下的内容再将目录删除，所以该操作不是原子性的。 如果目录中还有目录，则会引发递归动作。
	 * 
	 * @param filePath
	 *                要删除文件或目录的路径。
	 * @return 当且仅当成功删除文件或目录时，返回 true；否则返回 false。
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists())
			return false;
		return deleteFile(file);
	}

	private static boolean deleteFile(File file) {
		File[] files = file.listFiles();
		for (File deleteFile : files) {
			if (deleteFile.isDirectory()) {
				// 如果是文件夹，则递归删除下面的文件后再删除该文件夹
				if (!deleteFile(deleteFile)) {
					// 如果失败则返回
					return false;
				}
			} else {
				if (!deleteFile.delete()) {
					// 如果失败则返回
					return false;
				}
			}
		}
		return file.delete();
	}

	// 取文件夹大小
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计
			}
		}
		return dirSize;
	}

	/*
	 * FileCreate
	 */
	public static void fileCreate(String filename) {
		try {
			/**//* 查找文件，如果不存在，就创建 */
			File file = new File(filename);
			if (!file.exists())
				if (!file.createNewFile())
					throw new Exception("文件不存在，创建失败！");

		} catch (Exception e) {
			logger.error(e);
		}
	}

	/*
	 * DirCreate
	 */
	public static void dirCreate(String filepath) {
		try {
			/**//* 查找目录，如果不存在，就创建 */
			File dirFile = new File(filepath);
			if (!dirFile.exists()) {
				if (!dirFile.mkdir())
					throw new Exception("目录不存在，创建失败！");
			}

		} catch (Exception e) {
			logger.error( e);
		}
	}

	public static void dirCreate(String filepath, boolean sub) {
		if (sub) {
			filepath = filepath.replaceAll("\\\\", "/");
			String arr[] = filepath.split("\\/");
			String path = "";
			for (String dir : arr) {
				path += dir + "/";
				dirCreate(path);
			}

		} else {
			dirCreate(filepath);
		}
	}

	/*
	 * Write a TXT file
	 */
	public static boolean fileWrite(String filename, String str) {
		return MyFileUtil.fileWrite(filename, str, "UTF-8", "");
	}

	public static boolean fileWrite(String filename, String str, String mode) {
		return fileWrite(filename, str, "UTF-8", mode);
	}

	public static boolean fileWrite(String filename, String str, String encode, String mode) {
		return fileWrite(new File(filename), str, encode, mode);
	}

	public static boolean fileWrite(File file, String str) {
		return MyFileUtil.fileWrite(file, str, "UTF-8", "");
	}

	public static boolean fileWrite(File file, String str, String mode) {
		return fileWrite(file, str, "UTF-8", mode);
	}

	public static boolean fileWrite(File file, String str, String encode, String mode) {
		try {
			OutputStreamWriter fw;
			PrintWriter pw;
			if (mode.equals("a+")) {
				fw = new OutputStreamWriter(new FileOutputStream(file, true), encode);
				pw = new java.io.PrintWriter(fw);
				pw.print(str);
			} else {
				fw = new OutputStreamWriter(new FileOutputStream(file, false), encode);
				pw = new java.io.PrintWriter(fw);
				pw.print(str);
			}
			pw.close();
			fw.close();
			return true;
		} catch (Exception e) {
			logger.error( e);
			return false;
		}
	}

	public static boolean fileWriteBin(String filename, byte[] buf) {
		return fileWriteBin(new File(filename), buf);
	}

	public static boolean fileWriteBin(File file, byte[] buf) {
		try {
			if (!file.exists()) {
				file.createNewFile(); // 如果文件不存在，则创建
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buf);
			fos.close();
			return true;
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
	}

	/*
	 * 读取文件，原始数据，速度：中 2M文件读取10次:2.328秒
	 */
	public static String fileRead(String filename) {
		return fileRead(filename, "UTF-8");
	}

	public static String fileRead(File file) {
		return fileRead(file, "UTF-8");
	}

	public static String fileRead(String filename, String encode) {
		return fileRead(new File(filename), encode);
	}

	public static String fileRead(File file, String encode) {
		if (!file.exists()) {
			return null;
		}
		try {
			return fileRead(new FileInputStream(file), encode);
		} catch (FileNotFoundException e) {
			logger.error(e);
			return null;
		}
	}

	public static String fileRead(FileInputStream file, String encode) {
		int ch;
		StringBuffer strb = new StringBuffer();
		try {
			BufferedReader fw = new BufferedReader(new InputStreamReader(file, encode));
			while ((ch = fw.read()) > -1) {
				strb.append((char) ch);
			}
			fw.close();
		} catch (Exception e) {
			logger.error( e);
		}
		return strb.toString();
	}

	/*
	 * Read a TXT file
	 */
	public static String[] fileReadArray(String filename) {
		try {
			return MyFileUtil.fileRead(filename).split("\n");
		} catch (Exception e) {
			logger.error( e);
		}
		return new String[0];
	}

	/*
	 * 读取文件，以\n连接，速度：快 2M文件读取10次:1.406秒
	 */
	public static String fileReadLine(String filename, String encode) {
		String s;
		StringBuffer strb = new StringBuffer();
		try {
			BufferedReader brIn = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encode));
			while ((s = brIn.readLine()) != null) {
				strb.append(s);
				strb.append("\n");
			}
			brIn.close();
		} catch (Exception e) {
			logger.error(e);
		}
		return strb.toString();
	}

	/**
	 * 读文件到字节数组
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static byte[] fileReadBin(String filePath) {
		return fileReadBin(new File(filePath));
	}

	public static byte[] fileReadBin(File file) {
		try {
			if (file.exists() && file.isFile()) {
				long fileLength = file.length();
				if (fileLength > 0L) {
					BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
					byte[] b = new byte[(int) fileLength];
					while (fis.read(b) != -1) {
					}
					fis.close();
					fis = null;

					return b;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error( e);
		}
		return null;
	}

	public static BufferedInputStream getStream(String filePath) {
		File file = new File(filePath);
		return getStream(file);
	}

	public static BufferedInputStream getStream(File file) {
		try {
			if (file.exists() && file.isFile()) {
				return new BufferedInputStream(new FileInputStream(file));
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error( e);
		}
		return null;
	}

}
