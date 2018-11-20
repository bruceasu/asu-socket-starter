/**
 *
 */
package me.asu.socket.server;

import static me.asu.socket.Constants.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.asu.socket.message.IMessage;
import me.asu.socket.message.ProtoMessage;
import me.asu.socket.util.Stream;
import me.asu.util.*;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * @author suk
 */
@Slf4j
@Getter
public class Worker implements Runnable {
    private static final RuntimeException NOT_SUPPORT_TYPE = new RuntimeException( "not support data type");
    private static final UnsafeReferenceFieldUpdater<BufferedReader, Reader> READER_UPDATER = UnsafeUpdater
            .newReferenceFieldUpdater(BufferedReader.class, "in");
    private static final UnsafeReferenceFieldUpdater<PrintWriter, Writer>    WRITER_UPDATER = UnsafeUpdater
            .newReferenceFieldUpdater(PrintWriter.class, "out");

    private final Socket socket;
    @Setter
    boolean running = false;
    @Setter
    Handler handler;
    Stream         stream;
    ChannelContext ctx;
    int readTimeout = 0;
    private BlockingDeque<SendMessage> sendingQueue = new LinkedBlockingDeque<SendMessage>();

    /**
     * @param socket
     * @param handler
     */
    public Worker(final Socket socket, Handler handler, int readTimeout) throws IOException {
        this.socket = socket;
        this.readTimeout = readTimeout;
        this.stream = new Stream(socket);
        this.stream.setReadTimeout(this.readTimeout);
        this.ctx = new ChannelContext(this);
        this.handler = handler;
        if (handler != null) {
            handler.onOpen(this.ctx);
        }
    }

    public void addSendData(Object data) {
        sendingQueue.add(new SendMessage(data, null));
    }

    public void addSendData(Object data, SendCallBack callBack) {
        sendingQueue.add(new SendMessage(data, callBack));
    }

    public void shutdown() {
        log.info("worker for {} is shutting down...", ctx.getAddress());
        this.running = false;
        if (stream != null && !stream.isClosed()) {
            StreamUtils.closeQuietly(stream);
            stream = null;
        }
        if (handler != null) {
            handler.onClose(this.ctx);
        }
        log.info("worker for {} is shutdown.", ctx.getAddress());
    }

    @Override
    public void run() {
        running = true;
        Observable.create(new OnSubscribe<SendMessage>() {
            @Override
            public void call(Subscriber<? super SendMessage> subscriber) {
                try {
                    while (running) {
                        SendMessage m = sendingQueue.take();
                        subscriber.onNext(m);
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).observeOn(Schedulers.io()).subscribeOn(Schedulers.newThread())
                  .subscribe(new Subscriber<SendMessage>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          log.error("", e);
                          if (handler != null) {
                              handler.onError(ERROR_SEND, e, getCtx());
                          }
                          shutdown();
                      }

                      @Override
                      public void onNext(SendMessage m) {
                          try {
                              if (m.getData() instanceof IMessage) {
                                  stream.write((IMessage) m.getData());
                                  if (m.getCallBack() != null) {
                                      m.getCallBack().onComplete(true);
                                  }
                              } else if (m.getData() instanceof byte[]) {
                                  stream.write((byte[]) m.getData());
                                  if (m.getCallBack() != null) {
                                      m.getCallBack().onComplete(true);
                                  }
                              } else {

                                  delegateException(NOT_SUPPORT_TYPE, ERROR_SEND);
                              }
                          } catch (IOException ex) {
                              if (m.getCallBack() != null) {
                                  m.getCallBack().onError(ex);
                              }
                              delegateException(ex, ERROR_SEND);
                          } catch (IllegalStateException ex) {
                              delegateException(ex, ERROR_SEND);
                              shutdown();
                          }
                      }
                  });

        Observable.create(new OnSubscribe<IMessage>() {
            @Override
            public void call(Subscriber<? super IMessage> subscriber) {
                try {
                    boolean occurredError = false;
                    while (running) {
                        IMessage box;
                        if (Worker.this.handler != null) {
                            box = handler.createMessage();
                        } else {
                            box = new ProtoMessage();
                        }
                        if (Worker.this.stream.read(box)) {
                            subscriber.onNext(box);
                        } else {
                            IllegalStateException e = new IllegalStateException(
                                    "A error occurred when receiving data.");
                            subscriber.onError(e);
                            occurredError = true;
                            break;
                        }
                    }
                    if (!occurredError) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).observeOn(Schedulers.io()).subscribeOn(Schedulers.immediate())
                  .subscribe(new Subscriber<IMessage>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          if (e instanceof TimeoutException) {
                              delegateException(e, ERROR_TIMEOUT);
                          } else if (e instanceof IllegalStateException) {
                              delegateException(e, ERROR_CLOSED);
                          } else if (e instanceof SocketException) {
                              if ("Connection reset".equals(e.getMessage())) {
                                  // 客户端断开。
                                  delegateException(e, ERROR_CLOSED);
                              } else {
                                  delegateException(e, ERROR_RECV);
                              }
                          } else {
                              delegateException(e, ERROR_RECV);
                          }

                          shutdown();
                      }

                      @Override
                      public void onNext(IMessage box) {
                          if (Worker.this.handler != null) {
                              Worker.this.handler.onRecv(box, getCtx());
                          }
                      }
                  });
    }

    private void delegateException(Throwable e, int errorTimeout) {
        if (handler != null) {
            handler.onError(errorTimeout, e, getCtx());
        }
    }

    @Data
    class SendMessage {

        Object       data;
        SendCallBack callBack;

        public SendMessage(Object data, SendCallBack callBack) {
            this.data = data;
            this.callBack = callBack;
        }
    }
}
