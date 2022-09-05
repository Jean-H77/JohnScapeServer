package com.ruse.util.dumper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Grabs HTML files from the disk or from a website.
 */
@Log
public class HtmlGrabber {

    public static final String WIKI_URL = "https://oldschool.runescape.wiki/w/";

    public static final String EXPORT_DIRECTORY = "./temp/htmlgrab/";

    private ExecutorService service = Executors.newFixedThreadPool(30);

    public HtmlGrabber(boolean logging) {
        if (!logging) {
           // log.setLevel(Level.SEVERE);
        }
    }

    /**
     * Wait for the grabs to finish.
     * @throws InterruptedException
     */
    public void finish() throws InterruptedException {
        service.shutdown();
        service.awaitTermination(1000, TimeUnit.HOURS);
    }

    /**
     * Submit an html grab to the grabber service.
     * @param grab the html to grab
     */
    public void submit(HtmlGrab grab) {
        service.submit(() -> {
            File directory = new File(getExportDirectory(grab));
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IllegalStateException("Directory could not be created.");
                }
            }

            File f = new File(directory.getPath() + "/" + grab.getName() + ".html");
            if (f.exists()) {
                //log.info(String.format("Html file exists on disk, skipping web grab %s", grab));
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    String html = reader.lines().collect(Collectors.joining());
                    if (grab.getHtmlConsumer() != null) {
                        grab.getHtmlConsumer().accept(html);
                    }
                } catch (Exception cause) {
                    cause.printStackTrace();
                }
            } else {
                //log.info(String.format("Grabbing html from web %s", grab));
                String url = getWikiUrl(grab.getName());
                try {
                    Pair<String, Integer> html = HtmlUtils.read(url);
                    if (html.getLeft() != null) {
                        if (html.getLeft().length() > 0) {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                            writer.write(html.getLeft());
                            writer.close();
                            if (grab.getHtmlConsumer() != null) {
                                grab.getHtmlConsumer().accept(html.getLeft());
                            }
                        }
                    } else if (html.getRight() == 429) {
                        // Rate limited
                        //log.info("Rate is limited, resting..");
                        Thread.sleep(5_000);
                        submit(grab);
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    //log.warning(String.format("File not found [%s]", url));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Gets the export directory of a grab.
     * @param grab the grab.
     * @return the export directory
     */
    private String getExportDirectory(HtmlGrab grab) {
        return EXPORT_DIRECTORY + grab.getGroup() + "/";
    }

    /**
     * Gets the wiki url of a thing.
     * @param name the thing's name
     */
    private String getWikiUrl(String name) {
        return WIKI_URL + name.toLowerCase().replaceAll(" ", "_");
    }

}
