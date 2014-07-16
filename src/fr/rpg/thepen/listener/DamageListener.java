package fr.rpg.thepen.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class DamageListener implements Listener{
	private Main main;
	private Items items = main.items;
	public DamageListener(Main main){
		this.main = main;
	}
	@SuppressWarnings("deprecation")
    @EventHandler
	public void onDamageEntity(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Arrow){
			Arrow a = (Arrow) e.getDamager();
			if(a.getShooter() instanceof Player){
				Player p = (Player) a.getShooter();
				if(p.getItemInHand().equals(items.arbalete)){
					e.setDamage(8);
				}
			}
		}
	}
}
