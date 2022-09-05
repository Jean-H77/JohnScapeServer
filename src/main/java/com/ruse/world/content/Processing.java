package com.ruse.world.content;

import com.ruse.model.entity.character.player.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Processing {

    private static final int INTERFACE_ID = 58470;
    private static final int PROGRESS_BAR_ID = 58478;
    private static final int PRODUCT_ID = 58557;
    private static final int REQUIRED_ITEMS_ID = 58558;

    private final String[] ALL_RECIPES = new String[] {
            "Cannon balls", "Dragon d'hide", "Super duper recipe", "Quadratic duper recipe"
    };

    private final Player p;
    private int level;
    private int exp;

    private List<String> learnedRecipes;
    private List<String> unlearnedRecipes;

    public Processing(Player p) {
        this.p = p;
        level = 1;
        exp = 0;

        unlearnedRecipes = new ArrayList<>();
        learnedRecipes = new ArrayList<>();

        learnedRecipes.add("DragonHide");
        learnedRecipes.add("CannonBalls");
    }

    public void addExperience(int amount) {
        exp += amount;
    }

    public void openInterface() {
        p.getPacketSender().sendInterface(INTERFACE_ID);
        p.getPacketSender().sendProgressBar((short) exp, (short) EXPERIENCE[level], PROGRESS_BAR_ID);

       updateRecipeStrings();

    }

    public void updateRecipeStrings() {
        List<String> temp = new ArrayList<>();

        for(int i = 0; i < ALL_RECIPES.length; i++) {
                if(!learnedRecipes.contains(ALL_RECIPES[i])) {
                    temp.add(ALL_RECIPES[i]);
                }
            }

            for (int learnedRecipeStartId = 58486; learnedRecipeStartId < 58506; learnedRecipeStartId++) {
                if ((learnedRecipeStartId - 58486) >= learnedRecipes.size()) {
                    p.getPacketSender().sendString(learnedRecipeStartId, "");
                } else {
                    p.getPacketSender().sendString(learnedRecipeStartId, learnedRecipes.get(learnedRecipeStartId - 58486));
                }
            }

        for (int unlearnedRecipeStartId = 58511; unlearnedRecipeStartId < 58531; unlearnedRecipeStartId++) {
          /*  if (temp.contains()) {
                p.getPacketSender().sendString(unlearnedRecipeStartId, "");
            } else {
                p.getPacketSender().sendString(unlearnedRecipeStartId, learnedRecipes.get(unlearnedRecipeStartId - 58511));
            }*/
        }
    }

    public void unlockRecipe(String recipe) {
     //   requiresUpdate = true;

    }

    public boolean clickRecipe(int id) {
        if(id >= -7050 && id <= -7031) {
            int clickedId = id + 7050;

        if((clickedId) >= learnedRecipes.size())  return true;

            p.getPacketSender().sendMessage("Clicked " + learnedRecipes.get(clickedId));

            return true;
        }
        return false;
    }

    static enum Recipe {
        CannonBall(1, "Cannonball", 20493, 3);

        Recipe(int levelReq, String name, int itemId, int expGain) {
            this.levelReq = levelReq;
            this.name = name;
            this.itemId = itemId;
            this.expGain = expGain;
        }

        private int levelReq;
        private String name;
        private int itemId;
        private int expGain;

        public static final EnumSet<Recipe> recipes = EnumSet.allOf(Recipe.class);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    private static final int[] EXPERIENCE = new int[] {
            75, 77, 80, 90, 150
    };
}
