package joyconapi.hid;

import joyconapi.event.*;

/** HIDに関するイベント全般 */
public class HIDEvent<D> extends Event<HIDDevice, D> {
	public HIDEvent(HIDDevice target, D data) {
		super(target, data);
	}
}
