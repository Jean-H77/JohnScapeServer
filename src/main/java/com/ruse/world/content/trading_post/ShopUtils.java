package com.ruse.world.content.trading_post;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.entity.character.player.Player;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.trading_post.buying_page.Buyer;
import com.ruse.world.content.trading_post.buying_page.BuyingPage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ShopUtils {

    public static List<Listing> marketListings = new ArrayList<>();
    public static List<HistoryItem> marketHistory = new ArrayList<>();
    public static List<Coffer> marketCoffers = new ArrayList<>();

    public static Queue<Buyer> buyingQueue = new ConcurrentLinkedQueue<>();
    public static Queue<Listing> cancelingQueue = new ConcurrentLinkedQueue<>();

    static {
      //  testData();
    }

    public static Optional<Coffer> getCoffer(String playerName) {
        return marketCoffers
                .stream()
                .filter(coffer -> coffer.getOwner().equals(playerName))
                .findFirst();
    }

    public static List<Listing> getListings(String playerName) {
        return marketListings
                .stream()
                .filter(listing -> listing.getSeller().equals(playerName))
                .collect(Collectors.toList());
    }

    public static boolean isSlotOccupied(List<Listing> listings, int slot) {
        return listings != null && listings
                .parallelStream()
                .anyMatch(listing -> listing.getSlot() == slot && listing.getAmount() > 0);
    }

    public static Optional<Listing> getListing(List<Listing> listings, int slot) {
        return listings
                .parallelStream()
                .filter(listing -> listing.getSlot() == slot)
                .findFirst();
    }

    public static List<HistoryItem> getItemHistory(int itemId) {
        return marketHistory
                .stream()
                .filter(history -> history.getItemId() == itemId)
                .limit(30)
                .collect(Collectors.toList());
    }

    public static int getNextAvailableSlot(List<Listing> listings) {
        int maxSlots = 20;
        for(int i = 0; i < maxSlots; i++) {
            if(!isSlotOccupied(listings, i)) {
                return i;
            }
        }
        return -1;
    }

    public static int getPriceAverage(int itemId) {
        List<HistoryItem> itemHistory = new ArrayList<>(getItemHistory(itemId));

        Collections.reverse(itemHistory);

        if(itemHistory.size() == 0) return 0;

        // get last 100 sold
        int sum = 0;
        for(int i = 0; i < 100; i++) {
            if(itemHistory.size() > i) {
                sum += itemHistory.get(i).getPurchasePrice();
            } else {
                break;
            }
        }

        return sum/itemHistory.size();
    }

    public static void processCancels() {
        if(cancelingQueue.isEmpty()) return;
        Listing listing = cancelingQueue.poll();
        if(listing == null) return;
        if(!marketListings.contains(listing)) return;

        Player player = World.getPlayerByName(listing.getSeller());

        if(player != null && player.isRegistered()) {

            if(ItemDefinition.forId(listing.getItemId()).isStackable() || ItemDefinition.forId(listing.getItemId()).isNoted()) {
                if(player.getInventory().isFull()) {
                    if(player.getInventory().contains(listing.getItemId())) {
                        player.getInventory().add(listing.getItemId(), listing.getAmount());
                        marketListings.remove(listing);
                        player.getPlayerShopManager().showInterface();
                    } else {
                        player.getPacketSender().sendMessage("You do not have enough inventory spaces!");
                    }
                } else {
                    player.getInventory().add(listing.getItemId(), listing.getAmount());
                    marketListings.remove(listing);
                    player.getPlayerShopManager().showInterface();
                }

            } else {

                int freeSlots = player.getInventory().getFreeSlots();

                int listingAmount = listing.getAmount();

                int amountToRemove = 0;

                if(!player.getInventory().isFull()) {

                    amountToRemove = Math.min(listingAmount, freeSlots);

                    listing.setAmount(listingAmount - amountToRemove);

                    player.getInventory().add(listing.getItemId(), amountToRemove);

                    if(listing.getAmount() == 0) {
                        marketListings.remove(listing);
                    }

                    player.getPlayerShopManager().showInterface();

                } else {
                    player.getPacketSender().sendMessage("You do not have enough inventory spaces!");
                }
            }
        }
    }

    public static void processBuys() {
        if(buyingQueue.isEmpty()) return;
        Buyer buyer = buyingQueue.poll();
        if(!marketListings.contains(buyer.getListing())) return;

        Player buyingPlayer = buyer.getBuyer();
        int amountToBuy = buyer.getAmountToBuy();
        Listing listing = buyer.getListing();
        PlayerShopManager playerShopManager = buyingPlayer.getPlayerShopManager();
        Coffer coffer;

        if(buyingPlayer.isRegistered()) {

            if(buyingPlayer.getInventory().isFull() || (buyingPlayer.getInventory().getFreeSlots() < amountToBuy && !ItemDefinition.forId(listing.getItemId()).isStackable())
                || ItemDefinition.forId(listing.getItemId()).isStackable() && !buyingPlayer.getInventory().contains(listing.getItemId()) && buyingPlayer.getInventory().isFull()) {

                buyingPlayer.getPacketSender().sendMessage("You do not have enough inventory spaces!");

            } else {

                long total = (long) buyer.getAmountToBuy() * listing.getPrice();

                if(buyingPlayer.getInventory().getAmount(BuyingPage.CURRENCY_ID) >= total) {

                   Optional<Coffer> cofferOptional = getCoffer(listing.getSeller());

                    if(cofferOptional.isPresent()) {
                        coffer = cofferOptional.get();

                    } else {
                        coffer = new Coffer(listing.getSeller());
                        marketCoffers.add(coffer);
                    }

                    if(coffer.getAmount() + total > Integer.MAX_VALUE) {
                        buyingPlayer.getPacketSender().sendMessage("@red@This players coffer is currently full and cannot accept new buy orders.");

                    } else {

                        buyingPlayer.getInventory().add(listing.getItemId(), amountToBuy);
                        buyingPlayer.getInventory().delete(BuyingPage.CURRENCY_ID, (int) total);

                        listing.setAmount(listing.getAmount() - amountToBuy);

                        buyingPlayer.getPacketSender().sendMessage("@red@You have bought x" + amountToBuy + " of " + ItemDefinition.forId(listing.getItemId()).getName() + ".");

                        if (listing.getAmount() == 0) {
                            marketListings.remove(listing);
                        }

                        marketHistory.add(new HistoryItem(listing.getItemId(), amountToBuy, buyingPlayer.getUsername(), listing.getSeller(), listing.getPrice(), System.nanoTime()));
                        coffer.addAmount((int) total);
                    }

                    playerShopManager.getBuyingPage().getNewMarketListings();
                    playerShopManager.getBuyingPage().getFilteredSearch();

                } else {

                    buyingPlayer.getPacketSender().sendMessage("You need @red@" + Misc.currency(((long) buyer.getAmountToBuy() * listing.getPrice()), true) + "@bla@ to buy this item.");
                }
            }

            if(playerShopManager.getBuyingPage() != null) {
                playerShopManager.getBuyingPage().setSelectedListing(null);
            }
        }
    }

    public static void processQueues() {
        processCancels();
        processBuys();
    }

    public static String calculateAge(long timestamp) {

        String result = null;

        long elapsed = (System.nanoTime()  - timestamp);
        int day = (int) TimeUnit.NANOSECONDS.toDays(elapsed);
        long hours = TimeUnit.NANOSECONDS.toHours(elapsed) - (day * 24L);
        long minute = TimeUnit.NANOSECONDS.toMinutes(elapsed) - (TimeUnit.NANOSECONDS.toHours(elapsed)* 60);
        long second = TimeUnit.NANOSECONDS.toSeconds(elapsed) - (TimeUnit.NANOSECONDS.toMinutes(elapsed) *60);

        if(day > 0) {

            result = day + "d " + hours + " ago";

        } else {

            if(hours > 0) {

                result = hours + "h " + minute + "m ago";

            } else {

                if(minute > 0) {

                    result = minute + "m " + second + "s ago";
                } else {

                    result = second + "s ago";

                }
            }
        }
        return result;
    }

    public static void testData() {
        List<Integer> itemIds = new ArrayList<>();
        itemIds.add(4151);
        itemIds.add(4714);
        itemIds.add(4712);
        itemIds.add(4710);
        itemIds.add(4718);
        itemIds.add(11732);
        itemIds.add(6585);
        itemIds.add(4722);
        itemIds.add(4724);
        itemIds.add(4726);
        itemIds.add(4728);
        itemIds.add(4730);

        List<String> names = new ArrayList<>();
        names.add("John");
        names.add("Bob");
        names.add("Lob");
        names.add("Cob");
        names.add("Sob");
        names.add("Nob");
        names.add("Tob");
        names.add("CandleMan");
        names.add("StripeDude");

        for(int i = 0; i < 35; i++) {
              marketHistory.add(new HistoryItem(4151, 5, "John", "John1", 50_000, System.nanoTime() + Misc.rand(500_000_000)));
        }

        for(int i = 0; i < 500_000; i++) {
          marketListings.add(new Listing(0, Misc.randomElement(itemIds), Misc.rand(100_000_000), 69, Misc.randomElement(names), System.nanoTime()));
        }
    }

    public static void loadAll() {
        loadShops();
        loadCoffers();
        loadItemHistory();
    }

    public static void saveAll() {
        saveShops();
        saveCoffers();
        saveItemHistory();
    }

    public static void loadShops() {
        Path path = Paths.get("./data/saves/marketboard/shops.json");
        File file = path.toFile();

        createFileAndDirIfNotExists(file);

        if(file.length() == 0) return;

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder()
                    .create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            Listing[] temp = builder.fromJson(reader.get("MarketListings").getAsJsonArray(), Listing[].class);

            marketListings.addAll(Arrays.asList(temp));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveShops() {

        Path path = Paths.get("./data/saves/marketboard/shops.json");

        File file = path.toFile();
        file.getParentFile().setWritable(true);

        createFileAndDirIfNotExists(file);

        try (FileWriter writer = new FileWriter(file)) {
            JsonObject object = new JsonObject();
            Gson builder = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            object.add("MarketListings",  builder.toJsonTree(marketListings));

            writer.write(builder.toJson(object));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCoffers() {
        Path path = Paths.get("./data/saves/marketboard/coffers.json");

        File file = path.toFile();
        file.getParentFile().setWritable(true);

        createFileAndDirIfNotExists(file);

        try (FileWriter writer = new FileWriter(file)) {
            JsonObject object = new JsonObject();
            Gson builder = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            object.add("MarketCoffers",  builder.toJsonTree(marketCoffers));

            writer.write(builder.toJson(object));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCoffers() {
        Path path = Paths.get("./data/saves/marketboard/coffers.json");
        File file = path.toFile();

        createFileAndDirIfNotExists(file);

        if(file.length() == 0) return;

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder()
                    .create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            Coffer[] temp = builder.fromJson(reader.get("MarketCoffers").getAsJsonArray(), Coffer[].class);

            marketCoffers.addAll(Arrays.asList(temp));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadItemHistory() {
        Path path = Paths.get("./data/saves/marketboard/itemHistory.json");
        File file = path.toFile();

        createFileAndDirIfNotExists(file);

        if(file.length() == 0) return;

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder()
                    .create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            HistoryItem[] temp = builder.fromJson(reader.get("MarketHistory").getAsJsonArray(), HistoryItem[].class);

            marketHistory.addAll(Arrays.asList(temp));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveItemHistory() {
        Path path = Paths.get("./data/saves/marketboard/itemHistory.json");

        File file = path.toFile();
        file.getParentFile().setWritable(true);
        createFileAndDirIfNotExists(file);

        try (FileWriter writer = new FileWriter(file)) {
            JsonObject object = new JsonObject();
            Gson builder = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            object.add("MarketHistory",  builder.toJsonTree(marketHistory));

            writer.write(builder.toJson(object));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createFileAndDirIfNotExists(File file) {
        if (!file.getParentFile().exists() || !file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (SecurityException e) {
                System.out.println("Unable to create directory");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ShopUtils() {

    }
}
