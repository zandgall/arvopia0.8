package com.zandgall.arvopia.guis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.zandgall.arvopia.Console;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.trading.TradingKeys;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.utils.BevelIndent;
import com.zandgall.arvopia.utils.BevelPlatform;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Public;

public class Trading extends Gui implements Serializable {
	
	//Voices
	public static String DEFAULT_VOICE = "DefaultVoice",
			DEFAULT_MALE = "DefaultVoiceMale",
			DEFAULT_FEMALE = "DefaultVoiceFemale";

	private static final long serialVersionUID = -6161346849428080782L;

	public String text, name;

	public boolean shown = false;

	public boolean trading = false, ownSpeeches = false, resetOnClose = false;

	public TradingKeys speeches;

	public ArrayList<Button> responses;

	public Map<String, String> questMapper;

	public int responseAmmount = 0;

	public int sectionindex = 0, speechindex = 0;
	public double minorindex = 0, responseScroll=0;
	
	boolean loadedSounds = false;

	//Specifications
	public double speed = 0.5, soundSpace = 2;
	public String voice = "DefaultVoice";
	
	BufferedImage imgicon;
	
	public Trading(Handler game, String text, String name, BufferedImage imgicon) {
		super(game);

		this.imgicon=imgicon;

		this.name = name;

		if (body == null) {
			body = new BevelPlatform(4, 2, this.game.getWidth() - 100, this.game.getHeight() - 100);
			icon = new BevelPlatform(4, 2, 150, 150, 200, 100);
			indent = new BevelIndent(4, 2, this.game.getWidth() - 275, 120);
			responseindent= new BevelIndent(4, 2, this.game.getWidth() - 350, 130);
			buttonindent = new BevelIndent(4, 2, 230, 130, 255, 0);
		}

//		responseIndent.affectImage(Color.green);

		String out = "";
		for (int i = 0; i < text.toCharArray().length; i++) {
			out += text.toCharArray()[i];
			String[] o = out.split("\n");
			if (text.toCharArray()[i] == ' ' && Tran.measureString(o[o.length - 1],
					Public.runescape.deriveFont(Font.PLAIN, 18)).x >= this.game.getWidth() - 340)
				out += "\n";
			else if (Tran.measureString(o[o.length - 1], Public.runescape.deriveFont(Font.PLAIN, 18)).x >= this.game
					.getWidth() - 300)
				out += "\n";
		}

		responses = new ArrayList<Button>();

		this.text = out;
	}
	
	public void setResetOnClose(boolean tf) {
		resetOnClose = tf;
	}

	public void useownSpeech(boolean ownSpeeches) {
		this.ownSpeeches = ownSpeeches;
	}

	public void setSpeeches(TradingKeys speeches) {
		this.speeches = speeches;

		if (speeches != null) {
			responseAmmount = speeches.responseAmmount(speechindex);
			minorindex=0;

			for (int i = 0; i < responseAmmount; i++) {
				String s = speeches.getResponse(speechindex, i);
				if (speeches.getResponse(speechindex, i) != null
						&& Tran.measureString(speeches.getResponse(speechindex, i), Public.fipps).x > 180)

					for (int j = 0; j < speeches.getResponse(speechindex, i).length(); j++) {
						if (Tran.measureString(speeches.getResponse(speechindex, i).substring(0, j),
								Public.fipps).x >= 180) {
							s = speeches.getResponse(speechindex, i).substring(0, j) + "...";
							break;
						}
					}

				if (responses.size() <= i)
					responses.add(
							new Button(game, 70, 220 + (i * 40), 198, 18, speeches.getResponse(speechindex, i), s));
				else
					responses.set(i,
							new Button(game, 70, 220 + (i * 40), 198, 18, speeches.getResponse(speechindex, i), s));
			}
		}
	}

	public void setQuests(Map<String, String> quests) {
		this.questMapper = quests;
	}

	public void setSection(int h) {
		sectionindex = h;
		if (speeches != null) {
			responseAmmount = speeches.responseAmmount(speechindex);
			minorindex=0;
			
			for (int i = 0; i < responseAmmount; i++) {
				String s = speeches.getResponse(speechindex, i);
				if (Tran.measureString(speeches.getResponse(speechindex, i), Public.fipps).x > 180)
					for (int j = 0; j < speeches.getResponse(speechindex, i).length(); j++) {
						if (Tran.measureString(speeches.getResponse(speechindex, i).substring(0, j),
								Public.fipps).x >= 180) {
							s = speeches.getResponse(speechindex, i).substring(0, j) + "...";
							break;
						}
					}

				if (responses.size() <= i)
					responses.add(
							new Button(game, 70, 220 + (i * 40), 198, 18, speeches.getResponse(speechindex, i), s));
				else
					responses.set(i,
							new Button(game, 70, 220 + (i * 40), 198, 18, speeches.getResponse(speechindex, i), s));
			}
		}
	}

	public void useSpeech(String h) {
		if (h.contains("~end~")) {
			shown = false;
			ownSpeeches = false;
			return;
		}

		speechindex = speeches.indexOfSpeech(h);
		speechindex = Math.max(speechindex, 0);
		minorindex=0;
		if (speeches != null) {
			responseAmmount = speeches.responseAmmount(speechindex);

			for (int i = 0; i < responseAmmount; i++) {
				String s = speeches.getResponse(speechindex, i);
				if (Tran.measureString(speeches.getResponse(speechindex, i), Public.fipps).x > 180)
					for (int j = 0; j < speeches.getResponse(speechindex, i).length(); j++) {
						if (Tran.measureString(speeches.getResponse(speechindex, i).substring(0, j),
								Public.fipps).x >= 180) {
							s = speeches.getResponse(speechindex, i).substring(0, j) + "...";
							break;
						}
					}

				if (responses.size() <= i)
					responses.add(
							new Button(game, 70, 220 + (i * 40), 198, 18, speeches.getResponse(speechindex, i), s));
				else
					responses.set(i,
							new Button(game, 70, 220 + (i * 40), 198, 18, speeches.getResponse(speechindex, i), s));
			}
		}
	}

	public void setText(String text) {
		this.text = Public.limit(text, 340, Public.runescape.deriveFont(Font.PLAIN, 20));
	}
	
	public static void loadVoices(Handler game) {
		System.out.println("Getting voices...");
		addVoice(game, "Sounds/Voices/Default.ogg", "DefaultVoice");
		addVoice(game, "Sounds/Voices/DefaultMale.ogg", "DefaultVoiceMale");
		addVoice(game, "Sounds/Voices/DefaultFemale.ogg", "DefaultVoiceFemale");
	}
	
	@Override
	public void tick() {
		
		if(!loadedSounds) {
			loadedSounds = true;
			loadVoices(game);
		}
		
		if(minorindex<speeches.getSpeech(speechindex).length()&&ownSpeeches) {
			int p = (int) (minorindex/soundSpace);
			minorindex += speed;
			minorindex = Public.range(1, speeches.getSpeech(speechindex).length(), minorindex);
			if(!Character.isWhitespace(speeches.getSpeech(speechindex).charAt((int) minorindex-1)) && p!=(int) (minorindex/soundSpace)) {
				//game.soundSystem.stop(voice);
				game.setPosition(voice, (int) game.getPlayer().getX(), (int) game.getPlayer().getY(), 10);
				game.play(voice);
			}
		}
		
		if(game.getMouse().touches(60, 215, 290, 340))
			responseTick();
		
		if(!shown && resetOnClose) {
			ownSpeeches = true;
			speechindex = 0;
			minorindex = 0;
			setSpeeches(speeches);
		}
			
		
	}
	
	void responseTick() {
		
		responseScroll-=game.getMouse().getMouseScroll()*10; 
		responseScroll = Public.range(-40*responses.size()+125, 0, responseScroll);
		
		questCheck();
		
		for (int i = 0; i < responseAmmount; i++) {
			responses.get(i).tick(game.getMouse().rMouseX(), game.getMouse().rMouseY()-(int) responseScroll);
			responses.get(i).data = false;
			responses.get(i).locked = false;
			
			if (responses.get(i).on) {
				useSpeech(speeches.getNext(speechindex, responses.get(i).getDescription()));
				withNextSpeech();
			}
		}
		
		questCheck();
	}
	
	public void questCheck() {
		if(questMapper != null) {
			String speech = speeches.getSpeech(speechindex);

			for(String s: questMapper.keySet()) {
				
				// Check if current speech has a quest attached to it and it doesn't have a specific response
				if(speech.equals(s)) {
					Quest.begin(Quest.getQuest(questMapper.get(s)));
					//Loop through responses
					for(int i = 0; i<responseAmmount; i++) {
						//Check if the quest is completeable
						if(Quest.questcompletable(Quest.getQuest(questMapper.get(s))) && responses.get(i).on) {
							Quest.finish(Quest.getQuest(questMapper.get(s)));
						} else { // Lock every response until quest is finished
							responses.get(i).locked = true;
						}
					}
				} else if(s.contains(speech)&&s.contains("~`~")){ // Check if it has a specific response
					Quest.begin(Quest.getQuest(questMapper.get(s)));
					
					String responseName = s.split("~`~")[1]; // Get the name of the response
					
					for(int i = 0; i<responseAmmount; i++) {
						if(responses.get(i).getName().equals(responseName)) { // Find the response necessary
							//Check if the quest is completeable
							if(Quest.questcompletable(Quest.getQuest(questMapper.get(s))) && responses.get(i).on) {
								Quest.finish(Quest.getQuest(questMapper.get(s)));
							} else { // Lock every response until quest is finished
								responses.get(i).locked = true;
							}
						}
					}
				}
			}
			
		}
		
	}
	
	public static void addVoice(Handler game, String path, String id) {
		game.addSound(path, id, false, 0, 0, 0);
	}

	public void withNextSpeech() {
		
	}
	
	@Override
	public void render(Graphics g) {
		body.render((Graphics2D) g, 50, 50);
		
		icon.render((Graphics2D) g, game.getWidth() - 210, 60);

		indent.render((Graphics2D) g, 60, 90);

		g.drawImage(imgicon, game.getWidth() - 210, 56, 150, 150, null);

		g.setFont(Public.runescape.deriveFont(Font.BOLD, 24));

		responseindent.render((Graphics2D) g, (int) (game.getWidth() - 430), 215);

		Tran.drawOutlinedText(g, 70, 80, name, 1, Color.black, Color.white);
		if (!ownSpeeches) {
			g.setFont(Public.runescape.deriveFont(Font.PLAIN, 18));
			Tran.TEXT_MODE = 1;
			Tran.drawOutlinedText(g, 70, 95, text, 1, Color.black, Color.white);
			Tran.TEXT_MODE = 0; 
		} else {
			
			g.setFont(Public.runescape.deriveFont(Font.PLAIN, 20));
			Tran.TEXT_MODE = 1;
			Tran.drawOutlinedText(g, 70, 95,
					Public.limit(speeches.getSpeech(speechindex).substring(0, (int) minorindex), 430, Public.runescape.deriveFont(Font.PLAIN, 20)), 1,
					Color.black, Color.white);
			Tran.TEXT_MODE = 0;
			for(int i = 0; i<responseAmmount; i++) {
				if (responses.get(i).hovered) {
					Tran.TEXT_MODE = 1;
					g.setFont(Public.runescape.deriveFont(Font.PLAIN, 20));
					Tran.drawOutlinedText(g, game.getWidth() - 426, 220, Public.limit(responses.get(i).getDescription(),
							200, Public.runescape.deriveFont(Font.PLAIN, 20)), 1, Color.black, Color.white);
					Tran.TEXT_MODE = 0;
				}
			}
			
			buttonindent.render((Graphics2D) g, 60, 215);
			
			g.drawImage(responseRender(), 60, 215, null);
		}
	}
	
	BufferedImage responseRender() {
		BufferedImage mask = new BufferedImage(230, 130, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = mask.createGraphics();
		g.translate(-60, -215+(int) responseScroll);
		for (int i = 0; i < responseAmmount; i++) {
			responses.get(i).render(g);
		}
		
		g.dispose();
		return mask;
	}

	@Override
	public void init() {

	}

	public static BevelPlatform body, icon;
	public static BevelIndent indent, responseindent, buttonindent;

}
