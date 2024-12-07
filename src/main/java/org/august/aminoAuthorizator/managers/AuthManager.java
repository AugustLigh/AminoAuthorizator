package org.august.aminoAuthorizator.managers;

import org.august.aminoAuthorizator.util.AuthCodeGenerator;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    private final HashMap<Player, String> playerCodes = new HashMap<>();

    public String generateCode(Player player) {

        String code;
        // проверка на уникальность
        do {
            code = AuthCodeGenerator.generateRandomCode(4); // TODO 4 - количество символов. Магическое число. лучше вынести в конфиг.
        } while (playerCodes.containsValue(code));

        playerCodes.put(player, code);
        return code;
    }

//    public boolean checkCode(String code, boolean removeAfterCheck) {
//        Player player = findPlayerByCode(code);
//        if (player != null) {
//            if (removeAfterCheck) {
//                playerCodes.remove(player);
//            }
//            return true;
//        }
//        return false;
//    }

    public void removePlayer(Player player) {
        playerCodes.remove(player);
    }

    public boolean isPlayerAuthorized(Player player) {
        return !playerCodes.containsKey(player);
    }

    public Player findPlayerByCode(String code) {
        return playerCodes.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(code))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
