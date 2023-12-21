

import angle.*;

public class Gyroscopes {
	
}

class Gyroscope {
	public final EularAngle orientation;
	public final EularAngleVelocity angleVelocity;

	public Gyroscope(EularAngle orientation, EularAngleVelocity angleVelocity) {
		this.orientation = orientation;
		this.angleVelocity = angleVelocity;
	}
}
