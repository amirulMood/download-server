package com.task.download_server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.UUID;

@RestController
public class DownloadController {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @GetMapping("/trigger-download")
    public ResponseEntity<String> triggerDownload(@RequestParam String clientUrl) {
        try {
            URI uri = new URI(clientUrl);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

            Path outDir = Path.of("downloads");
            Files.createDirectories(outDir);
            Path outFile = outDir.resolve("file-" + UUID.randomUUID() + ".bin");

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200)
                return ResponseEntity.status(response.statusCode()).body("Failed: " + response.statusCode());

            try (InputStream is = response.body()) {
                Files.copy(is, outFile);
            }

            return ResponseEntity.ok("Downloaded from " + clientUrl + " → " + outFile.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}