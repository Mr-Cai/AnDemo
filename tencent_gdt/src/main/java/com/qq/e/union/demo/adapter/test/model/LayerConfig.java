package com.qq.e.union.demo.adapter.test.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * name : SELF_SELLING // 三方渠道名称
 * posId : 1041 // 三方渠道posId
 */
public class LayerConfig {
<<<<<<< HEAD
  private final String posId;
  private final List<NetworkConfig> networkConfigs;
=======
  private String posId;
  private List<NetworkConfig> networkConfigs;
>>>>>>> 317cf34fed5d7c1141d569e91395ed6661807d05

  public LayerConfig(JSONObject layerConfig) {
    posId = layerConfig.optString("phyPosId");
    networkConfigs = new ArrayList<>();
    JSONArray array = layerConfig.optJSONArray("network");
    if (array != null && array.length() >= 0) {
      for (int i = 0; i < array.length(); i++) {
        JSONObject config = array.optJSONObject(i);
        networkConfigs.add(new NetworkConfig(config));
      }
    }
  }

  public String getPosId() {
    return posId;
  }

  public List<NetworkConfig> getNetworkConfigs() {
    return networkConfigs;
  }
}
