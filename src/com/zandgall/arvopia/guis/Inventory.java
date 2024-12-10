package com.zandgall.arvopia.guis;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.PlayerItem;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.utils.BevelIndent;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Inventory extends Gui {
	private static final long serialVersionUID = 1L;

	public static int craftAmm = 0;
	
	private ArrayList<CraftOutput> outputs;
	private Map<String, Integer> preAmounts;
	private Map<String, InventoryItem> items;
	String selectedItem = "null";
	int selected = 0;
	Button craft;

	BevelPlatform body, craftOutput; 
	
	BevelIndent slot; 
	
	int nexx = 30, nexy = 20;
	
	double craftsOffset = 0, craftsMax = 0;
	int itemsOffset = 0, itemsMax = 0;
	
	public boolean full = false;
	
	public Inventory(Handler game) {
		super(game);

		outputs = ForgeHandler.craftings;
		
		preAmounts = new HashMap<>();
		items = new HashMap<>();

		craft = new Button(game, 460, 230, "Crafts the selected item IF you are able to", "Craft");
		
		body = new BevelPlatform(2, 2, 720, 400);
		craftOutput = new BevelPlatform(2, 2, 210, 210, 100, 10);
		
		slot = new BevelIndent(2,2,40,40);
		
	}

	public void tickItems() {
		for(String s: game.getPlayer().items.keySet()) {
			if(game.getPlayer().items.get(s).amount==0) {
				if(preAmounts.containsKey(s)) {
					preAmounts.remove(s);
					items.remove(s);
					calculateNextItemPos();
				}
				continue;
			}
			if(preAmounts.containsKey(s)) {
				if(preAmounts.get(s)!=game.getPlayer().items.get(s).amount) {
					preAmounts.put(s, game.getPlayer().items.get(s).amount);
					items.get(s).amount=game.getPlayer().items.get(s).amount;
				}
			} else {
				preAmounts.put(s, game.getPlayer().items.get(s).amount);
				for(int i = 0; i<game.getPlayer().toolbar.length; i++) {
					if(game.getPlayer().toolbar[i]=="null") {
						game.getPlayer().toolbar[i]=s;
						break;
					}
				}
				items.put(s, new InventoryItem(game, game.getPlayer().items.get(s).item(), nexx, nexy, game.getPlayer().items.get(s).amount, s));
				calculateNextItemPos();
			}
		}
	}
	
	public void tick() {
		if(ForgeHandler.presize!=game.getPlayer().items.size()) {
			ForgeHandler.init(game, game.getPlayer());
			outputs = ForgeHandler.craftings;
		}
		
		int y = 30;
		for (int i = 0; i < outputs.size(); i++) {
			if (outputs.get(i) == null)
				return;
			if ((outputs.get(i).semiCraftable(game.getPlayer().items))) {
				outputs.get(i).x = (int) (y-craftsOffset);
				outputs.get(i).y = game.getHeight()-50;
				((CraftOutput) outputs.get(i)).tick(36, 36);
				y += 40;

				if (outputs.get(i).clicked) {
					selected = i;
				}
			}
		}
		if(game.getMouse().rMouseY()>360) {
			craftsOffset+=game.getMouse().getMouseScroll()*10;
		} else {
//			itemsOffset+=game.getMouse().getMouseScroll()*10;
		}
		craftsOffset = Math.max(0, Math.min(y-720, craftsOffset));
		
		if(game.getMouse().fullLeft&&selectedItem=="null") {
			for(InventoryItem i: items.values()) {
				if(game.getMouse().touches(i.getX(), i.getY()+itemsOffset, i.getX()+36, i.getY()+36))
					selectedItem=i.getName();
			}
		}
		if(selectedItem!="null") {
			items.get(selectedItem).setPos(game.getMouse().rMouseX()-18, game.getMouse().rMouseY()-18+itemsOffset);
		}
		if(!game.getMouse().fullLeft&&selectedItem!="null") {
			attemptPlacement(selectedItem);
		}
		
		game.getPlayer().toolbar = new String[] {"null","null","null","null","null","null","null","null","null","null"};
		for(InventoryItem i: items.values()) {
			if(i.getY()==20) {
				int t = (i.getX()-30)/40;
				t = (int) Public.range(0, 9, t);
				game.getPlayer().toolbar[t]=i.getName();
			}
		}
		
		craft.on = false;
		craft.tick();
		
		if ((craft.on) && (((CraftOutput) outputs.get(selected)).craftable(game.getPlayer().items))) {
			craftAmm++;
			if(craftAmm==1)
				Achievement.award(Achievement.firstcraft);
			if(craftAmm==10)
				Achievement.award(Achievement.smithy);
			ForgeHandler.crafted(game, (CraftOutput) outputs.get(selected));
		}
	}
	
	void attemptPlacement(String selected) {
		int x = getPlacement(selected).x;
		int y = getPlacement(selected).y;
		System.out.println("Placement: " + x+", " + y);
		if(y==60)
			return;
		for(InventoryItem i: items.values()) {
			if(items.get(selected)!=i) {
				if(i.getX()==x&&i.getY()==y)
					return;
			}
		}
		items.get(selected).setPos(x,y);
		this.selectedItem="null";
	}
	
	Point getPlacement(String selected) {
		return new Point(Public.grid(items.get(selected).getX()-30+18, 40, 0)*40+30,Public.grid(items.get(selected).getY()-20+18, 40, 0)*40+20);
	}
	
	public void calculateNextItemPos() {
		int x = 30;
		int y = 20;
		for(InventoryItem i: items.values()) {
			if(x==i.getX() && y==i.getY()) {
				x+=40;
				if(x==430)
					y+=40;
				if(y==60)
					y+=40;
				calculateNextItemPos(x, y);
				return;
			}
		}
		nexx=x;
		nexy=y;
	}
	
	public void calculateNextItemPos(int nx, int ny) {
		int x = nx;
		int y = ny;
		for(InventoryItem i: items.values()) {
			if(x==i.getX() && y==i.getY()) {
				x+=40;
				if(x==430)
					y+=40;
				if(y==60)
					y+=40;
				calculateNextItemPos(x, y);
				return;
			}
		}
		nexx=x;
		nexy=y;
	}

	public void render(Graphics g) {
//		g.setColor(new Color(0, 0, 200, 100));
//		g.fillRect(0, 0, game.getWidth(), game.getHeight());

		body.render((Graphics2D) g, 0, 0);
		
		for(int i = 0; i<10; i++) {
			slot.render((Graphics2D) g, i*40+30, 20);
		}
		
		for(int j = 0; j<6; j++)
			for(int i = 0; i<10; i++) {
				slot.render((Graphics2D) g, i*40+30, j*40+100);
			}
		
//		int y = 50;
//		for (String i : game.getPlayer().items.keySet()) {
//			if (game.getPlayer().items.get(i).amount > 0) {
//				g.drawImage(game.getPlayer().items.get(i).item, 1, y, null);
//				y += 20;
//			}
//		}
		int f = 0;
		
		for(InventoryItem i: items.values()) {
			i.render(g, i.getX()+2, i.getY()+2, 36, 36);
			if(i.amount>0) {
				f++;
			}
		}
		
		if(f>=70)
			full=true;
		
		for (CraftOutput c : outputs) {
			if (c != null && c.semiCraftable(game.getPlayer().items)) {
				c.render(g, 36, 36);
			}
		}
		
		craftOutput.render((Graphics2D) g, 455, 15);
		
		if (outputs.size()<=selected||outputs.get(selected) == null || (!outputs.get(selected).craftable(game.getPlayer().items)))
			craft.locked = true;
		else craft.locked = false;
		craft.render(g);
		if (outputs.size()<=selected || outputs.get(selected) == null)
			return;
		int x = 22;
		g.drawImage(outputs.get(selected).image, 461, 21, 198, 198, null);

		g.setFont(new Font("Arial", 1, 20));
		g.setColor(Color.black);
		g.drawString(outputs.get(selected).name, 460, 290);
		g.setFont(com.zandgall.arvopia.utils.Public.defaultFont);
		if(outputs.get(selected).description!=null)
			Tran.drawString(g, Public.limit(outputs.get(selected).description, 250, g.getFont()), 460, 310);
		
		for (String s : outputs.get(selected).recipe.recipe.keySet()) {
			if(game.getPlayer().items.containsKey(s)) {
				InventoryItem i = new InventoryItem(game, game.getPlayer().items.get(s).item(), 0, 0, outputs.get(selected).recipe.recipe.get(s), s);
				if(game.getPlayer().items.get(s).amount<outputs.get(selected).recipe.recipe.get(s)) {
					g.setColor(new Color(255, 0, 0, 200));
				}else {
					g.setColor(new Color(0, 0, 0, 200));
				}
				i.render(g, 670, x, 36, 36);
				x += 40;
			}
		}
	}
	
	public void init() {
	}

	public static class ForgeHandler {
		public static Handler game;
		
		public static ArrayList<CraftOutput> craftings;
		public static int presize = 0;

		public ForgeHandler() {
		}

		public static void init(Handler game, Player p) {
			
			ForgeHandler.game=game;
			
			craftings = new ArrayList<>();
			
			presize = p.items.size();

			for(String s : p.items.keySet()) {
				PlayerItem i = p.items.get(s);
				if(i.recipe==null)
					continue;
				int[] am = new int[i.recipe.values().size()];
				for(int j = 0; j<am.length; j++) {
					am[j] = (int) i.recipe.values().toArray()[j];
				}
				String[] na = new String[i.recipe.keySet().size()];
				for(int j = 0; j<na.length; j++) {
					na[j] = (String) i.recipe.keySet().toArray()[j];
				}
				System.out.println(s + ": " + i.description + "\n"+i.recipe.size()+"\n"+na.toString()+"\n"+am.toString()+"\n~~~~~~~~~~~~~~");
				addCraft(i.item(), na, am, s, i.description);
			}
			
		}

		public static void addCraft(BufferedImage thumbnail, String[] names, int[] amounts, String name, String description) {
			craftings.add(new CraftOutput(game, thumbnail, 120, 30, names, amounts, name, description));
			
		}
		
		public static void crafted(Handler game, CraftOutput c) {
			Player p = game.getWorld().getEntityManager().getPlayer();
			
			p.putItem(c.name, 1);
			
			c.doSubtract();
		}
	}
}
