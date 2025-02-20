package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window {

    private JFrame frame;
    private Canvas canvas;

    private String title;
    private int width;
    private int height;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        createWindow();
    }

    private void createWindow() {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(true);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                canvas.requestFocus();
                e.consume();
            }
        });

        frame.addWindowFocusListener(new WindowAdapter() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                canvas.requestFocus();
            }
        });

        frame.add(canvas);
        frame.pack();

        canvas.requestFocus();
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
