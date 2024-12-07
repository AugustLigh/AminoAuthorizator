package org.august.aminoAuthorizator.amino.WSRealization.websoket;

import okhttp3.*;
import org.apache.http.client.utils.URIBuilder;
import org.august.AminoApi.dto.intermediate.ProxySettings;
import org.august.AminoApi.generators.SigGenerator;
import org.august.AminoApi.services.ClientApi;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WSSManager {

    private static final String BASE_URL = "ws://ws2.aminoapps.com";
    private static final String SIGN_BODY_PARAM = "signbody";
    private static final String NDC_DEVICE_ID_HEADER = "NDCDEVICEID";
    private static final String NDC_AUTH_HEADER = "NDCAUTH";
    private static final String NDC_MSG_SIG_HEADER = "NDC-MSG-SIG";

    private final OkHttpClient okHttpClient;
    private final ClientApi aminoClient;
    private final MessageHandler customWSListener;

    public WSSManager(ClientApi aminoClient) {
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(8L, TimeUnit.SECONDS)
                .writeTimeout(8L, TimeUnit.SECONDS)
                .pingInterval(60000L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();

        this.aminoClient = aminoClient;
        this.customWSListener = new MessageHandler(aminoClient);
    }

    public WSSManager(ClientApi aminoClient, ProxySettings proxy) {
        this.okHttpClient = createProxyConnection(proxy);
        this.aminoClient = aminoClient;
        this.customWSListener = new MessageHandler(aminoClient);
    }

    public OkHttpClient createProxyConnection(ProxySettings proxy) {
        final String host = proxy.getHost();
        final int port = proxy.getPort();

        final String password = proxy.getPassword();
        final String username = proxy.getUsername();

        Proxy okProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));

        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        return new OkHttpClient.Builder()
                .proxy(okProxy)
                .proxyAuthenticator(proxyAuthenticator)
                .connectTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(8L, TimeUnit.SECONDS)
                .writeTimeout(8L, TimeUnit.SECONDS)
                .pingInterval(60000L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    public WebSocket connect() {
        String deviceId = aminoClient.getDeviceId();
        String sid = aminoClient.getSid();

        final long timestamp = System.currentTimeMillis();
        final String signBody = deviceId + "|" + timestamp;
        String url = buildURI(signBody);

        return okHttpClient.newWebSocket(createRequest(url, deviceId, sid, signBody), customWSListener);
    }
    private String buildURI(String signBody) {
        URI uri;
        try {
            uri = new URIBuilder(BASE_URL)
                    .addParameter(SIGN_BODY_PARAM, signBody)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri.toString();
    }
    public Request createRequest(String websocketURL, String deviceId, String sid, String signBody) {
        return new Request.Builder()
                .url(websocketURL)
                .addHeader(NDC_DEVICE_ID_HEADER, deviceId)
                .addHeader(NDC_AUTH_HEADER, sid)
                .addHeader(NDC_MSG_SIG_HEADER, SigGenerator.genSig(signBody))
                .build();
    }

    public void heartbeat(WebSocket socket) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            String pingMessage = "{\"t\":116,\"o\":{\"threadChannelUserInfoList\":[]}}";
            socket.send(pingMessage);
        };
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
    }
}
