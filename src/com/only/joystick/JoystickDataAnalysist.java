package com.only.joystick;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.only.controller.InputAdapterKeyEvent;
import com.only.controller.data.GlobalData;
import com.only.core.EventService;
import com.only.touch.Position;
import com.only.touch.Profile;
import com.only.utils.TouchUtils;

public class JoystickDataAnalysist {
	private static final String TAG = "JoystickDataAnalysist";
	public static final int MSG_JOYSTICK_RIGHT_DATA = 0X01;
	public static final int MSG_JOYSTICK_LEFT_DATA = 0X02;
	private static boolean rightMotionKey = false;
	private static boolean leftMotionKey = false;
	private static float rightJoystickCurrentPosX = 0.0f;
	private static float rightJoystickCurrentPosY = 0.0f;
	private static float leftJoystickCurrentPosX = 0.0f;
	private static float leftJoystickCurrentPosY = 0.0f;
	private static float joystickR = 0.0f;
	private static float rightJoystickCurrentR = 0.0f;
	private static float leftJoystickCurrentR = 0.0f;
	private static Profile rightJoystickProfile = null;
	private static Profile leftJoystickProfile = null;
	
	public static void init() {
		EventService.setJoystickHandler(joystickHandler);
	}
	
	private static  double calcSinA(int bx, int by, int joystickType) {
		 int ox = 0x7f;
		 int oy = 0x7f;
		 int x = Math.abs(ox - bx);
		 int y = Math.abs(oy - by);
		 double r = Math.sqrt(Math.pow((double) x, 2) + Math.pow((double)y, 2));
		 if (joystickType == Position.TYPE_LEFT_JOYSTICK) {
			 leftJoystickCurrentR = (float) r;
		 } else if (joystickType == Position.TYPE_RIGHT_JOYSTICK) {
			 rightJoystickCurrentR = (float) r;
		 }
		 joystickR = 127;
		 double sin = ((double)y) / r;
		 return sin;
	 }
	 
	 private static void processRightJoystickData(byte bx, byte by) { // x = buffer[3] y = buffer[4]
		 int ox = 0x7f;
		 int oy = 0x7f;
		 int ux = bx;
		 int uy = by;
		 if (bx < 0) ux = 256 + bx;
		 if (by < 0) uy = 256 + by;
		 
//		 if (bx != 0x7f || by != 0x7f) {
			 if (GlobalData.keyList != null) {
				 if (rightJoystickProfile == null) rightJoystickProfile = new Profile();
				 for (Profile bp: GlobalData.keyList) {
					 if (bp.posR > 0 && bp.posType == Position.TYPE_RIGHT_JOYSTICK) {
						 double sin = calcSinA(ux, uy, Position.TYPE_RIGHT_JOYSTICK);
						 double touchR1 = (bp.posR/joystickR) * rightJoystickCurrentR;
						 Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " rightJoystickCurrentR = " + rightJoystickCurrentR);
						 double y = touchR1 * sin;
						 double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
						 float rawX = 0.0f;
						 float rawY = 0.0f;
						 if (ux < ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫乏
							 rawX = bp.posX - (float)x;
							 rawY = bp.posY - (float)y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis positive left part");
						 } else if (ux > ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫彸
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY - (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis positive right part");
						 } else if (ux < ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫乏
							 rawX = bp.posX  - (float) x;
							 rawY = bp.posY + (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis negtive left part");
						 } else if (ux > ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫彸
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY + (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis negtiveleft part");
						 } else if (ux == ox && uy < oy) { //Y杞村彉鍖?							 rawX = bp.posX;
							 rawY = bp.posY - (float)y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis Y < 0x7f");
						 } else if (ux == ox && uy > oy) { //Y杞村彉鍖?							 rawX = bp.posX;
							 rawY = bp.posY + (float) y;
							 rightMotionKey = true;
							 Log.e(TAG, "axis Y > 0x7f");
						 } else if (ux < ox && uy == oy) { //X杞村彉鍖?							 rawX = bp.posX - (float)x;
							 rawY = bp.posY;
							 rightMotionKey = true;
							 Log.e(TAG, "axis X < 0x7f");
						 } else if (ux > ox && uy == oy) { //X杞村彉鍖?							 rawX = bp.posX + (float) x;
							 rawY = bp.posY;
							 rightMotionKey = true;
							 Log.e(TAG, "axis X  > 0x7f");
						 } else if (ux == ox && uy == oy && rightMotionKey) {
							 Log.e(TAG, "right  you release map");
//							 BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
//									 MotionEvent.ACTION_UP, rightJoystickCurrentPosX, rightJoystickCurrentPosY, 0));
//							 BlueoceanMotionManager.injectMotionEventUp(BlueoceanMotionManager.fd, 1);
							 rightJoystickProfile.posX = rightJoystickCurrentPosX;
							 rightJoystickProfile.posY = rightJoystickCurrentPosY;
							 TouchUtils.sendTouchMapUp(rightJoystickProfile);
							 rightMotionKey = false;
						 }
						 if (rightMotionKey) {
//								 BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
//										 MotionEvent.ACTION_DOWN, rawX, rawY, 0));
	//							 BlueoceanMotionManager.injectMotionEventDown(BlueoceanMotionManager.fd, 1, rawX, rawY);
								 
//							 Log.e(TAG, " no release map fuck");
//							 BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
//									 MotionEvent.ACTION_UP, rawX, rawY, 0));
//							 motionKey = false;
							 
							 rightJoystickProfile.posX = rawX;
							 rightJoystickProfile.posY = rawY;
							 TouchUtils.sendTouchMapDown(rightJoystickProfile);
						 }
						rightJoystickCurrentPosX = rawX;
						rightJoystickCurrentPosY = rawY;
						 Log.e(TAG, "right test up bp.posR = " + bp.posR + " bp.postX = " + bp.posX + " bp.posY = " + bp.posY
								 + " y = " + y + " x = " + x + " rawX = " + rawX + " rawY = " + rawY + " bx = " + Integer.toHexString(bx) 
								 + " by = " + Integer.toHexString(by) + " ux = " + ux + " uy = " + uy);
					 }
				 }
			 }
//		 }
	 }
	 
	 private static void processLeftJoystickData(byte bx, byte by) { // x = buffer[3] y = buffer[4]
		 int ox = 0x7f;
		 int oy = 0x7f;
		 int ux = bx;
		 int uy = by;
		 if (bx < 0) ux = 256 + bx;
		 if (by < 0) uy = 256 + by;
		 
//		 if (bx != 0x7f || by != 0x7f) {
			 if (GlobalData.keyList != null) {
				 for (Profile bp: GlobalData.keyList) {
					 if (bp.posR > 0 && bp.posType == Position.TYPE_LEFT_JOYSTICK) {
						 double sin = calcSinA(ux, uy, Position.TYPE_LEFT_JOYSTICK);
//						 double y = bp.posR * sin;
//						 double x = Math.sqrt(Math.pow(bp.posR, 2) - Math.pow(y, 2));
						 double touchR1 = (bp.posR/joystickR) * leftJoystickCurrentR;
						 Log.e(TAG, "touchR1 = " + touchR1 + " bp.posR" + bp.posR + " joystickR = " + joystickR + " leftJoystickCurrentR = " + leftJoystickCurrentR);
						 double y = touchR1 * sin;
						 double x = Math.sqrt(Math.pow(touchR1, 2) - Math.pow(y, 2));
						 float rawX = 0.0f;
						 float rawY = 0.0f;
						 if (ux < ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫乏
							 rawX = bp.posX - (float)x;
							 rawY = bp.posY - (float)y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis positive left part");
						 } else if (ux > ox && uy < oy) {  //鍧愭爣杞翠笂鍗婇儴鐨勫彸
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY - (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis positive right part");
						 } else if (ux < ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫乏
							 rawX = bp.posX  - (float) x;
							 rawY = bp.posY + (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis negtive left part");
						 } else if (ux > ox && uy > oy) { //鍧愭爣杞翠笅鍗婇儴鐨勫彸
							 rawX = bp.posX + (float) x;
							 rawY = bp.posY + (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis negtiveleft part");
						 } else if (ux == ox && uy < oy) { //Y杞村彉鍖?							 rawX = bp.posX;
							 rawY = bp.posY - (float)y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis Y < 0x7f");
						 } else if (ux == ox && uy > oy) { //Y杞村彉鍖?							 rawX = bp.posX;
							 rawY = bp.posY + (float) y;
							 leftMotionKey = true;
							 Log.e(TAG, "axis Y > 0x7f");
						 } else if (ux < ox && uy == oy) { //X杞村彉鍖?							 rawX = bp.posX - (float)x;
							 rawY = bp.posY;
							 leftMotionKey = true;
							 Log.e(TAG, "axis X < 0x7f");
						 } else if (ux > ox && uy == oy) { //X杞村彉鍖?							 rawX = bp.posX + (float) x;
							 rawY = bp.posY;
							 leftMotionKey = true;
							 Log.e(TAG, "axis X  > 0x7f");
						 } else if (ux == ox && uy == oy && leftMotionKey) {
							 Log.e(TAG, "left joystick you release map");
//							 BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
//									 MotionEvent.ACTION_UP, leftJoystickCurrentPosX, leftJoystickCurrentPosY, 0));
//							 BlueoceanMotionManager.injectMotionEventUp(BlueoceanMotionManager.fd, 2);
							 leftJoystickProfile.posX = leftJoystickCurrentPosX;
							 leftJoystickProfile.posY = leftJoystickCurrentPosY;
							 TouchUtils.sendTouchMapUp(leftJoystickProfile);
							 leftMotionKey = false;
						 }
						 if (leftMotionKey) {
//							 BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
//									 MotionEvent.ACTION_DOWN, rawX, rawY, 0));
	//						 BlueoceanMotionManager.injectMotionEventDown(BlueoceanMotionManager.fd,  2, rawX, rawY);
							 leftJoystickProfile.posX = rawX;
							 leftJoystickProfile.posY = rawY;
							 TouchUtils.sendTouchMapDown(leftJoystickProfile);
//						 Log.e(TAG, " no release map fuck");
//						 BlueoceanCore.instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), 
//								 MotionEvent.ACTION_UP, rawX, rawY, 0));
//						 motionKey = false;
						 }
						 leftJoystickCurrentPosX = rawX;
						 leftJoystickCurrentPosY = rawY;
						 Log.e(TAG, "left test up bp.posR = " + bp.posR + " bp.postX = " + bp.posX + " bp.posY = " + bp.posY
								 + " y = " + y + " x = " + x + " rawX = " + rawX + " rawY = " + rawY + " bx = " + Integer.toHexString(bx) 
								 + " by = " + Integer.toHexString(by) + " ux = " + ux + " uy = " + uy);
					 }
				 }
			 }
//		 }
	 }
	 
	 public static Handler joystickHandler = new Handler() {
		 public void handleMessage(Message msg) {
			 switch (msg.what) {
			 case MSG_JOYSTICK_RIGHT_DATA:
				 InputAdapterKeyEvent event = (InputAdapterKeyEvent) msg.obj;
				 
				 break;
			 case MSG_JOYSTICK_LEFT_DATA:
				 break;
			 }
		 }
	 };
}
