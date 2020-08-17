package entries.FlameGameformats;

import com.tfc.flame.FlameConfig;
import com.tfc.flame.IFlameAPIMod;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main implements IFlameAPIMod {
	private static String gameDir = "";
	private static String game = "";
	private static String version = "";
	
	@Override
	public void setupAPI(String[] args) {
		FlameConfig.field.append("helolgeowgkwpoeg\n");
		try {
			boolean isDir = false;
			boolean isVersion = false;
			boolean isGame = false;
			for (String s : args) {
				if (s.equals("--gameDir")) {
					isDir = true;
				} else if (isDir) {
					gameDir = s;
					isDir = false;
				} else if (s.equals("--game")) {
					isGame = true;
				} else if (isGame) {
					game = s;
					isGame = false;
				} else if (s.equals("--version")) {
					isVersion = true;
				} else if (isVersion) {
					version = s;
					isVersion = false;
				}
			}
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		
		if (game.equals("")) {
			if (gameDir.contains("minecraft")) {
				game = "Minecraft";
			}
		}
		File fi = new File(gameDir + "\\flame_mods");
		String message = "";
		for (File f : fi.listFiles()) {
			if (f.getName().endsWith(".zip") || f.getName().endsWith(".jar")) {
				try {
					ZipFile file;
					if (f.getName().endsWith(".jar")) {
						file = new JarFile(f);
					} else {
						file = new ZipFile(f);
					}
					try {
						boolean foundGameFormat = false;
						Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							if (entry.getName().endsWith("GameFormat.properties")) {
								foundGameFormat = true;
								InputStream stream = file.getInputStream(entry);
								Scanner sc = new Scanner(stream);
								String gameFor = "";
								String versionMin = "";
								String versionMax = "";
								while (sc.hasNextLine()) {
									String s = sc.nextLine();
									if (s.startsWith("Game:")) {
										gameFor = s.replace("Game:", "");
									} else if (s.startsWith("VersionMin:")) {
										versionMin = s.replace("VersionMin:", "");
									} else if (s.startsWith("VersionMax:")) {
										versionMax = s.replace("VersionMax:", "");
									}
								}
								if (!gameFor.equals("Anything")) {
									if (!gameFor.equals(game)) {
										message += "Mod " + f.getName().replace(".zip", "").replace(".jar", "") + " wants to be installed on " + gameFor + " not " + game + ". ";
									}
								}
								String[] gameVersion = version.split("\\.");
								String[] min = versionMin.split("\\.");
								String[] max = versionMax.split("\\.");
								int numberGood = 0;
								
								if (versionMin.equals("Anything")) {
									numberGood = gameVersion.length;
								} else {
									for (int i = 0; i < gameVersion.length; i++) {
										int num1 = Integer.parseInt(stripLetters(gameVersion[i].replace(".", "")));
										try {
											int num2 = Integer.parseInt(stripLetters(min[i].replace(".", "")));
											if (num1 >= num2) {
												numberGood++;
											}
										} catch (Throwable ignored) {
										}
									}
								}
								if (numberGood < gameVersion.length) {
									message += "Mod " + f.getName().replace(".zip", "").replace(".jar", "") + " wants to be installed on a version between " + versionMin + " and " + versionMax + " not " + version + ". ";
								} else {
									numberGood = 0;
									if (versionMax.equals("Anything")) {
										numberGood = gameVersion.length;
									} else {
										for (int i = 0; i < gameVersion.length; i++) {
											int num1 = Integer.parseInt(stripLetters(gameVersion[i].replace(".", "")));
											try {
												int num2 = Integer.parseInt(stripLetters(max[i].replace(".", "")));
												if (num1 <= num2) {
													numberGood++;
												}
											} catch (Throwable ignored) {
											}
										}
									}
									if (numberGood < gameVersion.length) {
										message += "Mod " + f.getName().replace(".zip", "").replace(".jar", "") + " wants to be installed on a version between " + versionMin + " and " + versionMax + " not " + version + ". ";
									}
								}
								message+="\n";
								stream.close();
							}
						}
						if (!foundGameFormat) {
							message += "Could not find a game format for mod: " + f.getName().replace(".zip", "").replace(".jar", "")+".\n";
						}
					} catch (Throwable err) {
						message += "Could not find a game format for mod: " + f.getName().replace(".zip", "").replace(".jar", "")+".\n";
						FlameConfig.logError(err);
					}
					file.close();
				} catch (Throwable err) {
					message += "Could not find a game format for mod: " + f.getName().replace(".zip", "").replace(".jar", "")+".\n";
				}
			}
		}
		if (message.replace("\n","").equals("")) {
			FlameConfig.field.append("[Flame GameFormats]: No warning!");
		} else {
			FlameConfig.field.append("[Flame GameFormats]: "+message);
		}
	}
	
	private static String stripLetters(String input) {
		String output = "";
		for (char c:input.toCharArray()) {
			try {
				output += ""+Integer.parseInt(""+c);
			} catch (Throwable ignored) {}
		}
		return output;
	}
	
	@Override
	public void preinit(String[] args) {
	
	}
	
	@Override
	public void init(String[] args) {
	
	}
	
	@Override
	public void postinit(String[] args) {
	
	}
}
