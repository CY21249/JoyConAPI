package vector;

public class Vector3D {
	public final double x;
	public final double y;
	public final double z;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D add(Vector3D other) {
		return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	public Vector3D times(double k) {
		return new Vector3D(this.x * k, this.y * k, this.z * k);
	}

	public double mag() {
		return Math.sqrt(this.mag_2());
	}

	public double mag_2() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	/**
	 * 
	 * @return Vector3D | null
	 */
	public Vector3D norm() {
		double mag = this.mag();
		if (mag == 0)
			return null;
		return this.times(1 / mag);
	}
}
