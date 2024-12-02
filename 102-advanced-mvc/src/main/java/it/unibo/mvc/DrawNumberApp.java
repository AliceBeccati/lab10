package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final String RESOURCE_PATH = "config.yml";
    // Usa il class loader per ottenere il file come risorsa
    //InputStream inputStream = DrawNumberApp.class.getClassLoader().getResourceAsStream("config.yml");
    //private static final int MIN = 0;
    //private static final int MAX = 100;
    //private static final int ATTEMPTS = 10;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final String resourcesFileConfig, final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        readConfig(resourcesFileConfig);
        this.model = new DrawNumberImpl(MIN, MAX, ATTEMPTS);
    }

    
    @Override
    public void readConfig(final String file) {
        try (BufferedReader buffer = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                    System.out.println(buffer.readLine()); // NOPMD: required by the exercise
                    
                } catch (IOException e2) {
                    //JOptionPane.showMessageDialog(frame, e2, "Error", JOptionPane.ERROR_MESSAGE);
                    e2.printStackTrace(); // NOPMD: allowed as this is just an exercise
                }
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * @param args
     *            ignored
     * @throws FileNotFoundException 
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(RESOURCE_PATH, new DrawNumberViewImpl());
    }

}
