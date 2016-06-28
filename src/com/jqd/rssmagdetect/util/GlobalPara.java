package com.jqd.rssmagdetect.util;


/**
 * @author jiangqideng@163.com
 * @date 2016-6-28 下午7:21:29
 * @description 几个公共的变量，其实后来发现没什么这个没什么用，以后如果有实际控制的需求会方便点
 */
public class GlobalPara {
	public long timeOfStartScan=0;
	public long timeSinceStart=0;
	public int position_index=1;
	
	private volatile static GlobalPara globalPara = null;
	public static GlobalPara getInstance() {
		if (globalPara == null) {
			synchronized (GlobalPara.class) {
				if (globalPara == null) {
					globalPara = new GlobalPara();
				}
			}
		}
		return globalPara;
	}
}
