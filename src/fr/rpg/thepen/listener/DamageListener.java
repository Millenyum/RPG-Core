package fr.rpg.thepen.listener;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class DamageListener implements Listener{

	Main main;
	Items items = main.items;
	
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
		if(e.getDamager() instanceof Fireball){
			Fireball a = (Fireball) e.getDamager();
			if(a.getShooter() instanceof Player){
				Player p = (Player) a.getShooter();
				if(p.getItemInHand().equals(items.m_parchemin_2)){
					if(p != e.getEntity()){
						e.setDamage(6);
					}
				}
			}
		}
	}
}
