package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.SpriteSheet;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;
import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class Tree extends StaticEntity {
	private static final long serialVersionUID = 1L;

	BufferedImage[][] tree = PublicAssets.tree;

	public static BufferedImage stump, trunk, trunk2, branch0, branch1, branch2, branchn0, branchn1, branchn2, leaf1, leaf2, leaf0;
	public static BufferedImage top0, top1, top2, top3, top4, top5;

	private BufferedImage sprite, out0, out1, out2, out3, out4, leaf, out5, out6, out7, out8;

	public static double TEMP_DIFF = 2;

	double[] chanceLeaves = { 0.05D, 0.1D, 0.2D, 0.5D, 0.5D, 1.0D, 2.0D, 4.0D, 5.0D, 6.0D, 6.0D, 2.0D, 0.0D, 0.0D, 0.0D,
			0.0D, 0.0D, 0.01D, 0.02D };

	public int age = 0;

	int growthTime;
	long lastTime = 0L;

	double TempOffset = 0;

	ArrayList<Leaf> leaves;

	int widthflip = 1;

	double debY = 0;

	HashMap<Integer, Integer> branches = new HashMap<Integer, Integer>();

	public static Color fallColor(double t) {
		int r = (int) (210 * pow(E, -pow(t - 40, 2) / 220) + 45);
		int g = (int) (200 * (atan((t - 40) / 4) / PI + 0.75));
		int b = (int) (50 * (atan((t - 40) / 8) / PI + 0.75));

		return new Color(r, g, b);
	}

	public static Color springColor(double t) {
		int r = 45;
		int g = (int) (200 * (atan((t - 40) / 4) / PI + 0.75));
		int b = (int) (50 * (atan((t - 40) / 8) / PI + 0.75));

		return new Color(r, g, b);
	}

	public static void init() {
		BufferedImage sheet = ImageLoader.loadImage("/textures/Statics/Tree.png");
		BufferedImage lsheet = ImageLoader.loadImage("/textures/Statics/Leaves.png");
		stump = sheet.getSubimage(0, 126, 36, 18);
		trunk = sheet.getSubimage(0, 108, 36, 18);
		trunk2 = sheet.getSubimage(0, 90, 36, 18);
		branch0 = sheet.getSubimage(0, 54, 36, 18);
		branch1 = sheet.getSubimage(0, 36, 54, 18);
		branch2 = sheet.getSubimage(0, 0, 81, 36);
		branchn0 = sheet.getSubimage(126, 54, 36, 18);
		branchn1 = sheet.getSubimage(108, 36, 54, 18);
		branchn2 = sheet.getSubimage(81, 0, 81, 36);

		top0 = sheet.getSubimage(54, 72,108, 72);
		top1 = sheet.getSubimage(162, 72,108, 72);
		top2 = sheet.getSubimage(270, 72,108, 72);
		top3 = sheet.getSubimage(378, 72,108, 72);
		top4 = sheet.getSubimage(486, 72,108, 72);
		top5 = sheet.getSubimage(594, 72,108, 72);

		leaf0 = lsheet.getSubimage(72, 54, 108, 90);
		leaf1 = lsheet.getSubimage(12, 0, 61, 45);
		leaf2 = lsheet.getSubimage(40, 46, 22, 21);
	}

	public Tree(Handler handler, double x, double y, int age) {
		super(handler, x, y, 36, 144, false, age * 2, PlayerItem.AXE);
		debY = y;
		if (stump == null) {
			init();
		}
		maxSnowy = 100;
		snowy = 0;

		layer = Public.random(-10.0D, 10.0D);

		leaves = new ArrayList<Leaf>();

		this.age = age;
		for(int i = 0; i < age / 3; i++)
			branches.put(i, (int) (Math.round(Public.debugRandom(-0.65, 0.65))*Public.random(1, 3)));
		updateSprite();
		updateLeaves();
		bounds.x = 0;
		bounds.y = -(int)((90 + (int)(age/3)*18)*((age+4.0)/16.0));
		bounds.width = (int)(108*((age+4.0)/16.0));
		bounds.height = -bounds.y;
		// Go to center of spawn
		this.x -= bounds.width/2.0;

		if (Math.random() < 0.5D) {
			widthflip = -1;
		}

		growthTime = ((int) Public.random(-1000.0D, 5000.0D)); 

		TempOffset = Public.random(-10, 10);
	}
	
	boolean l = false;

	private void updateSprite() {
		sprite = new BufferedImage(108, 90 + (age/3)*18, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = sprite.createGraphics();
		switch(age/3) {
			case 0:
				g.drawImage(top5, 0, 0, null);
				break;
			case 1:
				g.drawImage(top4, 0, 0, null);
				break;
			case 2:
				g.drawImage(top3, 0, 0, null);
				break;
			case 3:
				g.drawImage(top2, 0, 0, null);
				break;
			case 4:
				g.drawImage(top1, 0, 0, null);
				break;
			default:
				g.drawImage(top0, 0, 0, null);
				break;
		}
		for(int i = 0; i < age/3; i++) {
			switch(branches.get(i)) {
				case 1:
					branches.put(i, 2);
					g.drawImage(branch0, 36, 72 + i * 18, null);
					break;
				case 2:
					branches.put(i, 3);
					g.drawImage(branch1, 36, 72 + i * 18, null);
					break;
				case 3:
					g.drawImage(branch2, 36, 54 + i * 18, null);
					break;
				case -1:
					branches.put(i, -2);
					g.drawImage(branchn0, 36, 72 + i * 18, null);
					break;
				case -2:
					branches.put(i, -3);
					g.drawImage(branchn1, 18, 72 + i * 18, null);
					break;
				case -3:
					g.drawImage(branchn2, -9, 54 + i * 18, null);
					break;
				default:
					g.drawImage(trunk, 36, 72 + i * 18, null);
			}
		}
		g.drawImage(stump, 36, 72+(age/3)*18, null);

		out0 = new BufferedImage(sprite.getWidth()+2, sprite.getHeight()+2, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D h = out0.createGraphics();
		BufferedImage bleach = Tran.bleachImage(sprite, Color.orange);
		h.drawImage(bleach, 0, 0, null);
		h.drawImage(bleach, 1, 0, null);
		h.drawImage(bleach, 2, 0, null);
		h.drawImage(bleach, 0, 1, null);
		h.drawImage(bleach, 2, 1, null);
		h.drawImage(bleach, 0, 2, null);
		h.drawImage(bleach, 1, 2, null);
		h.drawImage(bleach, 2, 2, null);
		h.dispose();
		out1 = Tran.effectAlpha(out0, 200);
		out2 = Tran.effectAlpha(out0, 150);
		out3 = Tran.effectAlpha(out0, 100);
		out4 = Tran.effectAlpha(out0, 50);
	}

	private void updateLeaves() {
		leaf = new BufferedImage(108, 108+(age/3)*18, BufferedImage.TYPE_4BYTE_ABGR);
		BufferedImage l1 = Tran.effectColor(leaf1, fallColor((game.getEnviornment().getTemp() + TempOffset)));
		BufferedImage l2 = Tran.effectColor(leaf2, fallColor((game.getEnviornment().getTemp() + TempOffset)));
		Graphics2D g = leaf.createGraphics();
		g.drawImage(Tran.effectColor(leaf0, fallColor((game.getEnviornment().getTemp() + TempOffset))), 0, 0, null);
		for(int i = 0; i < age/3; i++) {
			switch(branches.get(i)) {
				case 2:
					g.drawImage(l1, 40, 64 + i * 18, null);
					break;
				case 3:
					g.drawImage(l2, 47, 54 + i * 18, null);
					break;
//				case -2:
//					g.drawImage(Tran.flip(l1, -1,1), 46, 64 + i * 18, null);
//					break;
//				case -3:
//					g.drawImage(Tran.flip(l2, -1,1), 0, 54 + i * 18, null);
//					break;
			}
		}
		g.dispose();

//		leaf = Tran.effectColor(leaf, fallColor((game.getEnviornment().getTemp() + TempOffset)));
	}

	public void tick() {
		
//		System.out.println("snowy : " + snowy);
		
		if (game.getWorld().getEnviornment().getTotalTime() - lastTime >= Math.pow(1.9, age+1)*1000 + growthTime) {
			lastTime = game.getWorld().getEnviornment().getTotalTime();
			if((age/3)*3 == age)
				branches.put(age/3, (int) Math.round(Public.debugRandom(-0.65, 0.65)));
			age += 1;
			updateSprite();
			growthTime = ((int) Public.random(-5000.0D - game.getWorld().getEnviornment().getHumidity() * 10.0D,
					10000.0D));

			if (age == 16) {
				game.getWorld().kill(this);
				return;
			}

			bounds.x = 0;
			bounds.y = -(int)((90 + (int)(age/3)*18)*((age+4.0)/16.0));
			bounds.width = (int)(108*((age+4.0)/16.0));
			bounds.height = -bounds.y;
		}

		if((game.getEnviornment().getTemp() + TempOffset*2) % TEMP_DIFF == 0)
			updateLeaves();
		if ((age < 8) && (getStage() != 0)) {
			game.getWorld().kill(this);
		}

		if ((Public.chance(chanceLeaves[getStage()] / 2))) {
			game.getWorld().getEntityManager()
					.addEntity(new Leaf(game, Public.random(x-(age+4)/4.0, x + bounds.width),
							Public.random(y - 18.0*((age+4)/16.0), y + 90.0*((age+4)/16.0))-bounds.height,
							(int) ((game.getEnviornment().getTemp() + TempOffset) / TEMP_DIFF)), false);
		}
	}
	public void render(Graphics2D g) {
		AffineTransform pre = g.getTransform();
		g.translate((int) Public.xO(x + (widthflip == -1 ? bounds.width : 0)), (int) Public.yO(debY+bounds.y));
		g.scale((age+4)/16.0*widthflip, (age+4)/16.0);
		g.drawImage(sprite, 0, 0, null);
		g.drawImage(leaf, -4, -18, null);
		g.setTransform(pre);
	}
	
	public void renderLight(Graphics2D g, int opacity) {
		AffineTransform pre = g.getTransform();
		g.translate((int) Public.xO(x + (widthflip == -1 ? bounds.width : 0) - widthflip), (int) Public.yO(debY+bounds.y - 1));
		g.scale((age+4)/16.0*widthflip, (age+4)/16.0);
//		g.translate((int) Public.xO(x - 1), (int) Public.yO(debY+bounds.y-1));
//		g.scale((age+4)/16.0, (age+4)/16.0);
		if(opacity>200)
			g.drawImage(out0, 0, 0, null);
		else if(opacity>150)
			g.drawImage(out1, 0, 0, null);
		else if(opacity>100)
			g.drawImage(out2, 0, 0, null);
		else if(opacity>50)
			g.drawImage(out3, 0, 0, null);
		else if(opacity>0)
			g.drawImage(out4, 0, 0, null);
		g.setTransform(pre);
	}

	private int getStage() {
		double temp = game.getWorld().getEnviornment().getTemp() + TempOffset;
		int day = game.getWorld().getEnviornment().rohundo;

//    if ((day < 151) && (day > 59)) {
//      if (day < 65)
//        return 12;
//      if (day < 90)
//        return 15;
//      if (day < 110)
//        return 16;
//      if (day < 120) {
//        return 17;
//      }
//      return 18;
//    }
		if (day > 40) {
			// 11 Stages between Green and bare
			if (temp > 66)
				return 0;
			if (temp > 60)
				return 1;
			if (temp > 54)
				return 2;
			if (temp > 48)
				return 3;
			if (temp > 42)
				return 4;
			if (temp > 36)
				return 5;
			if (temp > 30)
				return 6;
			if (temp > 24)
				return 7;
			if (temp > 18)
				return 8;
			if (temp > 12)
				return 9;
			if (temp > 6)
				return 10;
			if (temp > 0)
				return 11;
			else {
				if (snowy > 6)
					return 13;
				else if (snowy > 2)
					return 12;
				else
					return 11;
			}
		} else if (day < 41) {
			if (temp > 64)
				return 0;
			if (temp > 54)
				return 18;
			if (temp > 44)
				return 17;
			if (temp > 34)
				return 16;
			if (temp > 24)
				return 15;
			else {
				if (snowy > 6)
					return 13;
				else if (snowy > 2)
					return 12;
				else
					return 11;
			}
		}
		return 0;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
		updateSprite();
	}

	public class Leaf extends StaticEntity {
		private static final long serialVersionUID = 1L;
		
		double rotation = 270.0D;
		double spiralOff = Public.random(0.0D, 1.0D);

		double[] spiralDiff = { 5.0D, 3.0D, 1.0D, 3.0D, 5.0D, 6.0D, 7.0D, 6.0D };

		int widthFlip = 1;

		int state = 1;

		int stage;

		int frame = 0;
//		BufferedImage[][] frames = PublicAssets.leaves;
		BufferedImage[] leaves = new BufferedImage[8];
		long timer = 0L;

		long age = 0L;
		public boolean left;
		public boolean right;
		public boolean top;
		public boolean bottom;
		public boolean down;
		public boolean lefts;
		public boolean rights;
		public boolean tops;
		public boolean bottoms;
		double xMove = 0.0D;
		double yMove = 0.0D;
		double weight = Public.random(0.1D, 0.5D);
		boolean frame2already;

		long start;
		
		public Leaf(Handler handler, double x, double y, int stage) {
			super(handler, x, y, 12, 12, false, 1, PlayerItem.NONE);
			
			start = System.currentTimeMillis();

			BufferedImage full = Tran.effectColor(ImageLoader.loadImage("/textures/statics/Shrubbery/Leaves.png"), Tree.fallColor(game.getEnviornment().getTemp()));
			for(int i = 0; i < 8; i++) {
				leaves[i] = full.getSubimage(i*12,0, 12, 12);
			}

			this.stage = stage;
			if (stage <= 0) {
				this.stage = 0;
				handler.getWorld().kill(this);
			}
			layer = -1;
		}

		public void render(Graphics2D g) {
			frame = ((int) Public.range(0.0D, 6.0D, frame));
			g.drawImage(Tran.flip(leaves[frame], widthFlip, 1), (int) (x - game.xOffset()),
					(int) (y - game.yOffset()), null);
		}

		boolean backforth;

		public void tick() {
			checkCol();

			double wind = game.getWind();

			if ((state != 0) && (state != 1)) {
				widthFlip = (wind > 0.0D ? 1 : -1);
			}
			if (state == 2) {
				if (widthFlip == 1) {
					rotation += spiralOff + spiralDiff[frame];
				} else {
					rotation -= spiralOff + spiralDiff[frame];
				}
				if (frame == 2) {
					if (frame2already) {
						state = 3;
					} else
						frame2already = true;
				}
			}
			if (state == 3) {
				if (timer > 100L) {
					rotation += Public.random(-5.0D, 5.0D);

					if (widthFlip == 1) {
						if (Public.identifyRange(-70.0D, 70.0D, rotation)) {
							if (rotation > 90.0D) {
								rotation -= 5.0D;
							} else
								rotation += 5.0D;
						}
					} else if (Public.identifyRange(110.0D, 250.0D, rotation)) {
						if (rotation > 250.0D) {
							rotation -= 5.0D;
						} else {
							rotation += 5.0D;
						}
					}
					timer = 0L;

					if (Public.chance(0.1D)) {
						state = 2;
					}
				}
				timer += 1L;
			}

			if (state == 0) {
				frame = 2;
				rotation = 0.0D;

				if (((wind > 0.0D) && (!right)) || ((wind < 0.0D) && (!left))) {
					x += wind / 10.0D;
				}
				if (!bottom) {
					state = 3;
					timer = 200L;
				}
			}

			if ((!Public.over(wind, game.getWorld().getEnviornment().getMaxWind(),
					game.getWorld().getEnviornment().getMaxWind(), 0.1D + weight / 2.0D))
					|| (Public.angle(rotation)[1] < weight)) {
				state = 1;
			}

			if (bottom) {
				state = 0;
			}
			if ((state != 0) && (state != 1)) {
				if (((!right) && (Public.angle(rotation)[0] > 0.0D)) || ((!left) && (Public.angle(rotation)[0] < 0.0D)))
					x += Public.angle(rotation)[0] * Math.abs(wind);
				if (((!bottom) && (Public.angle(rotation)[1] > 0.0D)) || ((!top) && (Public.angle(rotation)[1] < 0.0D)))
					y += Public.angle(rotation)[1] * 5.0D + weight;
			} else if (state == 1) {
				widthFlip = 1;

				if (Public.over(wind, game.getWorld().getEnviornment().getMaxWind(),
						game.getWorld().getEnviornment().getMaxWind(), 0.2D)) {
					state = 2;
				}

				if (!bottoms) {
					if ((frame == 0) && (backforth)) {
						yMove = (Math.random() / 5.0D);
						xMove = (Math.random() / 5.0D);
					} else if (((frame == 1) || (frame == 2)) && (backforth)) {
						yMove = (Math.random() / 10.0D);
						xMove = (Math.random() / 5.0D);
					} else if (((frame == 3) || (frame == 4)) && (backforth)) {
						yMove = (-Math.random() / 10.0D);
						xMove = (Math.random() / 10.0D);
					} else if ((frame == 3) || (frame == 4)) {
						yMove = (Math.random() / 5.0D);
						xMove = (-Math.random() / 5.0D);
					} else if ((frame == 1) || (frame == 2)) {
						yMove = (Math.random() / 5.0D);
						xMove = (-Math.random() / 5.0D);
					} else if (frame == 0) {
						yMove = (-Math.random() / 10.0D);
						xMove = (-Math.random() / 10.0D);
					}
					if (xMove + game.getWind() / 10.0D > 0.0D) {
						if (!right) {
							x += game.getWind() / 10.0D + xMove * 4.0D;
						}
					} else if ((xMove + game.getWind() / 10.0D < 0.0D) && (!left)) {
						x += game.getWind() / 10.0D + xMove * 4.0D;
					}

					if (right) {
						x -= 1.0D;
					}
					if (left) {
						x += 1.0D;
					}

					yMove += 0.001D;

					if (!bottom) {
						y += yMove * 5.0D + weight;
					}

					if (timer > 15L) {
						if (frame == 0) {
							backforth = true;
						} else if (frame >= 4) {
							backforth = false;
						}
						if (backforth) {
							frame += 1;
						} else {
							frame -= 1;
						}
						timer = 0L;
					}
					timer += 1L;
				} else {
					state = 0;
				}
			}

			if (state != 1) {
				setFrame();
			}

			game.getWorld().outOfBounds(this);

			if (System.currentTimeMillis()-start > 10000) {
				game.getWorld().kill(this);
			}
		}

		private void setFrame() {
			double r = rotation;
			if(r >= 360.0D)
				r %= 360.0D;
			while (r < 0.0D)
				r += 360.0D;
			if ((r >= 247.5D) && (r < 292.5D)) {
				frame = 0;
			} else if ((r >= 292.5D) && (r < 337.5D)) {
				frame = 1;
			} else if ((r >= 337.5D) || (r < 22.5D)) {
				frame = 2;
			} else if ((r >= 22.5D) && (r < 67.5D)) {
				frame = 3;
			} else if ((r >= 67.5D) && (r < 112.5D)) {
				frame = 4;
			} else if ((r >= 112.5D) && (r < 157.5D)) {
				frame = 5;
			} else if ((r >= 157.5D) && (r < 202.5D)) {
				frame = 6;
			} else if ((r >= 202.5D) && (r < 247.5D))
				frame = 7;
		}

		public boolean collisionWithTile(int x, int y) {
			return World.getTile(x, y).isSolid();
		}

		public boolean collisionTile(int x, int y) {
			return World.getTile((int) Math.floor(x / Tile.TILEWIDTH), (int) Math.floor(y / Tile.TILEHEIGHT)).isTop();
		}

		public boolean collisionWithDown(int x, int y) {
			return (World.getTile(x, y).isSolid()) || (World.getTile(x, y).isTop());
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

			int ty = (int) ((y + bounds.y + bounds.height) / Tile.TILEHEIGHT);
			if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
					|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
					|| (checkCollision(0.0F, 0.0F))) {
				bottom = true;
			} else if ((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
					|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))) {
				if (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {
					down = true;
				}

				if (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1) {
					bottoms = true;
					bottom = true;
				}
			}
			ty = (int) ((y + bounds.y + bounds.height + 2.0D) / Tile.TILEHEIGHT);
			if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
					|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
					|| (checkCollision(0.0F, 1.0F))
					|| (((collisionWithDown((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
							|| (collisionWithDown((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty)))
							&& (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1))) {
				bottoms = true;
			}

			ty = (int) ((y + bounds.y) / Tile.TILEHEIGHT);
			if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
					|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
					|| (checkCollision(0.0F, 0.0F))) {
				top = true;
			}
			ty = (int) ((y + bounds.y - 2.0D) / Tile.TILEHEIGHT);
			if ((collisionWithTile((int) ((x + bounds.x + 2.0D) / Tile.TILEWIDTH), ty))
					|| (collisionWithTile((int) ((x + bounds.x + bounds.width - 2.0D) / Tile.TILEWIDTH), ty))
					|| (checkCollision(0.0F, -1.0F))) {
				tops = true;
			}

			int tx = (int) ((x + bounds.x + bounds.width) / Tile.TILEWIDTH);
			if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
					|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
					|| (checkCollision(1.0F, 0.0F))) {
				right = true;
			}
			tx = (int) ((x + bounds.x + bounds.width + 2.0D) / Tile.TILEWIDTH);
			if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
					|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
					|| (checkCollision(1.0F, 0.0F))) {
				rights = true;
			}

			tx = (int) ((x + bounds.x) / Tile.TILEWIDTH);
			if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
					|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
					|| (checkCollision(0.0F, 0.0F))) {
				left = true;
			}
			tx = (int) ((x + bounds.x - 2.0D) / Tile.TILEWIDTH);
			if ((collisionWithTile(tx, (int) (y + bounds.y + 2.0D) / Tile.TILEHEIGHT))
					|| (collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2.0D) / Tile.TILEHEIGHT))
					|| (checkCollision(-1.0F, 0.0F))) {
				lefts = true;
			}
		}
	}

	public String outString() {
		return "Tree " + x + " " + y + " " + layer + " " + age;
	}

}
