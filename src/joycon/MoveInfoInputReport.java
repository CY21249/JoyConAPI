package joycon;

import joycon.angle.*;

public class MoveInfoInputReport extends BasicInfoInputReport {
	/** ジョイコンの加速度 */
	// public final Vector3D accelerators;
	/** ジョイコンの各速度 */
	public final EularAngleVelocity angleVelocity;
	/** ジョイコンの姿勢 */
	public final EularAngle orientation;

	MoveInfoInputReport(byte reportID, EularAngleVelocity angleVelocity, EularAngle orientation) {
		this(new BasicInfoInputReport(reportID), angleVelocity, orientation);
	}

	protected MoveInfoInputReport(BasicInfoInputReport p, EularAngleVelocity angleVelocity, EularAngle orientation) {
		super(p);
		this.angleVelocity = angleVelocity;
		this.orientation = orientation;
	}
}