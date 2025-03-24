package me.literka;

import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.event.*;
import me.literka.cooldowns.CooldownStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GeneralListener implements Listener {

	@EventHandler
	public void onStart(AbilityStartEvent event) {
		Ability ability = event.getAbility();
		Player player = ability.getPlayer();
		if (player == null)
			return;

		BendingCooldowns.cooldownManager.applyCooldowns(player, ability.getName(), null, CooldownStorage.Type.START);
	}

	@EventHandler
	public void onProgress(AbilityProgressEvent event) {
		Ability ability = event.getAbility();
		Player player = ability.getPlayer();
		if (player == null)
			return;

		BendingCooldowns.cooldownManager.applyCooldowns(player, ability.getName(), null, CooldownStorage.Type.PROGRESS);
	}

	@EventHandler
	public void onEnd(AbilityEndEvent event) {
		Ability ability = event.getAbility();
		Player player = ability.getPlayer();
		if (player == null)
			return;

		BendingCooldowns.cooldownManager.applyCooldowns(player, ability.getName(), null, CooldownStorage.Type.END);
	}

	@EventHandler
	public void onCooldown(PlayerCooldownChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getResult() != PlayerCooldownChangeEvent.Result.ADDED || player == null)
			return;

		BendingCooldowns.cooldownManager.applyCooldowns(player, event.getAbility(), event, CooldownStorage.Type.COOLDOWN);
	}

	@EventHandler
	public void onCollision(AbilityCollisionEvent event) {
		BendingCooldowns.cooldownManager.applyCollisionCooldowns(event.getCollision());
	}

}