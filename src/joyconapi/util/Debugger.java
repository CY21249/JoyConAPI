package joyconapi.util;

public class Debugger {
  public static final Debugger common = new Debugger(false);

  private boolean isActive;

  public boolean getIsActive() {
    return this.isActive;
  }

  public void activate() {
    this.isActive = true;
  }

  public void inactivate() {
    this.isActive = false;
  }

  private Debugger(boolean isActive) {
    this.isActive = isActive;
  }

  public void log(String source, Object ...messages) {
    String message = "[" + source + "] ";
    for (Object m : messages)
      message += m + " ";

    System.out.println(message);
  }
}
