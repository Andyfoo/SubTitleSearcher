package zimu.gui.parms;

public class DownParm {
	public static DownParm def = new DownParm();
	
	/**
	 * 文件名规则
	 * 0=与视频同名
	 * 1=与视频同名后再追加chn数字
	 */
	public int filenameType = 0;
	
	public static int filenameType_DEF = 0;
	public static int filenameType_BAT = 1;
	
	
	public boolean simplified = false;
	public String charset = "UTF-8";


	public boolean isSimplified() {
		return simplified;
	}



	public void setSimplified(boolean simplified) {
		this.simplified = simplified;
	}


	public String getCharset() {
		return charset;
	}



	public void setCharset(String charset) {
		this.charset = charset;
	}



	public int getFilenameType() {
		return filenameType;
	}



	public void setFilenameType(int filenameType) {
		this.filenameType = filenameType;
	}
	
}
