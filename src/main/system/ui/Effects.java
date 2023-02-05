package main.system.ui;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class Effects {
    public static DropShadow blueGlow;
    public static DropShadow poisonGlow;
    public static DropShadow rarity_1glow;
    public static DropShadow rarity_2glow;
    public static DropShadow rarity_3glow;
    public static DropShadow rarity_4glow;
    public static DropShadow rarity_5glow;


    public static void loadEffects() {
        //blue glow
        blueGlow = new DropShadow();
        blueGlow.setRadius(30);
        blueGlow.setOffsetX(0);
        blueGlow.setOffsetY(0);
        blueGlow.setColor(Color.rgb(105, 182, 238, 0.8));
        blueGlow.setBlurType(BlurType.THREE_PASS_BOX);
        blueGlow.setSpread(0.4);


        //rarity 1 glow
        rarity_1glow = new DropShadow();
        rarity_1glow.setSpread(50);
        rarity_1glow.setRadius(45);
        rarity_1glow.setOffsetX(0);
        rarity_1glow.setOffsetY(0);
        rarity_1glow.setColor(Colors.NormalGrey);
        rarity_1glow.setBlurType(BlurType.THREE_PASS_BOX);
        rarity_1glow.setSpread(0.35);

        //rarity 2 glow
        rarity_2glow = new DropShadow();
        rarity_2glow.setSpread(50);
        rarity_2glow.setRadius(45);
        rarity_2glow.setOffsetX(0);
        rarity_2glow.setOffsetY(0);
        rarity_2glow.setColor(Colors.rareColor);
        rarity_2glow.setBlurType(BlurType.THREE_PASS_BOX);
        rarity_2glow.setSpread(0.40);

        //rarity 3 glow
        rarity_3glow = new DropShadow();
        rarity_3glow.setSpread(50);
        rarity_3glow.setRadius(45);
        rarity_3glow.setOffsetX(0);
        rarity_3glow.setOffsetY(0);
        rarity_3glow.setColor(Colors.epicColor);
        rarity_3glow.setBlurType(BlurType.THREE_PASS_BOX);
        rarity_3glow.setSpread(0.45);

        //rarity 4 glow
        rarity_4glow = new DropShadow();
        rarity_4glow.setSpread(50);
        rarity_4glow.setRadius(50);
        rarity_4glow.setOffsetX(0);
        rarity_4glow.setOffsetY(0);
        rarity_4glow.setColor(Colors.legendaryColor);
        rarity_4glow.setBlurType(BlurType.THREE_PASS_BOX);
        rarity_4glow.setSpread(0.55);

        // rarity 5 glow
        rarity_5glow = new DropShadow();
        rarity_5glow.setSpread(50);
        rarity_5glow.setRadius(50);
        rarity_5glow.setOffsetX(0);
        rarity_5glow.setOffsetY(0);
        rarity_5glow.setColor(Colors.setItem);
        rarity_5glow.setBlurType(BlurType.THREE_PASS_BOX);
        rarity_5glow.setSpread(0.55);
    }
}
