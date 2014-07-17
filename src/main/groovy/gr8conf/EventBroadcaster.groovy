package gr8conf

import java.util.concurrent.CopyOnWriteArrayList
import ratpack.func.Action
import ratpack.handling.Context


import static ratpack.websocket.WebSockets.websocket

class EventBroadcaster {
  private final Map<String, List<Action<String>>> listenerRegistry = new HashMap<>()

  public void register(Context context) {
    websocket(context) { ws ->
      register(context.request.path) {
        ws.send(it)
      }
    } connect {
      it.onClose {
        it.openResult.close()
      }
    }
  }

  public AutoCloseable register(String wsContext, Action<String> subscriber) {
    if (!listenerRegistry.containsKey(wsContext)) {
      listenerRegistry[wsContext] = new CopyOnWriteArrayList<>()
    }

    listenerRegistry[wsContext] << subscriber
    new AutoCloseable() {
      final String ctx = wsContext

      void close() {
        listenerRegistry[ctx].remove subscriber
      }
    }
  }

  public void broadcast(String wsContext, String msg) {
    listenerRegistry[wsContext]*.execute(msg)
  }
}
