package hid;

import event.EventListener;

/** HIDデバイスからのイベントを監視するリスナ */
public abstract class HIDEventListener implements EventListener<HIDEvent> {
	@Override
	public void dispatchEvent(HIDEvent event) {
		if (event instanceof ReceivedPacketEvent)
			this.onInputReport((ReceivedPacketEvent) event);
		else
			throw new Error("Not Implemented Event: " + event);
	}

	/** HIDデバイスから InputReport イベントが来た時に呼び出されるハンドラ */
	protected abstract void onInputReport(ReceivedPacketEvent evnet);

	// public abstract void onConnected(ConnectedEvent event);
	// public abstract void onClosed(ClosedEvent event);
}
