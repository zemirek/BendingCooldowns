package me.literka.cooldowns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.util.Collision;
import com.projectkorra.projectkorra.board.BendingBoardManager;
import com.projectkorra.projectkorra.event.PlayerCooldownChangeEvent;
import com.projectkorra.projectkorra.util.ChatUtil;
import com.projectkorra.projectkorra.util.Cooldown;
import me.literka.BendingCooldowns;
import me.literka.cooldowns.CooldownStorage.Type;
import org.bukkit.entity.Player;

public class CooldownManager {

	private final Map<String, List<String>> groups = new HashMap<>();
	private final Map<Type, CooldownStorage> cooldownsByType = new HashMap<>();

	public CooldownManager() {
		for (Type type : Type.values()) {
			cooldownsByType.put(type, new CooldownStorage());
		}

		new CooldownLoader(this);
	}

	public Map<String, List<String>> getGroups() {
		return groups;
	}

	public CooldownStorage getCooldownStorage(Type type) {
		return cooldownsByType.get(type);
	}

	public void applyCooldowns(Player player, String abilityName, PlayerCooldownChangeEvent event, Type type) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		Map<String, Long> cooldowns = getCooldownStorage(type).get(abilityName);

		if (bPlayer == null || cooldowns == null)
			return;

		processCooldowns(bPlayer, cooldowns, event);
	}

	public void applyCollisionCooldowns(Collision collision) {
		applyCollisionCooldowns(collision.getAbilityFirst(), collision.getAbilitySecond());
		applyCollisionCooldowns(collision.getAbilitySecond(), collision.getAbilityFirst());
	}

	private void applyCollisionCooldowns(CoreAbility first, CoreAbility second) {
		BendingPlayer bPlayer = first.getBendingPlayer();
		CooldownStorage cooldownStorage = getCooldownStorage(Type.COLLISION);
		Map<String, Long> cooldowns = cooldownStorage.get(first.getName() + ":" + second.getName());

		if (cooldowns == null)
			cooldowns = cooldownStorage.get(first.getName());

		if (bPlayer == null || cooldowns == null)
			return;

		processCooldowns(bPlayer, cooldowns, null);
	}

	private void processCooldowns(BendingPlayer bPlayer, Map<String, Long> cooldowns, PlayerCooldownChangeEvent event) {
		boolean isCooldownEventUsed = event != null;

		for (Map.Entry<String, Long> e : cooldowns.entrySet()) {
			String ability = e.getKey();
			long cooldown = e.getValue();
			boolean isOriginal = isCooldownEventUsed && ability.equalsIgnoreCase(event.getAbility());

			long currentCooldown = !isOriginal
					? bPlayer.getCooldown(ability) - System.currentTimeMillis()
					: event.getCooldown();
			if (currentCooldown > cooldown)
				return;

			if (!isOriginal) {
				addCooldown(bPlayer, ability, cooldown, !isCooldownEventUsed);
			} else if (!BendingCooldowns.canSetCooldownEvent) {
				BendingCooldowns.plugin.getServer().getScheduler().runTaskLater(
						BendingCooldowns.plugin,
						() -> addCooldown(bPlayer, ability, cooldown - 50, false),
						1
				);
			} else {
				event.setCooldown(cooldown);
			}
		}
	}

	public void addCooldown(BendingPlayer bPlayer, String ability, long cooldown, boolean callEvent) {
		if (callEvent) {
			bPlayer.addCooldown(ability, cooldown);
			return;
		}

		if (cooldown <= 0)
			return;

		bPlayer.getCooldowns().put(ability, new Cooldown(cooldown + System.currentTimeMillis(), false));

		if (ability.equalsIgnoreCase(bPlayer.getBoundAbilityName()))
			ChatUtil.displayMovePreview(bPlayer.getPlayer());

		BendingBoardManager.updateBoard(bPlayer.getPlayer(), ability, true, 0);
	}
}