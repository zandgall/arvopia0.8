package com.zandgall.arvopia.guis;

import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.utils.BevelIndent;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Public;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class PlayerGui extends Gui {
	private static final long serialVersionUID = 1L;

	Player p;

	BevelIndent smallRed, smallGreen, smallBlue;
	BevelIndent bigRed, bigGreen, bigBlue;

	boolean statsMenu = false;
	
	BevelIndent unselected, selected;

	BevelPlatform fulltoolbar;
	
	public PlayerGui(com.zandgall.arvopia.Handler game) {
		super(game);
//		p = game.getWorld().getEntityManager().getPlayer();

		unselected = new BevelIndent(2, 2, 20, 20, 255, 0);
		selected = new BevelIndent(2, 2, 24, 24, 300, 0);
		
		fulltoolbar = new BevelPlatform(2, 2, 80, 215);
		
		smallRed = new BevelIndent(2, 2, 100, 10, 400, 0);
		smallRed.affectImage(Color.red);
		smallGreen = new BevelIndent(2, 2, 100, 10, 400, 0);
		smallGreen.affectImage(Color.green);
		smallBlue = new BevelIndent(2, 2, 100, 10, 400, 0);
		smallBlue.affectImage(Color.blue);

		bigRed = new BevelIndent(2, 2, 200, 20, 400, 0);
		bigRed.affectImage(Color.red);
		bigGreen = new BevelIndent(2, 2, 200, 20, 400, 0);
		bigGreen.affectImage(Color.green);
		bigBlue = new BevelIndent(2, 2, 200, 20, 400, 0);
		bigBlue.affectImage(Color.blue);
	}

	public void tick() {
		if (p != game.getWorld().getEntityManager().getPlayer()) {
			p = game.getWorld().getEntityManager().getPlayer();
		}

		if (KeyManager.checkBind("Stats") && KeyManager.existantTyped)
			statsMenu = !statsMenu;
	}

	public void render(Graphics g) {
		
		String stats = (KeyEvent.getKeyText(KeyManager.keybinds.getValues("Stats").get(0))).toUpperCase();
		String inventory = (KeyEvent.getKeyText(KeyManager.keybinds.getValues("Crafting").get(0))).toUpperCase();
		String interact  = (KeyEvent.getKeyText(KeyManager.keybinds.getValues("Interact").get(0))).toUpperCase();
		
		g.setFont(Public.runescape.deriveFont(12.0f));
		Tran.drawOutlinedText(g, 5, 376, stats+" - Stats", 1, Color.black, Color.white);
		Tran.drawOutlinedText(g, 5, 376, stats, 2, Color.black, Color.white);
		Tran.drawOutlinedText(g, 5, 388, inventory+" - Inventory", 1, Color.black, Color.white);
		Tran.drawOutlinedText(g, 5, 388, inventory, 2, Color.black, Color.white);
		if(game.getPlayer().closestNPC!=null&&game.getPlayer().closestNPC.getX()>game.xOffset()&&game.getPlayer().closestNPC.getX()<game.xOffset()+720)
			Tran.drawOutlinedText(g, 5, 400, interact+" - Interact", 1, Color.black, Color.white);
		else Tran.drawOutlinedText(g, 5, 400, interact+" - Weather", 1, Color.black, Color.white);
		Tran.drawOutlinedText(g, 5, 400, interact, 2, Color.black, Color.white);
		
		fulltoolbar.render((Graphics2D) g, 670, 10);
		
		for(int i = 0; i<p.selectedTool; i++) {
			unselected.render((Graphics2D) g, 695, i*20 + 15);
			if(p.items.containsKey(p.toolbar[i])) {
				g.drawImage(p.items.get(p.toolbar[i]).item(), 696, i*20+16, null);
				Tran.drawOutlinedText(g, 695, i*20+35, p.items.get(p.toolbar[i]).amount+"", 1, Color.black, Color.white);
			}
		}
		for(int i = p.selectedTool+1; i<10; i++) {
			unselected.render((Graphics2D) g, 695, i*20 + 19);
			if(p.items.containsKey(p.toolbar[i])) {
				g.drawImage(p.items.get(p.toolbar[i]).item(), 696, i*20+20, null);
				Tran.drawOutlinedText(g, 695, i*20+37, p.items.get(p.toolbar[i]).amount+"", 1, Color.black, Color.white);
			}
		}
		selected.render((Graphics2D) g, 693, p.selectedTool*20 + 15);
		
		if(p.items.containsKey(p.toolbar[p.selectedTool])) {
			g.drawImage(p.items.get(p.toolbar[p.selectedTool]).item(), 696, p.selectedTool*20+18, null);
			Tran.drawOutlinedText(g, 695, p.selectedTool*20+36, p.items.get(p.toolbar[p.selectedTool]).amount+"", 1, Color.black, Color.white);
		}
		
		for(int i = 1; i <11; i++)
			Tran.drawOutlinedText(g, 680, i*20+11, i%10+":", 1, Color.black, Color.white);
		
		if (statsMenu) {
			g.setFont(new java.awt.Font("Dialog", 1, 18));
			
			g.drawImage(p.getFrame(), 1, 1, null);
			
			g.drawImage(bigRed.getImage(), 150, 1, null);
			g.drawImage(
					bigGreen.getImage().getSubimage(0, 0, (int) Math.max(200.0D * (p.health / p.MAX_HEALTH), 1), 20),
					150, 1, null);

			g.setColor(java.awt.Color.black);
			Tran.drawOutlinedText(g, 152, 17, "Health: " + Public.grid(p.health, 0.1, 0) / 10.0, 1, Color.black,
					Color.white);

			g.drawImage(bigRed.getImage(), 150, 25, null);

			g.drawImage(bigBlue.getImage().getSubimage(0, 0, (int) (200.0D * (p.breath / 20.0D)), 20), 150, 25, null);

			Tran.drawOutlinedText(g, 152, 42, "Breath: " + Public.grid(p.breath, 0.1, 0) / 10.0, 1, Color.black,
					Color.white);
			
			Tran.drawOutlinedText(g, 152, 60, "Age: " + (game.getEnviornment().rohundo + game.getEnviornment().lapse*81) + " days", 1, Color.black, Color.white);
		} else {
			g.setFont(new java.awt.Font("Dialog", 1, 12));

			g.drawImage(smallRed.getImage(), 1, 20, null);
			g.drawImage(
					smallGreen.getImage().getSubimage(0, 0, (int) Math.max(100.0D * (p.health / p.MAX_HEALTH), 1), 10),
					1, 20, null);

			g.setColor(java.awt.Color.black);
			g.setFont(new java.awt.Font("Arial", 0, 10));
			g.drawString("Health: " + (int) p.health, 3, 30);

			if (p.breath < 20.0D) {
				g.drawImage(smallRed.getImage(), 1, 55, null);

				g.drawImage(smallBlue.getImage().getSubimage(0, 0, (int) (100.0D * (p.breath / 20.0D)), 10), 1, 55,
						null);

				g.setColor(java.awt.Color.black);
				g.setFont(new java.awt.Font("Arial", 0, 10));
				g.drawString("Breath: " + (int) p.breath, 3, 64);
			}
			
		}
		
		for(int i = 0; i<p.lives; i++)
			g.drawImage(ImageLoader.loadImage("/textures/Player/Heart.png"), i*24, 0, 22, 20, null);

	}

	public void init() {
	}
}
