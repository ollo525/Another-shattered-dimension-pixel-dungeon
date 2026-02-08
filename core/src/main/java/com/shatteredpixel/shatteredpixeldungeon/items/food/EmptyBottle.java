package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class EmptyBottle extends Item {

	public int durability = 3;

	{
		image = ItemSpriteSheet.EMPTY_BOTTLE; // Indeks 13
		stackable = false;
	}

	@Override
	public String name() {
		return "Pusta butelka (" + durability + "/3)";
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add("NAPEŁNIJ");
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals("NAPEŁNIJ")) {
			if (Dungeon.level.water[hero.pos]) {
				
				// USUWANIE SIEBIE
				detach(hero.belongings.backpack);
				
				// SPAWNOWANIE PEŁNEJ
				WaterBottle water = new WaterBottle();
				water.durability = this.durability;
				if (!water.doPickUp(hero)) {
					Dungeon.level.drop(water, hero.pos).sprite.drop();
				}
				
				GLog.i("Napełniłeś butelkę.");
				hero.spendAndNext(1f);
				
			} else {
				GLog.w("Musisz stać w wodzie!");
			}
		} else {
			super.execute(hero, action);
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