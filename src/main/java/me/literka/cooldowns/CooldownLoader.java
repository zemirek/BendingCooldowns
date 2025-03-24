package me.literka.cooldowns;

import me.literka.BendingCooldowns;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import me.literka.cooldowns.CooldownStorage.Type;

import java.util.*;

public class CooldownLoader {

	private final CooldownManager cooldownManager;

	public CooldownLoader(CooldownManager cooldownManager) {
		this.cooldownManager = cooldownManager;

		FileConfiguration config = BendingCooldowns.config.get();
		loadGroups(config);
		loadCooldowns(config);
	}

	private void loadGroups(FileConfiguration config) {
		ConfigurationSection groupSection = config.getConfigurationSection("Groups");
		for (String groupName : groupSection.getKeys(false)) {
			String[] groupAbilities = groupSection.getString(groupName).split("\\s*,\\s*");

			cooldownManager.getGroups().put(groupName, List.of(groupAbilities));
		}
	}

	private void loadCooldowns(FileConfiguration config) {
		ConfigurationSection cooldownSection = config.getConfigurationSection("Cooldowns");
		for (String keyAbilOrGroup : cooldownSection.getKeys(false)) {
			for (String extractedAbility : extractAbilitiesFromGroup(keyAbilOrGroup)) {
				processCooldownEntries(extractedAbility, cooldownSection.getStringList(keyAbilOrGroup));
			}
		}
	}

	private List<String> extractAbilitiesFromGroup(String key) {
		return key.startsWith("$") ? cooldownManager.getGroups().get(key.substring(1)) : List.of(key);
	}

	private void processCooldownEntries(String ability, List<String> cooldownEntries) {
		for (String cooldownInfo : cooldownEntries) {
			CooldownData cooldownData = parseCooldownData(cooldownInfo);
			List<String> extracted = extractAbilitiesFromGroup(cooldownData.abilityOrGroup());
			if (cooldownData.flags().isEmpty()) {
				storeCooldownEntries(cooldownManager.getCooldownStorage(Type.START), ability, extracted, cooldownData.cooldown());
				continue;
			}

			assignCooldownsByFlags(ability, extracted, cooldownData.flags(), cooldownData.cooldown());
		}
	}

	private CooldownData parseCooldownData(String input) {
		int abilityFlagSeparator = input.indexOf(" ");
		int flagsCooldownSeparator = input.lastIndexOf(" ");
		String ability = input.substring(0, abilityFlagSeparator);
		long cooldown = Long.parseLong(input.substring(flagsCooldownSeparator + 1));
		if (abilityFlagSeparator == flagsCooldownSeparator)
			return new CooldownData(ability, new HashMap<>(), cooldown);

		String[] parts = input.substring(abilityFlagSeparator + 2, flagsCooldownSeparator).split("\\s*-");
		Map<String, List<String>> flags = new HashMap<>();
		for (String part : parts) {
			if (!part.contains("(")) {
				flags.put(part, new ArrayList<>());
				continue;
			}

			int argStart = part.indexOf('(');
			int argEnd = part.lastIndexOf(')');

			if (argStart > argEnd)
				continue;

			String flag = part.substring(0, argStart);
			List<String> args = Arrays.asList(part.substring(argStart + 1, argEnd).split("\\s*,\\s*"));
			flags.put(flag, args);
		}

		return new CooldownData(ability, flags, cooldown);
	}

	private void assignCooldownsByFlags(String ability, List<String> extracted, Map<String, List<String>> flags, long cooldown) {
		if (flags.containsKey("s")) storeCooldownEntries(cooldownManager.getCooldownStorage(Type.START), ability, extracted, cooldown);
		if (flags.containsKey("p")) storeCooldownEntries(cooldownManager.getCooldownStorage(Type.PROGRESS), ability, extracted, cooldown);
		if (flags.containsKey("e")) storeCooldownEntries(cooldownManager.getCooldownStorage(Type.END), ability, extracted, cooldown);
		if (flags.containsKey("c")) storeCooldownEntries(cooldownManager.getCooldownStorage(Type.COOLDOWN), ability, extracted, cooldown);
		if (flags.containsKey("C")) {
			CooldownStorage cooldownStorage = cooldownManager.getCooldownStorage(Type.COLLISION);
			List<String> args = flags.get("C");
			if (args == null || args.size() != 1) {
				storeCooldownEntries(cooldownStorage, ability, extracted, cooldown);
				return;
			}

			for (String extractedAbility : extractAbilitiesFromGroup(args.get(0))) {
				storeCooldownEntries(cooldownStorage, ability + ":" + extractedAbility, extracted, cooldown);
			}
		}
	}

	private void storeCooldownEntries(CooldownStorage cooldownStorage, String ability, List<String> extracted, long cooldown) {
		Map<String, Long> map = cooldownStorage.createEntryMap(ability);
		for (String extractedAbility : extracted) {
			map.put(extractedAbility, cooldown);
		}
	}

	public record CooldownData(String abilityOrGroup, Map<String, List<String>> flags, long cooldown) {}

}