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


    // Открываем окно
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(500, 500); // Canvas - это "полотно", которое располагается в открывшемся окне. На этом полотне, как раз, и рисует алгоритм
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        double x0 = 10, y0 = 150, x1 = 490, y1 = 200; // Задаем координаты начал и конца линни
        Color startColor = Color.RED; // Задаем первый цвет
        Color endColor = Color.BLUE; // Задаем второй цвет

        drawWuLine(pw, x0, y0, x1, y1, startColor, endColor);

        StackPane root = new StackPane(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Антиаллясинг - Способ визуального сглаживания, который встроен в алгоритм отрисовки By. Его суть заключается в том, чтобы не втупую отрисовать
    // наклонную линию попиксельно и получить "лесенку", а отрисовать "переходы", представляющие собой дополнительные пиксели в местах "лесенок" по сторонам от основных пикселе.
    // Чем дальше от основного пикселя находится этот антиаллясинговый пиксель, тем он более тусклый. Так создается эффект сглаживания.

    // Интерполяция, в этом алгоритме она Линейная -  это способ слияния несокльких цветов, для получения чего-то среднего. Проще говоря, это простой плавный переход между цветами.
    // Обязательно почитайте об этих алгоритмах, а лучше, попросите GPT подробно рассказать в чем заключается их суть и как они работают. Препод обязательно спросит.

    // Метод отрисовки By (Улучшенный алгоритм Брезенхерма) с антиаллясингом и интерполяцией
    private void drawWuLine(PixelWriter pw, double x0, double y0, double x1, double y1, Color startColor, Color endColor) {
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

        // Те самые ифки, которые позволяют использовать написанный алгоритм для всевозможных вариантов отрисовки, исключая неверную работу алгоритма.

        // Подробнее узнайте у GPT что именно они делают.
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

            // Рисуем попиксельно - пиксель основной линии + антиаллясинговый пиксель.
            if (steep) {
                pw.setColor(yint, xint, colorMain);
                pw.setColor(yint + 1, xint, colorSecondary);// Рисуем попиксельно - пиксель основной линии + антиаллясинговый пиксель.

            } else {
                pw.setColor(xint, yint, colorMain);
                pw.setColor(xint, yint + 1, colorSecondary);
            }

            intery = intery + gradient;
            System.out.println(intery);
        }
    }


    // Метод линейной интерполяции. ОБЯЗАТЕЛЬНО изучите его, препод спросит.
    private Color interpolateColor(Color startColor, Color endColor, double ratio, double brightness) {
        if (ratio < 0.0) ratio = 0.0;
        if (ratio > 1.0) ratio = 1.0;

        double red = startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed());
        double green = startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen());
        double blue = startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue());

        // Учитывая яркость, адаптируем альфа-канал
        return new Color(red, green, blue, brightness);
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
