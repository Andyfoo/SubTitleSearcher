package zimu.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;


/**
 * UI线程工具类
 *
 */
public class UIThreadUtil {
	static final Log logger = LogFactory.get();
	
	/**
	 * 把可运行的对象放入队列后就返回
	 */
	public static void addInvokeLater(Runnable runx){
		SwingUtilities.invokeLater(runx);
	}
	/**
	 * 一直等待直到提交的run方法执行完毕后才返回
	 * @param groupName
	 * @param task
	 */
	public static void addInvokeAndWait(Runnable runx){
		try {
			SwingUtilities.invokeAndWait(runx);
		} catch (InvocationTargetException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
	
	

	public static void main(String[] args){
		addInvokeAndWait(new Runnable() {
			public void run() {
				System.out.println("1111");
			}
		});
		addInvokeAndWait(new Runnable() {
			public void run() {
				System.out.println("2222");
			}
		});
		
		
	}

}
