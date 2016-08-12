package com.example.nanchen.calendarviewdemo.view;

/**
 * 选中日期的监听事件
 *
 * @author nanchen
 * @date   2016-08-11 09:14:21
 */
public interface ClickDataListener {
	/**
	 * 点击日期day的点击事件监听
	 * @param year    年
	 * @param month   月
	 * @param day     日
     */
	void onClickData(int year, int month, int day);
}
