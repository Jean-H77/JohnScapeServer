package com.ruse.world.content.tradingpost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradingPost {

    private static final Logger logger = Logger.getLogger("TradingPost");

    private static final String savePath = "./data/saves/tradingpost/";
    private static final Map<String, Coffer> coffers = Collections.synchronizedMap(new HashMap<>());
    private static final List<Offer> offers = new ArrayList<>();
    private static final HashMap<String, List<ItemHistory>> pastTransactions = new HashMap<>();

    public static void getOrCreateNewCoffer(final String owner,
                                            final Consumer<Coffer> cofferConsumer) {
        getCoffer(owner).thenAccept(coffer -> {
            if(coffer == null) {
                coffer = new Coffer();
                saveCoffer(owner, coffer);
                coffers.put(owner, coffer);
            }

            cofferConsumer.accept(coffer);
        });
    }

    public static CompletableFuture<Coffer> getCoffer(final String owner) {
        Coffer cachedCoffer = coffers.get(owner);
        if(cachedCoffer != null) {
            return CompletableFuture.completedFuture(cachedCoffer);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {

                File file = new File(savePath+"coffers/"+owner);

                if(!file.exists()) {
                    return null;
                }

                Gson gson = new Gson();

                try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                    return gson.fromJson(br, Coffer.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (final Exception e) {
                logger.log(Level.WARNING, "Unable to get players coffer " + owner, e);
            }

            return null;
        });
    }

    public static void saveCoffer(final String owner, final Coffer coffer) {
        Thread.startVirtualThread(() -> {
            try {
                Path path = Path.of(savePath+"/coffers");
                File directory = new File(path.toString());
                String cofferPath = savePath+"coffers/"+owner+".json";

                if(!directory.exists()) {
                    Files.createDirectories(path);
                }

                File file = new File(cofferPath);

                if(!file.exists()) {
                    Files.createFile(Path.of(cofferPath));
                }

                try(Writer writer = new PrintWriter(file.getPath())) {
                    Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
                    gsonBuilder.toJson(coffer, writer);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Unable to save coffer of " + owner , e);
            }
        });
    }

    public static void saveOffers(String owner, List<Offer> offers) {
        Thread.startVirtualThread(() -> {
            try {
                Path path = Path.of(savePath+"/offers");
                File directory = new File(path.toString());
                String cofferPath = savePath+"offers/"+owner+".json";

                if(!directory.exists()) {
                    Files.createDirectories(path);
                }

                File file = new File(cofferPath);

                if(!file.exists()) {
                    Files.createFile(Path.of(cofferPath));
                }

                try(Writer writer = new PrintWriter(file.getPath())) {
                    Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
                    gsonBuilder.toJson(offers, writer);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Unable to save offers of " + owner , e);
            }
        });
    }

    public static void loadOffers() {
        Path path = Path.of(savePath+"offers/");
        File directory = new File(path.toString());

        try {
            if(!directory.exists()) {
                logger.log(Level.INFO, "Creating new offers directory");
                Files.createDirectories(path);
                return;
            }

            File[] files = new File(savePath+"offers/").listFiles();

            assert files != null;

            for(File f : files) {
                if(f == null) continue;

                try(BufferedReader br = new BufferedReader(new FileReader(f))) {

                    Gson gson = new Gson();
                    List<Offer> temp = List.of(gson.fromJson(br, Offer[].class));
                    offers.addAll(temp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to load trading post offers",e);
        }

    }
}
