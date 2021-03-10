package com.zandgall.arvopia.quests;

import com.zandgall.arvopia.Handler;

public class Cutscene {
	public static Cutscene practice = new Cutscene("practice", 100.0D, 100.0D, 6.0D);
	static Handler game;
	public String name;
	private double x;
	private double y;
	public double zoom;

	public Cutscene(String name, double x, double y, double zoom) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}

	public static void init(Handler game) {
		Cutscene.game = game;
	}

	public static boolean run(Cutscene c) {
//		if (Public.difference(com.zandgall.arvopia.Game.scale, c.zoom) > 2.0D) {
		game.getGameCamera().setxOffset((float) c.x);
		game.getGameCamera().setyOffset((float) c.y);
		System.out.println("RUNNING: " + c);
//			if (Public.dist(game.xOffset(), game.yOffset(), c.x, c.y) <= 2.0D) {
//				if (game.getMouse().isRight())
//					return false;
//			} else {
//				if (game.xOffset() < c.x)
//					game.getGameCamera().setxOffset((float) ((c.x + 1.0D - game.xOffset()) / 10.0D));
//				if (game.xOffset() > c.x) {
//					game.getGameCamera().setxOffset((float) ((c.x - 1.0D - game.xOffset()) / 10.0D));
//				}
//				if (game.yOffset() < c.y)
//					game.getGameCamera().setxOffset((float) ((c.y + 1.0D - game.yOffset()) / 10.0D));
//				if (game.yOffset() > c.y) {
//					game.getGameCamera().setxOffset((float) ((c.y - 1.0D - game.yOffset()) / 10.0D));
//				}
//			}
//		}
		return true;
	}
}
