package dev.mxace.luachat.utils;

import dev.mxace.luachat.Luachat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtils {
    public static Path createDirIfNeeded(Path path) {
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException ignored) {
        } catch (Exception ex) {
            Luachat.LOGGER.error("Failed to create directory", ex);
        }

        return path;
    }

    public static String readFile(Path path) throws IOException {
        try (InputStream io = Files.newInputStream(path)) {
            return new String(io.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Luachat.LOGGER.error("Failed to read " + path);
            throw ex;
        }
    }
}
