package com.zandgall.arvopia.entity.statics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.BevelIndent;
import com.zandgall.arvopia.utils.Public;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class StaticEntity extends com.zandgall.arvopia.entity.Entity {
	private static final long serialVersionUID = 1L;
	
	public double health;
	public double maxHealth;
	public int weakness;
	
	public BevelIndent healthBody, healthGreen;

	public StaticEntity(Handler handler, double x, double y, int width, int height, boolean solid, int health,
			int weakness) {
		super(handler, x, y, width, height, solid, false, false, true);

		this.health = (health * 10);
		maxHealth = (health * 10);

		this.weakness = weakness;
	}

	public void reset() {
	}

	public void showBox(Graphics g) {
		if (maxHealth > 0.0D) {
			
			if(healthBody==null) {
				healthBody = new BevelIndent(2, 2, bounds.width, 10, 400, 50);
				healthBody.affectImage(Color.red);
				healthGreen = new BevelIndent(2, 2, bounds.width, 10, 400, 50);
				healthGreen.affectImage(Color.green);
			}
			
//			g.setColor(Color.red);
//			g.fillRect((int) Public.xO(x + bounds.x), (int) Public.yO(y + bounds.y - 20.0D), bounds.width, 10);
//
//			g.setColor(Color.green);
//			g.fillRect((int) Public.xO(x + bounds.x), (int) Public.yO(y + bounds.y - 20.0D),
//					(int) (bounds.width * (health / maxHealth)), 10);
//
//			g.setColor(Color.black);
//			g.drawRect((int) Public.xO(x + bounds.x), (int) Public.yO(y + bounds.y - 20.0D), bounds.width, 10);
			
			healthBody.render((Graphics2D) g, (int) (Public.xO(x+bounds.x)), (int) Public.yO(y+bounds.y-20.0D));
			g.drawImage(healthGreen.getImage().getSubimage(0, 0, (int) Public.range(1, healthGreen.getImage().getWidth(), bounds.width*(health/maxHealth)), 10), (int) (Public.xO(x+bounds.x)), (int) Public.yO(y+bounds.y-20.0D), null);
			
		}

		if (isSolid) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.green);
		}

		g.drawRect((int) (x + bounds.x - game.getGameCamera().getxOffset()),
				(int) (y + bounds.y - game.getGameCamera().getyOffset()), bounds.width, bounds.height);
	}
}
