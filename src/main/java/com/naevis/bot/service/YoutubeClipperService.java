package com.naevis.bot.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class YoutubeClipperService {
    public String clipVideo(String link, String start, String end, Boolean withSubs, Boolean isGif) throws IOException, InterruptedException {
        String workbenchDir = System.getProperty("user.home") + "/.workbench";
        File workbench = new File(workbenchDir);
        if (!workbench.exists()) {
            workbench.mkdir();
        }
        String resultFileName = UUID.randomUUID() + ".mp4";

        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append("yes N | ~/.bin/naevis-clip ")
                .append(" ").append(link)
                .append(" ").append(start)
                .append(" ").append(end)
                .append(" ").append(resultFileName);

        if (withSubs) {
            cmdBuilder.append(" --subs");
        } else if (isGif) {
            cmdBuilder.append(" --gif");
        }

        Process process = new ProcessBuilder()
                .command("/bin/bash", "-c", cmdBuilder.toString())
                // .redirectErrorStream(true)
                .start();

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
        String resultedFileName = new YoutubeClipperService().clipVideo(link, "2:39.89", "3:29.00", false, false);
        System.out.println(resultedFileName);
    }
}
