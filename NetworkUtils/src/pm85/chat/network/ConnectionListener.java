package pm85.chat.network;

public interface ConnectionListener {
    void onConnectionReady(Connection connection);
    void onReceiveString(Connection connection, String string);
    void onDisconnect(Connection connection);
    void onException(Connection connection, Exception e);
}
