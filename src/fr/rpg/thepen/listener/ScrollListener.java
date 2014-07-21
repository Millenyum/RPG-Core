package fr.rpg.thepen.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.slikey.effectlib.effect.WarpEntityEffect;
import de.slikey.effectlib.util.ParticleEffect;
import fr.rpg.thepen.InvisibilityEffect;
import fr.rpg.thepen.Items;
import fr.rpg.thepen.Main;

public class ScrollListener implements Listener{
	private Main main;
	private Items items = main.items;
	public ScrollListener(Main main){
		this.main = main;
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract2(final PlayerInteractEvent e){
		if(e.getPlayer().getItemInHand() != null){
			if(e.getPlayer().getItemInHand().getItemMeta() != null){
				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_1)){
					if(main.getMana(e.getPlayer()) >= 25){
						WarpEntityEffect effect = new WarpEntityEffect(main.effectmanager, e.getPlayer());
						effect.start();
						double x = main.getConfig().getDouble(main.getRace(e.getPlayer()).toLowerCase() + "s.x");
						double y = main.getConfig().getDouble(main.getRace(e.getPlayer()).toLowerCase() + "s.y");
						double z = main.getConfig().getDouble(main.getRace(e.getPlayer()).toLowerCase() + "s.z");
						float yaw = main.getConfig().getInt(main.getRace(e.getPlayer()).toLowerCase() + "s.yaw");
						float pitch = main.getConfig().getInt(main.getRace(e.getPlayer()).toLowerCase() + "s.pitch");
						Location loc = new Location(e.getPlayer().getWorld(), x, y, z, yaw, pitch);
						e.getPlayer().teleport(loc);
						effect.start();
						main.setMana(e.getPlayer(), main.getMana(e.getPlayer()) - 25);
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 25 points de mana.");
					}
					e.setCancelled(true);
					e.getPlayer().updateInventory();
				}
				////////////////////////////////////////////////////////////////////////////////////////
				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_2)){
					if(main.getMana(e.getPlayer()) >= 20){
						Fireball fire = e.getPlayer().launchProjectile(Fireball.class);
						e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 1000));
						fire.setVelocity(e.getPlayer().getLocation().getDirection().multiply(3));
						main.setMana(e.getPlayer(), main.getMana(e.getPlayer()) - 20);
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 20 points de mana.");
					}
					e.setCancelled(true);
					e.getPlayer().updateInventory();
				}

				//////////////////////////////////////////////////////////////////////////////////////

				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_3)){
					if(main.getMana(e.getPlayer()) >= 45){
						List<Entity> entitylist = e.getPlayer().getNearbyEntities(8, 8, 8);
						for(int i = 0; i < entitylist.size(); i++){
							if(entitylist.get(i) instanceof LivingEntity){
								LivingEntity en = (LivingEntity) entitylist.get(i);
								en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 250, 0));
								WarpEntityEffect effect = new WarpEntityEffect(main.effectmanager, e.getPlayer());
								effect.particle = ParticleEffect.ANGRY_VILLAGER;
								effect.start();
								main.setMana(e.getPlayer(), main.getMana(e.getPlayer()) - 45);
							}
						}
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 45 points de mana.");
					}
					e.setCancelled(true);
					e.getPlayer().updateInventory();
				}

				////////////////////////////////////////////////////////////////////////////////////

				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_4)){
					if(main.getMana(e.getPlayer()) >= 50){
						main.setMana(e.getPlayer(), main.getMana(e.getPlayer()) - 50);
						final Location loc = e.getPlayer().getLocation();
						final int id = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
							int radius = 3;

							@Override
							public void run() {
								main.createCyl(loc, radius, Material.FIRE);
								main.createCyl(loc, radius-1, Material.AIR);
								radius ++;

							}
						}, 0L, 10L).getTaskId();
						Bukkit.getScheduler().runTaskLater(main, new Runnable() {

							@Override
							public void run() {
								main.createCyl(loc, 3 + 5, Material.AIR);
								Bukkit.getScheduler().cancelTask(id);
							}
						}, 50L);
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 50 points de mana.");
					}
					e.setCancelled(true);
					e.getPlayer().updateInventory();
				}

				/////////////////////////////////////////////////////////////////////////////

				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_5)){
					if(main.getMana(e.getPlayer()) >= 55){
						main.setMana(e.getPlayer(), main.getMana(e.getPlayer()) - 55);
						List<Entity> entitylist = e.getPlayer().getNearbyEntities(5, 5, 5);
						for(int i = 0; i < entitylist.size(); i++){
							entitylist.get(i).setVelocity(new Vector(0.0, 1.0, 0.0));
							Location loc = e.getPlayer().getLocation();
							e.getPlayer().getWorld().playEffect(loc, Effect.STEP_SOUND, Material.DIRT);

						}
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 50 points de mana.");
					}

					e.setCancelled(true);;
					e.getPlayer().updateInventory();
				}

				////////////////////////////////////////////////////////////////////////////////////

				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_6)){
					if(main.getMana(e.getPlayer()) >= 30){
						main.setMana(e.getPlayer(), main.getMana(e.getPlayer()) - 30);
						e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 2));
						InvisibilityEffect effect = new InvisibilityEffect(main.effectmanager, e.getPlayer().getLocation());
						effect.visibleRange = 10;
						effect.start();
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 30 points de mana.");
					}
					e.setCancelled(true);;
					e.getPlayer().updateInventory();
				}

				////////////////////////////////////////////////////////////////////////////////////

				if(e.getPlayer().getItemInHand().getItemMeta().equals(items.m_parchemin_7)){
					if(main.getMana(e.getPlayer()) >= 30){
					e.getPlayer().setHealth(20);
					e.getPlayer().setFoodLevel(20);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 2.0, 0.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 0.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 0.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 0.0, 0.0), 10);

					ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, 0.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(0.0, 0.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, 0.0, 0.0), 10);

					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 2.0, 0.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 1.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(0.0, 1.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().add(1.0, 1.0, 0.0), 10);

					ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, -1.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(0.0, -1.0, 1.0), 10);
					ParticleEffect.HEART.display(e.getPlayer().getLocation().subtract(1.0, -1.0, 0.0), 10);

					e.setCancelled(true);
					e.getPlayer().updateInventory();
					}
					else{
						e.getPlayer().sendMessage(ChatColor.GOLD + "[RPG] " + ChatColor.DARK_RED + "Tu n'as pas assez de mana... Ce sort coûte 30 points de mana.");
					}
				}
			}
		}
	}
}
