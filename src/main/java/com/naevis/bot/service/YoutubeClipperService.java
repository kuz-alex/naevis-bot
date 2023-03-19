package com.naevis.bot.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class YoutubeClipperService {
    public String clipVideo(String link, String start, String end) throws IOException, InterruptedException {
        String workbenchDir = System.getProperty("user.home") + "/.workbench";
        File workbench = new File(workbenchDir);
        if (!workbench.exists()) {
            workbench.mkdir();
        }
        String resultFileName = UUID.randomUUID() + ".mp4";

        String[] cmd = {"/bin/bash", "-c", "~/.bin/naevis-clip " + link + " " + start + " " + end + " " + resultFileName};
        Process process = Runtime.getRuntime().exec(cmd);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to clip video");
        }
        return workbenchDir + "/" + resultFileName;
    }

    @SneakyThrows
    public static void main(String[] args) {
        String link = "https://youtube.com/watch?v=QZHbypZSTGg";
        // QZHbypZSTGg 2:39.89 3:29.00
        String resultedFileName = new YoutubeClipperService().clipVideo(link, "2:39.89", "3:29.00");
        System.out.println(resultedFileName);
    }
}
