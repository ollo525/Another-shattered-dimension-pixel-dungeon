package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class BigWaterBottle extends Food {

	public int durability = 2;

	{
		image = ItemSpriteSheet.BIG_WATER_BOTTLE; // Indeks 14
		stackable = false;
		energy = Hunger.HUNGRY * 0.8f;
	}

@Override
public String name() {
    return Messages.get(this, "name_dur", durability);
}
	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero); // Nawodnienie
		durability--;

		// USUWANIE SIEBIE
		detach(hero.belongings.backpack);

		if (durability > 0) {
			// SPAWNOWANIE PUSTEJ
			EmptyBottle empty = new Pasty();
			empty.durability = this.durability; // Przekazujemy "życie" butelki
			if (!empty.doPickUp(hero)) {
				com.shatteredpixel.shatteredpixeldungeon.Dungeon.level.drop(empty, hero.pos).sprite.drop();
			}
		} else {
			GLog.w( Messages.get(this, "shatters") );
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