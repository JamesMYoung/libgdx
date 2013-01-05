package com.badlogic.gdx.controllers;

public class OisJoystick {
	public static enum OisPov {
		Centered,
		North,
		South,
		East,
		West,
		NorthEast,
		SouthEast,
		NorthWest,
		SouthWest
	}
	
	private long joystickPtr;
	private boolean[] buttons;
	private int[] axes;
	private int[] povs;
	private int[] slidersX;
	private int[] slidersY;

	public OisJoystick(long joystickPtr) {
		this.joystickPtr = joystickPtr;
		initialize(this);
		this.buttons = new boolean[getNumButtons()];
		this.axes = new int[getNumAxes()];
		this.povs = new int[getNumPovs()];
		this.slidersX = new int[getNumSliders()];
		this.slidersY = new int[getNumSliders()];
	}
	
	private void povMoved (int pov, int value) {
		this.povs[pov] = value;
	}

	private void axisMoved (int axis, int value) {
		this.axes[axis] = value;
	}

	private void sliderMoved (int slider, int x, int y) {
		this.slidersX[slider] = x;
		this.slidersY[slider] = y;
	}

	private void buttonPressed (int button) {
		this.buttons[button] = true;
	}

	private void buttonReleased (int button) {
		this.buttons[button] = false;
	}
	
	public void update() {
		updateJni(joystickPtr, this);
	}
	
	public int getNumAxes() {
		return getNumAxesJni(joystickPtr);
	}

	public int getNumButtons() {
		return getNumButtonsJni(joystickPtr);
	}
	
	public int getNumPovs() {
		return getNumPovsJni(joystickPtr);
	}
	
	public int getNumSliders() {
		return getNumSlidersJni(joystickPtr);
	}
	
	public int getAxis(int axis) {
		return axes[axis];
	}
	
	public OisPov getPov(int pov) {
		switch(povs[pov]) {
			case 0x00000000: return OisPov.Centered;
			case 0x00000001: return OisPov.North;
			case 0x00000010: return OisPov.South;
			case 0x00000100: return OisPov.East;
			case 0x00001000: return OisPov.West;
			case 0x00000101: return OisPov.NorthEast;
			case 0x00000110: return OisPov.SouthEast;
			case 0x00001001: return OisPov.NorthWest;
			case 0x00001010: return OisPov.SouthWest;
			default: 
				throw new RuntimeException("Unexpected POV value reported by OIS: " + povs[pov]);
		}
	}
	
	public boolean isButtonPressed(int button) {
		return buttons[button];
	}

	// @off
	/*JNI
	#include <OISJoyStick.h>
	#include <OISInputManager.h>
	 
	static jclass callbackClass = 0;
	static jmethodID povMovedId = 0;
	static jmethodID axisMovedId = 0;
	static jmethodID sliderMovedId = 0;
	static jmethodID buttonPressedId = 0;
	static jmethodID buttonReleasedId = 0;
	
	static void initializeClasses(JNIEnv* env, jobject clazz) {
		// we leak one global ref
		if(callbackClass == 0) {
			callbackClass = (jclass)env->NewGlobalRef(env->GetObjectClass(clazz));
			povMovedId = env->GetMethodID(callbackClass, "povMoved", "(II)V");
			axisMovedId = env->GetMethodID(callbackClass, "axisMoved", "(II)V");
			sliderMovedId = env->GetMethodID(callbackClass, "sliderMoved", "(III)V");
			buttonPressedId = env->GetMethodID(callbackClass, "buttonPressed", "(I)V");
			buttonReleasedId = env->GetMethodID(callbackClass, "buttonReleased", "(I)V");
		}
	}

	class Listener : public OIS::JoyStickListener {
	public:
		Listener(JNIEnv* env, jobject obj) {
			this->env = env;
			this->obj = obj;
		}

		JNIEnv* env;
		jobject obj;

		bool povMoved (const OIS::JoyStickEvent &event, int pov);
		bool axisMoved (const OIS::JoyStickEvent &event, int axis);
		bool sliderMoved (const OIS::JoyStickEvent &event, int sliderID);
		bool buttonPressed (const OIS::JoyStickEvent &event, int button);
		bool buttonReleased (const OIS::JoyStickEvent &event, int button);
	};

	bool Listener::buttonPressed (const OIS::JoyStickEvent &event, int buttonId) {
		env->CallVoidMethod(obj, buttonPressedId, (jint)buttonId);
		return true;
	}

	bool Listener::buttonReleased (const OIS::JoyStickEvent &event, int buttonId) {
		env->CallVoidMethod(obj, buttonReleasedId, (jint)buttonId);
		return true;
	}

	bool Listener::axisMoved (const OIS::JoyStickEvent &event, int axisId) {
		env->CallVoidMethod(obj, axisMovedId, (jint)axisId, (jint)event.state.mAxes[axisId].abs);
		return true;
	}

	bool Listener::povMoved (const OIS::JoyStickEvent &event, int povId) {
		env->CallVoidMethod(obj, povMovedId, (jint)povId, (jint)event.state.mPOV[povId].direction);
		return true;
	}

	bool Listener::sliderMoved (const OIS::JoyStickEvent &event, int sliderId) {
		env->CallVoidMethod(obj, sliderMovedId, (jint)sliderId, (jint)event.state.mSliders[sliderId].abX, (jint)event.state.mSliders[sliderId].abY);
		return true;
	}
	 */
	
	private native void initialize(OisJoystick joystick); /*
		initializeClasses(env, joystick);
	*/
	
	private native void updateJni(long joystickPtr, OisJoystick callback); /*
		OIS::JoyStick* joystick = (OIS::JoyStick*)joystickPtr;
		Listener listener(env, callback);
		joystick->setEventCallback(&listener);
		joystick->capture();
	*/
	
	private native int getNumAxesJni (long joystickPtr); /*
		OIS::JoyStick* joystick = (OIS::JoyStick*)joystickPtr;
		return joystick->getNumberOfComponents(OIS::OIS_Axis);
	*/
	
	private native int getNumButtonsJni (long joystickPtr); /*
		OIS::JoyStick* joystick = (OIS::JoyStick*)joystickPtr;
		return joystick->getNumberOfComponents(OIS::OIS_Button);
	*/
	
	private native int getNumPovsJni (long joystickPtr); /*
		OIS::JoyStick* joystick = (OIS::JoyStick*)joystickPtr;
		return joystick->getNumberOfComponents(OIS::OIS_POV);
	 */
	
	private native int getNumSlidersJni (long joystickPtr); /*
		OIS::JoyStick* joystick = (OIS::JoyStick*)joystickPtr;
		return joystick->getNumberOfComponents(OIS::OIS_Slider);
	 */
}
