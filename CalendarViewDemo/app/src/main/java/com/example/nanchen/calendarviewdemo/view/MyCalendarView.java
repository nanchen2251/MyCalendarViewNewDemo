package com.example.nanchen.calendarviewdemo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.nanchen.calendarviewdemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * 自定义的日历控件
 *
 * @author nanchen
 * @date   2016-08-11 09:44:27
 */
public class MyCalendarView extends LinearLayout implements OnClickListener {

	private final String TAG = MyCalendarView.class.getSimpleName();
	private int year_c = 0;// 今天的年份
	private int month_c = 0;// 今天的月份
	private int day_c = 0;// 今天的日期
	private String currentDate = "";//当前日期
	private Context mContext;
	private TextView currentMonth;// 显示日期
	private ImageView prevMonth;// 去上一个月
	private ImageView nextMonth;// 去下一个月
	private int gvFlag = 0;
	private GestureDetector gestureDetector = null;//用户手势检测
	private CalendarAdapter adapter = null;//适配器
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private ClickDataListener clickDataListener;

	public MyCalendarView(Context context) {
		this(context, null);
	}

	public MyCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	/**
	 * 初始化布局
	 */
	private void initView() {
		//加载布局，一个头布局，一个横向的星期布局，下面一个ViewFlipper用于滑动
		View view = View.inflate(mContext, R.layout.calen_calendar, this);
		currentMonth = (TextView) view.findViewById(R.id.currentMonth);
		prevMonth = (ImageView) view.findViewById(R.id.prevMonth);
		nextMonth = (ImageView) view.findViewById(R.id.nextMonth);
		setListener();
		setCurrentDay();
		gestureDetector = new GestureDetector(mContext, new MyGestureListener());
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		adapter = new CalendarAdapter(mContext, getResources(), jumpMonth,
				jumpYear, year_c, month_c, day_c);
		addGridView();
		gridView.setAdapter(adapter);
		flipper.addView(gridView, 0);
		addTextToTopTextView(currentMonth);
	}

	private void setListener() {
		prevMonth.setOnClickListener(this);
		nextMonth.setOnClickListener(this);

	}

	public void setClickDataListener(ClickDataListener clickDataListener) {
		this.clickDataListener = clickDataListener;
	}

	private void setCurrentDay() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(FormatDate.DATE_FORMAT, Locale.CHINA);
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
	}

	/**
	 * 移动到下一个月
	 * 
	 * @param gvFlag
	 */
	private void enterNextMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth++; // 下一个月
		adapter = new CalendarAdapter(mContext, this.getResources(), jumpMonth,
				jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(adapter);
		addTextToTopTextView(currentMonth); // 移动到下一月后，将当月显示在头标题中
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		flipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_left_out));
		flipper.showNext();
		flipper.removeViewAt(0);
	}

	/**
	 * 移动到上一个月
	 * 
	 * @param gvFlag
	 */
	private void enterPrevMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth--; // 上一个月

		adapter = new CalendarAdapter(mContext, this.getResources(), jumpMonth,
				jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(adapter);
		gvFlag++;
		addTextToTopTextView(currentMonth); // 移动到上一月后，将当月显示在头标题中
		flipper.addView(gridView, gvFlag);

		flipper.setInAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_right_out));
		flipper.showPrevious();
		flipper.removeViewAt(0);
	}

	/**
	 * 添加头部的年份 闰哪月等信息
	 * 
	 * @param view
	 */
	private void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		// draw = getResources().getDrawable(R.drawable.top_day);
		// view.setBackgroundDrawable(draw);
		textDate.append(adapter.getShowYear()).append("年")
				.append(adapter.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
	}

	private void addGridView() {
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(mContext);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(40);
		// gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if (Width == 720 && Height == 1280) {
			gridView.setColumnWidth(40);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 去除gridView边框
		gridView.setVerticalSpacing(0);
		gridView.setHorizontalSpacing(0);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				int startPosition = adapter.getStartPositon();
				int endPosition = adapter.getEndPosition();
				if (startPosition <= position + 7
						&& position <= endPosition - 7) {
					int scheduleDay = Integer.parseInt(adapter.getDateByClickItem(position)
							.split("\\.")[0]); // 这一天的阳历
					int scheduleYear = Integer.parseInt(adapter.getShowYear());
					int scheduleMonth = Integer.parseInt(adapter.getShowMonth());
					((CalendarAdapter) arg0.getAdapter())
							.setColorDataPosition(position);
					if (clickDataListener != null) {
						clickDataListener.onClickData(scheduleYear,
								scheduleMonth, scheduleDay);
					}
				}
			}
		});
		gridView.setLayoutParams(params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextMonth: // 下一个月
			enterNextMonth(gvFlag);
			Log.d(TAG, "gvFlag=" + gvFlag);
			break;
		case R.id.prevMonth: // 上一个月
			enterPrevMonth(gvFlag);
			break;

		}

	}

	private static class MyGestureListener extends SimpleOnGestureListener {
		private MyCalendarView calendarView;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 像左滑动
				calendarView.enterNextMonth(gvFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				calendarView.enterPrevMonth(gvFlag);
				return true;
			}
			return false;
		}
	}
}
