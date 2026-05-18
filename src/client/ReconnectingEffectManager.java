package client;

import java.io.IOException;
import java.io.File;

public class ReconnectingEffectManager {
    private static Process effectProcess;
    private static final String os = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return os.contains("win");
    }

    public static void startEffect(String message) {

        try {
            String projectPath = System.getProperty("user.dir");
            ProcessBuilder pb;
            String fileName = isWindows() ? "effect.bat" : "effect.sh";
            File scriptFile = new File(projectPath + File.separator + "src"
                    + File.separator + "effects"
                    + File.separator + fileName);

            if (isWindows()) {
                pb = new ProcessBuilder("cmd", "/c", scriptFile.getAbsolutePath(), message);
            } else {
                pb = new ProcessBuilder("bash", scriptFile.getAbsolutePath(), message);
            }

            // CHỈ lấy Output, KHÔNG lấy Input từ bàn phím
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            // Tuyệt đối KHÔNG dùng pb.redirectInput(...)

            effectProcess = pb.start();
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi hiệu ứng: " + e.getMessage());
        }
    }

    public static void stopEffect() {
        if (effectProcess != null && effectProcess.isAlive()) {
            try {
                if (isWindows()) {
                    // Windows cần Taskkill để ngắt hoàn toàn cây tiến trình của .bat và powershell
                    Runtime.getRuntime().exec("taskkill /F /T /PID " + effectProcess.toHandle().pid());
                } else {
                    effectProcess.destroy();
                }
                effectProcess.waitFor();
            } catch (Exception ignored) {
            }
        }
        // Hiện lại con trỏ và dọn dẹp dòng cuối
        System.out.print("\r\033[?25h" + " ".repeat(40) + "\r");
        System.out.flush();
    }
}
