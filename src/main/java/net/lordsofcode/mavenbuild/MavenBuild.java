package net.lordsofcode.mavenbuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MavenBuild extends JavaPlugin implements Listener {

	private Set<String> paths;
	private boolean reloading = false;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		paths = getConfig().getKeys(false);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		if (reloading) {
			for (String path : paths) {
				String dirPath = getConfig().getString(path + ".path");
				List<String> include = getConfig().getStringList(
						path + ".files");
				try {
					getLogger().info("Building maven directory " + dirPath);
					File dir = new File(dirPath);
					Process p = Runtime.getRuntime().exec("mvn install",
							new String[] {}, dir);
					p.waitFor();
					getLogger().info("Successfully built directory " + dirPath);
					List<File> files = getFiles(dir, include);
					for (File file : files) {
						File newFile = new File("plugins/" + file.getName());
						moveFile(file, newFile);
						getLogger().info(
								"Successfully copied " + file.getName()
										+ " to plugins folder");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			reloading = false;
		}
	}

	private List<File> getFiles(File dir, List<String> include) {
		List<File> fileList = new ArrayList<File>();
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".jar")
					&& checkFileName(file.getName(), include)) {
				fileList.add(file);
			}
			if (file.isDirectory()) {
				for (File f : getFiles(file, include)) {
					fileList.add(f);
				}
			}
		}
		return fileList;
	}

	private boolean checkFileName(String fileName, List<String> includes) {
		if (fileName.startsWith("original-"))
			return false;
		for (String include : includes) {
			if (include.contains("*")) {
				String[] split = include.split("\\*");
				boolean b = false;
				for (String sp : split) {
					if (!fileName.contains(sp)) {
						b = true;
						break;
					}
				}
				if (b)
					continue;
				return true;
			} else {
				return fileName.replace(".jar", "").equals(include);
			}
		}
		return false;
	}

	private void moveFile(File file1, File file2) {
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			inStream = new FileInputStream(file1);
			outStream = new FileOutputStream(file2);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerReload(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("reload")) {
			reloading = true;
		}
	}

	@EventHandler
	public void onConsoleReload(ServerCommandEvent event) {
		if (event.getCommand().equalsIgnoreCase("reload")) {
			reloading = true;
		}
	}

}
