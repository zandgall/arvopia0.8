package com.zandgall.arvopia.items.tools;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.environment.Light;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.PublicAssets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Torch extends Tool {
	private Animation torchAn;
	private Animation stab;
	private Light torch;
	private boolean one = false;

	public Light getLight() {
		return torch;
	}

	public boolean hasLight() {
		return true;
	}

	public Torch(Handler game) {
		super(game, "Torch", false, 2.0D, 15, 15);

		one = false;

		torchAn = new Animation(150, new BufferedImage[] { PublicAssets.torch.getSubimage(0, 0, 18, 45),
				PublicAssets.torch.getSubimage(18, 0, 18, 45) }, "", "Torch");
		stab = new Animation(150, new BufferedImage[] {}, "Stab", "Torch");
		torch = new Light(game, 100, 100, 15, 1, Color.orange);
	}

	public void tick() {
		if (!one) {
			game.getWorld().getEnvironment().getLightManager().addLight(torch);
			one = true;
		}

		torch.turnOn();

		torchAn.tick();
		stab.tick();
	}

	public void custom1(int x, int y) {
		torch.setX(x - 9);
		torch.setY(y + 7);
	}

	public BufferedImage texture() {
		return torchAn.getFrame();
	}

	public BufferedImage getFrame() {
		return stab.getFrame();
	}

	public void render(Graphics g, int x, int y) {
	}

	public int getYOffset() {
		return 22;
	}

	public int getXOffset() {
		return torchAn.getFrame().getWidth() / 2;
	}

	public boolean smashOrStab() {
		return false;
	}

	public void setFrame(int frameInt) {
	}

	public Tool.tools Type() {
		return Tool.tools.NONE;
	}

	public void custom2(int i) {
	}
}
