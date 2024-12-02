package it.unibo.mvc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    // Usa il class loader per ottenere il file come risorsa
    //InputStream inputStream = DrawNumberApp.class.getClassLoader().getResourceAsStream("config.yml")
    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final String file, final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        //lettura da file
        final Configuration.Builder config = new Configuration.Builder();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(file)))) {
            String[] s;
            s = buffer.readLine().split(": "); // NOPMD: required by the exercise
            config.setMin(Integer.parseInt(s[1]));
            s = buffer.readLine().split(": "); // NOPMD: required by the exercise
            config.setMax(Integer.parseInt(s[1])); 
            s = buffer.readLine().split(": "); // NOPMD: required by the exercise
            config.setAttempts(Integer.parseInt(s[1]));
        } catch (IOException e) {
            System.out.println((e.getMessage()));
        }catch (NumberFormatException e) {
            System.out.println((e.getMessage()));
        }
        this.model = new DrawNumberImpl(config.build());

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
        new DrawNumberApp("config.yml", new DrawNumberViewImpl());
    }

}
