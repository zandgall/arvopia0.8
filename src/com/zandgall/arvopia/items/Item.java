package com.zandgall.arvopia.items;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Initiator;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Item implements Serializable {
	private static final long serialVersionUID = -537924685486505575L;

	public static final int NORMAL = 0, FLOATING = 1;

	public static Item whitePetal;
	public static Item pinkPetal;
	public static Item bluePetal;

	public static Item metal, dirt, wood, foxFur, honey, butterflyWing;

	protected Handler game;
	protected BufferedImage texture;
	protected String name;
	protected final int id;
	protected Rectangle bounds;
	public boolean left;
	public boolean right;
	public boolean top;
	public boolean bottom;
	public boolean down;
	public boolean lefts;
	public boolean rights;
	public boolean tops;
	public boolean bottoms;
	protected double x;
	protected double y;
	protected int count;
	protected boolean pickedUp = false;

	double xMove;
	double yMove;
	double rand = 0.0;
	public boolean downed;
	private long timer;

	Animation animation;
	BufferedImage dead, inventory;

	int width, height;

	public Item(Animation animation, BufferedImage dead, BufferedImage inventory, String name, int id) {
		this.name = name;
		this.id = id;
		this.animation = animation;
		this.dead = dead;
		this.inventory = inventory;
		count = 1;
		BufferedImage i = animation.getArray()[0];
		height = i.getHeight();
		width = i.getWidth();

		bounds = new Rectangle((int) x, (int) y, width, height);

		if (id == NORMAL) {
			yMove = (-(Math.random() * 5.0D));
		}
		timer = 0L;
		rand = Public.random(0.5, 0.75);
		if (Math.random() < 0.5D) {
			direction = false;
		} else {
			direction = true;
		}
	}

	public static void init(Player p) {

		// Player's items
		Handler game = ArvopiaLauncher.game.handler;

		p.addItem("Petals", ImageLoader.loadImage("/textures/Inventory/Items/Petals.png"));
		p.addItem("Metal", ImageLoader.loadImage("/textures/Inventory/Items/Metal.png"));
		p.addItem("Wood", ImageLoader.loadImage("/textures/Inventory/Items/Wood.png"));
		p.addItem("Dirt", ImageLoader.loadImage("/textures/Inventory/Items/Dirt.png"));
		p.addItem("Butterfly Wings", ImageLoader.loadImage("/textures/Inventory/Items/ButterflyWing.png"));
		p.addItem("Charcoal", ImageLoader.loadImage("/textures/Inventory/Items/Charcoal.png"),
				getMap(new String[] { "Wood", "Metal" }, new int[] { 1, 1 }));
		p.items.get("Charcoal").description="An easily burned material, used for making torches";
		p.addItem("Honey", ImageLoader.loadImage("/textures/Inventory/Items/Honey.png"));
		p.addItem("Fine Metal", ImageLoader.loadImage("/textures/Inventory/Items/FineMetal.png"),
				getMap(new String[] { "Metal" }, new int[] { 4 }));
		p.items.get("Fine Metal").description="Used for forging of many different tools, from a rock chipper all the way too a scythe";
		p.addItem("Fox Fur", ImageLoader.loadImage("/textures/Inventory/Items/FoxFur.png"));
		p.addItem("Fur Cloth", ImageLoader.loadImage("/textures/Inventory/Items/FurCloth.png"),
				getMap(new String[] { "Fox Fur" }, new int[] { 3 }));
		p.items.get("Fur Cloth").description="Cloth used for umbrellas, that's all for now sorry I got work to get back to";

		PlayerItem i;
		
		i = new PlayerItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Axe/AxeThumbnail.png"), //Handler, thumbnail
				ImageLoader.loadImage("/textures/Inventory/Tools/Axe/Axe.png"), true, 100, 5, 1.5, 90, PlayerItem.AXE); //World image, durable, durablility, damage, delay, range, type
		i.front=true;
		i.setRecipe(getMap(new String[] {"Fine Metal", "Wood"}, new int[] {6, 3}));
		i.description="A tool useful for chopping trees.. as well as the bones of your enemies";
		p.items.put("Axe", i);
		
		i = new PlayerItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/RockChipper/RockChipperThumbnail.png"), //Handler, thumbnail
				ImageLoader.loadImage("/textures/Inventory/Tools/RockChipper/RockChipper.png"), true, 100, 2, 1.2, 100, PlayerItem.PICKAXE); //World image, durable, durablility, damage, delay, range, type
		i.front=true;
		i.setRecipe(getMap(new String[] {"Fine Metal", "Wood"}, new int[] {4, 2}));
		i.description="Good tool for chopping up rocks, like a pickaxe, but more offbrand";
		p.items.put("Rock Chipper", i);
		
		i = new PlayerItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Scythe/ScytheThumbnail.png"), //Handler, thumbnail
				ImageLoader.loadImage("/textures/Inventory/Tools/Scythe/Scythe.png"), true, 100, 7, 2, 90, PlayerItem.SCYTHE); //World image, durable, durablility, damage, delay, range, type
		i.front=true;
		i.setRecipe(getMap(new String[] {"Fine Metal", "Wood"}, new int[] {7, 3}));
		i.description="The weapon of death.."+System.lineSeparator()+System.lineSeparator()+"..and for cropping flowers :D";
		p.items.put("Scythe", i);
		
		i = new PlayerItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Sword/SwordThumbnail.png"), //Handler, thumbnail
				ImageLoader.loadImage("/textures/Inventory/Tools/Sword/Sword.png"), true, 100, 3, 1.1, 150, PlayerItem.SWORD); //World image, durable, durablility, damage, delay, range, type
		i.front=true;
		i.setRecipe(getMap(new String[] {"Fine Metal", "Wood"}, new int[] {3, 1}));
		i.description="A basic weapon, not too powerful but it get's the job done";
		p.items.put("Sword", i);
		
		i = new PlayerItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Torch/TorchThumbnail.png"), //Handler, thumbnail
				ImageLoader.loadImage("/textures/Inventory/Tools/Torch/Torch.png").getSubimage(0, 0, 18, 45), true, true, 9, 10, 100, 2, 1.2, 110, PlayerItem.NONE, 9, 3); //World image, durable, durablility, damage, delay, range, type
		i.front=true;
		i.setRecipe(getMap(new String[] {"Charcoal", "Wood"}, new int[] {1, 3}));
		i.light.color = Color.orange;
		i.world = new Animation(100, new BufferedImage[] {ImageLoader.loadImage("/textures/Inventory/Tools/Torch/Torch.png").getSubimage(0, 0, 18, 45), ImageLoader.loadImage("/textures/Inventory/Tools/Torch/Torch.png").getSubimage(18, 0, 18, 45)}, "Torch", "Shorts");
		i.description="A flaming stick to guide your path, not the best weapon, but will suffice if it's all you got";
		p.items.put("Torch", i);
		
		i = new PlayerItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Umbrella/UmbrellaThumbnail.png"), //Handler, thumbnail
				ImageLoader.loadImage("/textures/Inventory/Tools/Umbrella/UmbrellaRe.png"), true, 100, 2, 1.3, 100, PlayerItem.UMBRELLA); //World image, durable, durablility, damage, delay, range, type
		i.front=true;
		i.setRecipe(getMap(new String[] {"Fur Cloth", "Wood"}, new int[] {2, 3}));
		i.description="Used to keep you dry in the rain";
		p.items.put("Umbrella", i);

		System.out.println("PLAYERITEMS: " + p.items.size() + " " + game.getPlayer().items.size());

		// In world items

		System.out.println(ImageLoader.loadImage("/textures/Statics/Flowers/PetalParticlesWhite.png").getWidth());
		Assets s = new Assets(ImageLoader.loadImage("/textures/Statics/Flowers/PetalParticlesWhite.png"), 6, 6,
				"Petals");
		Animation a = new Animation(250,
				new BufferedImage[] { s.get(0, 0), s.get(0, 1), s.get(0, 2), s.get(0, 3), s.get(0, 2), s.get(0, 1) },
				"Floating", "White Petals");
		whitePetal = new Item(a, ImageLoader.loadImage("/textures/Statics/Flowers/WhitePetal.png"),
				ImageLoader.loadImage("/textures/Inventory/Items/Petals.png"), "Petals", Item.FLOATING);

		s = new Assets(ImageLoader.loadImage("/textures/Statics/Flowers/PetalParticlesBlue.png"), 6, 6, "Petals");
		a = new Animation(250,
				new BufferedImage[] { s.get(0, 0), s.get(0, 1), s.get(0, 2), s.get(0, 3), s.get(0, 2), s.get(0, 1) },
				"Floating", "Blue Petals");
		bluePetal = new Item(a, ImageLoader.loadImage("/textures/Statics/Flowers/BluePetal.png"),
				ImageLoader.loadImage("/textures/Inventory/Items/Petals.png"), "Petals", Item.FLOATING);

		s = new Assets(ImageLoader.loadImage("/textures/Statics/Flowers/PetalParticlesPink.png"), 6, 6, "Petals");
		a = new Animation(250,
				new BufferedImage[] { s.get(0, 0), s.get(0, 1), s.get(0, 2), s.get(0, 3), s.get(0, 2), s.get(0, 1) },
				"Floating", "Pink Petals");
		pinkPetal = new Item(a, ImageLoader.loadImage("/textures/Statics/Flowers/PinkPetal.png"),
				ImageLoader.loadImage("/textures/Inventory/Items/Petals.png"), "Petals", Item.FLOATING);

		BufferedImage b;

		b = ImageLoader.loadImage("/textures/Inventory/Items/Metal.png");
		a = new Animation(1000, new BufferedImage[] { b }, "Item", "Metal");
		metal = new Item(a, b, b, "Metal", Item.NORMAL);

		b = ImageLoader.loadImage("/textures/Inventory/Items/Wood.png");
		a = new Animation(1000, new BufferedImage[] { b }, "Item", "Wood");
		wood = new Item(a, b, b, "Wood", Item.NORMAL);

		b = ImageLoader.loadImage("/textures/Inventory/Items/Dirt.png");
		a = new Animation(1000, new BufferedImage[] { b }, "Item", "Dirt");
		dirt = new Item(a, b, b, "Dirt", Item.NORMAL);

		b = ImageLoader.loadImage("/textures/Inventory/Items/FoxFur.png");
		a = new Animation(1000, new BufferedImage[] { b }, "Item", "Fox Fur");
		foxFur = new Item(a, b, b, "Fox Fur", Item.NORMAL);

		b = ImageLoader.loadImage("/textures/Inventory/Items/Honey.png");
		a = new Animation(1000, new BufferedImage[] { b }, "Item", "Honey");
		honey = new Item(a, b, b, "Honey", Item.NORMAL);

		b = ImageLoader.loadImage("/textures/Inventory/Items/ButterflyWing.png");
		a = new Animation(1000, new BufferedImage[] { b }, "Item", "ButterflyWing");
		butterflyWing = new Item(a, b, b, "Butterfly Wings", Item.NORMAL);
		
		Initiator.aitemInit(game, p);
		
	}

	private static Map<String, Integer> getMap(String[] keys, int[] values) {
		Map<String, Integer> out = new HashMap<String, Integer>();
		for (int i = 0; i < keys.length; i++) {
			out.put(keys[i], Integer.valueOf(values[i]));
		}
		return out;
	}

	public void tick() {
		if ((game.getWorld().getEntityManager().getPlayer().getCollision(0.0F, 0.0F)
				.intersects(new Rectangle((int) x, (int) y, width, height)))
				|| (new Rectangle((int) (game.getMouse().getMouseX() + game.getGameCamera().getxOffset()),
						(int) (game.getMouse().getMouseY() + game.getGameCamera().getyOffset()), 10, 20)
								.intersects(new Rectangle((int) x, (int) y, width, height)))) {
			if(!game.getPlayer().crafting.full) {
				pickedUp = true; 
				game.getEntityManager().getPlayer().putItem(name, 1);
			}
		}
		checkCol();

		if (id == FLOATING) {
			BufferedImage[] aliveArray = animation.getArray();

			if (downed) {
				timer += 1L;
				if (timer > 1000L) {
					pickedUp = true;
				}
			} else if (!checkFloor()) {
				animation.tick();
				if (animation.getFrame() == aliveArray[0]) {
					yMove = (rand / 3.0D);
					xMove = (rand / 10.0D);
				} else if (animation.getFrame() == aliveArray[1]) {
					yMove = (rand / 5.0D);
					xMove = (rand / 5.0D);
				} else if (animation.getFrame() == aliveArray[2]) {
					yMove = (rand / 10.0D);
					xMove = (rand / 5.0D);
				} else if (animation.getFrame() == aliveArray[3]) {
					yMove = (-rand / 10.0D);
					xMove = (rand / 10.0D);
				} else if (animation.getFrame() == aliveArray[4]) {
					yMove = (rand / 5.0D);
					xMove = (-rand / 5.0D);
				} else if (animation.getFrame() == aliveArray[5]) {
					yMove = (rand / 5.0D);
					xMove = (-rand / 5.0D);
				} else if (animation.getFrame() == aliveArray[6]) {
					yMove = (-rand / 10.0D);
					xMove = (-rand / 10.0D);
				}
				if (xMove + game.getWind() / 10.0D > 0.0D) {
					if (!right) {
						x += game.getWind() / 10.0D + xMove;
					}
				} else if ((xMove + game.getWind() / 10.0D < 0.0D) && (!left)) {
					x += game.getWind() / 10.0D + xMove;
				}

				if (right) {
					x -= 1.0D;
				}
				if (left) {
					x += 1.0D;
				}

				yMove += 0.001D;

				y += yMove;
			} else {
				downed = true;
			}
		} else if (id == NORMAL) {
			if (checkFloor()) {
				downed = true;
			}

			if (downed) {
				if (timer >= 10000L) {
					pickedUp = true;
				} else {
					timer += 1L;
				}
			} else {
				if (direction) {
					int tx = (int) (x + bounds.width) / Tile.TILEWIDTH;
					if ((!collisionWithTile(tx, (int) y / Tile.TILEHEIGHT))
							&& (!collisionWithTile(tx, (int) (y + bounds.height) / Tile.TILEHEIGHT))) {
						x += 1.0D;
					}
				} else {
					int tx = (int) x / Tile.TILEWIDTH;
					if ((!collisionWithTile(tx, (int) y / Tile.TILEHEIGHT))
							&& (!collisionWithTile(tx, (int) (y + bounds.height) / Tile.TILEHEIGHT))) {
						x -= 1.0D;
					}
				}
				y += yMove;
				yMove += 0.1D;
			}
		}
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

		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 1.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 1.0D) / Tile.TILEWIDTH), ty))) {
			bottom = true;
		} else if ((collisionWithDown((int) ((x + bounds.x + 1.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 1.0D) / Tile.TILEWIDTH), ty))) {
			if (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {
				down = (yMove >= 0.0D);
			}

			if ((y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1) && (yMove >= 0.0D)) {
				bottoms = true;
				bottom = true;
			}
		}

		ty = (int) ((y + yMove + bounds.y + bounds.height + 2.0D) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
				|| (((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
						|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty)))
						&& (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1))) {
			bottoms = true;
		}

		ty = (int) ((y + yMove + bounds.y) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))) {
			top = true;
		}
		ty = (int) ((y + yMove + bounds.y - 2.0D) / Tile.TILEHEIGHT);
		if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
				|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))) {
			tops = true;
		}

		int tx = (int) ((x + xMove + bounds.x + bounds.width) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))) {
			right = true;
		}
		tx = (int) ((x + xMove + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))) {
			rights = true;
		}

		tx = (int) ((x + xMove + bounds.x) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))) {
			left = true;
		}
		tx = (int) ((x + xMove + bounds.x - 2.0D) / Tile.TILEWIDTH);
		if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
				|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))) {
			lefts = true;
		}
	}

	protected boolean collisionWithDown(int x, int y) {
		return (World.getTile(x, y).isSolid()) || (World.getTile(x, y).isTop());
	}

	public boolean checkFloor() {
		int ty = (int) (y + yMove + bounds.height) / Tile.TILEHEIGHT;
		return (collisionWithDown((int) (x + bounds.width) / Tile.TILEWIDTH, ty))
				&& (collisionWithDown((int) x / Tile.TILEWIDTH, ty));
	}

	protected boolean collisionWithTile(int x, int y) {
		return World.getTile(x, y).isSolid();
	}

	public boolean checkCollision(float xOffset, float yOffset) {
		for (Entity e : game.getWorld().getEntityManager().getEntities()) {
			if ((e.isSolid) && (e.getCollision(0.0F, 0.0F).intersects(getCollision(xOffset, yOffset)))) {
				return true;
			}
		}
		return false;
	}

	public Rectangle getCollision(float xOffset, float yOffset) {
		return new Rectangle((int) (x + bounds.x + xOffset), (int) (y + bounds.y + yOffset), bounds.width,
				bounds.height);
	}

	public void render(Graphics g, boolean box) {
		if (game == null)
			return;
		render(g, (int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()));

		if (box) {
			showBox(g, (int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()));
		}
	}

	public void showBox(Graphics g, int x, int y) {
		g.setColor(Color.orange);

		g.drawRect(x, y, bounds.width, bounds.height);
		g.drawRect(game.getMouse().getMouseX(), game.getMouse().getMouseY(), 10, 20);
	}

	public void render(Graphics g, int x, int y) {
		g.drawImage(getFrame(), x, y, null);
	}

	public Item createNew(int x, int y) {
		Item i = new Item(animation, dead, inventory, name, id);
		i.setPosition(x, y);
		return i;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		bounds.x = x;
		bounds.y = y;
	}

	private boolean direction;

	public Handler getHandler() {
		return game;
	}

	public void setHandler(Handler handler) {
		game = handler;
	}

	public BufferedImage getTexture() {
		return texture;
	}

	public void setTexture(BufferedImage texture) {
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public boolean isPickedUp() {
		return pickedUp;
	}

	public BufferedImage getFrame() {
		if (downed) {
			return dead;
		}
		return animation.getFrame();
	}
}
