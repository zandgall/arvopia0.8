package com.zandgall.arvopia.gfx;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.gfx.transform.Tran;

public class PublicAssets {
	public static BufferedImage[] flower;
	public static Animation whiteFlower;
	public static Animation pinkFlower;
	public static Animation blueFlower;
	public static Animation[] flowerFinal;
	public static BufferedImage[] shrubbery;
	public static BufferedImage snowyGrassEntity;
	public static BufferedImage[] stone;
	public static BufferedImage[] bee;
	public static BufferedImage[] butterfly;
	public static BufferedImage[] fox;
	public static BufferedImage beehive;
	public static Animation foxStill;
	public static Animation foxWalk;
	public static Animation foxSit;
	public static Animation cjump;
	public static Animation cstill;
	public static Animation ccrouch;
	public static Animation cwalk;
	public static Animation cpunch;
	public static Animation chold;
	public static Animation cstab;
	public static BufferedImage cairKick;
	public static Assets cannibal;
	public static BufferedImage grass;
	public static BufferedImage torch;
	public static BufferedImage torchStab;
	public static BufferedImage sword;
	public static BufferedImage swordStab;
	public static BufferedImage[] bridge;
	public static BufferedImage[] cloud;
	public static BufferedImage[] respawn;
	public static BufferedImage[] start;
	public static BufferedImage[][] tree, treeTrunk, treeLeaves;
	public static BufferedImage[][] leaves;
	public static Animation[] treeAnim;

	public PublicAssets() {
	}

	public static void init() {
		torch = ImageLoader.loadImage("/textures/Inventory/Tools/Torch/Torch.png");
		torchStab = ImageLoader.loadImage("/textures/Inventory/Tools/Torch/TorchStab.png");

		sword = ImageLoader.loadImage("/textures/Inventory/Tools/Sword/Sword.png");
		swordStab = ImageLoader.loadImage("/textures/Inventory/Tools/Sword/SwordStab.png");

		beehive = ImageLoader.loadImage("/textures/Statics/Beehive.png");

		grass = ImageLoader.loadImage("/textures/Tiles/DirtTileset.png").getSubimage(72, 0, 18, 18);

		SpriteSheet s = new SpriteSheet(ImageLoader.loadImage("/textures/Statics/Flowers/Flowers.png"));

		flower = new BufferedImage[9];
		flower[0] = s.get(0, 0, 18, 18);
		flower[1] = s.get(0, 18, 18, 18);
		flower[2] = s.get(0, 36, 18, 18);
		flower[3] = s.get(18, 0, 18, 18);
		flower[4] = s.get(18, 18, 18, 18);
		flower[5] = s.get(18, 36, 18, 18);
		flower[6] = s.get(36, 0, 18, 18);
		flower[7] = s.get(36, 18, 18, 18);
		flower[8] = s.get(36, 36, 18, 18);

		shrubbery = new BufferedImage[13];
		shrubbery[0] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Grass.png").getSubimage(0, 0, 18, 18);
		shrubbery[1] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Grass.png").getSubimage(18, 0, 18, 18);
		shrubbery[2] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Grass.png").getSubimage(36, 0, 18, 18);
		shrubbery[3] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Fern.png");
		shrubbery[4] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Dandylion.png");
		shrubbery[5] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Bush.png").getSubimage(0, 0, 38, 39);
		shrubbery[6] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Bush.png").getSubimage(39, 17, 20, 22);
		shrubbery[7] = ImageLoader.loadImage("/textures/Statics/Shrubbery/Bush.png").getSubimage(49, 20, 20, 19);

		snowyGrassEntity = ImageLoader.loadImage("/textures/Statics/Shrubbery/SnowGrassOverlay.png");

		whiteFlower = new Animation(175, new BufferedImage[] { flower[0], flower[1], flower[0], flower[2] }, "White",
				"Flower");
		pinkFlower = new Animation(175, new BufferedImage[] { flower[3], flower[4], flower[3], flower[5] }, "Pink",
				"Flower");
		blueFlower = new Animation(175, new BufferedImage[] { flower[6], flower[7], flower[6], flower[8] }, "Blue",
				"Flower");

		flowerFinal = new Animation[] { whiteFlower, pinkFlower, blueFlower };

		Color[] c = new Color[] { 
				new Color(30, 190, 60), // 1
				new Color(90, 190, 60), // 2
				new Color(150, 190, 60), // 3
				new Color(200, 160, 10), // 4
				new Color(200, 110, 10), // 5
				new Color(195, 80, 10), // 6
				new Color(190, 60, 30), // 7
				new Color(130, 55, 25), // 8
				new Color(70, 50, 20), // 9
				new Color(70, 50, 20, 255), // 10
				new Color(70, 50, 20, 180), // 11
				new Color(70, 50, 20, 90), // 12
				new Color(220, 220, 250, 0), // 13
				new Color(220, 220, 250, 125), // 13
				new Color(220, 220, 250), // 15
				new Color(30, 190, 80, 0), // 16
				new Color(30, 190, 80, 125), // 16
				new Color(30, 190, 80),// 18
		};

		tree = new BufferedImage[17][116];
		treeTrunk = new BufferedImage[17][116];
		treeLeaves = new BufferedImage[17][116];

		for (int y = 0; y < 17; y++) {

			s = new SpriteSheet(Tran.effectColor(ImageLoader.loadImage("/textures/Statics/Shrubbery/TreeLeaves.png"), c[y]));
			SpriteSheet temp = new SpriteSheet(ImageLoader.loadImage("/textures/Statics/Shrubbery/TreeTEMP.png"));
			SpriteSheet trunk = new SpriteSheet(ImageLoader.loadImage("/textures/Statics/Shrubbery/TreeTrunk.png"));
			
			for (int x = 0; x < 13; x++) {
				tree[y][x] = temp.get(x * 36, 0, 36, 144);
				treeLeaves[y][x] = s.get(x * 36, 0, 36, 144);
				treeTrunk[y][x] = trunk.get(x * 36, 0, 36, 144);
			}

			for (int x = 0; x < 2; x++) {
				tree[y][x+13] = temp.get((13 * 36) + x * 72, 0, 72, 144);
				treeLeaves[y][x] = s.get((13 * 36) + x * 36, 0, 36, 144);
				treeTrunk[y][x] = trunk.get((13 * 36) + x * 36, 0, 36, 144);
			}
		}

		leaves = new BufferedImage[6][7];
//		BufferedImage leafPack = ImageLoader.loadImage("/textures/Statics/Shrubbery/Leaves.png");
//		for (int x = 0; x < 7; x++) {
//			for (int y = 0; y < 6; y++) {
//				leaves[y][x] = leafPack.getSubimage(x * 12, y * 12, 12, 12);
//			}
//		}

		s = new SpriteSheet(ImageLoader.loadImage("/textures/Statics/Stone.png"));

		stone = new BufferedImage[3];
		stone[0] = s.get(0, 0, 18, 18);
		stone[1] = s.get(18, 0, 18, 18);
		stone[2] = s.get(36, 0, 18, 18);

		s = new SpriteSheet(ImageLoader.loadImage("/textures/Creatures/Bee.png"));

		bee = new BufferedImage[4];
		bee[0] = s.get(0, 0, 6, 6);
		bee[1] = s.get(6, 0, 6, 6);
		bee[2] = s.get(12, 0, 6, 6);
		bee[3] = s.get(18, 0, 6, 6);

		s = new SpriteSheet(ImageLoader.loadImage("/textures/Creatures/Butterfly.png"));

		butterfly = new BufferedImage[8];
		butterfly[0] = s.get(0, 0, 18, 18);
		butterfly[1] = s.get(18, 0, 18, 18);
		butterfly[2] = s.get(18, 18, 18, 18);
		butterfly[3] = s.get(0, 18, 18, 18);
		butterfly[4] = s.get(36, 0, 18, 18);
		butterfly[5] = s.get(54, 0, 18, 18);
		butterfly[6] = s.get(54, 18, 18, 18);
		butterfly[7] = s.get(36, 18, 18, 18);

		s = new SpriteSheet(ImageLoader.loadImage("/textures/Creatures/Fox.png"));

		fox = new BufferedImage[8];
		fox[0] = s.get(0, 0, 54, 36);
		fox[1] = s.get(54, 0, 54, 36);
		fox[2] = s.get(108, 0, 54, 36);
		fox[3] = s.get(162, 0, 54, 36);
		fox[4] = s.get(0, 36, 54, 36);
		fox[5] = s.get(54, 36, 54, 36);
		fox[6] = s.get(108, 36, 54, 36);
		fox[7] = s.get(162, 36, 54, 36);

		s = new SpriteSheet(ImageLoader.loadImage("/textures/Tiles/Bridge.png"));

		bridge = new BufferedImage[] { s.get(0, 0, 18, 18), s.get(18, 0, 18, 18), s.get(18, 18, 18, 18),
				s.get(0, 18, 18, 18), s.get(36, 0, 18, 18), s.get(36, 18, 18, 18), s.get(54, 0, 18, 18),
				s.get(54, 18, 18, 18) };

		s = new SpriteSheet(ImageLoader.loadImage("/textures/Statics/Cloud.png"));

		cloud = new BufferedImage[4];
		cloud[0] = s.get(0, 0, 54, 36);
		cloud[1] = s.get(0, 36, 54, 36);
		cloud[2] = s.get(0, 72, 54, 36);
		cloud[3] = s.get(0, 108, 54, 36);

		cannibal = new Assets(ImageLoader.loadImage("/textures/Creatures/Cannibal/Cannibal.png"), 36, 54, "Cannibal");

		cjump = new Animation(1000, new BufferedImage[] { cannibal.get(0, 1) }, "Jump", "Cannibal");
		cstill = new Animation(750, new BufferedImage[] { cannibal.get(0, 0), cannibal.get(1, 0) }, "Still",
				"Cannibal");
		cwalk = new Animation(250, new BufferedImage[] { cannibal.get(1, 1), cannibal.get(3, 1) }, "Walk", "Cannibal");
		ccrouch = new Animation(750, new BufferedImage[] { cannibal.get(2, 0), cannibal.get(3, 0) }, "Crouch",
				"Cannibal");

		cannibal = new Assets(ImageLoader.loadImage("/textures/Creatures/Cannibal/CannibalPunch.png"), 36, 54,
				"Cannibal Punching");
		cpunch = new Animation(150, new BufferedImage[] { cannibal.get(1, 0), cannibal.get(2, 0), cannibal.get(0, 0) },
				"Punch", "Cannibal");
		cairKick = ImageLoader.loadImage("/textures/Creatures/Cannibal/CannibalAirKick.png");

		cannibal = new Assets(ImageLoader.loadImage("/textures/Creatures/Cannibal/CannibalHolding.png"), 36, 54,
				"Cannibal Holding");
		chold = new Animation(750, new BufferedImage[] { cannibal.get(0, 0), cannibal.get(1, 0) }, "Hold", "Cannibal");

		cannibal = new Assets(ImageLoader.loadImage("/textures/Creatures/Cannibal/CannibalStab.png"), 36, 54,
				"Cannibal Stabbing");
		cstab = new Animation(150, new BufferedImage[] { cannibal.get(0, 0), cannibal.get(1, 0), cannibal.get(2, 0) },
				"Stab", "Cannibal");
	}
}
