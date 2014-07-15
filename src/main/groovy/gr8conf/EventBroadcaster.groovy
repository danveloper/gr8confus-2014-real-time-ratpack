package gr8conf

import java.util.concurrent.CopyOnWriteArrayList
import ratpack.func.Action

class EventBroadcaster {
  private final List<Action<String>> listeners = new CopyOnWriteArrayList<>()

  public AutoCloseable register(Action<String> subscriber) {
    listeners << subscriber
    new AutoCloseable() {
      void close() {
        listeners.remove subscriber
      }
    }
  }

  public void broadcast(String msg) {
    listeners*.execute(msg)
  }
}
