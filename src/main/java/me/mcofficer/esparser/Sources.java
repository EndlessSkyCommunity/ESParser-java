/*
Sources.java
Copyright (c) 2014 Michael Zahniser
Copyright (C) 2017 Frederick W. Goy IV
Copyright (C) 2018 MCOfficer

This program is a derivative of the source code from the Endless Sky
project, which is licensed under the GNU GPLv3.

Endless Sky source: https://github.com/endless-sky/endless-sky


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package me.mcofficer.esparser;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Sources {

    public static ArrayList<File> getSources(Path dataPath, @Nullable Path pluginPath) throws IOException {
        ArrayList<File> files = new ArrayList<>();

        Files.walk(dataPath).forEachOrdered(path -> files.add(path.toFile()));

        if (pluginPath != null)
            for (File dir : Objects.requireNonNull(pluginPath.toFile().listFiles(File::isDirectory)))
                for (File subdir : Objects.requireNonNull(dir.listFiles(File::isDirectory)))
                    if (subdir.getName().equalsIgnoreCase("data"))
                        Files.walk(subdir.toPath()).forEachOrdered(path -> {
                            if (!path.toFile().isDirectory())
                                files.add(path.toFile());
                        });

        return files;
    }

    public static ArrayList<File> getSources(String dataPath, @Nullable String pluginPath) throws IOException {
        Path _dataPath = Paths.get(dataPath).normalize();
        Path _pluginPath = pluginPath == null ? null : Paths.get(pluginPath).normalize();
        return getSources(_dataPath, _pluginPath);
    }

    public static HashMap<String, File> getImages(Path imagePath, @Nullable Path pluginPath) throws IOException{
        HashMap<String, File> files = new HashMap<>();

        Files.walk(imagePath).forEachOrdered(path -> files.put(imagePath.relativize(path).toString(), path.toFile()));

        if (pluginPath != null)
            for (File dir : Objects.requireNonNull(pluginPath.toFile().listFiles(File::isDirectory)))
                for (File subdir : Objects.requireNonNull(dir.listFiles(File::isDirectory)))
                    if (subdir.getName().equalsIgnoreCase("image"))
                        Files.walk(subdir.toPath()).forEachOrdered(path -> {
                            if (!path.toFile().isDirectory())
                                files.put(subdir.toPath().relativize(path).toString(), path.toFile());
                        });

        return files;
    }

    public static HashMap<String, File> getImages(String imagePath, @Nullable String pluginPath) throws IOException {
        Path _imagePath = Paths.get(imagePath).normalize();
        Path _pluginPath = pluginPath == null ? null : Paths.get(pluginPath).normalize();
        return getImages(_imagePath, _pluginPath);
    }

    public static Path getConfigPath() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Path path = null;

        if (os.startsWith("linux"))
            path = Paths.get("~/.local/share/endless-sky/");
        else if (os.startsWith("mac"))
            path = Paths.get("~/Library/Application Support/endless-sky/");
        else if (os.startsWith("windows"))
            path = Paths.get("%appdata%\\endless-sky");
        else
            throw new IOException("Failed to detect operating system");

        if (!path.toFile().exists())
            throw new IOException("Supposed config path does not exist or has been moved");

        return path;
    }

    public static Path getGamePath() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Path path = null;

        if (os.startsWith("linux"))
            path = Paths.get("~/.steam/steam/steamapps/common/Endless Sky/data");
        else if (os.startsWith("mac"))
            path = Paths.get("~/Library/Application Support/Steam/SteamApps/common/Endless Sky/data");
        else if (os.startsWith("windows"))
            path = Paths.get("%programfiles(x86)%\\Steam\\steamapps\\common\\Endless Sky\\data");
        else
            throw new IOException("Failed to detect operating system");

        if (!path.toFile().exists())
            throw new IOException("Supposed game path does not exist or has been moved");

        return path;
    }
}
