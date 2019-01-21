package zimu.util;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;


/**
 * 分组线程池工具类
 *
 */
public class ThreadPoolGroupUtil {
	static final Log logger = LogFactory.get();
	
	private static ConcurrentSkipListMap<String, ExecutorService> esMap = new ConcurrentSkipListMap<String, ExecutorService>();
	/**
	 * 添加指定类型的线程池
	 * @param type
	 * @param nThreads
	 */
	public static void addGroup(String groupName, int nThreads){
		if(esMap.containsKey(groupName)){
			return;
		}
		logger.info(String.format("add thread pool group: groupName=%s, nThreads=%d", groupName, nThreads));
		esMap.put(groupName, Executors.newFixedThreadPool(nThreads));
	}
	/**
	 * 添加线程
	 * @param groupName
	 * @param task
	 */
	public static void addThread(String groupName, Runnable task){
		addGroup(groupName, 20);
		logger.info(String.format("add thread to group: %s", groupName));
		esMap.get(groupName).submit(task);
	}
	
	/**
	 * 清除所有任务
	 */
	public static void clear(){
		logger.info("clear all thread pool group");
		Set<String> keys = esMap.keySet();
		for(String key : keys){
			ExecutorService es = esMap.get(key);
			es.shutdownNow();
			esMap.remove(key);
		}
	}
	/**
	 * 清除指定任务
	 */
	public static void clear(String type){
		logger.info(String.format("clear thread pool group: %s", type));
		ExecutorService es = esMap.get(type);
		if(es!=null){
			es.shutdownNow();
			esMap.remove(type);
		}
	}
	/**
	 * 开启监控
	 */
	public static void monitor(){
		monitor(5000);
	}
	public static void monitor(final int time){
		new Thread("THREAD_POOL_GROUP_MONITOR"){
			public void run(){
				try {
					logger.info("monitor thread pool group");
					while(true){
						Set<String> keys = esMap.keySet();
						logger.info(String.format("thread pool group num：%d", keys.size()));
						for(String key : keys){
							ThreadPoolExecutor es = (ThreadPoolExecutor)esMap.get(key);
							//System.out.println(JSON.toJSON(es));
							logger.info(String.format("thread pool group(%s)：maxPool=%d, pool=%d, active=%d, task=%d, completedTask=%d", key, 
									es.getMaximumPoolSize(),es.getPoolSize(), es.getActiveCount(), es.getTaskCount(), es.getCompletedTaskCount()));
						}
						try {
							sleep(time);
						} catch (InterruptedException e) {
							logger.error("", e);
						}
					}
				} catch (Exception e) {
					logger.error("", e);
				}
				
			}
		}.start();
		
	}
	public static void sleep(int n){
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}


	public static void main(String[] args){
		addThread("task1", new Thread() {
			public void run() {
				while (!Thread.interrupted()) {
					System.out.println("beat");
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
		});
		addThread("task1", new Thread() {
			public void run() {
				while (!Thread.interrupted()) {
					System.out.println("beat2222");
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
		});
		addThread("task1", new Thread() {
			public void run() {
				System.out.println("beat3333");
			}
		});
		
		addThread("task2", new Thread() {
			public void run() {
				try{
					while (!Thread.interrupted()) {
						System.out.println("22222");
						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
					}
				}catch(Exception e){
					
				}
			}
		});	
		monitor();
		//sleep(5000);
		//clear();
		
	}

}
