package main.system;


import javafx.scene.image.Image;

import java.awt.Rectangle;
import java.util.Objects;

public class Storage {
    public final Rectangle box_primaryFire = new Rectangle(0, 0, 16, 16);
    public final Rectangle box_secondaryFire = new Rectangle(0, 0, 32, 32);
    public static Image Lightning1, Lightning2, Lightning3, Lightning4, Lightning5, Lightning6, Lightning7, Lightning8, Lightning9, Lightning10;
    public static Image secondaryFire1, secondaryFire2, secondaryFire3, secondaryFire4, secondaryFire5, secondaryFire6;
    public Image primaryFire1;
    public static Image gruntImage1;
    public Image shooterImage1;
    public static Image coin1, coin2, coin3, coin4;

    public Storage() {
    }

    public void loadImages() {
        loadAbilityImages();
        loadEntityImages();
        loadDropImages();
    }

    private void loadDropImages() {
        loadCoinImages();
    }

    private void loadEntityImages() {
        getGruntImages();
        getShooterImages();
    }

    private void loadCoinImages() {
        coin1 = setup("/items/drops/coin1.png");
        coin2 = setup("/items/drops/coin2.png");
        coin3 = setup("/items/drops/coin3.png");
        coin4 = setup("/items/drops/coin4.png");
    }

    private void loadAbilityImages() {
        getLightningImages();
        getEnergySphereImages();
        getPrimaryFireImages();
    }

    private void getGruntImages() {
        gruntImage1 = setup("/Entitys/enemies/enemy01.png");
    }

    private void getShooterImages() {
        shooterImage1 = setup("/Entitys/enemies/shooter/Old_man.png");
    }

    private void getEnergySphereImages() {
        secondaryFire1 = setupEnergySphere("SecondaryFire01.png");
        secondaryFire2 = setupEnergySphere("SecondaryFire02.png");
        secondaryFire3 = setupEnergySphere("SecondaryFire03.png");
        secondaryFire4 = setupEnergySphere("SecondaryFire04.png");
        secondaryFire5 = setupEnergySphere("SecondaryFire05.png");
        secondaryFire6 = setupEnergySphere("SecondaryFire06.png");
    }

    private void getLightningImages() {
        Lightning1 = setupLightning("lightn01.png");
        Lightning2 = setupLightning("lightn02.png");
        Lightning3 = setupLightning("lightn03.png");
        Lightning4 = setupLightning("lightn04.png");
        Lightning5 = setupLightning("lightn05.png");
        Lightning6 = setupLightning("lightn06.png");
        Lightning7 = setupLightning("lightn07.png");
        Lightning8 = setupLightning("lightn08.png");
        Lightning9 = setupLightning("lightn09.png");
        Lightning10 = setupLightning("lightn10.png");
    }

    private void getPrimaryFireImages() {
        primaryFire1 = setup("/projectiles/PrimaryFire/PrimaryFire01.png");
    }

    private Image setup(String imagePath) {
        return new Image((Objects.requireNonNull(getClass().getResourceAsStream("/resources" + imagePath))));
    }

    private Image setupLightning(String imagePath) {
        return new Image((Objects.requireNonNull(getClass().getResourceAsStream("/projectiles/Lightning/" + imagePath))));
    }

    private Image setupEnergySphere(String imagePath) {
        return new Image((Objects.requireNonNull(getClass().getResourceAsStream("/projectiles/EnergySphere/" + imagePath))));
    }
}
