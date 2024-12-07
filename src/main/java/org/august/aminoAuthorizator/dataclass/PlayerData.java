package org.august.aminoAuthorizator.dataclass;

import org.json.simple.JSONObject;

public class PlayerData {
    private final String minecraftName;
    private final String aminoUserId;

    public PlayerData(String name, String aminoUserId) {
        this.minecraftName = name;
        this.aminoUserId = aminoUserId;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public String getAminoUserId() {
        return aminoUserId;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("minecraftName", minecraftName);
        json.put("aminoUserId", aminoUserId);
        return json;
    }

    public static PlayerData fromJson(JSONObject json) {
        String minecraftName = json.get("minecraftName").toString();
        String aminoUserId = json.get("aminoUserId").toString();
        return new PlayerData(minecraftName, aminoUserId);
    }
}
