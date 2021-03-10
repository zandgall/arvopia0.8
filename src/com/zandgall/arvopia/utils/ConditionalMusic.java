package com.zandgall.arvopia.utils;

import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.state.OptionState;

public abstract class ConditionalMusic {
	
	String[] files;
	
	boolean playing = false;
	
	Handler game;
	public ConditionalMusic(Handler game, String[] files) {
		this.files = files;
		this.game=game;
	}
	
	public void tick(ArrayList<ConditionalMusic> music) {
		playing = false;
		for(ConditionalMusic c: music) {
			if(c!=this && c.playable(game)) {
				playing = true;
			}
		}
		
		int filenum = (int) Public.random(0, files.length-1);
		
		boolean thisplay = false;
		for(int i = 0; i<files.length; i++) {
			if(game.currentMusic==files[i])
				thisplay=true;
		}
		
		if(playable(game) && !playing && !thisplay) {
			if(game.currentMusic!=null)
				game.fadeOutIn(files[filenum], 2000);
			else game.music(files[filenum], false);
			
			game.soundSystem.setVolume("music", OptionState.msVolume);
		}
	}
	
	public abstract boolean playable(Handler g);
	
}
