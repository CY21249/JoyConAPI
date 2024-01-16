package joyconapi;


import joyconapi.angle.*;
import joyconapi.vector.Vector3D;

public class MoveInfoInputReport extends BasicInfoInputReport {
	/** ジョイコンの加速度 */
	public final Vector3D acceleration;
	/** ジョイコンの各速度 */
	public final EularAngleVelocity angleVelocity;
	/** ジョイコンの姿勢 */
	public final EularAngle orientation;

	MoveInfoInputReport(byte reportID, Vector3D acceleration, EularAngleVelocity angleVelocity, EularAngle orientation) {
		this(new BasicInfoInputReport(reportID), acceleration, angleVelocity, orientation);
	}

	protected MoveInfoInputReport(BasicInfoInputReport p, Vector3D acceleration, EularAngleVelocity angleVelocity, EularAngle orientation) {
		super(p);
		this.acceleration = acceleration;
		this.angleVelocity = angleVelocity;
		this.orientation = orientation;
	}
}