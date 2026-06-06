package com.meow.dump.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RootUtils {
    
    private static Boolean rootCached = null;
    private static long rootCacheTime = 0;
    private static final long ROOT_CACHE_DURATION = 60000;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    
    private static Process suProcess = null;
    private static DataOutputStream suOutput = null;
    private static BufferedReader suReader = null;
    private static BufferedReader suError = null;
    private static final Object suLock = new Object();
    
    public static boolean hasRootAccess() {
        if (rootCached != null && (System.currentTimeMillis() - rootCacheTime) < ROOT_CACHE_DURATION) {
            return rootCached;
        }
        
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("echo test\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            rootCached = process.exitValue() == 0;
            rootCacheTime = System.currentTimeMillis();
            return rootCached;
        } catch (Exception e) {
            rootCached = false;
            rootCacheTime = System.currentTimeMillis();
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    
    private static void ensureSuSession() {
        synchronized (suLock) {
            try {
                if (suProcess == null || !suProcess.isAlive()) {
                    suProcess = Runtime.getRuntime().exec("su");
                    suOutput = new DataOutputStream(suProcess.getOutputStream());
                    suReader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
                    suError = new BufferedReader(new InputStreamReader(suProcess.getErrorStream()));
                }
            } catch (Exception e) {
                suProcess = null;
                suOutput = null;
                suReader = null;
                suError = null;
            }
        }
    }
    
    public static void runAsync(Runnable task) {
        executor.execute(task);
    }
    
    public static CommandResult runCommand(String command) {
        return runCommand(new String[]{command});
    }
    
    public static CommandResult runCommand(String[] commands) {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader reader = null;
        BufferedReader errorReader = null;
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        int exitCode = -1;
        
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            
            for (String command : commands) {
                os.writeBytes(command + "\n");
            }
            os.writeBytes("echo EXITCODE:$?\n");
            os.writeBytes("exit\n");
            os.flush();
            
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("EXITCODE:")) {
                    try {
                        exitCode = Integer.parseInt(line.substring(9));
                    } catch (NumberFormatException ignored) {}
                    break;
                }
                output.append(line).append("\n");
            }
            
            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }
            
            if (exitCode == -1) {
                process.waitFor();
                exitCode = process.exitValue();
            }
            
        } catch (Exception e) {
            error.append(e.getMessage());
        } finally {
            try {
                if (os != null) os.close();
                if (reader != null) reader.close();
                if (errorReader != null) errorReader.close();
            } catch (Exception ignored) {}
            if (process != null) {
                process.destroy();
            }
        }
        
        return new CommandResult(exitCode, output.toString(), error.toString());
    }
    
    public static boolean fileExists(String path) {
        CommandResult result = runCommand("[ -f " + path + " ] && echo \"exists\" || echo \"notfound\"");
        return result.getOutput().contains("exists");
    }
    
    public static boolean isExecutable(String path) {
        CommandResult result = runCommand("[ -x " + path + " ] && echo \"executable\" || echo \"notexec\"");
        return result.getOutput().contains("executable");
    }
    
    public static void makeExecutable(String path) {
        runCommand("chmod +x " + path);
    }
    
    public static void collapseStatusBar() {
        runCommand("cmd statusbar collapse");
    }
    
    public static class CommandResult {
        private final int exitCode;
        private final String output;
        private final String error;
        
        public CommandResult(int exitCode, String output, String error) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
        }
        
        public int getExitCode() {
            return exitCode;
        }
        
        public String getOutput() {
            return output;
        }
        
        public String getError() {
            return error;
        }
        
        public boolean isSuccess() {
            return exitCode == 0;
        }
    }
}
