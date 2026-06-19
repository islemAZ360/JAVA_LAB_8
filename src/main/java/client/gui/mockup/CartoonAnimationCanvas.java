package main.java.client.gui.mockup;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.ArcType;
import java.util.ArrayList;
import java.util.List;

/**
 * Cartoon Animation Canvas - Vẽ và animate 3 con vật: Gấu, Mèo, Vịt
 * Dựa trên cấu trúc ObjectVisualizationCanvas (AnimationTimer + Canvas + GraphicsContext)
 */
public class CartoonAnimationCanvas extends Canvas {

    // ---------- Thời gian & trạng thái animation ----------
    private double globalTime = 0; // tăng mỗi frame (~60fps)
    private AnimationTimer timer;

    // ---------- Danh sách các nhân vật ----------
    private final List<CartoonCharacter> characters = new ArrayList<>();

    // ---------- Mây trôi trên trời ----------
    private final List<Cloud> clouds = new ArrayList<>();

    // ---------- Màu sắc cố định ----------
    private static final Color SKY_TOP    = Color.web("#87CEEB");
    private static final Color SKY_BOTTOM = Color.web("#E0F6FF");
    private static final Color GRASS_TOP  = Color.web("#7CFC00");
    private static final Color GRASS_BOT  = Color.web("#228B22");
    private static final Color SUN_COLOR  = Color.web("#FFD700");

    public CartoonAnimationCanvas() {
        getStyleClass().add("cartoon-canvas");

        // Khởi tạo 3 nhân vật
        characters.add(new CartoonCharacter("Медведь", 0.25, 0.65, CharacterType.BEAR));
        characters.add(new CartoonCharacter("Кот", 0.50, 0.62, CharacterType.CAT));
        characters.add(new CartoonCharacter("Утка", 0.75, 0.68, CharacterType.DUCK));

        // Khởi tạo mây
        for (int i = 0; i < 5; i++) {
            clouds.add(new Cloud(Math.random() * 1.2 - 0.1, 0.05 + Math.random() * 0.15,
                    0.3 + Math.random() * 0.4));
        }

        this.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double w = newBounds.getWidth();
            double h = newBounds.getHeight();

            // Nếu kích thước thay đổi > 0 thì vẽ lại
            if (w > 0 && h > 0) {
                draw();
            }
        });

        startAnimation();
    }

    private void startAnimation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                globalTime += 0.016; // ~60fps
                draw();
            }
        };
        timer.start();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        double w = getWidth();
        return w == 0 ? 800 : w;
    }

    @Override
    public double prefHeight(double width) {
        double h = getHeight();
        return h == 0 ? 600 : h;
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        // Canvas cần được set lại width/height property để cập nhật bộ đệm vẽ (buffer)
        if (getWidth() != width || getHeight() != height) {
            setWidth(width);
            setHeight(height);
        }
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    // ============================================================
    //  HÀM VẼ CHÍNH
    // ============================================================
    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();

//        Debug
//        System.out.println("Canvas đang vẽ với kích thước: " + w + " x " + h);
        if (w == 0 || h == 0) return;

        gc.clearRect(0, 0, w, h);

        drawSky(gc, w, h);
        drawSun(gc, w, h);
        drawClouds(gc, w, h);
        drawGrass(gc, w, h);
        drawFlowers(gc, w, h);

        // Vẽ nhân vật (theo thứ tự y để có depth)
        characters.forEach(c -> drawCharacter(gc, c, w, h));

        drawTitle(gc, w);
    }

    // ============================================================
    //  BACKGROUND
    // ============================================================
    private void drawSky(GraphicsContext gc, double w, double h) {
        LinearGradient skyGrad = new LinearGradient(0, 0, 0, h * 0.7, false, CycleMethod.NO_CYCLE,
                new Stop(0, SKY_TOP), new Stop(1, SKY_BOTTOM));
        gc.setFill(skyGrad);
        gc.fillRect(0, 0, w, h * 0.7);
    }

    private void drawSun(GraphicsContext gc, double w, double h) {
        double cx = w * 0.85, cy = h * 0.15, r = Math.min(w, h) * 0.08;

        // Tia sáng xoay
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(globalTime * 20);
        gc.setStroke(Color.web("#FFE066"));
        gc.setLineWidth(3);
        for (int i = 0; i < 12; i++) {
            gc.rotate(30);
            gc.strokeLine(0, r * 1.3, 0, r * 2.0);
        }
        gc.restore();

        // Mặt trời với glow
        RadialGradient sunGrad = new RadialGradient(0, 0, cx, cy, r, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FFFACD")), new Stop(0.7, SUN_COLOR), new Stop(1, Color.web("#FFA500")));
        gc.setFill(sunGrad);
        gc.fillOval(cx - r, cy - r, r * 2, r * 2);
    }

    private void drawClouds(GraphicsContext gc, double w, double h) {
        for (Cloud cloud : clouds) {
            // Mây trôi từ trái sang phải
            cloud.x += 0.0003;
            if (cloud.x > 1.2) cloud.x = -0.2;

            double cx = w * cloud.x;
            double cy = h * cloud.y;
            double s  = w * cloud.size;

            gc.setFill(Color.web("#FFFFFF"));
            gc.fillOval(cx - s * 0.5, cy, s, s * 0.5);
            gc.fillOval(cx - s * 0.25, cy - s * 0.25, s * 0.8, s * 0.6);
            gc.fillOval(cx + s * 0.15, cy, s * 0.7, s * 0.45);
        }
    }

//    private void drawGrass(GraphicsContext gc, double w, double h) {
//        double grassY = h * 0.65;
//        LinearGradient grassGrad = new LinearGradient(0, grassY, 0, h, false, CycleMethod.NO_CYCLE,
//                new Stop(0, GRASS_TOP), new Stop(1, GRASS_BOT));
//        gc.setFill(grassGrad);
//
//        // Đường cỏ lượn sóng
//        gc.beginPath();
//        gc.moveTo(0, grassY);
//        for (double x = 0; x <= w; x += 20) {
//            double y = grassY + Math.sin(x * 0.05 + globalTime * 2) * 5;
//            gc.lineTo(x, y);
//        }
//        gc.lineTo(w + 4, h);
//        gc.lineTo(0, h);
//        gc.closePath();
//        gc.fill();
//    }

    private void drawGrass(GraphicsContext gc, double w, double h) {
        double grassY = h * 0.65;
        LinearGradient grassGrad = new LinearGradient(0, grassY, 0, h, false, CycleMethod.NO_CYCLE,
                new Stop(0, GRASS_TOP), new Stop(1, GRASS_BOT));
        gc.setFill(grassGrad);

        gc.beginPath();
        gc.moveTo(0, grassY);

        for (double x = -20; x <= w + 20; x += 20) {
            double y = grassY + Math.sin(x * 0.05 + globalTime * 2) * 5;
            gc.lineTo(x, y);
        }

        gc.lineTo(w + 2, h + 2);
        gc.lineTo(-2, h + 2);
        gc.closePath();
        gc.fill();
    }

    private void drawFlowers(GraphicsContext gc, double w, double h) {
        double grassY = h * 0.65;
        Color[] colors = {Color.web("#FF69B4"), Color.web("#FF4500"),
                Color.web("#FFD700"), Color.web("#FFFFFF"), Color.web("#DA70D6")};
        for (int i = 0; i < 15; i++) {
            double fx = (i * 73.7) % w;
            double fy = grassY + 20 + (i * 17) % (int)(h * 0.25);
            double s  = 4 + (i % 3) * 2;
            Color col = colors[i % colors.length];

            // Cánh hoa
            gc.setFill(col);
            for (int p = 0; p < 5; p++) {
                double angle = p * 72 + globalTime * 10;
                double px = fx + Math.cos(Math.toRadians(angle)) * s;
                double py = fy + Math.sin(Math.toRadians(angle)) * s;
                gc.fillOval(px - s * 0.6, py - s * 0.6, s * 1.2, s * 1.2);
            }
            // Nhụy
            gc.setFill(Color.web("#FFD700"));
            gc.fillOval(fx - s * 0.4, fy - s * 0.4, s * 0.8, s * 0.8);
        }
    }

    // ============================================================
    //  VẼ NHÂN VẬT
    // ============================================================
    private void drawCharacter(GraphicsContext gc, CartoonCharacter c, double w, double h) {
        double cx = w * c.baseX;
        double cy = h * c.baseY;
        double size = Math.min(w, h) * 0.15;

        // Animation riêng cho từng loại
        double bobY = 0, armAngle = 0, tailWag = 0;
        switch (c.type) {
            case BEAR:
                // Gấu đi lắc lư
                bobY = Math.sin(globalTime * 3) * 5;
                armAngle = Math.sin(globalTime * 4) * 20;
                break;
            case CAT:
                // Mèo nhảy
                bobY = Math.abs(Math.sin(globalTime * 2.5)) * -25;
                tailWag = Math.sin(globalTime * 5) * 30;
                break;
            case DUCK:
                // Vịt đi lắc lư
                bobY = Math.sin(globalTime * 4) * 3;
                armAngle = Math.sin(globalTime * 6) * 15;
                break;
        }

        cy += bobY;

        // Bóng dưới chân
        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillOval(cx - size * 0.4, cy + size * 0.45, size * 0.8, size * 0.15);

        // Vẽ theo loại
        switch (c.type) {
            case BEAR: drawBear(gc, cx, cy, size, armAngle); break;
            case CAT:  drawCat(gc, cx, cy, size, tailWag); break;
            case DUCK: drawDuck(gc, cx, cy, size, armAngle); break;
        }

        // Tên nhân vật
        gc.setFill(Color.web("#2C1810"));
        gc.setFont(Font.font("Arial", 14));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(c.name, cx, cy + size * 0.65);
    }

    // ---------- GẤU NÂU ----------
    private void drawBear(GraphicsContext gc, double cx, double cy, double s, double armAngle) {
        // Thân
        gc.setFill(Color.web("#8B4513"));
        gc.fillOval(cx - s * 0.35, cy - s * 0.1, s * 0.7, s * 0.55);

        // Bụng sáng
        gc.setFill(Color.web("#D2B48C"));
        gc.fillOval(cx - s * 0.2, cy, s * 0.4, s * 0.4);

        // Đầu
        gc.setFill(Color.web("#8B4513"));
        gc.fillOval(cx - s * 0.3, cy - s * 0.45, s * 0.6, s * 0.5);

        // Tai
        gc.fillOval(cx - s * 0.32, cy - s * 0.55, s * 0.2, s * 0.2);
        gc.fillOval(cx + s * 0.12, cy - s * 0.55, s * 0.2, s * 0.2);
        gc.setFill(Color.web("#D2B48C"));
        gc.fillOval(cx - s * 0.28, cy - s * 0.52, s * 0.12, s * 0.12);
        gc.fillOval(cx + s * 0.16, cy - s * 0.52, s * 0.12, s * 0.12);

        // Mắt
        gc.setFill(Color.web("#000000"));
        gc.fillOval(cx - s * 0.15, cy - s * 0.35, s * 0.08, s * 0.1);
        gc.fillOval(cx + s * 0.07, cy - s * 0.35, s * 0.08, s * 0.1);
        // Highlight mắt
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillOval(cx - s * 0.13, cy - s * 0.37, s * 0.03, s * 0.04);
        gc.fillOval(cx + s * 0.09, cy - s * 0.37, s * 0.03, s * 0.04);

        // Mũi
        gc.setFill(Color.web("#000000"));
        gc.fillOval(cx - s * 0.05, cy - s * 0.22, s * 0.1, s * 0.08);

        // Miệng cười
        gc.setStroke(Color.web("#000000"));
        gc.setLineWidth(2);
        gc.strokeArc(cx - s * 0.1, cy - s * 0.15, s * 0.2, s * 0.12, 0, 180, ArcType.OPEN);

        // Má hồng
        gc.setFill(Color.web("#FFB6C1"));
        gc.fillOval(cx - s * 0.25, cy - s * 0.25, s * 0.1, s * 0.06);
        gc.fillOval(cx + s * 0.15, cy - s * 0.25, s * 0.1, s * 0.06);

        // Tay trái (đứng yên)
        gc.setFill(Color.web("#8B4513"));
        gc.fillOval(cx - s * 0.45, cy - s * 0.05, s * 0.15, s * 0.3);

        // Tay phải (vẫy)
        gc.save();
        gc.translate(cx + s * 0.35, cy - s * 0.05);
        gc.rotate(armAngle);
        gc.fillOval(0, 0, s * 0.15, s * 0.3);
        gc.restore();

        // Chân
        gc.setFill(Color.web("#6B3410"));
        gc.fillOval(cx - s * 0.25, cy + s * 0.35, s * 0.2, s * 0.15);
        gc.fillOval(cx + s * 0.05, cy + s * 0.35, s * 0.2, s * 0.15);
    }

    // ---------- MÈO CAM ----------
    private void drawCat(GraphicsContext gc, double cx, double cy, double s, double tailWag) {
        // Đuôi (cong)
        gc.save();
        gc.translate(cx + s * 0.35, cy + s * 0.1);
        gc.rotate(tailWag);
        gc.setStroke(Color.web("#FF8C00"));
        gc.setLineWidth(s * 0.1);
        gc.strokeLine(0, 0, s * 0.3, -s * 0.4);
        gc.restore();

        // Thân
        gc.setFill(Color.web("#FF8C00"));
        gc.fillOval(cx - s * 0.3, cy - s * 0.05, s * 0.6, s * 0.5);

        // Sọc trên thân
        gc.setStroke(Color.web("#CC5500"));
        gc.setLineWidth(3);
        for (int i = 0; i < 3; i++) {
            gc.strokeLine(cx - s * 0.15 + i * s * 0.12, cy - s * 0.02,
                    cx - s * 0.1 + i * s * 0.12, cy + s * 0.2);
        }

        // Bụng trắng
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillOval(cx - s * 0.15, cy + s * 0.05, s * 0.3, s * 0.35);

        // Đầu
        gc.setFill(Color.web("#FF8C00"));
        gc.fillOval(cx - s * 0.28, cy - s * 0.4, s * 0.56, s * 0.45);

        // Tai nhọn
        gc.setFill(Color.web("#FF8C00"));
        gc.beginPath();
        gc.moveTo(cx - s * 0.28, cy - s * 0.3);
        gc.lineTo(cx - s * 0.35, cy - s * 0.55);
        gc.lineTo(cx - s * 0.1, cy - s * 0.4);
        gc.closePath();
        gc.fill();

        gc.beginPath();
        gc.moveTo(cx + s * 0.1, cy - s * 0.4);
        gc.lineTo(cx + s * 0.25, cy - s * 0.55);
        gc.lineTo(cx + s * 0.28, cy - s * 0.3);
        gc.closePath();
        gc.fill();

        // Tai trong (hồng)
        gc.setFill(Color.web("#FFB6C1"));
        gc.beginPath();
        gc.moveTo(cx - s * 0.24, cy - s * 0.32);
        gc.lineTo(cx - s * 0.3, cy - s * 0.5);
        gc.lineTo(cx - s * 0.14, cy - s * 0.38);
        gc.closePath();
        gc.fill();

        gc.beginPath();
        gc.moveTo(cx + s * 0.14, cy - s * 0.38);
        gc.lineTo(cx + s * 0.22, cy - s * 0.5);
        gc.lineTo(cx + s * 0.24, cy - s * 0.32);
        gc.closePath();
        gc.fill();

        // Mắt to xanh
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillOval(cx - s * 0.18, cy - s * 0.3, s * 0.12, s * 0.15);
        gc.fillOval(cx + s * 0.06, cy - s * 0.3, s * 0.12, s * 0.15);
        gc.setFill(Color.web("#00FF7F"));
        gc.fillOval(cx - s * 0.14, cy - s * 0.28, s * 0.08, s * 0.12);
        gc.fillOval(cx + s * 0.1, cy - s * 0.28, s * 0.08, s * 0.12);
        gc.setFill(Color.web("#000000"));
        gc.fillOval(cx - s * 0.12, cy - s * 0.26, s * 0.04, s * 0.08);
        gc.fillOval(cx + s * 0.12, cy - s * 0.26, s * 0.04, s * 0.08);

        // Mũi hồng
        gc.setFill(Color.web("#FF69B4"));
        gc.beginPath();
        gc.moveTo(cx - s * 0.02, cy - s * 0.15);
        gc.lineTo(cx + s * 0.05, cy - s * 0.15);
        gc.lineTo(cx + s * 0.015, cy - s * 0.1);
        gc.closePath();
        gc.fill();

        // Miệng
        gc.setStroke(Color.web("#000000"));
        gc.setLineWidth(1.5);
        gc.strokeLine(cx + s * 0.015, cy - s * 0.1, cx - s * 0.02, cy - s * 0.05);
        gc.strokeLine(cx + s * 0.015, cy - s * 0.1, cx + s * 0.05, cy - s * 0.05);

        // Ria mép
        gc.setStroke(Color.web("#000000"));
        gc.setLineWidth(1);
        gc.strokeLine(cx - s * 0.05, cy - s * 0.12, cx - s * 0.25, cy - s * 0.15);
        gc.strokeLine(cx - s * 0.05, cy - s * 0.08, cx - s * 0.25, cy - s * 0.05);
        gc.strokeLine(cx + s * 0.08, cy - s * 0.12, cx + s * 0.28, cy - s * 0.15);
        gc.strokeLine(cx + s * 0.08, cy - s * 0.08, cx + s * 0.28, cy - s * 0.05);

        // Chân
        gc.setFill(Color.web("#FF8C00"));
        gc.fillOval(cx - s * 0.22, cy + s * 0.35, s * 0.15, s * 0.12);
        gc.fillOval(cx + s * 0.07, cy + s * 0.35, s * 0.15, s * 0.12);
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillOval(cx - s * 0.22, cy + s * 0.4, s * 0.15, s * 0.07);
        gc.fillOval(cx + s * 0.07, cy + s * 0.4, s * 0.15, s * 0.07);
    }

    // ---------- VỊT VÀNG ----------
    private void drawDuck(GraphicsContext gc, double cx, double cy, double s, double armAngle) {
        // Thân
        gc.setFill(Color.web("#FFD700"));
        gc.fillOval(cx - s * 0.35, cy - s * 0.05, s * 0.7, s * 0.5);

        // Cánh
        gc.save();
        gc.translate(cx - s * 0.1, cy + s * 0.05);
        gc.rotate(armAngle);
        gc.setFill(Color.web("#FFC125"));
        gc.fillOval(0, 0, s * 0.3, s * 0.25);
        gc.restore();

        // Đầu
        gc.setFill(Color.web("#FFD700"));
        gc.fillOval(cx - s * 0.25, cy - s * 0.4, s * 0.45, s * 0.4);

        // Mắt
        gc.setFill(Color.web("#FFFFFF"));
        gc.fillOval(cx - s * 0.15, cy - s * 0.3, s * 0.1, s * 0.12);
        gc.fillOval(cx + s * 0.05, cy - s * 0.3, s * 0.1, s * 0.12);
        gc.setFill(Color.web("#000000"));
        gc.fillOval(cx - s * 0.12, cy - s * 0.28, s * 0.05, s * 0.08);
        gc.fillOval(cx + s * 0.08, cy - s * 0.28, s * 0.05, s * 0.08);

        // Má hồng
        gc.setFill(Color.web("#FFB6C1"));
        gc.fillOval(cx - s * 0.2, cy - s * 0.2, s * 0.08, s * 0.05);
        gc.fillOval(cx + s * 0.12, cy - s * 0.2, s * 0.08, s * 0.05);

        // Mỏ cam
        gc.setFill(Color.web("#FF8C00"));
        gc.beginPath();
        gc.moveTo(cx - s * 0.05, cy - s * 0.2);
        gc.lineTo(cx + s * 0.2, cy - s * 0.22);
        gc.lineTo(cx + s * 0.2, cy - s * 0.1);
        gc.lineTo(cx - s * 0.05, cy - s * 0.12);
        gc.closePath();
        gc.fill();
        gc.setStroke(Color.web("#CC5500"));
        gc.setLineWidth(1);
        gc.strokeLine(cx + s * 0.05, cy - s * 0.2, cx + s * 0.05, cy - s * 0.12);

        // Chân cam
        gc.setFill(Color.web("#FF8C00"));
        gc.fillOval(cx - s * 0.2, cy + s * 0.38, s * 0.15, s * 0.1);
        gc.fillOval(cx + s * 0.05, cy + s * 0.38, s * 0.15, s * 0.1);
        // Ngón chân
        gc.fillOval(cx - s * 0.25, cy + s * 0.42, s * 0.08, s * 0.05);
        gc.fillOval(cx - s * 0.15, cy + s * 0.42, s * 0.08, s * 0.05);
        gc.fillOval(cx, cy + s * 0.42, s * 0.08, s * 0.05);
        gc.fillOval(cx + s * 0.1, cy + s * 0.42, s * 0.08, s * 0.05);
    }

    // ---------- Tiêu đề ----------
    private void drawTitle(GraphicsContext gc, double w) {
        gc.setFill(Color.web("#FFFFFF"));
        gc.setFont(Font.font("Arial", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("🐻 Cartoon Animation 🐱", w / 2, 30);
    }

    // ============================================================
    //  DATA CLASSES
    // ============================================================
    private enum CharacterType { BEAR, CAT, DUCK }

    private static class CartoonCharacter {
        final String name;
        final double baseX, baseY; // vị trí tương đối (0-1)
        final CharacterType type;

        CartoonCharacter(String name, double x, double y, CharacterType type) {
            this.name = name;
            this.baseX = x;
            this.baseY = y;
            this.type = type;
        }
    }

    private static class Cloud {
        double x, y, size;
        Cloud(double x, double y, double size) {
            this.x = x; this.y = y; this.size = size;
        }
    }

}
