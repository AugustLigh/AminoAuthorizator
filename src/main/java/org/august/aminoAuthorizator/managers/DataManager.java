package org.august.aminoAuthorizator.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.august.aminoAuthorizator.dataclass.PlayerData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataManager {

    private final File dataFile;
    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final JSONParser parser = new JSONParser();

    public DataManager(String filePath, String fileName) {
        dataFile = new File(filePath, fileName);
    }

    // Сохранение данных в файл
    public void saveData() {
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            }

            JSONArray jsonArray = new JSONArray();
            for (PlayerData data : playerDataMap.values()) {
                jsonArray.add(data.toJson());
            }

            try (FileWriter writer = new FileWriter(dataFile)) {
                writer.write(jsonArray.toJSONString());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка данных из файла
    public void loadData() {
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                Object obj = parser.parse(reader);
                JSONArray jsonArray = (JSONArray) obj;

                playerDataMap.clear();
                for (Object item : jsonArray) {
                    JSONObject json = (JSONObject) item;
                    PlayerData data = PlayerData.fromJson(json);
                    playerDataMap.put(data.getMinecraftName(), data);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPlayerData(PlayerData data) {
        playerDataMap.put(data.getMinecraftName(), data);
    }

    public PlayerData getPlayerData(String minecraftName) {
        return playerDataMap.get(minecraftName);
    }

    public boolean hasPlayerData(String minecraftName) {
        return playerDataMap.containsKey(minecraftName);
    }

    public Map<String, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }
}