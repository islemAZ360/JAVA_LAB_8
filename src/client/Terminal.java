package client;

public class Terminal {
    private static final String ESC = "\033[";
    private static volatile boolean animRunning = false;
    private static Thread animThread;

    private static final int[] COLORS = {
            234,236,238,240,242,244,246,248,250,252,
            254,255,254,252,250,248,246,242,238,234
    };

    // ── startAnimation ────────────────────────────────────────────────────────
    // Gọi sau Terminal.log() — log() để cursor sau ">>> "
    // startAnimation() dùng \n để tạo blank zone + >>> mới phía dưới
    public static void startAnimation(String text) {
        if (animRunning) return;

        synchronized (Terminal.class) {
            // log() đã in ">>> " → \n tạo blank animation zone, in >>> mới
            System.out.print("\n>>> ");
            System.out.flush();
        }

        animRunning = true;
        animThread = new Thread(() -> {
            int step = 0;
            while (animRunning) {
                synchronized (Terminal.class) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ESC).append("s");     // save cursor (đang ở >>> line)
                    sb.append(ESC).append("1A\r");  // lên 1 → animation zone
                    sb.append(ESC).append("2K");    // xóa

                    for (int i = 0; i < text.length(); i++) {
                        int idx = Math.floorMod(step - i, COLORS.length);
                        sb.append(ESC).append("38;5;").append(COLORS[idx]).append("m");
                        sb.append(text.charAt(i));
                    }

                    sb.append(ESC).append("0m");
                    sb.append(ESC).append("u");     // restore về >>> line
                    System.out.print(sb);
                    System.out.flush();
                    step++;
                }
                try { Thread.sleep(50); }
                catch (InterruptedException e) { break; }
            }
        }, "anim-thread");
        animThread.setDaemon(true);
        animThread.start();
    }

    // ── stopAnimation ─────────────────────────────────────────────────────────
    public static void stopAnimation() {
        animRunning = false;
        if (animThread != null) {
            animThread.interrupt();
            try { animThread.join(200); } catch (InterruptedException ignored) {}
            animThread = null;
        }
        synchronized (Terminal.class) {
            // Xóa animation line, giữ cursor tại >>> line
            System.out.print(ESC + "s" + ESC + "1A\r" + ESC + "2K" + ESC + "u");
            System.out.flush();
        }
    }

    public static void stopAnimation(boolean isAuto) {
        animRunning = false;
        if (animThread != null) {
            animThread.interrupt();
            try { animThread.join(200); } catch (InterruptedException ignored) {}
            animThread = null;
        }
        synchronized (Terminal.class) {
            // Xóa animation line, giữ cursor tại >>> line
            System.out.print("\u001B[1A\u001B[2K\r\u001B[2K");
            System.out.flush();
        }
    }

    // ── log ───────────────────────────────────────────────────────────────────
    public static synchronized void log(String message) {
        if (animRunning) {
            // Xóa >>> line → lên xóa animation line → in message → tạo lại cấu trúc
            System.out.print("\r" + ESC + "2K" + ESC + "1A\r" + ESC + "2K");
            System.out.println(message);
            System.out.print("\n>>> ");   // blank animation zone + input prompt
        } else {
            System.out.print("\r" + ESC + "2K");
            System.out.println(message);
        }
        System.out.flush();
    }
}
