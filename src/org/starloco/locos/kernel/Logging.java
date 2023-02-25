package org.starloco.locos.kernel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Logging {
    private static final Logging singleton = new Logging();
    private final ArrayList<Log> logs = new ArrayList<>();

    public static Logging getInstance() {
        return singleton;
    }

    public void initialize() {
        if (!new File("logs").exists())
            new File("logs/").mkdir();

        try {
            System.setOut(new PrintStream(System.out, true, "IBM850"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        if (!new File("logs/Error").exists())
            new File("logs/Error").mkdir();

        try {
            System.setErr(new PrintStream(Files.newOutputStream(Paths.get("logs/Error/" + new SimpleDateFormat("dd-MM-yyyy - HH-mm-ss", Locale.FRANCE).format(new Date()) + ".log"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        logs.stream().filter(log -> log.getBuffer() != null).forEach(log -> {
            try {
                log.getBuffer().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.logs.clear();
    }

    public void write(String name, String arg0) {
        for (Log log : logs) {
            if (log.getName().equals(name)) {
                try {
                    log.write(arg0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        final String date = Calendar.getInstance().get(Calendar.YEAR) + "-"
                + Calendar.getInstance().get(Calendar.MONTH) + "-"
                + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        try {
            this.logs.add(new Log(name, date));
            this.write(name, arg0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Log {
        private final String name;
        private final BufferedWriter buffer;

        public Log(String name, String date) throws IOException {
            this.name = name;

            if (!new File("logs/" + this.name).exists())
                new File("logs/" + this.name).mkdir();

            this.buffer = new BufferedWriter(new FileWriter("logs/" + this.name
                    + "/" + date, true));
            this.write("Starting logger..");
        }

        public void write(String arg0) throws IOException {
            final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            final int min = Calendar.getInstance().get(Calendar.MINUTE);
            final int sec = Calendar.getInstance().get(Calendar.SECOND);

            final String date = "[" + (hour < 10 ? "0" : "") + hour + " : "
                    + (min < 10 ? "0" : "") + min + " : "
                    + (sec < 10 ? "0" : "") + sec + "] : ";

            this.buffer.write(date + arg0);
            this.buffer.newLine();
            this.buffer.flush();
        }

        public String getName() {
            return name;
        }

        public BufferedWriter getBuffer() {
            return buffer;
        }
    }
}