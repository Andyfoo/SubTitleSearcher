package zimu.tests;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test1 {

	public static void main(String[] args) {
		Runtime rt = Runtime.getRuntime();
		StringBuffer result = new StringBuffer();
		try {
			String upgradeCmd = "E:\\workspace\\_me\\dev\\my_tools\\SubTitleSearcher\\target\\test.bat";
			
			Process p = rt.exec("cmd /k "+upgradeCmd);

			InputStream fis = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(fis,"GBK");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line);
				result.append("\r\n");
			}
			System.out.println(result.toString());
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
