package joyconapi.event;

public class Event<T, D> {
	public final T target;
	public final D data;

	public Event(T target, D data) {
		this.target = target;
		this.data = data;
	}
}