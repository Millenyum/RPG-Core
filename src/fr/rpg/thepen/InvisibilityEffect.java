package fr.rpg.thepen;

import org.bukkit.Location;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LocationEffect;
import de.slikey.effectlib.util.ParticleEffect;
import de.slikey.effectlib.util.RandomUtils;

public class InvisibilityEffect extends LocationEffect {

	public InvisibilityEffect(EffectManager effectManager, Location location) {
		super(effectManager, location);
		type = EffectType.REPEATING;
		period = 10;
		iterations = 200;
	}

	@Override
	public void onRun() {
		for (int i = 0; i < 40; i++) {
			location.add(RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * 0.6d));
			location.add(0, RandomUtils.random.nextFloat() * 2, 0);
			ParticleEffect.FIREWORKS_SPARK.display(location, visibleRange);
		}
	}

}