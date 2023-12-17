package joycon.angle;

public class EularAngleVelocity {
	// 値は rps (回毎秒: 1秒間に何回回転するか)
	public final double x;
	public final double y;
	public final double z;

	public EularAngleVelocity(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
