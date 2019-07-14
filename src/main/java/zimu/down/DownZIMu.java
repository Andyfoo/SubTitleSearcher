package zimu.down;

import java.io.File;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.github.stuxuhai.jpinyin.ChineseHelper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import zimu.AppConfig;
import zimu.common.sites.ShooterCommon;
import zimu.common.sites.SubHDCommon;
import zimu.common.sites.XunLeiCommon;
import zimu.common.sites.ZIMuKuCommon;
import zimu.gui.ExtractDialog;
import zimu.gui.MainWin;
import zimu.gui.parms.DownParm;
import zimu.gui.parms.MovFileInfo;
import zimu.gui.parms.SearchParm;
import zimu.util.HtHttpUtil;
import zimu.util.MyFileUtil;
import zimu.util.StringUtil;

public class DownZIMu {
	static final Log logger = LogFactory.get();
	//结果数组 
	public static JSONArray dataArr = new JSONArray();
	//搜索缓存
	private final static ConcurrentSkipListMap<String, JSONArray> searchTempMap = new ConcurrentSkipListMap<String, JSONArray>();
	
	static ExecutorService threadPool = Executors.newFixedThreadPool(20);

	/**
	 * 搜索字幕
	 * @param searchParm
	 * @return
	 */
	public static boolean searchList(SearchParm searchParm) {
		if (MovFileInfo.movFilename == null || !(new File(MovFileInfo.movFilename)).exists()) {
			return false;
		}
		clear();
		String filenameStr = StringUtil.basename(MovFileInfo.movFilename);
		String filenameBase = filenameStr.contains(".") ? filenameStr.substring(0, filenameStr.lastIndexOf(".")) : filenameStr;
		JSONArray list;
		

		// 射手
		if (searchParm.from_sheshou) {
			threadPool.submit(new Runnable() {
				public void run() {
					try {
						JSONArray list = ShooterCommon.DownList(MovFileInfo.movFilename);
						if(list!=null) {
							searchTempMap.put("sheshou", list);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
		}
		// 迅雷
		if (searchParm.from_xunlei) {
			threadPool.submit(new Runnable() {
				public void run() {
					try {
						JSONArray list = XunLeiCommon.DownList(MovFileInfo.movFilename);
						if(list!=null) {
							searchTempMap.put("xunlei", list);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
		}
		// 字幕库
		if (searchParm.from_zimuku) {
			threadPool.submit(new Runnable() {
				public void run() {
					try {
						JSONArray list = ZIMuKuCommon.DownList(StringUtil.basename(MovFileInfo.movFilename));
						if(list!=null) {
							searchTempMap.put("zimuku", list);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
		}
		if (searchParm.from_subhd) {
			threadPool.submit(new Runnable() {
				public void run() {
					try {
						JSONArray list = SubHDCommon.DownList(StringUtil.basename(MovFileInfo.movFilename));
						if(list!=null) {
							searchTempMap.put("subhd", list);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
			});
		}

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			logger.error("", e);
		}
		ThreadPoolExecutor es = (ThreadPoolExecutor)threadPool;
		while(es.getActiveCount() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}
		// 射手
		list = searchTempMap.get("sheshou");
		if(list!=null&&list.size()>0) {
			// subTable.set
			// System.out.println(list);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					JSONObject row = list.getJSONObject(i);
					JSONObject fileRow = row.getJSONArray("Files").getJSONObject(0);
					String key = DigestUtil.md5Hex(row.toString());
					String rate = "-";
					addRow(key, filenameBase + "." + fileRow.getStr("Ext"), rate, row, "射手网");
				}
			}
		}
		
		// 迅雷
		list = searchTempMap.get("xunlei");
		if(list!=null&&list.size()>0) {
			try {
				// subTable.set
				// System.out.println(list);
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						JSONObject row = list.getJSONObject(i);
						if (row == null || row.size() == 0 || row.isNull("surl")) {
							continue;
						}
						row.put("Ext", row.getStr("surl").substring(row.getStr("surl").lastIndexOf(".") + 1));
						String key = DigestUtil.md5Hex(row.toString());
						String title = row.getStr("sname");
						if (StrUtil.isEmpty(title)) {
							title = filenameBase + "." + row.getStr("Ext");

						}
						String rate = StrUtil.isNotBlank(row.getStr("rate")) ? row.getStr("rate") + "星" : "-";
						addRow(key, title, rate, row, "迅雷");
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
		// 字幕库
		list = searchTempMap.get("zimuku");
		if(list!=null&&list.size()>0) {
			try {
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						JSONObject row = list.getJSONObject(i);

						row.put("Ext", row.getStr("ext"));
						String key = DigestUtil.md5Hex(row.toString());
						String title = row.getStr("title");
						;

						String rate = StrUtil.isNotBlank(row.getStr("rate")) ? row.getStr("rate") : "-";
						addRow(key, title, rate, row, "字幕库");
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
		// SubHD
		list = searchTempMap.get("subhd");
		if(list!=null&&list.size()>0) {
			try {
				list = SubHDCommon.DownList(StringUtil.basename(MovFileInfo.movFilename));
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						JSONObject row = list.getJSONObject(i);

						row.put("Ext", row.getStr("ext"));
						String key = DigestUtil.md5Hex(row.toString());
						String title = String.format("%s [%s][%s][下载次数:%s]", row.getStr("title"), row.getStr("ext"), row.getStr("lang"), row.getStr("downCount"));

						String rate = StrUtil.isNotBlank(row.getStr("rate")) ? row.getStr("rate") : "-";
						addRow(key, title, rate, row, "SubHD");
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
		return true;
	}

	public static void clear() {
		searchTempMap.clear();
		dataArr.clear();
	}

	public static void addRow(String key, String title, String rate, JSONObject data, String from) {
		JSONObject dataRow = new JSONObject();
		dataRow.put("key", key);
		dataRow.put("title", title);
		dataRow.put("rate", rate);
		dataRow.put("data", data);
		dataRow.put("from", from);
		dataArr.add(dataRow);
	}
	
	/**
	 * 下载字幕
	 * @param index
	 * @param downParm
	 * @return
	 */
	public static boolean down(int index, DownParm downParm) {
		JSONObject row = dataArr.getJSONObject(index);
		if (row == null) {
			// MainWin.alert("数据错误");
			logger.error("数据错误");
			return false;
		}
		String from = row.getStr("from");
		JSONObject data = row.getJSONObject("data");

		String filepath = StringUtil.dirname(MovFileInfo.movFilename);
		String filenameStr = StringUtil.basename(MovFileInfo.movFilename);
		String filenameBase = filenameStr.substring(0, filenameStr.lastIndexOf("."));
		boolean downResult = false;
		if (from.equals("射手网")) {
			JSONObject fileRow = data.getJSONArray("Files").getJSONObject(0);
			String link = fileRow.getStr("Link");
			downResult = downAndSave(index, row,fileRow, filepath + filenameBase, link, downParm);
		} else if (from.equals("迅雷")) {
			JSONObject fileRow = data;
			String link = fileRow.getStr("surl");
			downResult = downAndSave(index, row,fileRow,  filepath + filenameBase, link, downParm);
		} else if (from.equals("字幕库")) {
			JSONObject fileRow = data;
			String link = fileRow.getStr("url");
			downResult = downAndSave_ZiMuKu(index, row,fileRow,  filepath + filenameBase, link, downParm);
		} else if (from.equals("SubHD")) {
			JSONObject fileRow = data;
			String link = fileRow.getStr("url");
			downResult = downAndSave_SubHD(index, row,fileRow,  filepath + filenameBase, link, downParm);
		}

		if (!downResult) {
			logger.error("下载失败");
		}
		return downResult;

	}

	/**
	 * 字幕库
	 * 
	 * @param row
	 * @param filename
	 * @param url
	 * @param charset
	 * @param simplified
	 * @return
	 */
	public static boolean downAndSave_ZiMuKu(int index, JSONObject row,JSONObject fileRow,  String filename, String url, DownParm downParm) {
		try {
			JSONObject zimuContent = ZIMuKuCommon.downContent(url);
			if (zimuContent == null)
				return false;
			String ext = zimuContent.getStr("ext");

			byte[] data = Base64.decode(zimuContent.getStr("data"));
			// *.srt、*.ass、*.ssa、*.zip、*.rar、*.7z
			if (data == null || data.length < 100) {
				return false;
			}
			logger.info("save=" + filename);
			if (ext.equals("zip") || ext.equals("rar") || ext.equals("7z")) {
				new ExtractDialog(MainWin.frame, index, downParm, row.getJSONObject("data").getStr("title"),ext,filename, data).setVisible(true);
				return true;
			} else if (ArrayUtil.contains(AppConfig.subExtNames, ext.toLowerCase())) {
				// Vector<Object> colData = MainWin.getSubTableData(row.getStr("key"));
				// String charset = (String)colData.get(5);

				if(downParm.filenameType == DownParm.filenameType_BAT) {
					filename += (".chn" + (index+1)) + "." + ext;
				}else {
					filename += "." + ext;
				}
				if (downParm.simplified) {
					String dataStr = new String(data, downParm.charset);
					dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
					MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
				} else {
					MyFileUtil.fileWriteBin(filename, data);
				}
			} else {
				return false;
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;

	}

	/**
	 * SubHD
	 * 
	 * @param row
	 * @param filename
	 * @param url
	 * @param charset
	 * @param simplified
	 * @return
	 */
	public static boolean downAndSave_SubHD(int index, JSONObject row, JSONObject fileRow, String filename, String url, DownParm downParm) {
		try {
			JSONObject zimuContent = SubHDCommon.downContent(url);
			if (zimuContent == null)
				return false;
			String ext = zimuContent.getStr("ext");

			byte[] data = Base64.decode(zimuContent.getStr("data"));
			// *.srt、*.ass、*.ssa、*.zip、*.rar、*.7z
			if (data == null || data.length < 100) {
				return false;
			}
			logger.info("save=" + filename);
			if (ext.equals("zip") || ext.equals("rar") || ext.equals("7z")) {
				new ExtractDialog(MainWin.frame, index, downParm, row.getJSONObject("data").getStr("title"),ext,filename, data).setVisible(true);
				return true;
			} else if (ArrayUtil.contains(AppConfig.subExtNames, ext.toLowerCase())) {
				// Vector<Object> colData = MainWin.getSubTableData(row.getStr("key"));
				// String charset = (String)colData.get(5);

				if(downParm.filenameType == DownParm.filenameType_BAT) {
					filename += (".chn" + (index+1)) + "." + ext;
				}else {
					filename += "." + ext;
				}
				if (downParm.simplified) {
					String dataStr = new String(data, downParm.charset);
					dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
					MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
				} else {
					MyFileUtil.fileWriteBin(filename, data);
				}
			} else {
				return false;
			}

			return true;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;

	}

	/**
	 * 射手、迅雷
	 * 
	 * @param row
	 * @param filename
	 * @param url
	 * @param charset
	 * @param simplified
	 * @return
	 */
	public static boolean downAndSave(int index, JSONObject row,JSONObject fileRow,  String filename, String url, DownParm downParm) {
		try {
			byte[] data = HtHttpUtil.http.getBytes(url, null, url);
			if (data == null || data.length < 100) {
				return false;
			}
			if(downParm.filenameType == DownParm.filenameType_BAT) {
				filename += (".chn" + (index+1)) + "." + fileRow.getStr("Ext");
			}else {
				filename += "." + fileRow.getStr("Ext");
			}
			
			
			logger.info("save=" + filename);
			// Vector<Object> colData = MainWin.getSubTableData(row.getStr("key"));
			// String charset = (String)colData.get(5);
			if (downParm.simplified) {
				String dataStr = new String(data, downParm.charset);
				dataStr = ChineseHelper.convertToSimplifiedChinese(dataStr);
				MyFileUtil.fileWrite(filename, dataStr, "UTF-8", "");
			} else {
				MyFileUtil.fileWriteBin(filename, data);
			}
			return true;
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}


	public static void main(String[] args) {
		MovFileInfo.movFilename = "D:/_win10/Users/FH/Documents/downsizing.2017.720p.bluray.x264-geckos.mkv";
		SearchParm searchParm = new SearchParm();
		searchParm.from_sheshou = true;
		searchParm.from_subhd = true;
		searchParm.from_xunlei = true;
		searchParm.from_zimuku = true;
		searchList(searchParm);
		System.out.println(dataArr);

	}

}
