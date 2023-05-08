package com.naevis.bot.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Service
@Slf4j
public class YoutubeClipperService {
    static public VideoInfo clipVideo(String link, String start, String end, Boolean withSubs, Boolean isGif) throws IOException, InterruptedException {
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
                .start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                output.append(errorLine).append("\n");
            }

            log.info(output.toString());
            throw new RuntimeException("Failed to clip video");
        }

        String path = workbenchDir + "/" + resultFileName;
        int[] dimensions = getVideoDimensions(path);
        return new VideoInfo(path, dimensions[0], dimensions[1]);
    }

    static public int[] getVideoDimensions(String fullPath) throws IOException, InterruptedException {
        Process process = new ProcessBuilder()
                .command("/bin/bash", "-c", "ffprobe -v error -select_streams v -show_entries stream=width,height -of csv=p=0:s=x " + fullPath)
                .start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            // TODO: Read the error stream and throw an exception.
            // throw new RuntimeException("Failed to get video dimensions");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        String[] dimensions = output.toString().split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);

        return new int[]{width, height};
    }

    @Data
    @AllArgsConstructor
    static public class VideoInfo {
        private String path;
        private int width;
        private int height;
    }

    @SneakyThrows
    public static void main(String[] args) {
        // String link = "https://youtube.com/watch?v=QZHbypZSTGg";
        // QZHbypZSTGg 2:39.89 3:29.00
        // YoutubeClipperService.getVideoDimensions("/Users/kuz-alex/.workbench/source_some1a.mp4");
        // VideoInfo test = YoutubeClipperService.clipVideo(link, "2:39.89", "3:29.00", false, false);
        // System.out.println("testeroni333: " + test);
    }
}
