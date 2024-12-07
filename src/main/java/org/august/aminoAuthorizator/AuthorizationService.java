package org.august.aminoAuthorizator;

import org.august.AminoApi.dto.response.AuthDto;
import org.august.AminoApi.services.ClientApi;

import java.util.concurrent.*;

public class AuthorizationService {
    private final ClientApi aminoClient;
    private final int maxAttempts;
    private final long timeoutSeconds;

    public AuthorizationService(ClientApi aminoClient, int maxAttempts, long timeoutSeconds) {
        this.aminoClient = aminoClient;
        this.maxAttempts = maxAttempts;
        this.timeoutSeconds = timeoutSeconds;
    }

    public AuthDto authorize(String email, String password) throws Exception {
        int attempt = 0;
        CompletableFuture<AuthDto> future = new CompletableFuture<>();

        while (attempt < maxAttempts) {
            attempt++;
            try {
                AuthDto authDto = CompletableFuture.supplyAsync(() -> {
                    try {
                        return aminoClient.authorization(email, password);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                }).get(timeoutSeconds, TimeUnit.SECONDS);

                return authDto; // Успешная авторизация
            } catch (TimeoutException | ExecutionException e) {
                if (attempt == maxAttempts) {
                    throw new Exception("Max login attempts reached", e);
                }
                Thread.sleep(5000); // Задержка перед повтором
            }
        }
        throw new Exception("Authorization failed after all attempts.");
    }
}

