package com.only.touch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScreenView extends View implements Runnable {
	private static final String TAG = "BlueOceanScreenView";
	private String msg = "";
	private float tx;
	private float ty;
	private float radius = 30;
	private float type = Position.TYPE_OTHERS;
	private boolean isCircle = false;
	private boolean drawable = false;
	private Paint areaPaint;
	private Paint infoPaint;
	private Rect rect;
	public List<Position> posList;

	public ScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		areaPaint = new Paint();
		areaPaint.setColor(Color.RED);
		infoPaint = new Paint();
		infoPaint.setColor(Color.RED);
		posList = new ArrayList<Position>();
		rect = new Rect();
		// TODO Auto-generated constructor stub
		new Thread(this).start();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		drawTouchArea(canvas);
		drawInfo(canvas);
		drawCurrentArea(canvas);
	}
	
	private void drawTouchArea(Canvas canvas) {
		if (posList == null) return;
		for (int i = 0; i < posList.size(); i++) {
			Position bop = posList.get(i);
			areaPaint.setColor(bop.color);
			if (bop.r == 0)
				canvas.drawCircle(bop.x, bop.y, 30, areaPaint);
			else 
				canvas.drawCircle(bop.x, bop.y, bop.r, areaPaint);
		}
	}
	
	private void drawCurrentArea(Canvas canvas) {
		if (!drawable) return;
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		canvas.drawCircle(tx, ty, radius, paint);
	}
	
	public void setColor(int color) {
		areaPaint.setColor(color);
	}
	
	private void drawInfo(Canvas canvas) {
		if (posList == null) return;
		for (int i = 0; i < posList.size(); i ++) {
			Position bop = posList.get(i);
			infoPaint.getTextBounds(bop.msg.toCharArray(), 0, bop.msg.length(), rect);
			canvas.drawText(bop.msg, bop.x - rect.width()/2, bop.y + rect.height()/2, infoPaint);
		}
	}
	
	public void drawCircle(float x, float y) {
		tx = x;
		ty = y;
		radius = 30;
		isCircle = false;
	}
	
	public void drawCircle2(float x, float y, float r) {
		tx = x;
		ty = y;
		radius = r;
		isCircle = true;
	}
	
	public float getTouchX() {
		return tx;
	}
	
	public float getTouchY() {
		return ty;
	}
	
	public float getTouchR() {
		return isCircle ? radius : 0;
	}
	
	public void setCircleType(float type) {
		this.type = type;
	}
	
	public float getCircleType() {
		return type; 
	}
	
	public void drawInfo(String msg) {
		this.msg = msg;
	}
	
	public void drawNow(boolean drawable) {
		this.drawable = drawable;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.currentThread().interrupted()) {
			try {
				postInvalidate();
				Thread.sleep(100);
			} catch (Exception io) {
				
			}
		}
	}
}
