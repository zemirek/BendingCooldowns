package me.literka;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.Config;
import me.literka.cooldowns.CooldownManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class BendingCooldowns extends JavaPlugin {

	public static BendingCooldowns plugin;
	public static Config config;
	public static boolean canSetCooldownEvent;
	public static CooldownManager cooldownManager;

	@Override
	public void onEnable() {
		plugin = this;
		config = new Config(new File("bendingcooldowns.yml"));
		addConfigDefaults();
		canSetCooldownEvent = getPKVersion() > 1113;
		cooldownManager = new CooldownManager();

		getServer().getPluginManager().registerEvents(new GeneralListener(), this);

		new BCooldownsCommand();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}

	public static void onReload() {
		HandlerList.unregisterAll(plugin);

		config.reload();
		addConfigDefaults();
		cooldownManager = new CooldownManager();
		plugin.getServer().getPluginManager().registerEvents(new GeneralListener(), plugin);
	}

	private static void addConfigDefaults() {
		FileConfiguration c = config.get();
		if (!c.contains("Groups")) {
			c.addDefault("Groups.AirAbilities", "AirBlast, AirSwipe, AirBurst");
		}

		if (!c.contains("Cooldowns")) {
			c.addDefault("Cooldowns.FireBlast", List.of("FireBall 2000"));
			c.addDefault("Cooldowns.FireBall", List.of("FireBlast 1000"));
			c.addDefault("Cooldowns.LightningBurst", List.of("Discharge -c 15000", "Lightning -c 5000"));
			c.addDefault("Cooldowns.Lightning", List.of("Discharge -s 5000"));
			c.addDefault("Cooldowns.Discharge", List.of("Lightning -s 5000", "Discharge -s 20000"));
			c.addDefault("Cooldowns.AirSwipe", List.of("AirBlade 3000"));
			c.addDefault("Cooldowns.AirBlade", List.of("AirSwipe -C(AirSwipe) 3000"));
		}
		c.options().header("""
				Groups:
				  You can create groups and use them instead of ability names.
				  To use a group you should add "$" before the name of the group
				
				Flags:
				  s: When the ability starts
				  p: When the ability "progresses"
				  e: When the ability ends
				  c: When the ability goes on cooldown normally
				  C: When the ability collides (optional: You can specify which ability or group of abilities it needs to collide with for example: "-C(FireBlast)")
				Include multiple flags like this:
				  EarthBlast -e -p -s 400
				  Catapult -s -c 6000
				""");
		config.save();
	}

	private static int getPKVersion() {
		String[] split = ProjectKorra.plugin.getDescription().getVersion().split("\\.", 3);

		int major = Integer.parseInt(split[0]);
		int minor = split.length > 1 ? Integer.parseInt(split[1]) : 0;
		int fix = split.length > 2 ? Integer.parseInt(split[2]) : 0;

		return major * 1000 + minor * 10 + fix;
	}
}