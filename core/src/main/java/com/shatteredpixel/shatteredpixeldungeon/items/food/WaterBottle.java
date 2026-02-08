package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class WaterBottle extends Food {

	public int durability = 3;

	{
		image = ItemSpriteSheet.WATER_BOTTLE; // Indeks 14
		stackable = false;
		energy = Hunger.HUNGRY * 0.55f;
	}

	@Override
	public String name() {
		return "Butelka wody (" + durability + "/3)";
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero); // Nawodnienie
		durability--;

		// USUWANIE SIEBIE
		detach(hero.belongings.backpack);

		if (durability > 0) {
			// SPAWNOWANIE PUSTEJ
			EmptyBottle empty = new EmptyBottle();
			empty.durability = this.durability; // Przekazujemy "życie" butelki
			if (!empty.doPickUp(hero)) {
				com.shatteredpixel.shatteredpixeldungeon.Dungeon.level.drop(empty, hero.pos).sprite.drop();
			}
		} else {
			GLog.w("The bottle shatters as you swallow the last drop!");
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("durability", durability);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		durability = bundle.getInt("durability");
	}
}