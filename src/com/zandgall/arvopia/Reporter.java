package com.zandgall.arvopia;

import com.zandgall.arvopia.utils.FileLoader;
import com.zandgall.arvopia.utils.Utils;

import java.awt.Color;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.net.http.*;

public class Reporter {
	public static ArrayList<String> scannedLines = new ArrayList<String>();

	public static Scanner sc = new Scanner(System.in);

	public static boolean sentUser; 
	public static String user;

	// Face Hair Hands Shirt Eyes Pants Shoes
	public static Color fc = Color.white, hc = Color.white, hac = Color.white, shc = Color.white, ec = Color.white,
			pc = Color.white, scs = Color.white, ep = Color.white, in = Color.white;

	public static void init() {
		File check = new File(Game.prefix + "/Arvopia/username.txt");
		System.out.println(Game.prefix + "/Arvopia/username.txt : " + check.exists());
		sentUser = check.exists();

		if (sentUser) {
			try {
				String[] file = FileLoader.readFile(Game.prefix + "/Arvopia/username.txt").split(System.lineSeparator());
				user = file[0];

				file = file[1].split("\\s+");

				fc = new Color(Utils.parseInt(file[0]));
				hc = new Color(Utils.parseInt(file[1]));
				hac = new Color(Utils.parseInt(file[2]));
				shc = new Color(Utils.parseInt(file[3]));
				ec = new Color(Utils.parseInt(file[4]));
				pc = new Color(Utils.parseInt(file[5]));
				scs = new Color(Utils.parseInt(file[6]));
				ep = new Color(Utils.parseInt(file[7]));
				in = new Color(Utils.parseInt(file[8]));

			} catch (Exception e) {
				String builder;

				builder = "Unnamed" + System.lineSeparator();

				fc = new Color(255, 215, 135);
				hc = new Color(72, 46, 9);
				hac = new Color(255, 215, 135);
				shc = new Color(255, 255, 255);
				ec = new Color(100, 100, 100);
				pc = new Color(100, 164, 214);
				scs = new Color(92, 59, 21);
				ep = Color.white;
				in = Color.white;

				builder += fc.getRGB() + " ";
				builder += hc.getRGB() + " ";
				builder += hac.getRGB() + " ";
				builder += shc.getRGB() + " ";
				builder += ec.getRGB() + " ";
				builder += pc.getRGB() + " ";
				builder += scs.getRGB() + " ";
				builder += ep.getRGB() + " ";
				builder += in.getRGB() + " ";

				Utils.fileWriter(builder, Game.prefix + "/Arvopia/username.txt");
			}
		} else {
			String builder;

			builder = "Unnamed" + System.lineSeparator();

			fc = new Color(255, 215, 135);
			hc = new Color(72, 46, 9);
			hac = new Color(255, 215, 135);
			shc = new Color(255, 255, 255);
			ec = new Color(100, 100, 100);
			pc = new Color(100, 164, 214);
			scs = new Color(92, 59, 21);
			ep = Color.white;
			in = Color.white;

			builder += fc.getRGB() + " ";
			builder += hc.getRGB() + " ";
			builder += hac.getRGB() + " ";
			builder += shc.getRGB() + " ";
			builder += ec.getRGB() + " ";
			builder += pc.getRGB() + " ";
			builder += scs.getRGB() + " ";
			builder += ep.getRGB() + " ";
			builder += in.getRGB() + " ";

			Utils.fileWriter(builder, Game.prefix + "/Arvopia/username.txt");
		}
	}

	public static void save() {
		String builder;

		builder = user + System.lineSeparator();

		builder += fc.getRGB() + " ";
		builder += hc.getRGB() + " ";
		builder += hac.getRGB() + " ";
		builder += shc.getRGB() + " ";
		builder += ec.getRGB() + " ";
		builder += pc.getRGB() + " ";
		builder += scs.getRGB() + " ";
		builder += ep.getRGB() + " ";
		builder += in.getRGB() + " ";

		Utils.fileWriter(builder, Game.prefix + "/Arvopia/username.txt");
	}

	public static void out(String error) {
		String toEmail = "Alexanderdgall@gmail.com";
		String fromEmail = "messanger.zandgall@gmail.com";

		if (!sentUser) {
			System.out.print("What's your username? : ");
			user = sc.nextLine();
			sentUser = true;
			Utils.fileWriter(user, Game.prefix + "/Arvopia/username.txt");
		} else {
			user = FileLoader.readFile(Game.prefix + "/Arvopia/username.txt");
		}

		String subject = "Bug report: " + user;
		String message = "Bug report from: " + user + System.lineSeparator() + System.lineSeparator();
		message = message + "Error: " + error + System.lineSeparator() + System.lineSeparator();
		System.out.print("Anything you want to say? (Leave blank for no) : ");
		String custom = sc.nextLine();
		if (custom.length() > 1)
			message = message + user + ": " + custom + System.lineSeparator() + System.lineSeparator();
		message = message + " ~ End of bug report";

		try {
			Properties props = System.getProperties();
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "true");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("messanger.zandgall@gmail.com", "sWhQr7qmvSHKNQh");
				}

			});
			Message m = new MimeMessage(session);
			System.out.println("Sending message " + subject + ": " + message + " from " + fromEmail + " to " + toEmail);
			m.setFrom(new InternetAddress(fromEmail));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			m.setSubject(subject);
			m.setText(message);

			Transport.send(m);
			System.out.println("Message sent!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
					+ System.lineSeparator() + System.lineSeparator());
		}
	}

	public static void sendMessage(String subject, String message, boolean silent) {
		String toEmail = "Alexanderdgall@gmail.com";
		String fromEmail = "messanger.zandgall@gmail.com";

		user = FileLoader.readFile(Game.prefix + "/Arvopia/username.txt");

		try {
			Properties props = System.getProperties();
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "true");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("messanger.zandgall@gmail.com", "sWhQr7qmvSHKNQh");
				}
			});
			Message m = new MimeMessage(session);
			if (!silent)
				System.out.println(
						"Sending message " + subject + ": " + message + " from " + fromEmail + " to " + toEmail);
			m.setFrom(new InternetAddress(fromEmail));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			m.setSubject(subject);
			m.setText("Message from: " + user + System.lineSeparator() + message);
			Transport.send(m);

			if (!silent)
				System.out.println("Message sent!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
					+ System.lineSeparator() + System.lineSeparator());
		}
	}

	public static void addUser() {
		String toEmail = "Alexanderdgall@gmail.com";
		String fromEmail = "messanger.zandgall@gmail.com";
		sentUser = true;
		Utils.fileWriter(user, Game.prefix + "/Arvopia/username.txt");
		String subject = "New user!";
		String message = user + " has joined the world of Arvopia" + System.lineSeparator();

		try {
			Properties props = System.getProperties();
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "true");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("messanger.zandgall@gmail.com", "sWhQr7qmvSHKNQh"); //sWhQr7qmvSHKNQh
				}

			});
			MimeMessage m = new MimeMessage(session);
			System.out.println("Sending message " + subject + ": " + message + " from " + fromEmail + " to " + toEmail);
			m.setFrom(new InternetAddress(fromEmail));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			m.setSubject(subject);
			m.setText(message);

			Transport.send(m);
			System.out.println("Message sent!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
					+ System.lineSeparator() + System.lineSeparator());
		}

		// Send HTTP request to www.zandgall.com/data/post in order to mark down username
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://www.zandgall.com/data/post?name=" + user))
				.build();
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenAccept(System.out::println)
				.join();
	}

	public static void quick(String error) {
		String toEmail = "Alexanderdgall@gmail.com";
		String fromEmail = "messanger.zandgall@gmail.com";
		String user = FileLoader.readFile(Game.prefix + "/Arvopia/username.txt");
		String subject = "Bug report: " + user;
		String message = "Bug report from: " + user + System.lineSeparator() + System.lineSeparator();
		message = message + "Error: " + error + System.lineSeparator() + System.lineSeparator();
		message = message + " ~ End of bug report";

		try {
			Properties props = System.getProperties();
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.auth", "true");
			props.put("mail.debug", "true");
			props.put("mail.store.protocol", "pop3");
			props.put("mail.transport.protocol", "smtp");
			Session session = Session.getDefaultInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("messanger.zandgall@gmail.com", "sWhQr7qmvSHKNQh");
				}

			});
			MimeMessage m = new MimeMessage(session);
			System.out.println("Sending message " + subject + ": " + message + " from " + fromEmail + " to " + toEmail);
			m.setFrom(new InternetAddress(fromEmail));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			m.setSubject(subject);
			m.setText(message);

			Transport.send(m);
			System.out.println("Message sent!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(System.lineSeparator() + System.lineSeparator() + System.lineSeparator()
					+ System.lineSeparator() + System.lineSeparator());
		}
	}
}
