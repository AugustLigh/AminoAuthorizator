package org.august.aminoAuthorizator.amino.WSRealization.websoket;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.august.AminoApi.dto.response.WSSMessage.MessageInformation;
import org.august.AminoApi.dto.response.WSSMessage.WSSMsgDTO;
import org.august.AminoApi.services.ClientApi;
import org.august.aminoAuthorizator.AminoAuthorizator;
import org.august.aminoAuthorizator.amino.WSRealization.listeners.CommandManager;
import org.august.aminoAuthorizator.amino.WSRealization.listeners.MessageManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler extends WebSocketListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
//    private static MessageHandler instance;
    private final ClientApi aminoClient;
    private final CommandManager commandManager = new CommandManager();
    private final MessageManager messageManager = new MessageManager();
    public static final int VALID_TYPE = 1000;
    public static final int VALID_MSG_TYPE = 0;
    public MessageHandler(ClientApi aminoclient) {
        this.aminoClient = aminoclient;
    }


    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
//        super.onOpen(webSocket, response);
        LOGGER.info("Connection opened!");
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        LOGGER.debug(reason);
        LOGGER.info("Connection closed!");
        AminoAuthorizator.getWssManager().connect();
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        LOGGER.info(String.valueOf(response != null ? response.code() : null));
        if (response != null && response.code() == 403) { // 403 is the HTTP status code for "Forbidden"
            try {
                Thread.sleep(300000); // 5 minutes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        webSocket.close(1000, "test");
        AminoAuthorizator.getWssManager().connect();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        LOGGER.info(text);
        WSSMsgDTO dtoObject = aminoClient.getGson().fromJson(text, WSSMsgDTO.class);
        MessageInformation chatEvent = dtoObject.getO();
        int type = dtoObject.getT();

        if (type == 10) return;
        if (type != VALID_TYPE) return;

        String content = chatEvent.getChatMessage().getContent();
        int msgType = chatEvent.getChatMessage().getType();

        if (chatEvent.getChatMessage().getAuthor().getUid().equals(AminoAuthorizator.getAuthDto().getAuid())){
            return;
        }

        if (msgType != VALID_MSG_TYPE) {
            return;
        }

        if (content.startsWith("!")) {
            String command = content.substring(1);
            commandManager.executeCommand(command, chatEvent);
        } else {
            messageManager.executePattern(content, chatEvent);
        }
    }
}
