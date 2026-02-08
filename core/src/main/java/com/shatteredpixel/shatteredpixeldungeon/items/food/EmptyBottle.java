package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
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
		return "Empty Bottle (" + durability + "/3)";
	}
	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add("FILL"); // Używamy klucza "FILL"
		return actions;
	}
	@Override
	public void execute(Hero hero, String action) {
		if (action.equals("FILL")) { // Tu też musi być "FILL"!
			
			if (Dungeon.level.water[hero.pos]) {
				
				// USUWANIE SIEBIE
				detach(hero.belongings.backpack);
				
				// SPAWNOWANIE PEŁNEJ
				WaterBottle water = new WaterBottle();
				water.durability = this.durability;
				if (!water.doPickUp(hero)) {
					Dungeon.level.drop(water, hero.pos).sprite.drop();
				}
				
				GLog.i("You filled the bottle");
				hero.spendAndNext(1f);
				
			} else {
				GLog.w("You need to stay in water!");
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
    	@Override
	public boolean isIdentified() {
		return true;
	}
    	@Override
	public boolean isUpgradable() {
		return false;
	}
		@Override
	public String name() {
		return Messages.get(this, "name");
	}


}