package com.zandgall.arvopia;

import com.zandgall.arvopia.utils.Utils;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log implements Serializable {
	private static final long serialVersionUID = -4362712707400368144L;
	public String file;
	public String log;
	public boolean exists = false;

	public Log(String file, String source) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		Date date = new Date();

		String dateString = formatter.format(date);

		this.file = (file + " " + dateString + " (" + System.currentTimeMillis() + ").txt");

		log = (log + "Logger Initiated for " + source + " at " + dateString + System.lineSeparator());

		Utils.fileWriter("Logger Initiated for " + source + " at " + dateString, this.file);

		System.out.println("Logger Initiated for " + source + " at " + dateString);

		exists = true;
	}

	public boolean exists() {
		if (exists)
			return exists;
		return false;
	}

	public void log(String log) {
		System.out.println(log);

//		this.log = (this.log + System.lineSeparator() + log);

//		ArvopiaLauncher.game.actions.add(log);

//		Utils.existWriter(this.log, file);
	}

	public void out(String string) {
		ArvopiaLauncher.game.actions.add(string);

		System.out.println(string);
	}

	public void logSilent(String log) {
		this.log = (this.log + System.lineSeparator() + log);

//		ArvopiaLauncher.game.actions.add(log);
//		
//		Utils.existWriter(this.log, file);
	}
}
