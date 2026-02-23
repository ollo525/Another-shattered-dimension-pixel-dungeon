/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;

public class ScrollOfUpgrade extends InventoryScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
		preferredBag = Belongings.Backpack.class;

		unique = true;

		talentFactor = 2f;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable();
	}

	@Override
	protected void onItemSelected( Item item ) {

		GameScene.show(new WndUpgrade(this, item, identifiedByUse));

	}

	public void reShowSelector(boolean force){
		identifiedByUse = force;
		curItem = this;
		GameScene.selectItem(itemSelector);
	}

	public WndBag.ItemSelector getSelector(boolean force){
		identifiedByUse = force;
		curItem = this;
		return itemSelector;
	}

	public Item upgradeItem( Item item ){
		// Efekt wizualny ulepszenia
		upgrade( curUser );
		Degrade.detach( curUser, Degrade.class );

		// NOWA LOGIKA: System Fuzji dla broni białej
		if (item instanceof MeleeWeapon) {
			MeleeWeapon mw = (MeleeWeapon) item;
			float addMin, addMax;

			if (mw.quality == MeleeWeapon.ENHANCED) {
				// Jeśli broń była niebieska, dodajemy losowy bonus 1.00 - 1.25
				addMin = 1.00f + (float)Math.random() * 0.25f;
				addMax = 1.00f + (float)Math.random() * 0.25f;
			} else {
				// Jeśli normalna (lub czerwona), dodajemy jej aktualne modyfikatory
				addMin = mw.minMod;
				addMax = mw.maxMod;
			}

			// Dodajemy bonus do tablicy bohatera dla tego tieru i wszystkich wyższych
			for (int i = mw.tier; i <= 5; i++) {
				curUser.tierMinBonus[i] += addMin;
				curUser.tierMaxBonus[i] += addMax;
			}

			GLog.p("Zniszczyłeś broń, wzmacniając na stałe swoją wiedzę o Tierze " + mw.tier + " i wyższych!");
			
			// Niszczymy przedmiot
			item.detach(curUser.belongings.backpack);
			
			Statistics.upgradesUsed++;
			Badges.validateMageUnlock();
			Catalog.countUse(this.getClass());
			
			return null; // Zwracamy null, bo przedmiot przestał istnieć
		}

		// Reszta logiki dla zbroi, pierścieni i różdżek pozostaje bez zmian
		if (item instanceof Armor){
			Armor a = (Armor) item;
			boolean wasCursed = a.cursed;
			boolean wasHardened = a.glyphHardened;
			boolean hadCursedGlyph = a.hasCurseGlyph();
			boolean hadGoodGlyph = a.hasGoodGlyph();

			item = a.upgrade();

			if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()){
				removeCurse( Dungeon.hero );
			} else if (a.cursedKnown && wasCursed && !a.cursed){
				weakenCurse( Dungeon.hero );
			}
			if (wasHardened && !a.glyphHardened){
				GLog.w( Messages.get(Armor.class, "hardening_gone") );
			} else if (hadGoodGlyph && !a.hasGoodGlyph()){
				GLog.w( Messages.get(Armor.class, "incompatible") );
			}

		} else if (item instanceof Wand || item instanceof Ring) {
			boolean wasCursed = item.cursed;

			item = item.upgrade();

			if (item.cursedKnown && wasCursed && !item.cursed){
				removeCurse( Dungeon.hero );
			}

		} else {
			item = item.upgrade();
		}

		Badges.validateItemLevelAquired( item );
		Statistics.upgradesUsed++;
		Badges.validateMageUnlock();

		Catalog.countUse(item.getClass());

		return item;
	}
	
	public static void upgrade( Hero hero ) {
		hero.sprite.emitter().start( Speck.factory( Speck.UP ), 0.2f, 3 );
	}

	public static void weakenCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "weaken_curse") );
		hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 5 );
	}

	public static void removeCurse( Hero hero ){
		GLog.p( Messages.get(ScrollOfUpgrade.class, "remove_curse") );
		hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
		Badges.validateClericUnlock();
	}
	
	@Override
	public int value() {
		return isKnown() ? 50 * quantity : super.value();
	}

	@Override
	public int energyVal() {
		return isKnown() ? 10 * quantity : super.energyVal();
	}
}
