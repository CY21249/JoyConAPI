package joyconapi.angle;

/**
 * Roll-Pitch-Yaw角 (ZYXオイラー角) を表すクラス
 */
public class EularAngle {
	/** Rolling角 (単位: 回 (円1周に対する割合)) */ 
	public final double x;
	/** Pitching角 (単位: 回 (円1周に対する割合)) */ 
	public final double y;
	/** Yawing角 (単位: 回 (円1周に対する割合)) */ 
	public final double z;

	public EularAngle(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public EularAngle times(double k) {
		return new EularAngle(this.x * k, this.y * k, this.z * k);
	}
}
