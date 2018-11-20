package me.asu.socket.server;

public interface SendCallBack {

    void onComplete(boolean suc);

    void onError(Throwable throwable);
}
