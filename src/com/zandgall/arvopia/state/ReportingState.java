package com.zandgall.arvopia.state;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.TextEditor;
import java.awt.Graphics2D;

public class ReportingState extends State {
	private TextEditor subject;
	private TextEditor message;
	private Button send;
	private Button back;

	public ReportingState(Handler handler) {
		super(handler);
		init();
	}

	public void tick() {
		subject.tick();
		message.tick();
		send.tick();
		back.tick();

		if (back.on)
			State.setState(handler.getGame().menuState);
		if (send.on) {
			com.zandgall.arvopia.Reporter.sendMessage(subject.getContent(), message.getContent(), false);
			com.zandgall.arvopia.quests.Achievement.award(com.zandgall.arvopia.quests.Achievement.reporter);
		}
	}

	public void render(Graphics2D g) {
		g.setColor(new java.awt.Color(120, 225, 255));

		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

		subject.render(g);
		message.render(g);

		send.render(g);
		back.render(g);
	}

	public void init() {
		subject = new TextEditor(handler, 10, 10, 700, 1, "Subject", Public.defaultFont.deriveFont(12));
		message = new TextEditor(handler, 10, 30, 700, 20, "Message", Public.defaultFont.deriveFont(12));

		send = new Button(handler, 10, 350, "Click to send a message with the parameters above", "Send");
		back = new Button(handler, 70, 350, "Takes you back to title screen", "Back");
	}

	@Override
	public void renderGUI(Graphics2D g2d) {
		
	}
}
