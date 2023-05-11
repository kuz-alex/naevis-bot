package com.naevis.bot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShellUtils {
    public static CommandResult runCommand(String command) {
        int exitCode = -1;
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        BufferedReader successReader = null;
        BufferedReader errorReader = null;

        try {
            Process process = new ProcessBuilder()
                    .command("/bin/bash", "-c", command)
                    .start();

            exitCode = process.waitFor();

            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            stdout = new StringBuilder();
            stderr = new StringBuilder();

            String s;
            while ((s = successReader.readLine()) != null) {
                stdout.append(s).append("\n");
            }
            while ((s = errorReader.readLine()) != null) {
                stderr.append(s).append("\n");
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.toString());
        } finally {
            try {
                if (successReader != null) {
                    successReader.close();
                }
                if (errorReader != null) {
                    errorReader.close();
                }
            } catch (IOException e) {
                log.error(e.toString());
            }
        }

        return new CommandResult(exitCode, stdout.toString(), stderr.toString());
    }

    @Data
    @AllArgsConstructor
    public static class CommandResult {
        private final Integer exitCode;
        private final String stdout;
        private final String stderr;
    }

}
