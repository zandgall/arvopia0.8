package com.zandgall.arvopia.entity.creatures;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Initiator;
import com.zandgall.arvopia.SmartCostume;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.npcs.NPC;
import com.zandgall.arvopia.entity.statics.StaticEntity;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.SerialImage;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.Dialogue;
import com.zandgall.arvopia.guis.Inventory;
import com.zandgall.arvopia.guis.Menu;
import com.zandgall.arvopia.guis.Trading;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.state.CustomizationState;
import com.zandgall.arvopia.state.TitleState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.water.Water;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public class Player extends Creature {
	private static final long serialVersionUID = 1L;

	// public Map<String, Integer> itemAmounts = new HashMap<String, Integer>();
//	public Map<String, BufferedImage> itemImages = new HashMap<String, BufferedImage>();
	public Map<String, PlayerItem> items = new HashMap<String, PlayerItem>();

	public void putItem(String name, int amount) {
//		if(itemAmounts.containsKey(name)) {
//			itemAmounts.put(name, itemAmounts.get(name)+amount);
//		} else {
//			itemAmounts.put(name, amount);
//		}
		if (items.containsKey(name))
			items.get(name).amount += amount;
	}

	public void addItem(String name, BufferedImage image) {
		PlayerItem p = new PlayerItem(game, image, image, false, 0, 1, 1, 5);
		p.front = true;
		items.put(name, p);

	}

	public void addItem(String name, BufferedImage image, Map<String, Integer> recipe) {
		PlayerItem p = new PlayerItem(game, image, image, false, 0, 1, 1, 5);
		p.setRecipe(recipe);
		items.put(name, p);
	}

	public PlayerItem getItem(String name) {
		for (String s : items.keySet()) {
			if (s.equals(name))
				return items.get(s);
		}
		return null;
	}

	public static long toolst, watert, animationt, mouset, attackt;

	PlayerItem currentItem;
	public int selectedTool = 0;
	// TODO make String[] {"null", "null", "n...}
	public String[] toolbar = { "null", "null", "null", "null", "null", "null", "null", "null", "null", "null" };

	private double spawnX, spawnY;

	public String has = "";

	private double lastHeight;

	private boolean attackReady;

	private Rectangle attack;

	boolean attacking;

	boolean primed;

	private int attackDelay;

	private int delayRange;

	private double damage;

	private long timer;

	private long attackTimer;

	public Inventory crafting;

	public Menu menu;

	public boolean viewCrafting;

	public boolean viewMenu;
	Animation jump, jumpf, jumpi, still, stillf, stilli, crouch, crouchf, crouchi, walk, walkf, walki, punch, punchf,
			punchi, stab, stabf, stabi, smash, smashf, smashi, airKick, airKickf, airKicki, run, runf, runi;
	private Assets player;
	public boolean jumping = false;

	int renderCount = 0;

	int widthFlip = 1;

	String appearance = "Male";

	private MouseManager mouse;

	private int mouseX;
	private int mouseY;
	private boolean fullLeftMouse;
	public int lives;
	Entity closest;
	public NPC closestNPC;
	public double breath = 20.0D;

	Image imgtest;

	public BufferedImage sheet;

	public ArrayList<Creature> targets;

	public Player(Handler handler, double x, double y, boolean direction, double speed, int lives) {
		super(handler, x, y, 18, 47, direction, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false,
				DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, com.zandgall.arvopia.Reporter.user);

		game.addSound("Sounds/Swing.ogg", "Swoosh", false, 0, 0, 0);
		game.addSound("Sounds/Fire.ogg", "Fire", true, 0, 0, 0);

		spawnX = x;
		spawnY = y;

		targets = new ArrayList<Creature>();

		mouse = handler.getMouse();

		attack = new Rectangle((int) x, (int) y, 72, 54);

		health = 20.0D;
		MAX_HEALTH = 20;

		layer = 1.0D;

		attackReady = false;
		attacking = false;
		attackDelay = 15;
		delayRange = 15;
		damage = 1.0D;

		timer = 0L;
		attackTimer = 0L;

		this.lives = lives;

		menu = new Menu(handler);
		crafting = new Inventory(handler);

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		punch = SmartCostume.getFakeAnimation(SmartCostume.punch);

		airKick = SmartCostume.getFakeAnimation(SmartCostume.airkick);

		stab = SmartCostume.getFakeAnimation(SmartCostume.stab);

		smash = SmartCostume.getFakeAnimation(SmartCostume.smash);

		Initiator.aplayerInit(game, this);

		if (FrameGrabber.instance == null)
			FrameGrabber.instance = new FrameGrabber(this, game);

		setItemAnimations();
	}

	public Player(Handler handler) {
		super(handler, 0, 0, 18, 47, false, 1, DEFAULT_ACCELERATION, (int) MAX_SPEED, false, false, DEFAULT_JUMP_FORCE,
				DEFAULT_JUMP_CARRY, com.zandgall.arvopia.Reporter.user);

		String parentFiles = "Player/";
		TitleState ts = (TitleState) game.getGame().menuState;
		BufferedImage hair = ImageLoader.loadImageEX(parentFiles + "Hair/Hair0.png");
		BufferedImage head = ImageLoader.loadImageEX(parentFiles + "Face/Face0.png");
		BufferedImage eyes = ImageLoader.loadImageEX(parentFiles + "Eyes/Eyes0.png");
		BufferedImage eyepupils = ImageLoader.loadImageEX(parentFiles + "Pupils/Pupils0.png");
		BufferedImage hands = ImageLoader.loadImageEX(parentFiles + "Hands/Hands0.png");
		BufferedImage pants = ImageLoader.loadImageEX(parentFiles + "Pants/Pants0.png");
		BufferedImage shirt = ImageLoader.loadImageEX(parentFiles + "Body/Body0.png");
		BufferedImage shoes = ImageLoader.loadImageEX(parentFiles + "Shoes/Shoes0.png");

		int w = hair.getWidth();
		int h = hair.getHeight();

		sheet = new BufferedImage(w * 4, h * 4, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics g = sheet.getGraphics();
		g.drawImage(eyepupils, 0, 0, w * 4, h * 4, null);
		g.drawImage(head, 0, 0, w * 4, h * 4, null);
		g.drawImage(eyes, 0, 0, w * 4, h * 4, null);
		g.drawImage(hair, 0, 0, w * 4, h * 4, null);
		g.drawImage(pants, 0, 0, w * 4, h * 4, null);
		g.drawImage(hands, 0, 0, w * 4, h * 4, null);
		g.drawImage(shirt, 0, 0, w * 4, h * 4, null);
		g.drawImage(shoes, 0, 0, w * 4, h * 4, null);
//		g.dispose();

		if (SmartCostume.face != null)
			sheet = (new SerialImage(SmartCostume.getStill()).scale(4d, 4d).toImage());

		player = new Assets(sheet, 144, 216, "Player");
	}

	public void tick() {
		if(crafting==null)
			crafting = new Inventory(game);
		if (viewCrafting)
			crafting.tick();
		crafting.tickItems();
		if (viewMenu) {
			menu.tick();
		}
		
		game.getKeyManager();
//		if (KeyManager.keys[KeyEvent.VK_V] && KeyManager.existantTyped) {
//			Dialogue.dialogue.add(new Dialogue("Somebody once told me the world is gonna roll me, I ain't the sharpest tool in the shed", "Player",
//					Trading.DEFAULT_FEMALE, x, y, 0.3, 1));
//		}

		getInput();

		if (!game.getGame().paused) {
			fallDamage();

			mouseX = game.getMouse().getMouseX();
			mouseY = game.getMouse().getMouseY();
			fullLeftMouse = game.getMouse().fullLeft;

			if ((!game.getMouse().isIn()) && (game.options().getToggle("Pause on Mouse Exit"))) {
				viewMenu = true;
				game.getGame().pause();
			}

			long pre = System.nanoTime();

			toolHandling();
			toolSoundHandling();
			toolst = System.nanoTime() - pre;
			pre = System.nanoTime();

			waterHandling();

			watert = System.nanoTime() - pre;
			pre = System.nanoTime();

			if (health <= 0.0D) {
				game.getWorld().kill(this);
			}

			airKick.tick();
			punch.tick();
			if (currentItem != null) {
				stab.setSpeed(currentItem.delay);
				smash.setSpeed(currentItem.delay);
			}
			stab.tick();
			smash.tick();

			game.getWorld().outOfBounds(this);

			move();

			animationt = System.nanoTime() - pre;
			pre = System.nanoTime();

			checkMouse();

			mouset = System.nanoTime() - pre;
			pre = System.nanoTime();

			if ((bottoms) && (jumping)) {
				jumping = false;
			}

			attackManaging();
			attackHandling();

			attackt = System.nanoTime() - pre;
			pre = System.nanoTime();

			timer += 1L;
			attackTimer += 1L;

			bounds.x = 12;
			bounds.y = 8;
			bounds.width = 10;
			bounds.height = 45;
		}
	}

	public void setItemAnimations() {
//		jumpi = SmartCostume.getAccessoryAnimation(SmartCostume.air);
//		walki = SmartCostume.getAccessoryAnimation(SmartCostume.walk);
//		stilli = SmartCostume.getAccessoryAnimation(SmartCostume.still);
//		runi = SmartCostume.getAccessoryAnimation(SmartCostume.run);
//		crouchi = SmartCostume.getAccessoryAnimation(SmartCostume.crouch);
//		stabi = SmartCostume.getAccessoryAnimation(SmartCostume.stab);
//		smashi = SmartCostume.getAccessoryAnimation(SmartCostume.smash);
//		airKicki = SmartCostume.getAccessoryAnimation(SmartCostume.airkick);
//		punchi = SmartCostume.getAccessoryAnimation(SmartCostume.punch);
	}

	private void toolHandling() {
		for (int i = 0; i < 10; i++) {
			game.getKeyManager();
			if (KeyManager.num[i]) {
				selectedTool = i;
			}
		}

		if (game.getMouse().getMouseScroll() > 0)
			selectedTool += (int) game.getMouse().getMouseScroll();
		else
			selectedTool += (int) Math.ceil(game.getMouse().getMouseScroll());

		selectedTool %= 10;
		selectedTool = selectedTool < 0 ? selectedTool + 10 : selectedTool;

		PlayerItem pre = currentItem;

		delayRange = 15;
		if ((selectedTool < toolbar.length) && items.containsKey(toolbar[selectedTool])) {
			currentItem = ((PlayerItem) items.get(toolbar[selectedTool]));
			attackDelay = (int) currentItem.delay;
			damage = currentItem.damage;
			SmartCostume.accessories.put("Item", currentItem.toAccessory());
		} else {
			currentItem = null;
			attackDelay = 15;
			damage = 1.0D;
			SmartCostume.accessories.remove("Item");
		}

		if (currentItem != null) {
			currentItem.tick((int) x, (int) y);
		} else {
//			for (PlayerItem i : items.values()) {
//				if (i.light.isOn())
//					i.turnOff();
//			}
		}

		if (currentItem != pre && pre!=null) {
			setItemAnimations();
			if(pre.hasLight) {
				pre.light.turnOff();
				Console.log("Turned off light");
			}
		}

	}

	private void toolSoundHandling() {
		game.listenPosition((int) x, (int) y, 0);

		game.setPosition("Swoosh", (int) x, (int) y, 0);
		if (attackReady&&attacking) {
			game.soundSystem.stop("Swoosh");
			game.soundSystem.play("Swoosh");
		}

		game.setPosition("Fire", (int) x, (int) y, 0);
		if (currentItem != null && currentItem.hasLight) {
			if (!game.soundSystem.playing("Fire")) {
				game.soundSystem.play("Fire");
			}
		} else {
			if (game.soundSystem.playing("Fire"))
				game.soundSystem.stop("Fire");
		}
	}

	private void waterHandling() {
		if (inWater)
			if (breath > 0.0D) {
				breath -= 0.02D;
			} else if (breath <= 0.0D)
				health -= 0.1D;
		if ((!inWater) && (breath < 20.0D)) {
			breath += 0.1D;
		} else if (breath >= 20.0D)
			breath = 20.0D;
	}

	private void fallDamage() {
		if ((bottoms) || (inWater)) {
			if (y - lastHeight > (inWater ? 320 : 180)) {
				damage((int) Math.floor((y - lastHeight) / 120.0D));
			}

			lastHeight = y;
		} else if (y < lastHeight) {
			lastHeight = y;
		}
	}

	public void setY(double y) {
		this.y = y;
		this.lastHeight = y;
	}

	private void attackHandling() {
		if (!attacking) {
			attack.x = ((int) x);
			attack.y = ((int) y);
		}

		if (!fullLeftMouse) {
			attacking = false;
		}

		if (timer >= 1000000L)
			timer = 0L;
		if (attackTimer >= 1000000L)
			attackTimer = 0L;

		if (attackTimer == attackDelay) {
			attackReady = true;
		} else if ((attackTimer < attackDelay) || (attackTimer > attackDelay + delayRange)) {
			attackReady = false;
			if (attackTimer > attackDelay + delayRange) {
				attackTimer = 0L;
			}
		}
		if (currentItem != null) {
			game.getKeyManager();
			if (KeyManager.shift)
				attackReady = smash.justLooped;
			else
				attackReady = stab.justLooped;
		} else
			attackReady = punch.justLooped;
		
		// TODO
	}

	private void getInput() {
		xMove = 0;
		yMove = 0;

		if (viewMenu) {
			game.getKeyManager();
			if (KeyManager.esc) {
				viewMenu = false;
				game.getGame().unPause();
			}
		} else if (viewCrafting) {
			if ((KeyManager.checkBind("Crafting") && KeyManager.existantTyped) || (KeyManager.esc)) {
				viewCrafting = false;
			}
		} else {

			if (KeyManager.checkBind("Crafting") && KeyManager.existantTyped) {
				viewCrafting = true;
			}

			
			if (KeyManager.esc) {
				viewMenu = true;
				game.getGame().pause();
			
				KeyManager.typed = false;
			}

			if ((!KeyManager.checkBind("Down")) || (inWater)) {
				if (xVol > 0.0D)
					xMove += (speed + xVol);
				if (xVol < 0.0D) {
					xMove += (-speed + xVol);
				}
			}
			if (KeyManager.checkBind("Left")) {
				if ((!KeyManager.checkBind("Down") || KeyManager.checkBind("Up")) || (inWater)) {
					if ((xVol < maxSpeed) && (xVol > 0.0D))
						xVol -= FRICTION * 2.0D;
					xVol -= acceleration;

					if (KeyManager.shift)
						xVol = Math.min(xVol, -MAX_SPEED);
				}
				widthFlip = -1;
			} else if (KeyManager.checkBind("Right")) {
				if ((!KeyManager.checkBind("Down") || KeyManager.checkBind("Up")) || (inWater)) {
					if ((xVol < maxSpeed) && (xVol < 0.0D))
						xVol += FRICTION * 2.0D;
					xVol += acceleration;
					
					if (KeyManager.shift)
						xVol = Math.max(xVol, MAX_SPEED);
				}
				widthFlip = 1;
			} else if (inWater) {
				xVol *= 0.1D;
			} else if (!KeyManager.checkBind("Down")) {
				double txv = xVol;
				if (xVol < 0.0D) {
					xVol += acceleration + FRICTION;
				} else if (xVol > 0.0D)
					xVol -= acceleration + FRICTION;
				if (((txv > 0) && (xVol < 0)) || ((txv < 0) && (xVol > 0))) {
					xVol = 0;
				}
			}

			if (KeyManager.checkBind("Debug")) {
				game.log("Marked X: " + Math.round(x) + ", Y: "
						+ Math.round(y + bounds.y + bounds.height - Tile.TILEHEIGHT) + " Tile: ("
						+ Math.round(x / Tile.TILEWIDTH) + ", "
						+ Math.round((y + bounds.y + bounds.getHeight()) / Tile.TILEHEIGHT - 1.0D) + ")"
						+ " Tile full X Y: (" + Math.round(x / Tile.TILEWIDTH) * Tile.TILEWIDTH + ", "
						+ Math.round((y + bounds.y + bounds.getHeight()) / Tile.TILEHEIGHT - 1.0D) * Tile.TILEHEIGHT
						+ ")");
				game.log("X and Y offset: " + game.getGameCamera().getxOffset() + ", "
						+ game.getGameCamera().getyOffset());
			}

			if (KeyManager.checkBind("Up")) {
				if ((bottoms) && (!jumping)) {
					yVol = (-DEFAULT_JUMP_FORCE);
					jumping = true;
				} else if (touchingWater) {
					yVol -= 1.0F;
					yVol = (Math.max(yVol, -speed));
				}

				if (yVol < 0.0F) {
					yVol = ((yVol - DEFAULT_JUMP_CARRY));
				}
			}

			if (KeyManager.checkBind("Down")) {
				if (inWater) {
					yVol += 1.0F;
					yVol = (Math.min(yVol, speed * 2.0D));
				} else {
					if ((xVol > 0.0D) && (bottoms)) {
						xVol *= 0.75D;
						xMove =((xVol + speed));
						if (xVol < 0.001D)
							xVol = 0.0D;
					} else if (xVol > 0.0D) {
						xMove =((xVol + speed));
					}

					if ((xVol < 0.0D) && (bottoms)) {
						xVol *= 0.75D;
						xMove = ((xVol - speed));
						if (xVol > -0.001D)
							xVol = 0.0D;
					} else if (xVol < 0.0D) {
						xMove = ((xVol - speed));
					}

					bounds.y = 37;
					bounds.height = 16;
				}
			} else {
				bounds.y = 7;
				bounds.height = 46;
			}
			
		}
	}

	private void attackManaging() {
		if ((fullLeftMouse && game.getMouse().fullRight) && (attackReady)) {
			attacking = true;

			if (widthFlip == 1) {
				attack.x = ((int) x);
			} else {
				attack.x = ((int) (x - 72.0D + bounds.x + bounds.width));
			}
			attack.y = ((int) y);

			ArrayList<Creature> c = getInRadius(x + bounds.getCenterX(), y + bounds.getCenterY(), 100);

			if (!bottoms) {
				targets.clear();
				for (int i = 0; i < c.size() && i < 3; i++)
					if (c.get(i).getCollision(0, 0).intersects(x + kickX() - 20, y + kickY() - 60, 40, 120)) {
						c.get(i).damage(damage);
						targets.add(c.get(i));
					}
			} else {
				if (currentItem != null) {
					game.getKeyManager();
					c = currentItem.attack((int) x, (int) y, this, !KeyManager.shift, widthFlip);
					targets.clear();
					targets.addAll(c);
				} else {
					targets.clear();
					for (Creature s : c)
						if (s.getCollision(0, 0).intersects(x - 20, y - 60, 40, 120)) {
							s.damage(damage);
							targets.add(s);
						}
				}
			}

//			for(Creature s: c) {
//				if(currentItem!=null)
//					s.damage(currentItem.damage);
//				else s.damage(1);
//			}

			attackReady = false;

		} else if ((game.getMouse().fullRight) && (!attacking) && (!attackReady || game.getMouse().fullRight)) {
			primed = true;
			attacking = false;
			if (!game.getMouse().fullLeft) {
				punch.setFrame(0);
				stab.setFrame(0);
				smash.setFrame(0);
			}
		} else {
			primed = false;
		}
	}

	private int kickX() {
		return 34;
	}

	private int kickY() {
		return 41;
	}

	private void checkMouse() {

		Entity newClosest = getClosestStatic(mouseX + game.xOffset(), mouseY + game.yOffset());
//		Entity newClosest = null;
		if ((newClosest != null) && (closest != newClosest)) {
			closest = newClosest;
		}

		NPC newClosestNPC = getClosestNPC(mouseX + game.xOffset(), mouseY + game.yOffset());
//		NPC newClosestNPC = null;
		if ((newClosestNPC != null) && (newClosestNPC != closestNPC)) {
			closestNPC = newClosestNPC;
		}

		if ((fullLeftMouse && !game.getMouse().fullRight) && (!viewCrafting)) {
			double cx = mouseX + game.xOffset();
			double cy = mouseY + game.yOffset();

			if (closest != null) {
				boolean a = (cx > closest.getX() - closest.getWidth() / 2)
						&& (cx < closest.getX() + closest.getbounds().x + closest.getbounds().width * 1.5D);
				boolean b = (cy > closest.getY() - closest.getHeight() / 2)
						&& (cy < closest.getY() + closest.getbounds().y + closest.getbounds().height * 1.5D);

				if ((a) && (b) && (!closest.creature)) {
					if (closest.staticEntity) {
						StaticEntity e = (StaticEntity) closest;

						if (e.health <= 0.0D) {
							game.getWorld().kill(closest);

							closest = null;

						} else if ((currentItem != null) && (e.weakness == currentItem.type)) {
							e.health -= 5.0D;
						} else {
							e.health -= 1.0D;
						}

					} else {
						game.log("Entity:" + closest.staticEntity);

						game.getWorld().kill(closest);

						closest = null;
					}
				}
			}
		}
	}

	public void renderElse(Graphics2D g) {

		if (primed || attacking) {
			ArrayList<Creature> c = getInRadius(x + bounds.getCenterX(), y + bounds.getCenterY(), 100);

			for (Creature e : c) {
//				e.showBox(g);

				g.setFont(Public.defaultFont);

				e.showHealthBar(g);
			}
		}

	}

	public void render(Graphics2D g) {

//		SmartCostume.setDefault();
		
		checkCol();

		if (currentItem != null) {

			if (currentItem.hasLight && !currentItem.light.isOn()) {
				currentItem.light.setOn(true);
			}

		}

//		if (currentItem == null)
//			for (String s : items.keySet()) {
//				PlayerItem t = items.get(s);
//				if (t.hasLight && t.light.isOn())
//					t.light.setOn(false);
//			}

		FrameGrabber.game = game;
		FrameGrabber.p = this;
		
		g.drawImage(FrameGrabber.frame, (int) Math.round(Public.xO(x-35)), (int) Math.round(Public.yO(y-22)), null);

		g.drawImage(FrameGrabber.accessories, (int) Math.round(Public.xO(x-35)), (int) Math.round(Public.yO(y-22)), null);
		
//		g.drawImage(getFrame(), (int) Public.xO(x-35), (int) Public.yO(y-22), null);
//		
//		g.drawImage(Tran.flipNS(getAccessories(), widthFlip, 1), (int) Public.xO(x-35), (int) Public.yO(y-22), null);

//			if ((currentItem != null) && ((getFrame() == stab.getFrame()) || (getFrame() == smash.getFrame()))) {
//				g.drawImage(Tran.flip((getFrame() == stab.getFrame() ? currentItem.getStab() : currentItem.getSwing(smash.frameInt)), widthFlip, 1), (int) Public.xO(x) + getToolxoffset(),
//						(int) Public.yO(y) + getToolyoffset(), null);
//			}

		if (closest != null) {
			if (closest.creature) {
				Creature c = (Creature) closest;
				c.showHealthBar(g);
			} else {
				closest.showBox(g);
			}
		}
		// closestNPC

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}
	}

	public void renScreens(Graphics2D g) {
		if (viewCrafting) {
			crafting.render(g);
		}
		if (viewMenu) {
			menu.render(g);
		}
	}

	public BufferedImage getFrame() {
		checkCol();

		return FrameGrabber.frame;
		
		/*
		if (KeyManager.checkBind("Down")) {
			return widthFlip == -1 ? crouchf.getFrame() : crouch.getFrame();
		} else {
			crouch.setFrame(0);
		}
		if ((!bottoms || jumping) && !checkCollision(0, 10)) {
			if ((attacking) && (punch.getFrame() == punch.getArray()[2])) {
				return widthFlip == -1 ? airKickf.getFrame() : airKick.getFrame();
			}
			return widthFlip == -1 ? jumpf.getFrame() : jump.getFrame();
		}

		if (Math.round(Math.abs(xMove)) > 0.5) {

			still.setFrame(0);

			if (Math.round(Math.abs(xMove)) > 3)
				return widthFlip == -1 ? runf.getFrame() : run.getFrame();
			else
				return widthFlip == -1 ? walkf.getFrame() : walk.getFrame();
		} else {
			run.setFrame(0);
			walk.setFrame(0);
		}

		if ((attacking) || (primed)) {
			if (currentItem != null) {
				game.getKeyManager();
				if (KeyManager.shift) {
					return widthFlip == -1 ? smashf.getFrame() : smash.getFrame();
				}
				return widthFlip == -1 ? stabf.getFrame() : stab.getFrame();
			}
			return widthFlip == -1 ? punchf.getFrame() : punch.getFrame();
		}
		if (tops)
			return widthFlip == -1 ? crouchf.getFrame() : crouch.getFrame();
		return widthFlip == -1 ? stillf.getFrame() : still.getFrame();
		*/
	}

	public BufferedImage getAccessories() {

		SmartCostume.imitate(crouch, SmartCostume.crouch);
		SmartCostume.imitate(airKick, SmartCostume.airkick);
		SmartCostume.imitate(jump, SmartCostume.air);
		SmartCostume.imitate(run, SmartCostume.run);
		SmartCostume.imitate(walk, SmartCostume.walk);
		SmartCostume.imitate(smash, SmartCostume.smash);
		SmartCostume.imitate(stab, SmartCostume.stab);
		SmartCostume.imitate(punch, SmartCostume.punch);
		SmartCostume.imitate(still, SmartCostume.still);

		if (KeyManager.checkBind("Down")) {
//			return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.crouch));
			return crouchi.getFrame();
		}
		if ((!bottoms || jumping) && !checkCollision(0, 10)) {
			if ((attacking) && (punch.getFrame() == punch.getArray()[2])) {
				return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.airkick));

			}
			return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.air));
		}

		if (Math.round(Math.abs(xMove)) > 0.5) {
			if (Math.round(Math.abs(xMove)) > 3)
//				return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.run));
				return runi.getFrame();
			else
//				return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.walk));
				return walki.getFrame();
		}

		if ((attacking) || (primed)) {
			if (currentItem != null) {
				game.getKeyManager();
				if (KeyManager.shift) {
					return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.smash));
				}
				return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.stab));
			}
			return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.punch));
		}
		if (tops)
			return crouchi.getFrame();
//			return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.crouch));
//		return SmartCostume.getAccessories(SmartCostume.get(SmartCostume.still));
		return stilli.getFrame();
	}

	public void reset() {
	}

	public void checkCol() {
		down = false;
		left = false;
		right = false;
		top = false;
		bottom = false;
		lefts = false;
		rights = false;
		tops = false;
		bottoms = false;
		inWater = false;
		touchingWater = false;

		if(game.getWorld().getWaterManager()!=null&&game.getWorld().getWaterManager().getWater()!=null)
		for (Water w : game.getWorld().getWaterManager().getWater()) {
			if (w.box().contains(getCollision(0.0F, 0.0F))) {
				inWater = true;
			}
			if (w.box().intersects(getCollision(0.0F, 0.0F))) {
				touchingWater = true;
			}
		}
		int ty;
		bottom = (collisionBottom() || colBottom(0.0F, yMove));
		bottoms = bottom || collisionBottoms();
//		bottom = bottoms;
//		if(!bottoms)
//			Console.log(collisionBottoms() || colBottom(0.0F, yMove));
		ty = (int) ((y + yMove + bounds.y)/Tile.TILEHEIGHT);
		top = ((collisionWithTile((int) ((x + bounds.x + 2.0D)/Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D)/Tile.TILEWIDTH), ty)) || colTop(0, yMove));
		tops = top;

//		int tx = (int) ((x + getxMove() + bounds.x + bounds.width));
//		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D)))
//				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height)))
//				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2)))
//				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4)))
//				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75D)))
		right = (collisionRight() || colRight(xMove, 0));
		rights = right;
		left = (collisionLeft() || colLeft(xMove, 0));
		lefts = left;
	}

	public PlayerItem getCurrentTool() {
		return currentItem;
	}

	public void setCurrentTool(PlayerItem currentTool) {
		this.currentItem = currentTool;
	}

	public double getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(double spawnX) {
		this.spawnX = spawnX;
	}

	public double getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(double spawnY) {
		this.spawnY = spawnY;
	}

	public int getWidthFlip() {
		return widthFlip;
	}

}

class Appearance {
	public static final Color HAIR_DEFAULT = new Color(72, 46, 9);
}

class FrameGrabber implements Runnable {

	public static FrameGrabber instance = null;

	public static Player p;
	public static Handler game;

	public static BufferedImage frame, accessories;

	public FrameGrabber(Player p, Handler game) {
		FrameGrabber.p = p;
		FrameGrabber.game = game;

		instance = this;

		Thread t = new Thread(this, "Frame grabber (Player)");
		t.start();
	}

	@Override
	public void run() {

		long pre = System.currentTimeMillis();

		while (true) {
			long now = System.currentTimeMillis();
			try {
				if (now - pre >= 16) {
					updateFrame();
					updateAccessories();
					pre = now;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateFrame() {
		frame = Tran.flipNS(SmartCostume.getPose(get()), p.widthFlip, 1);
	}

	public void updateAccessories() {
		accessories = Tran.flipNS(SmartCostume.getAccessories(get()), p.widthFlip, 1);
	}

	public Map<String, Double> get() {

		SmartCostume.setSpeed(SmartCostume.walk, Math.abs(Math.abs(p.xMove) - 0.5) / 10);
		if (Math.abs(Math.abs(p.xMove) - 0.5) / 5 == 0)
			SmartCostume.setSpeed(SmartCostume.walk, 0.05);
		SmartCostume.setSpeed(SmartCostume.run, Math.abs(Math.abs(p.xMove) - 2.0) / 5);
		
		SmartCostume.imitate(p.punch, SmartCostume.punch);
		SmartCostume.imitate(p.stab, SmartCostume.stab);
		SmartCostume.imitate(p.smash, SmartCostume.smash);

		boolean checked = false;

		while (!checked)
			try {
				p.checkCol();
				checked = true;
			} catch (ConcurrentModificationException e) {
			}
		
		if (KeyManager.checkBind("Down")) {
			return SmartCostume.get(SmartCostume.crouch);
		}

		boolean onground = (!p.bottoms || p.jumping);

		if (onground) {
			if ((p.attacking)) {
				return SmartCostume.get(SmartCostume.airkick);
			}
			return SmartCostume.get(SmartCostume.air);
		}

		if (Math.round(Math.abs(p.xMove)) > 0.5) {
			if (Math.round(Math.abs(p.xMove)) > 3)
				return SmartCostume.get(SmartCostume.run);
			else
				return SmartCostume.get(SmartCostume.walk);
		}

		if ((p.attacking) || (p.primed)) {
			if (p.currentItem != null) {
				game.getKeyManager();
				if (KeyManager.shift) {
					return SmartCostume.get(SmartCostume.smash);
				}
				return SmartCostume.get(SmartCostume.stab);
			}
			return SmartCostume.get(SmartCostume.punch);
		}

		if (p.tops) {
			return SmartCostume.get(SmartCostume.crouch);
		}
		return SmartCostume.get(SmartCostume.still);

	}

}
