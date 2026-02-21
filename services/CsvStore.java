package services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Shared utility for reading/writing simple CSV storage files.
public final class CsvStore {
    private CsvStore() {}

    // Ensures `data/` exists before any write operation.
    public static void ensureDataDir() {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory", e);
        }
    }

    // Reads CSV rows excluding header; returns empty list when file is missing.
    public static List<String> readDataLines(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(lines.subList(1, lines.size()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + filePath, e);
        }
    }

    // Writes full CSV content with header and supplied rows.
    public static void writeAll(String filePath, String header, List<String> rows) {
        ensureDataDir();
        List<String> lines = new ArrayList<>();
        lines.add(header);
        lines.addAll(rows);

        try {
            Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + filePath, e);
        }
    }
}
