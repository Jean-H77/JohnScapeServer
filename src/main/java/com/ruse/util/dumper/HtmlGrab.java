package com.ruse.util.dumper;

import java.util.function.Consumer;

public class HtmlGrab {

    private final String name;
    private final String group;
    private final Consumer<String> htmlConsumer;

    /**
     * Create a new HtmlGrab.
     * @param name
     *          a name for the grabbed html
     * @param group
     *          a group/set name for the html
     * @param htmlConsumer
     *          a consumer for the html once it is grabbed
     */
    public HtmlGrab(String name, String group, Consumer<String> htmlConsumer) {
        this.name = name;
        this.group = group;
        this.htmlConsumer = htmlConsumer;
    }

    @Override
    public String toString() {
        return "HtmlGrab{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", htmlConsumer=" + htmlConsumer +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public Consumer<String> getHtmlConsumer() {
        return htmlConsumer;
    }

}
