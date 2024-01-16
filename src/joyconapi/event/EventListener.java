package joyconapi.event;

public interface EventListener<E extends Event> extends java.util.EventListener {
	public void dispatchEvent(E e);
}
