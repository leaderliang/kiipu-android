package com.mycreat.kiipu.utils;
/**
 * 文件下载进度回调
 *
 * */
public interface ProgressCallBack {

	/**
	 * 下载进度
	 * @param current
	 * @param total
	 */
	void onProgress(long current, long total);

}
