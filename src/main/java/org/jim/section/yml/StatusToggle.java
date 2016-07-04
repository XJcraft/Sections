package org.jim.section.yml;

import java.io.File;
import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jim.section.Section;
import org.jim.section.SectionPlugin;

public class StatusToggle {

    private Section section;
    private File dir;
    //private boolean haveMi = false;

    private boolean init = false;

    public StatusToggle(Section section) {
        super();
        this.section = section;
/*		dir = new File(SectionPlugin.instance.getDataFolder(), "status");
        if (!dir.exists())
			dir.mkdirs();
		try{

			Class.forName("uk.co.tggl.pluckerpluck.multiinv.listener.MIPlayerListener");
			haveMi = true;
		}catch(Exception e){
			SectionPlugin.instance.getLogger().info("未加载多背包插件，启动自带的背包切换...");
		}*/
    }

    public void init() {
        if (!init) {
            init = true;
            try {
                Class.forName("uk.co.tggl.pluckerpluck.multiinv.listener.MIPlayerListener");
            } catch (Exception e) {
                SectionPlugin.instance.getLogger().warning("未加载多背包插件，忽略...");
            }
        }
    }

    public void savePlayer(Player player) {
        if (miChange(section.getSpawn().getWorld().getName(), player.getWorld().getName())) {
            return;
        }
        // save section
        File saveFile = new File(dir, section.getName() + "_"
                + player.getName() + ".yml");
        try {
            save(saveFile, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // load default
        File loadFile = new File(dir, "default_" + player.getName() + ".yml");
        try {
            load(loadFile, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlayer(Player player) {
        if (miChange(section.getSpawn().getWorld().getName(), player.getWorld().getName())) {
            return;
        }
        // save default
        try {
            save(new File(dir, "default_" + player.getName() + ".yml"), player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // load section
        try {
            load(new File(dir, section.getName() + "_" + player.getName()
                    + ".yml"), player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void save(File file, Player player) throws Exception {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("ender-chest", player.getEnderChest().getContents());
        yaml.set("inventory", player.getInventory().getContents());
        yaml.set("armor-contents", player.getInventory().getArmorContents());
        yaml.set("potion-effects", player.getActivePotionEffects());
        yaml.set("health", player.getHealth());
        yaml.set("food-level", player.getFoodLevel());
        yaml.set("game-mode", player.getGameMode().getValue());
        yaml.set("level", player.getLevel());
        yaml.save(file);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void load(File file, Player player) throws Exception {
        if (!file.exists()) {
            player.getEnderChest().clear();
            player.getInventory().clear();
            for (PotionEffect p : player.getActivePotionEffects())
                player.removePotionEffect(p.getType());
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(file);
        player.getEnderChest().setContents(
                yaml.getList("ender-chest").toArray(new ItemStack[0]));
        player.getInventory().setArmorContents(
                yaml.getList("armor-contents").toArray(new ItemStack[0]));
        player.getInventory().setContents(
                yaml.getList("inventory").toArray(new ItemStack[0]));
        for (PotionEffect localPotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(localPotionEffect.getType());
        }
        player.addPotionEffects((Collection<PotionEffect>) yaml
                .getList("potion-effects"));

        player.setHealth(yaml.getDouble("health"));
        player.setFoodLevel(yaml.getInt("food-level"));
        player.setLevel(yaml.getInt("level"));
        player.setGameMode(GameMode.getByValue(yaml.getInt("game-mode")));
    }

    public void remove(String player) {
        File f = new File(dir, section.getName() + "_" + player + ".yml");
        if (f.exists())
            f.delete();
    }

    //是否需要使用MI进行背包切换
    private boolean miChange(String world1, String world2) {
		/*if(!haveMi)
			return false;
		if(StringUtils.equals(world1, world2))
			return false;
		String g1 = MIPlayerListener.getGroup(world1);
		String g2 = MIPlayerListener.getGroup(world2);
		if(StringUtils.equals(g1, g2))
			return false;*/
        init();
        return true;
    }
}
