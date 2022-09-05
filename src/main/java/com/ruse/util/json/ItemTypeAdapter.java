package com.ruse.util.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.ruse.model.Item;

public class ItemTypeAdapter extends TypeAdapter<Item> {


    @Override
    public void write(JsonWriter writer, Item item) throws IOException {
        writer.beginObject();
        writer.name("id");
        writer.value(item.getId());
        writer.name("amount");
        writer.value(item.getAmount());

        writer.name("int-attributes");
        writer.beginArray();
        for (Map.Entry<String, Integer> entry : item.getAttributes().getInts().entrySet()) {
            writer.beginObject();
            writer.name("key");
            writer.value(entry.getKey());
            writer.name("value");
            writer.value(entry.getValue());
            writer.endObject();
        }
        writer.endArray();

        writer.endObject();
    }

    @Override
    public Item read(JsonReader reader) throws IOException {
        Map<String, Integer> ints = new HashMap<>();
        int itemId = -1;
        int amount = 0;

        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            switch (reader.nextName()) {
                case "id":
                    itemId = reader.nextInt();
                    break;
                case "amount":
                    amount = reader.nextInt();
                    break;
                case "int-attributes":
                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY) {
                        reader.beginObject();
                        reader.nextName();
                        String key = reader.nextString();
                        reader.nextName();
                        int value = reader.nextInt();
                        reader.endObject();
                        ints.put(key, value);
                    }
                    reader.endArray();
                    break;
            }
        }

        reader.endObject();
        Item item = new Item(itemId, amount);
        ints.forEach((key, value) -> item.getAttributes().setInt(key, value));
        return item;
    }
}
