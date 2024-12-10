package com.zandgall.arvopia.tiles;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
	public static ArrayList<Tile> tiles = new ArrayList<>();
	public static HashMap<String, Tile> tileMap = new HashMap<>();

	public static final String[] pre_0_7_identities = {
		"TILE0", "TILE162", "TILE34", "TILE35", "TILE146", "TILE2", "TILE67", "TILE160", "TILE8", "TILE73", "TILE166", "TILE54", "TILE55", "TILE163", "TILE211", "TILE215", "TILE167", "TILE66", "TILE34", "TILE7", "TILE136", "TILE210", "TILE83", "TILE6", "TILE0", "TILE3", "TILE130", "TILE149", "TILE195",
			"BRIDGE16", "FENCE112", "FENCE240", "FENCE49", "FENCE49", "BRIDGE32", "FENCE240", "BRIDGE0",
			"TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0",
			"TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0", "TILE0",
			"TILE0"
	};
	public static EmptyTile n0 = new EmptyTile();
	public static Tile barrier = new Tile(ImageLoader.loadImage("/textures/Null.png"), "TILE1") {
		@Override
		public void tick(Handler paramHandler, int paramInt1, int paramInt2) {

		}

		@Override
		public boolean isSolid() {
			return true;
		}

		@Override
		public Color getColor() {
			return new Color(100, 200, 255);
		}
	};

	public static final int WIDTH = 18;
	public static final int HEIGHT = 18;
	protected int x;
	protected int y;
	protected static HashMap<Point, Double> snowy = new HashMap<>();

	public enum VALID_STATES {
		AIR, SOLID, ANYTHING, TOP_SOLID, NON_SOLID, SAME_TYPE, NOT_AIR, SUPPORTS_SHRUBBERY, DOESNT_SUPPORT_SHRUBBERY, SAME_TYPE_OR_SOLID
	}
	public static Map<Integer, VALID_STATES> VALID_STATES_COLOR_CODE = Map.of(Color.white.getRGB(), VALID_STATES.AIR, Color.black.getRGB(), VALID_STATES.SOLID, Color.red.getRGB(), VALID_STATES.ANYTHING, Color.green.getRGB(), VALID_STATES.TOP_SOLID,
			Color.blue.getRGB(), VALID_STATES.NON_SOLID, Color.yellow.getRGB(), VALID_STATES.SAME_TYPE, Color.magenta.getRGB(), VALID_STATES.NOT_AIR, Color.cyan.getRGB(), VALID_STATES.SUPPORTS_SHRUBBERY, Color.gray.getRGB(), VALID_STATES.DOESNT_SUPPORT_SHRUBBERY,
			new Color(0, 128, 255).getRGB(), VALID_STATES.SAME_TYPE_OR_SOLID);
	protected VALID_STATES[][] validityKernel;
	protected String id;
	protected boolean solid = false, top_solid = false, organic = false;
	private final BufferedImage texture, glow;
	
	public String modIdentifier = "Vanilla", tileIdentifier = "Tile";

	public Tile(BufferedImage texture, String id) {
		this.id = id;

		this.texture = texture;

		tiles.add(this);
		tileMap.put(id, this);

		validityKernel = new VALID_STATES[3][3];
		setValidityKernel(
				VALID_STATES.ANYTHING, VALID_STATES.ANYTHING, VALID_STATES.ANYTHING,
				VALID_STATES.ANYTHING, VALID_STATES.SAME_TYPE, VALID_STATES.ANYTHING,
				VALID_STATES.ANYTHING, VALID_STATES.ANYTHING, VALID_STATES.ANYTHING);

		glow = Tran.litUp(texture);
		
	}

	public Tile(BufferedImage texture) {
		this(texture, "TILE"+tiles.size());
	}

	protected void setValidityKernel(VALID_STATES m00, VALID_STATES m10, VALID_STATES m20, VALID_STATES m01, VALID_STATES m11, VALID_STATES m21, VALID_STATES m02, VALID_STATES m12, VALID_STATES m22){
		validityKernel[0][0] = m00;
		validityKernel[1][0] = m10;
		validityKernel[2][0] = m20;
		validityKernel[0][1] = m01;
		validityKernel[1][1] = m11;
		validityKernel[2][1] = m21;
		validityKernel[0][2] = m02;
		validityKernel[1][2] = m12;
		validityKernel[2][2] = m22;
	}
	protected void setValidityKernel(int m00, int m10, int m20, int m01, int m11, int m21, int m02, int m12, int m22) {
		setValidityKernel(
				VALID_STATES_COLOR_CODE.get(m00), VALID_STATES_COLOR_CODE.get(m10), VALID_STATES_COLOR_CODE.get(m20),
				VALID_STATES_COLOR_CODE.get(m01), VALID_STATES_COLOR_CODE.get(m11), VALID_STATES_COLOR_CODE.get(m21),
				VALID_STATES_COLOR_CODE.get(m02), VALID_STATES_COLOR_CODE.get(m12), VALID_STATES_COLOR_CODE.get(m22));
	}

	public static void resetSnowiness() {
		snowy.clear();
	}

	public static Tile getTile(String id) {
//		if(tileMap.containsKey(id))
		return tileMap.get(id);
//		return n0;
	}

	public void tick(Handler handler, int x, int y) {

	}

	public void render(Handler handler, Graphics2D g, int x, int y) {
		g.drawImage(getImage(), x*18, y*18, null);
	}
	
	public void lightRender(Graphics2D g, int x, int y, float opacity) {
		Composite pre = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g.drawImage(glow, x-1, y-1, null);
		g.setComposite(pre);
	}

	public boolean isSolid() {
		return solid;
	}

	public static void setSnowy(int x, int y, double snowiness) {
		Tile.snowy.put(new Point(x, y), snowiness);
	}

	public static void updateSnowy(int x, int y, double snowiness) {
		snowy.put(new Point(x, y), Tile.snowiness(x, y)+snowiness);
	}

	public static double snowiness(int x, int y) {
		if(snowy.containsKey(new Point(x, y)))
			return snowy.get(new Point(x, y));
		return 0;
	}
	
	public boolean isTop() {
		return top_solid;
	}
	
	public boolean isOrganic() {
		return organic;
	}

	public String getId() {
		return id;
	}

	public abstract Color getColor();

	public Image getImage() {
		return texture;
	}

	// Checks whether any particular tile would be valid at a location in the given context
	public int isValid(ArrayList<ArrayList<String>> context, int x, int y) {
		int out = 9; // Anytime a 'Anything' call is made, the priority of the output decreases
		for(int i = x-1; i < x+2; i++)
			for (int j = y-1; j < y+2; j++) {
				Tile other;
				if(i < 0 || i >= context.size() || j < 0 || j >= context.get(i).size())
					other = barrier;
				else other = getTile(context.get(i).get(j));
				switch(validityKernel[i-x+1][j-y+1]) {
					case AIR -> {
						if (other!=n0)
							return 0;
					}
					case NOT_AIR -> {
						if(other==n0)
							return 0;
					}
					case SAME_TYPE -> {
						if(other.getClass() != getClass())
							return 0;
					}
					case SOLID -> {
						if(!other.isSolid())
							return 0;
					}
					case TOP_SOLID -> {
						if(!other.isTop())
							return 0;
					}
					case NON_SOLID -> {
						if(other.isSolid())
							return 0;
					}
					case SUPPORTS_SHRUBBERY -> {
						if(!other.isOrganic())
							return 0;
					}
					case DOESNT_SUPPORT_SHRUBBERY -> {
						if(other.isOrganic())
							return 0;
					}
					case SAME_TYPE_OR_SOLID -> {
						if(j>y&&other.isTop())
							continue;
						if(!other.isSolid()&&other.getClass() != getClass())
							return 0;
					}
					case ANYTHING -> out--;
				}
			}
		return out;
	}
	public static void formatTile(ArrayList<ArrayList<String>> tile_map, int x, int y) {
		if(x<0||x >= tile_map.size()||y<0||y>=tile_map.get(x).size())
			return;
		ArrayList<String> possibilities = new ArrayList<>();
		int highestPriority = 1;
		for(Tile t: tiles) {
			int validity = t.isValid(tile_map, x, y);
			if (validity >= highestPriority) {
				if(highestPriority<validity)
					possibilities.clear();
				possibilities.add(t.id);
				highestPriority = validity;
			}
		}
		if(possibilities.size()==0) {
			System.err.println("Couldn't find a tile that fits the given context, " + x + ", " + y);
			System.err.println("Returning air instead");
			tile_map.get(x).set(y, "TILE0");
		} else
			tile_map.get(x).set(y, possibilities.get((int)Public.rand(0, possibilities.size())));
	}
	public static void formatTiles(ArrayList<ArrayList<String>> tile_map, int begin_x, int begin_y, int end_x, int end_y) {
		for(int x = begin_x; x <= end_x; x++)
			for(int y = begin_y; y <= end_y; y++)
				formatTile(tile_map, x, y);
	}

}
