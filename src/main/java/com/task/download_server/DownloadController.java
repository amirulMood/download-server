package com.task.download_server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

@RestController
public class DownloadController {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/download-file")
    public ResponseEntity<String> triggerDownload(@RequestParam String clientUrl) {
        try {
            URI uri = new URI(clientUrl);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

            // Allows for the download from the requestParam
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Get the file name
            String fileName = response.headers()
                    .firstValue("Content-Disposition")
                    .map(h -> h.replace("attachment; filename=", "").replace("\"", ""))
                    .orElse("downloaded_file.txt");

            // If folder dont exist make a new one
            Path outDir = Path.of("HOME");
            Files.createDirectories(outDir);

            // Name the file
            Path outFile = outDir.resolve(fileName);

            // Save/Replace file
            try (InputStream is = response.body()) {
                Files.copy(is, outFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return ResponseEntity.ok(outFile.toString().replace("\\", "/"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}