package com.naevis.bot.service;

import com.naevis.bot.util.ShellUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

        ShellUtils.CommandResult result = ShellUtils.runCommand(cmdBuilder.toString());

        log.info(String.format("Stdout for video %s: %s", link, result.getStdout()));
        log.info(String.format("Stderr for video %s: %s", link, result.getStderr()));

        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error clipping video: " + link);
        }

        String path = workbenchDir + "/" + resultFileName;
        int[] dimensions = getVideoDimensions(path);
        return new VideoInfo(path, dimensions[0], dimensions[1]);
    }

    static public int[] getVideoDimensions(String fullPath) {
        String command = "ffprobe -v error -select_streams v -show_entries stream=width,height -of csv=p=0:s=x " + fullPath;
        ShellUtils.CommandResult result = ShellUtils.runCommand(command);

        String[] dimensions = result.getStdout().replace("\n", "").split("x");
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
        String link = "https://youtube.com/watch?v=QZHbypZSTGg";
        // QZHbypZSTGg 2:39.89 3:29.00
         YoutubeClipperService.getVideoDimensions("/Users/kuz-alex/.workbench/source_some1a.mp4");
         // VideoInfo test = YoutubeClipperService.clipVideo(link, "2:39.89", "3:29.00", false, false);
         // System.out.println("testeroni333: " + test);
    }
}
