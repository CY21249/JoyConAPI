package joycon;

public class JoyConInputReport {
	public final byte reportID;

	JoyConInputReport(byte reportID) {
		this.reportID = reportID;
	}

	protected JoyConInputReport(JoyConInputReport ir) {
		this(ir.reportID);
	}
}

class BasicInfoInputReport extends JoyConInputReport {
	// public final String timer;
	// public final String batteryLevel;
	// public final String connectionInfo;
	// public final String buttonStatus;
	// public final String analogStickLeft;
	// public final String analogStickRight;
	// public final String vibrator;

	BasicInfoInputReport(byte reportID) {
		this(new JoyConInputReport(reportID));
	}

	protected BasicInfoInputReport(JoyConInputReport p) {
		super(p);
	}
}

