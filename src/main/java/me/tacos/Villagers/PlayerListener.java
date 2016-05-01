package me.tacos.Villagers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerListener implements Listener {
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(Main.getMain().getLists().contains(e.getEntity().getUniqueId().toString())){
			e.setCancelled(true);
		}
	}

}
