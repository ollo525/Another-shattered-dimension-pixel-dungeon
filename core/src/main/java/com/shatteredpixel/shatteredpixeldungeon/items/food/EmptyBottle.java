package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class EmptyBottle extends Item {

	public int durability = 3;

	{
		image = ItemSpriteSheet.EMPTY_BOTTLE; // Indeks FOOD+13
		stackable = false;
	}

	@Override
	public String name() {
		return Messages.get(this, "name") + " (" + durability + "/3)";
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add("FILL");
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals("FILL")) {
			if (Dungeon.level.water[hero.pos]) {
				
				detach(hero.belongings.backpack);
				
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