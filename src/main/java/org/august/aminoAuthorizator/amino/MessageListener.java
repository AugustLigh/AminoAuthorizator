package org.august.aminoAuthorizator.amino;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.august.AminoApi.dto.response.WSSMessage.MessageInformation;

import org.august.aminoAuthorizator.AminoAuthorizator;
import org.august.aminoAuthorizator.amino.WSRealization.decorators.Pattern;
import org.august.aminoAuthorizator.amino.WSRealization.decorators.PatternHandler;
import org.august.aminoAuthorizator.dataclass.PlayerData;
import org.august.aminoAuthorizator.managers.AuthManager;
import org.august.aminoAuthorizator.managers.DataManager;
import org.august.aminoAuthorizator.util.RandomJoke;
import org.bukkit.*;
import org.bukkit.entity.Player;

@PatternHandler
public class MessageListener {

    @Pattern(".*")
    public void codeChecker(MessageInformation message) {
        String content = message.getChatMessage().getContent().toUpperCase();

        if (content.length() == 4) { // TODO 4 - количество символов. Магическое число. лучше вынести в конфиг.
            DataManager dataManager = AminoAuthorizator.getDataManager();
            AuthManager authManager = AminoAuthorizator.getAuthManager();
            Player player = authManager.findPlayerByCode(content);

            if (player != null) {
                if (dataManager.getPlayerDataMap().containsKey(player.getName())) { // Если игрок есть в бд
                    PlayerData playerData = dataManager.getPlayerData(player.getName());

                    if (!playerData.getAminoUserId().equals(message.getChatMessage().getAuthor().getUid())) {
                        player.kick(Component.text("Неверный аккаунт").color(TextColor.color(255, 0, 0)));
                        return; // Прерывание выполнения
                    }

                } else { // Если игрока нет - то записываем айди
                    PlayerData playerData = new PlayerData(player.getName(), message.getChatMessage().getAuthor().getUid());
                    dataManager.addPlayerData(playerData);
                    dataManager.saveData();
                }

                // Обработка успешного логина
                handleSuccessfulLogin(player);
                authManager.removePlayer(player);
            } else {
                // Обработка случая, если игрок не найден
                // Нужно было для дебага. Сейчас можно удалить
                Bukkit.getLogger().warning("Игрок с кодом " + content + " не найден.");
            }
        }
    }

    // TODO логику лучше разделить. но я устал на сегондя
    public void handleSuccessfulLogin(Player player) {
        if (player != null) {
            // Отправить уведомление игроку
            player.sendTitle("§aЛогин успешен!", RandomJoke.getRandomPhrase(), 10, 70, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }
}
