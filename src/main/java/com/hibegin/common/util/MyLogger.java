package com.hibegin.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class MyLogger {
	private Log logger;
	public MyLogger(Class<?> clazz) {
		logger = LogFactory.get(clazz);
	}
	public static MyLogger getLogger(Class<?> clazz) {
		return new MyLogger(clazz);
	}
	public void info(String msg) {
		logger.info(msg);
	}
	public void warning(String msg) {
		logger.warn(msg);
	}
	public void debug(String msg) {
		logger.info(msg);
	}
	public void error(String msg, Throwable thrown) {
		logger.error(thrown, msg);
	}
	public void log(Level level, String msg, Throwable thrown) {
		logger.info(thrown, msg);
	}
	public void log(Level level, String msg) {
		logger.info(msg);
	}
	
	/**
	 * 记录完善的异常日志信息(包括堆栈信息)
	 *
	 * @param e
	 *                Exception
	 */
	public static String recordStackTraceMsg(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		e.printStackTrace(writer);
		StringBuffer buffer = stringWriter.getBuffer();
		return buffer.toString();
	}
}
