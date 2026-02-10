package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Pasty extends Item {

	public int durability = 2;

	{
		image = ItemSpriteSheet.PASTY;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add("fill");
		return actions;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}


	@Override
	public void execute(Hero hero, String action) {
		if (action.equals("fill")) {
			if (Dungeon.level.water[hero.pos]) {
				
				detach(hero.belongings.backpack);
				
				WaterBottle water = new BigWaterBottle();
				water.durability = this.durability;
				if (!water.doPickUp(hero)) {
					Dungeon.level.drop(water, hero.pos).sprite.drop();
				}
				GLog.i( Messages.get(this, "filled") );
				hero.spendAndNext(1f);
				
			} else {
				GLog.w( Messages.get(this, "need_water") );
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