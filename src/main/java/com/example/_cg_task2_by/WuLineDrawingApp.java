package com.example._cg_task2_by;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WuLineDrawingApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(500, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        double x0 = 50, y0 = 50, x1 = 450, y1 = 100;
        Color startColor = Color.RED;
        Color endColor = Color.BLUE;

        drawWuLine(pw, x0, y0, x1, y1, startColor, endColor);

        StackPane root = new StackPane(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawWuLine(PixelWriter pw, double x0, double y0, double x1, double y1, Color startColor, Color endColor) {
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

        if (steep) {
            double temp = x0;
            x0 = y0;
            y0 = temp;
            temp = x1;
            x1 = y1;
            y1 = temp;
        }

        if (x0 > x1) {
            double temp = x0;
            x0 = x1;
            x1 = temp;
            temp = y0;
            y0 = y1;
            y1 = temp;
        }

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dy / dx;
        double intery = y0 + gradient; // first y-intersection for the main loop

        for (double x = x0; x <= x1; x++) {
            int xint = (int)x;
            int yint = (int)intery;

            double brightnessMain = rfpart(intery);
            double brightnessSecondary = fpart(intery);

            Color colorMain = interpolateColor(startColor, endColor, (x - x0) / dx, brightnessMain);
            Color colorSecondary = interpolateColor(startColor, endColor, (x - x0) / dx, brightnessSecondary);

            if (steep) {
                pw.setColor(yint, xint, colorMain);
                pw.setColor(yint + 1, xint, colorSecondary);
            } else {
                pw.setColor(xint, yint, colorMain);
                pw.setColor(xint, yint + 1, colorSecondary);
            }


            intery = intery + gradient;
        }
    }

    private Color interpolateColor(Color startColor, Color endColor, double ratio, double brightness) {
        Color interpolatedColor = startColor.interpolate(endColor, ratio);
        return Color.color(
                interpolatedColor.getRed(),
                interpolatedColor.getGreen(),
                interpolatedColor.getBlue(),
                brightness * interpolatedColor.getOpacity());
    }

    private double fpart(double x) {
        return x - Math.floor(x);
    }

    private double rfpart(double x) {
        return 1 - fpart(x);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
