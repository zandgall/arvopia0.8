package com.zandgall.arvopia.entity.creatures.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.entity.creatures.Creature;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.Public;

public class Bird extends BasicTemplate {

	private static final long serialVersionUID = 1202001019398455L;
	
	double noiseIndex = 0;
	
	int state = 0; //0 = walking, 1 = pecking, 2 = flying
	
	boolean bouncing = false, preBouncing = false;
	int flightPercent = 20;
	
	int peckingState = 0, //0 = Still, 1 = Down, 2 = Up
			walkingState = 0; //0 = Still, 1 = Left, 2 = Right
	
	String type = "Blue";
	
	static final int s = 24;

	BufferedImage bounce, fall, peck0, peck1, peck2, move0, move1;

	static {
		ImageLoader.addRedirect("BirdBlue", createSheet(new Color(100, 200, 255)));
		ImageLoader.addRedirect("BirdRed", createSheet(new Color(255, 100, 150)));
		ImageLoader.addRedirect("BirdGreen", createSheet(new Color(200, 255, 200)));
		ImageLoader.addRedirect("Bird", createSheet(new Color(200, 200, 255)).getSubimage(0, 0, s, s));
	}
	
	public Bird() {
		super(ArvopiaLauncher.game.handler, 0, 0, s, s-1, 1, Creature.DEFAULT_ACCELERATION, 3, "Bird", BasicTemplate.PASSIVE, 3, 5, 5, 1, 1);
		
		if(Public.chance(33))
			type="Red";
		else if(Public.chance(50))
			type="Green";
		
		layer = Public.debugRandom(-3, -2);
		bounce = ImageLoader.loadImage("Bird"+type).getSubimage(108, 0, 36, 36);
		fall = ImageLoader.loadImage("Bird"+type).getSubimage(72, 0, 36, 36);
		move0 = ImageLoader.loadImage("Bird"+type).getSubimage(0, 0, 36, 36);
		move1 = ImageLoader.loadImage("Bird"+type).getSubimage(36, 0, 36, 36);
		peck0 = ImageLoader.loadImage("Bird"+type).getSubimage(72, 36, 36, 36);
		peck1 = ImageLoader.loadImage("Bird"+type).getSubimage(0, 36, 36, 36);
		peck2 = ImageLoader.loadImage("Bird"+type).getSubimage(36, 36, 36, 36);
	}

	public void aiMove() {

		// Set velocity based on directions
		setxMove(0.0F);
		yMove = 0.0F;

//		checkCol();
		
		preBouncing = bouncing;
		
		if (xVol > 0.0D) {
			setxMove((float) (getxMove() + (speed + xVol)));
		}
		if (xVol < 0.0D) {
			setxMove((float) (getxMove() + (-speed + xVol)));
		}

		if (yVol > 0.0D) {
			yMove = ((float) (yMove + (speed + yVol)));
		}
		if (yVol < 0.0D) {
			yMove = ((float) (yMove + (-speed + yVol)));
		}
		
		if(Math.abs(game.getPlayer().getxMove())>3 && Public.dist(centerX(), centerY(), game.getPlayer().centerX(), game.getPlayer().centerY())<100) {
			state = 2;
			bouncing = Public.chance(50);
		}
		
		if(state == 2) {
			//Flying
			
			if(bouncing) {
				yVol-=0.5;
				game.setPosition("BirdFlap"+layer, x, y, 0);
				game.play("BirdFlap"+layer);
			}
			
			yVol = Math.max(yVol, -1.5);
			
			if(Public.chance(0.5))
				flightPercent = (int) Public.random(10, 20);
			
			bouncing = (Public.chance(flightPercent));
			
			if(Public.chance(10))
				walkingState = (int) Public.random(0, 2);
			
			if(walkingState == 1)
				xVol+=0.1;
			if(walkingState == 2)
				xVol-=0.1;
			
			xVol*=0.8;
			xVol = Public.range(-1.5, 1.5, xVol);
			
			if(bottoms || Public.chance(0.1)) {
				state=0;
				game.soundSystem.stop("BirdFlap"+layer);
			}
		} else if(state == 0) {
			//Walking||Still
			bouncing=false;
			
			if(Public.chance(10))
				walkingState = (int) Public.random(0, 2);
			if(walkingState == 1)
				xVol=1;
			if(walkingState == 2)
				xVol=-1;
			if(walkingState == 0)
				xVol = 0;
			
			if(Public.chance(1))
				state=1;
			
			if(!bottoms || Public.chance(0.1)) {
				state = 2;
				yVol = -1;
			}
		} else if(state == 1) {
			//Pecking
			bouncing=false;
			
			xVol = 0;
			
			if(Public.chance(3))
				if(Public.chance(10))
					peckingState = 0;
				else peckingState = 1;
			
			if(Public.chance(1))
				state=0;
		}
		
		xMove = (float) (xVol*speed);
		yMove = (float) (yVol*speed);
		
		widthFlip = (xMove == 0 ? widthFlip : xMove > 0 ? 1 : -1);
		
	}
	
	public void render(Graphics2D g) {
		g.drawImage(com.zandgall.arvopia.gfx.transform.Tran.flip(getFrame(), widthFlip, 1),
				(int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), s, s, null);

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}
	}

	public BufferedImage getFrame() { 
		if(bouncing || preBouncing)
			return bounce;
		if(!bottoms)
			return fall;
		
		if(Math.abs(xMove)>0 && bottoms)
			return System.currentTimeMillis()/200 % 2 == 0 ? move0 : move1;
		
		if(state==1) {
			if(peckingState==0)
				return peck0;
			if(peckingState==1)
				return System.currentTimeMillis()/200 % 2 == 0 ? peck1 : peck2;
		}
		
		return move0;
	}

	public static BufferedImage createSheet(Color color) {
		BufferedImage returned = new BufferedImage(144, 722, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g = returned.createGraphics();
		g.drawImage(Tran.effectColor(ImageLoader.loadImage("/textures/Creatures/Berd.png").getSubimage(0, 0, 144, 72), color), 0, 0, null);
		g.drawImage(ImageLoader.loadImage("/textures/Creatures/Berd.png").getSubimage(0, 72, 144, 72), 0, 0, null);
		
		g.dispose();
		
		return returned;
	}
	
	boolean sounds = false;
	
	@Override
	public void custTick() {
		if(!sounds) {
			sounds = true;
			
			game.addSound("Sounds/Bird/BirdChirp.ogg", "BirdChirp", false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdFlap.ogg", "BirdFlap", false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdSong1.ogg", "BirdSong1", false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdSong2.ogg", "BirdSong2", false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdSong3.ogg", "BirdSong3", false, 0, 0, 0);
			
			game.addSound("Sounds/Bird/BirdChirp.ogg", "BirdChirp"+layer, false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdFlap.ogg", "BirdFlap"+layer, false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdSong1.ogg", "BirdSong1"+layer, false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdSong2.ogg", "BirdSong2"+layer, false, 0, 0, 0);
			game.addSound("Sounds/Bird/BirdSong3.ogg", "BirdSong3"+layer, false, 0, 0, 0);
			
		}
		
		if(Public.chance(0.1))
			chirp();
		
		if(Public.chance(0.01))
			sing();
		
	}
	
	private void chirp() {
		String addition = "" + (((OptionState) game.getGame().optionState).getToggle("Sound per layer") ? layer : "");
		game.setPosition("BirdChirp"+addition, x, y, 0); 
		game.play("BirdChirp"+addition);
	}
	
	private void sing() {
		int n = (int) Public.random(1, 3);
		String addition = "" + (((OptionState) game.getGame().optionState).getToggle("Sound per layer") ? layer : "");
		game.setPosition("BirdSong"+n+addition, x, y, 0);
		game.play("BirdSong"+n+addition);
		
	}
	
	public boolean alwaysTick() {
		return false;
	}
	
}
