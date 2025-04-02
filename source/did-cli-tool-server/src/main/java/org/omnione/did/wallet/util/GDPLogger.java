package org.omnione.did.wallet.util;
//added by choi sung hoon

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

//purpose : print visible hex log

import com.google.gson.JsonParser;
import org.omnione.did.wallet.util.json.GsonWrapper;

public class GDPLogger {
	
	public static Logger logger = Logger.getLogger("GDPLogger");

	public static boolean FLAG = false;
	
	public static boolean SYSOUT_PRINT = false;

	private static void log(Level level, String message) {
		if(FLAG || level.intValue() == Level.INFO.intValue()) {
			if(SYSOUT_PRINT) {
				System.out.println(message);
			}else {
				logger.log(Level.INFO, message);
			}
			
		}
	}

	public static void print(String message) {
		log(Level.FINE, message);
	}

	public static void debug(String message) {
		print(Level.FINE, "debug", message);
	}
	
	public static void info(String message) {
		print(Level.INFO, "info", message);
	}
	
	public static void print(String title, String message) {
		print(Level.FINE, title, message);
	}
	
	public static void print(Level level, String title, String message) {
		if (FLAG == true) {
			log(level, "[" + title + "] " + message);
		}
	}

	public static void printHex(String title, byte[] input) {

		if (FLAG == true & title != null & input != null) {
			log(Level.FINE,"[" + title + "] " + input.length + "byte");
			printHex(input);

		} // flag
	}// printHex

	public static void printPrettyJson(String title, String message) {
		if (FLAG == true) {
			log(Level.FINE,"[" + title + "]\n" + GDPLogger.toPrettyFormat(message));
		}
	}

	public static String toPrettyFormat(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		GsonWrapper gson = new GsonWrapper(true);
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}

	private static void printHex(byte[] input) {

		if (FLAG == true) {

			int length = input.length;
			int line = length / 16;
			log(Level.FINE,"> 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15    0123456789abcdef");
			log(Level.FINE,"> ===============================================    ================");

			for (int i = 0; i <= line; i++) {
				StringBuffer d = new StringBuffer(83);
				int column = Math.min(16, (length - i * 16));
				for (int j = 0; j < column; j++) {
					char hi = forDigit(input[i * 16 + j] >> 4 & 0x0F, 16);
					char lo = forDigit(input[i * 16 + j] & 0x0F, 16);
					d.append(Character.toUpperCase(hi));
					d.append(Character.toUpperCase(lo));
					d.append(':');
				}
				for (int j = 16; j >= column; j--) {
					d.append("   ");
				}
				for (int j = 0; j < column; j++) {
					char tmp = (char) input[i * 16 + j];
					if (isISOControl(tmp)) {
						d.append('.');
					}
					else {
						d.append((char) input[i * 16 + j]);
					}
				}
				log(Level.FINE,"> " + d);
			}

		} // flag

	}// printHex

	private static char forDigit(int i, int j) {
		if (i >= j || i < 0) {
			return '\0';
		}
		if (j < 2 || j > 36) {
			return '\0';
		}
		if (i < 10) {
			return (char) (48 + i);
		}
		else {
			return (char) (87 + i);
		}
	}

	private static boolean isISOControl(char c) {
		return c <= '\237' && (c <= '\037' || c >= '\177');
	}

}
