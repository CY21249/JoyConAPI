package joycon;

import java.time.*;
import hid.Packet;
import joycon.angle.*;
import joycon.vector.*;
import util.BitUtil;

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
		Vector3D[] accelerometers = this.parseAccelerometers(packet);
		Vector3D accelerometer = this.parseActualAccelerometer(accelerometers);

		EularAngleVelocity[] gyroscopes = this.parseGyroscopes(packet);
		EularAngleVelocity gyroscope = this.calculateActualGyroscope(gyroscopes);
		EularAngle orientation = this.toOrientation(target, gyroscope, accelerometer);
		return new MoveInfoInputReport(basicInfo, gyroscope, orientation);
	}

	/**
	 * 参考: parse.js:368 parseAccelerometers,
	 * 	https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering/blob/master/imu_sensor_notes.md#accelerometer---acceleration-in-g
	 */
	private Vector3D[] parseAccelerometers(Packet packet) {
		return new Vector3D[] {
			this.toAccelerometer(packet.data, 13),
			this.toAccelerometer(packet.data, 25),
			this.toAccelerometer(packet.data, 37)
		};
	}

	private Vector3D toAccelerometer(byte[] data, int index) {
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
			this.parseGyroscope(packet.data, 19),
			this.parseGyroscope(packet.data, 31),
			this.parseGyroscope(packet.data, 43)
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

		double norm = 1 / accelerometer.mag();
		EularAngle temp = new EularAngle(
			(1 - zeroBias) * lo.x + gyroscope.z * dt,
			bias * (lo.y + gyroscope.x * dt) 
				+ (1 - bias) * ((accelerometer.x * scale) * norm),
			bias * (lo.z + gyroscope.y * dt) 
				+ (1 - bias) * ((accelerometer.y * scale) * norm)
		);

		temp = new EularAngle(temp.x * mn, temp.y, temp.z)
			.times(1 / Math.PI);

		EularAngle orientation = target.hand < 0
			? new EularAngle(-temp.x % (1. / 4), -temp.y, -temp.z)
			: new EularAngle(temp.x % 1., -temp.y, temp.z);

		this.lastValue = new DecodeOrientationInfo(now, orientation);

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