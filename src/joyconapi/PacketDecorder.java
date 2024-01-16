package joyconapi;


import java.time.*;
import joyconapi.hid.*;
import joyconapi.angle.*;
import joyconapi.vector.*;
import joyconapi.util.*;

public class PacketDecorder {
	private DecodeOrientationInfo lastValue = new DecodeOrientationInfo(-1, new EularAngle(0, 0, 0));

	/**
	 * パケットをデコードする
	 * 参考: joycon.js:425
	 */
	public JoyConInputReport decode(JoyCon target, Packet packet) {
		JoyConInputReport base = this.decodeBase(packet);

		switch (packet.ids) {
			case 0x30: {
				// BasicInfo をパース
				BasicInfoInputReport basicInfo = this.decodeBasicInfo(packet, base);

				// MoveInfo をパース
				MoveInfoInputReport moveInfo = this.decodeMoveInfo(target, packet, basicInfo);
				return moveInfo;
			}
			default:
				return null;
		}

	}

	private JoyConInputReport decodeBase(Packet packet) {
		return new JoyConInputReport(parseInputReportID(packet));
	}

	/**
	 * InputReportIDをパースする
	 * 参考: parse.js:196 function parseInputReportID
	 */
	private byte parseInputReportID(Packet packet) {
		return packet.data[0];
	}

	private BasicInfoInputReport decodeBasicInfo(Packet packet, JoyConInputReport inputReport) {
		return new BasicInfoInputReport(inputReport);
	}

	private MoveInfoInputReport decodeMoveInfo(JoyCon target, Packet packet, BasicInfoInputReport basicInfo) {
		Vector3D[] accelerations = this.parseAccelerometers(packet);
		Vector3D acceleration = this.parseActualAccelerometer(accelerations);

		EularAngleVelocity[] angleVelocities = this.parseGyroscopes(packet);
		EularAngleVelocity angleVelocity = this.calculateActualGyroscope(angleVelocities);

		EularAngle orientation = this.toOrientation(target, angleVelocity, acceleration);

		return new MoveInfoInputReport(basicInfo, acceleration, angleVelocity, orientation);
	}

	/**
	 * 参考: parse.js:368 parseAccelerometers,
	 * 	https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering/blob/master/imu_sensor_notes.md#accelerometer---acceleration-in-g
	 * @returns {Vector3D} Acceleration 
	 */
	private Vector3D[] parseAccelerometers(Packet packet) {
		return new Vector3D[] {
			// packet.data (: byte[]) は index 0 に report id を含めないため、上記 GitHub の表の値の1つ手前としている
			this.parseAccelerometer(packet.data, 12),
			this.parseAccelerometer(packet.data, 24),
			this.parseAccelerometer(packet.data, 36)
		};
	}

	private Vector3D parseAccelerometer(byte[] data, int index) {
		return new Vector3D(
			this.toAcceleration(data, index),
			this.toAcceleration(data, index + 2),
			this.toAcceleration(data, index + 4)
		);
	}

	/**
	 * 参考: parse.js:145 toAcceleration,
	 * 	https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering/blob/master/imu_sensor_notes.md#normal-8000-mg
	 */
	private double toAcceleration(byte[] data, int index) {
		int imuValue = BitUtil.toInt16(data, index, true);
		return 0.00244 * imuValue;
	}

	/**
	 * 参考: parse.js:493 calculateActualAccelerometer
	 */
	private Vector3D parseActualAccelerometer(Vector3D[] accelerometers) {
		double elapsedTime = 0.005 * accelerometers.length;

		double sum_x = 0,
			sum_y = 0,
			sum_z = 0;
		for (Vector3D a : accelerometers) {
			sum_x += a.x;
			sum_y += a.y;
			sum_z += a.z;
		}

		return new Vector3D(sum_x, sum_y, sum_z)
			.times(1. / accelerometers.length)
			.times(elapsedTime);
	}	

	/**
	 * 参考: joycon.js:483 PacketParser.parseGyroscopes,
	 * 	parse.js:426 parseGyroscopes,
	 * 	https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering/blob/master/imu_sensor_notes.mduser-content-gyroscope---rotation-in-revolutionss
	 */
	private EularAngleVelocity[] parseGyroscopes(Packet packet) {
		return new EularAngleVelocity[] {
			// packet.data (: byte[]) は index 0 に report id を含めないため、上記 GitHub の表の値の1つ手前としている
			this.parseGyroscope(packet.data, 18 - 1),
			this.parseGyroscope(packet.data, 30 - 1),
			this.parseGyroscope(packet.data, 42 - 1)
		};
	}

	private EularAngleVelocity parseGyroscope(byte[] data, int index) {
		return new EularAngleVelocity(
			this.toAngleVelocity(data, index),
			this.toAngleVelocity(data, index + 2),
			this.toAngleVelocity(data, index + 4)
		);
	}

	/**
	 * 参考: parse.js:165 toRevolutionsPerSecond
	 * 	https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering/blob/master/imu_sensor_notes.md#normal
	 */
	private double toAngleVelocity(byte[] data, int index) {
		int imuValue = BitUtil.toInt16(data, index, true);
		return 0.0001694 * imuValue;
	}

	/** 
	 * 参考: parse.js:511 calculateActualGyroscope
	 */
	private EularAngleVelocity calculateActualGyroscope(EularAngleVelocity[] gyroscopes) {
		double elapsedTime = 0.005 * gyroscopes.length;

		double sum_x = 0,
			sum_y = 0,
			sum_z = 0;
		for (EularAngleVelocity g : gyroscopes) {
			sum_x += g.x;
			sum_y += g.y;
			sum_z += g.z;
		}

		return new EularAngleVelocity(
			sum_x / gyroscopes.length * elapsedTime,
			sum_y / gyroscopes.length * elapsedTime,
			sum_z / gyroscopes.length * elapsedTime
		);
	}

	/**
	 * 姿勢角(オイラー角)を計算する
	 * 参考: parse.js:79 toEulerAngles
	 */
	private EularAngle toOrientation(JoyCon target, EularAngleVelocity gyroscope, Vector3D accelerometer) {
		double bias = 0.75;
		double zeroBias = 0.0125;
		double scale = Math.PI / 2;
		double mn = 430;
		
		double lt = this.lastValue.timestamp;
		EularAngle lo = this.lastValue.orientation;

		double now = Instant.now().toEpochMilli() / 1000;
		double dt = lt < 0 ? 0 : now - lt;

		double norm = accelerometer.mag();
		// x: gamma (roll), y: beta (pitch), z: alpha (yaw)
		EularAngle temp = (norm == 0)
			? lo
			: new EularAngle(
				bias           * (lo.x + gyroscope.y * dt) + (1 - bias) * ((accelerometer.y * scale) / norm),
				bias           * (lo.y + gyroscope.x * dt) + (1 - bias) * ((accelerometer.x * scale) / norm),
				(1 - zeroBias) * (lo.z + gyroscope.z * dt)
			);
			// : new EularAngle(
			// 	bias           * (lo.x + (gyroscope.y + gyroscope.z) * dt) + (1 - bias) * (((accelerometer.y + accelerometer.z) * scale) / norm),
			// 	bias           * (lo.y + (gyroscope.z + gyroscope.x) * dt) + (1 - bias) * (((accelerometer.z + accelerometer.x) * scale) / norm),
			// 	(1 - zeroBias) * (lo.z + (gyroscope.x + gyroscope.y) * dt) + (1 - bias) * (((accelerometer.x + accelerometer.y) * scale) / norm)
			// );

		this.lastValue = new DecodeOrientationInfo(now, temp);
		
		EularAngle temp1 = new EularAngle(temp.x, temp.y, temp.z)
			.times(180 / Math.PI);

		EularAngle orientation = target.hand < 0
			? new EularAngle(-temp1.x, -temp1.y, -temp1.z)
			: new EularAngle(temp1.x, -temp1.y, temp1.z);

		return orientation;
	}
}

class DecodeOrientationInfo {
	public final double timestamp;
	public final EularAngle orientation;

	public DecodeOrientationInfo(double timestamp, EularAngle orientation) {
		this.timestamp = timestamp;
		this.orientation = orientation;
	}
}