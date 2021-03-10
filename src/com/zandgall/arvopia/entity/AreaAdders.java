package com.zandgall.arvopia.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Bee;
import com.zandgall.arvopia.entity.creatures.Butterfly;
import com.zandgall.arvopia.entity.creatures.Cannibal;
import com.zandgall.arvopia.entity.creatures.basic.Bat;
import com.zandgall.arvopia.entity.creatures.basic.Bear;
import com.zandgall.arvopia.entity.creatures.basic.Fairy;
import com.zandgall.arvopia.entity.creatures.basic.Fox;
import com.zandgall.arvopia.entity.creatures.basic.Skunk;
import com.zandgall.arvopia.entity.creatures.basic.Wolf;
import com.zandgall.arvopia.entity.creatures.npcs.Villager;
import com.zandgall.arvopia.entity.moveableStatics.Cloud;
import com.zandgall.arvopia.entity.statics.Flower;
import com.zandgall.arvopia.entity.statics.House;
import com.zandgall.arvopia.entity.statics.Shrubbery;
import com.zandgall.arvopia.entity.statics.Stone;
import com.zandgall.arvopia.entity.statics.Tree;
import com.zandgall.arvopia.utils.Public;

public class AreaAdders extends Entity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityAdder adder;
	
	double chance;
	String name;
	
	public int maximum, ammount = 0;
	
	boolean started = false;
	
	public AreaAdders(Handler game, double x, double y, int width, int height, String name, double chance) {
		super(game, x, y, width, height, false, false, false, false);
		this.chance=chance;
		this.width=width;
		this.height=height;
		this.name=name.replaceAll(" ", "");
		System.out.println("Checking adders...");
		for(EntityAdder a: game.getEntityManager().adders) {
			System.out.println(a.name);
			if(a.name.replaceAll(" ", "").contains(this.name) && this.name.contains(a.name.replaceAll(" ", ""))) {
				adder=a;
			}
		}
	}
	
	private boolean compare(String s) {
		return name.contains(s);
	}
	
	public void tick() {
		if(!started) {
			game.getWorld().updateMaxEntities(name, width/9);
			started=true;
		}
		
		if(Public.chance(chance)) {
			if(compare("Tree"))
				game.getWorld().addEntityOnGround(new Tree(game, 0, 0, (int) Public.random(0, 15)), Public.random(x, x+width), y, y+height);
			if(compare("Stone"))
				game.getWorld().addEntityOnGround(new Stone(game, 0, 0, (int) Public.random(0, 2)), Public.random(x, x+width), y, y+height);
			if(compare("House"))
				game.getWorld().addEntityOnGround(new House(game, 0, 0, (int) Public.random(0, 1)), Public.random(x, x+width), y, y+height);
			if(compare("Shrubbery"))
				game.getWorld().addEntityOnGround(new Shrubbery(game, 0, 0, (int) Public.random(3, 4)), Public.random(x, x+width), y, y+height);
			if(compare("Flower"))
				game.getWorld().addEntityOnGround(new Flower(game, 0, 0, (int) Public.random(0, 2), Public.debugRandom(-1, 1)), Public.random(x, x+width), y, y+height);
			if(compare("Cloud"))
				game.getWorld().addEntityInAir(new Cloud(game, 0, 0, (int) Public.random(0, 3), Public.debugRandom(-2, 2)), Public.random(x, x+width), Public.random(y, y+height));
			if(compare("Bee"))
				game.getWorld().addEntityInAir(new Bee(game, 0, 0, true, 10000), Public.random(x, x+width), Public.random(y, y+height));
			if(compare("Butterfly"))
				game.getWorld().addEntityInAir(new Butterfly(game, 0, 0, true, 10000), Public.random(x, x+width), Public.random(y, y+height));
			if(compare("Cannibal"))
				game.getWorld().addEntityOnGround(new Cannibal(game, 0, 0, Public.debugRandom(0.2, 1.2), 1, Public.chance(10)), Public.random(x, x+width), y, y+height);
			if(compare("Bat"))
				game.getWorld().addEntityInAir(new Bat(game, 0, 0), Public.random(x, x+width), Public.random(y, y+height));
			if(compare("Bear"))
				game.getWorld().addEntityOnGround(new Bear(game, 0, 0), Public.random(x, x+width), y, y+height);
			if(compare("Fairy"))
				game.getWorld().addEntityOnGround(new Fairy(game, 0, 0), Public.random(x, x+width), y, y+height);
			if(compare("Fox"))
				game.getWorld().addEntityOnGround(new Fox(game, 0, 0), Public.random(x, x+width), y, y+height);
			if(compare("Skunk"))
				game.getWorld().addEntityOnGround(new Skunk(game, 0, 0), Public.random(x, x+width), y, y+height);
			if(compare("Wolf"))
				game.getWorld().addEntityOnGround(new Wolf(game, 0, 0), Public.random(x, x+width), y, y+height); 
			if(compare("Villager"))
				game.getWorld().addEntityOnGround(new Villager(game, Public.random(x, x+width), Public.random(y, y+height), (int) Public.random(0, 6)), Public.random(x, x+width), y, y+height);
			if(adder!=null)
				adder.add(Public.random(x, x+width), Public.random(y, y+height));
		}
	}
	
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.red);
		g.drawRect((int) x, (int) y, width, height);
		g.drawRect((int) x+1, (int) y+1, width-2, height-2);
	}
	
}
