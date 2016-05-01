package me.tacos.Villagers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	
	private static Main main;
	private FileConfiguration config;
	private List<String> zomls = new ArrayList();
	private Map<Integer, String> zoml = new HashMap<Integer, String>();
	
	
	
	public void onEnable(){
		saveDefaultConfig();
		config = getConfig();
		loadAllMobs();
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		main = this;
	}
	
	
	
	public void onDisable(){
		List<String> list = new ArrayList<>();
		for(String ent : zoml.values()){
			list.add(ent);
		}
		for(World world:getServer().getWorlds()){
			for(Entity ent :world.getEntities()){
				if(list.contains(ent.getUniqueId().toString())){
					ent.remove();
				}
			}
		}
	}
	
	public static Main getMain(){
		return main;
	}
	
	public Map<Integer, String> getList(){
		return zoml;
	}
	
	public List<String> getLists(){
		return zomls;
	}
	
	public void loadAllMobs(){
		if(config.getConfigurationSection("moblist") != null){
			for(String id : config.getConfigurationSection("moblist").getValues(false).keySet()){
				EntityType type;
				Location loc = strToLoc(config.getString("moblist." + id));
			    type = EntityType.VILLAGER;
				Villager villager = (Villager) getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, type);
				villager.setCanPickupItems(false);
				zomls.add( villager.getUniqueId().toString());
				zoml.put(Integer.valueOf(id), villager.getUniqueId().toString());
			}
		}
	}
	
	public Location strToLoc(String str){
    	String[] split = str.split(Pattern.quote(","));
    	Location loc = new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
		return loc;
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("villager")){
				if(args.length == 1){
					if(!(sender.hasPermission("villagers.create") || sender instanceof Player)){
						return false;
					}
					Player player = (Player) sender;
					int a = Integer.valueOf(args[0]);
					if(zoml.containsKey(a)){
						return false;
					}
				    Villager villager = (Villager) getServer().getWorld(player.getWorld().getName()).spawnEntity(player.getLocation(), EntityType.VILLAGER);
					villager.setCanPickupItems(false);
					zoml.put(a, villager.getUniqueId().toString());
					zomls.add(villager.getUniqueId().toString());
					config.set("moblist." + a, player.getWorld().getName()+","+player.getLocation().getBlockX()+","+player.getLocation().getBlockY()+","+player.getLocation().getBlockZ());
					saveConfig();
					player.sendMessage("§aЖитель под номером §c" + a + " §aсоздан!");
				}
				return true;
			}else if(cmd.getName().equalsIgnoreCase("delvillager")){
					if(args.length == 1){
						if(!(sender.hasPermission("villagers.delete") || zoml.containsKey(Integer.valueOf(args[0])))){
							return false;
						}
						String uuid = zoml.get(Integer.valueOf(args[0]));
						for(World world:getServer().getWorlds()){
							for(Entity ent :world.getEntities()){
								if(ent.getUniqueId().toString().equals(uuid)){
									ent.remove();
								}
							}
						}
						zoml.remove(Integer.valueOf(args[0]));
						config.set("moblist." + args[0], null);
						saveConfig();
						sender.sendMessage("§4Житель под номером §6" + args[0] + " §4удален!");
						}
					}
		return false;
	}

}
