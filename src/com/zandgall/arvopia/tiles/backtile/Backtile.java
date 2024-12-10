package com.zandgall.arvopia.tiles.backtile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.Public;

public abstract class Backtile {
	public static ArrayList<Backtile> tiles = new ArrayList<>();
	public static HashMap<String, Backtile> tileMap = new HashMap<>();
	public static Backtile n0 = new EmptyTile();
	
//	public static Backtile g0 = new BackGrassTile(1, 0, 0);
//	public static Backtile g1 = new BackGrassTile(2, 1, 0);
//	public static Backtile g2 = new BackGrassTile(3, 2, 0);
//	public static Backtile g3 = new BackGrassTile(4, 0, 1);
//	public static Backtile g4 = new BackGrassTile(5, 1, 1);
//	public static Backtile g5 = new BackGrassTile(6, 2, 1);
//	public static Backtile g6 = new BackGrassTile(7, 0, 2);
//	public static Backtile g7 = new BackGrassTile(8, 1, 2);
//	public static Backtile g8 = new BackGrassTile(9, 2, 2);
//	public static Backtile g9 = new BackGrassTile(10, 0, 3);
//	public static Backtile g10 = new BackGrassTile(11, 1, 3);
//	public static Backtile g11 = new BackGrassTile(12, 2, 3);
//	public static Backtile g12 = new BackGrassTile(13, 3, 3);
	
	public static int TILEWIDTH = 18;
	public static int TILEHEIGHT = 18;
	public static final int DEFAULT_WIDTH = 18;
	public static final int DEFAULT_HEIGHT = 18;
	protected int x;
	protected int y;
	protected static int[][] snowy;
	protected String id;
	private BufferedImage texture;
	public enum VALID_STATES {
		AIR, NOT_AIR, ANYTHING, SAME_TYPE, DIFFERENT_TYPE
	}
	protected VALID_STATES[][] validityKernel;
	public static Map<Integer, VALID_STATES> VALID_STATES_COLOR_CODE = Map.of(Color.white.getRGB(), VALID_STATES.AIR, Color.black.getRGB(), VALID_STATES.NOT_AIR,
	Color.red.getRGB(), VALID_STATES.ANYTHING, Color.green.getRGB(), VALID_STATES.SAME_TYPE, Color.blue.getRGB(), VALID_STATES.DIFFERENT_TYPE);
	public Backtile(BufferedImage texture, String id) {
		this.id = id;

		this.texture = texture;

		tiles.add(this);
		tileMap.put(id, this);
		validityKernel = new VALID_STATES[3][3];
		setValidityKernel(
				VALID_STATES.ANYTHING, VALID_STATES.ANYTHING, VALID_STATES.ANYTHING,
				VALID_STATES.ANYTHING, VALID_STATES.SAME_TYPE, VALID_STATES.ANYTHING,
				VALID_STATES.ANYTHING, VALID_STATES.ANYTHING, VALID_STATES.ANYTHING);
	}

	public Backtile(BufferedImage texture) {
		this(texture, "BACKTILE"+tiles.size());
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

	public static Backtile getTile(String id) {
		if(tileMap.containsKey(id))
			return tileMap.get(id);

		return n0;
	}

	public static void set(int width, int height) {
		snowy = new int[width][height];
	}

	public abstract void tick(Handler paramHandler, int paramInt1, int paramInt2);

	public abstract void init();

	public abstract void reset();

	public void render(Handler handler, Graphics2D g, int x, int y) {
		g.drawImage(texture, x*18, y*18, null);
	}
	
	public Image getImage() {
		return texture;
	}
	
	public void overRender(Graphics g, int x, int y) {
		g.drawImage(texture, x, y, TILEWIDTH, TILEHEIGHT, null);
	}

	public boolean isSolid() {
		return false;
	}

	public boolean varietable() {
		return false;
	}

	public boolean tickable() {
		return false;
	}

	public int snowyness(int x, int y) {
		return snowy[x][y];
	}

	public boolean isTop() {
		return false;
	}

	public String getId() {
		return id;
	}

	public abstract Color getColor();

	public int isValid(ArrayList<ArrayList<String>> context, int x, int y) {
		int out = 9; // Anytime a 'Anything' call is made, the priority of the output decreases
		for(int i = x-1; i < x+2; i++)
			for (int j = y-1; j < y+2; j++) {
				if(i < 0 || i >= context.size() || j < 0 || j >= context.get(i).size())
					continue;
				Backtile other = getTile(context.get(i).get(j));
				switch(validityKernel[i-x+1][j-y+1]) {
					case AIR -> {
						if (!Objects.equals(context.get(i).get(j), "TILE0"))
							return 0;
					}
					case NOT_AIR -> {
						if (Objects.equals(context.get(i).get(j), "TILE0"))
							return 0;
					}
					case DIFFERENT_TYPE -> {
						if(other.getClass() == getClass())
							return 0;
					}
					case SAME_TYPE -> {
						if(other.getClass() != getClass())
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
		for(Backtile t: tiles) {
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
			tile_map.get(x).set(y, possibilities.get((int) Public.rand(0, possibilities.size())));
	}
	public static void formatTiles(ArrayList<ArrayList<String>> tile_map, int begin_x, int begin_y, int end_x, int end_y) {
		for(int x = begin_x; x <= end_x; x++)
			for(int y = begin_y; y <= end_y; y++)
				formatTile(tile_map, x, y);
	}
	
}
