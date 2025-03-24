package me.literka.cooldowns;

import java.util.HashMap;
import java.util.Map;

public class CooldownStorage {

	private final Map<String, Map<String, Long>> cooldowns;

	public CooldownStorage() {
		cooldowns = new HashMap<>();
	}

	public Map<String, Long> createEntryMap(String key) {
		return cooldowns.computeIfAbsent(key, s -> new HashMap<>());
	}

	public Map<String, Long> get(String key) {
		return cooldowns.get(key);
	}

	public enum Type {
		START, PROGRESS, END, COOLDOWN, COLLISION
	}
}