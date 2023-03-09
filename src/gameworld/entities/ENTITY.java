package gameworld.entities;

import gameworld.entities.companion.ENT_Owly;
import gameworld.entities.damage.DamageType;
import gameworld.entities.damage.dmg_numbers.DamageNumber;
import gameworld.entities.damage.effects.Effect;
import gameworld.entities.damage.effects.EffectType;
import gameworld.entities.damage.effects.arraybased.Effect_ArrayBased;
import gameworld.entities.loadinghelper.ResourceLoaderEntity;
import gameworld.entities.monsters.ENT_SkeletonArcher;
import gameworld.entities.monsters.ENT_SkeletonWarrior;
import gameworld.player.Player;
import gameworld.quest.Dialog;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.MainGame;
import main.system.Storage;
import main.system.enums.Zone;
import main.system.ui.Colors;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Main inheritable class for all entities
 *
 * @see ENT_SkeletonWarrior
 * @see ENT_Owly
 * @see ENT_SkeletonArcher
 */
abstract public class ENTITY {
    public boolean AfterAnimationDead;
    public boolean stunned;
    public Dialog dialog;
    protected float health;
    public final ArrayList<Effect> BuffsDebuffEffects = new ArrayList<>();
    public final float[] effects = new float[Player.effectsSizeTotal];
    public ResourceLoaderEntity animation;
    public boolean collidingWithPlayer;
    public Zone zone;
    public int spriteCounter;
    protected int goalCol;
    protected int goalRow;
    protected float amountedDamageSinceLastDamageNumber;
    protected long timeSinceLastDamageNumber;
    protected long timeSinceLastDamageSound;
    public Image entityImage1;
    protected Image entityImage2;
    protected Image entityImage3;
    protected Image entityImage4;
    protected Image entityImage5;
    protected Image entityImage6;
    protected Image enemyImage;
    public int entityWidth;
    public int entityHeight;
    protected int searchTicks;
    public boolean onPath;
    public float worldY;
    public float worldX;
    public int screenX;
    public int screenY;
    public final Point activeTile = new Point();
    public int maxHealth;
    public float movementSpeed;
    public int level;

    public int hitDelay;
    public boolean collisionUp, collisionDown;
    public boolean collisionLeft;
    public boolean collisionRight;
    public boolean dead;
    public MainGame mg;
    public String direction;
    public Rectangle collisionBox;
    public boolean hpBarOn;
    public int hpBarCounter;
    public int nextCol1;
    public int nextRow1;
    public int nextCol2;
    public int nextRow2;
    public int nextCol3;
    public int nextRow3;
    public int nextCol4;
    public int nextRow4;

    /**
     * Returns boolean value of: Is the player further than 650 worldPixels away
     *
     * @return the player being away more than 650 worldPixels
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean playerTooFarAbsolute() {
        return Math.abs(worldX - Player.worldX) >= 650 || Math.abs(worldY - Player.worldY) >= 650;
    }


    private void decideMovement(int nextX, int nextY) {
        float enLeftX = worldX;
        float enRightX = (worldX + entityWidth);
        float enTopY = worldY;
        float enBottomY = (worldY + entityHeight);
        collisionRight = false;
        collisionLeft = false;
        collisionDown = false;
        collisionUp = false;
        mg.collisionChecker.checkEntityAgainstTile(this);
        if (enLeftX < nextX && !collisionRight) {
            worldX += movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enLeftX > nextX && !collisionLeft) {
            worldX -= movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enTopY < nextY && !collisionDown) {
            worldY += movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enTopY > nextY && !collisionUp) {
            worldY -= movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enRightX > nextX) {
            worldX -= movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enRightX < nextX) {
            worldX += movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enBottomY > nextY) {
            worldX -= movementSpeed * (1 - (effects[45] / 100.0f));
        } else if (enBottomY < nextX) {
            worldX += movementSpeed * (1 - (effects[45] / 100.0f));
        }
    }


    /**
     * Searches a path from active tile to goalTile.
     * Limited to a distance of 16 in both directions
     *
     * @param goalCol     the column of the goal tile
     * @param goalRow     the row of the goal tile
     * @param maxDistance max distance to set nodes in both directions
     */
    protected void searchPath(int goalCol, int goalRow, int maxDistance) {
        int startCol = activeTile.x;
        int startRow = activeTile.y;
        mg.pathF.setNodes(startCol, startRow, goalCol, goalRow, maxDistance);
        if (!stunned && !(startCol == goalCol && startRow == goalRow) && mg.pathF.search()) {
            int nextX = mg.pathF.pathList.get(0).col * 48;
            int nextY = mg.pathF.pathList.get(0).row * 48;
            decideMovement(nextX, nextY);
        } else {
            onPath = false;
        }
    }


    /**
     * Same as searchPath(int, int, int) but not limited in distance
     *
     * @param goalCol     the column of the goal tile
     * @param goalRow     the row of the goal tile
     * @param maxDistance the maximum distance that is searched in every direction
     * @see ENTITY#searchPath(int, int, int)
     */
    public void searchPathUncapped(int goalCol, int goalRow, int maxDistance) {
        int startCol = activeTile.x;
        int startRow = activeTile.y;
        mg.pathF.setNodes(startCol, startRow, goalCol, goalRow, maxDistance);
        if (startCol == goalCol && startRow == goalRow) {
            onPath = false;
        } else if (mg.pathF.searchUncapped()) {
            int nextX = mg.pathF.pathList.get(0).col * 48;
            int nextY = mg.pathF.pathList.get(0).row * 48;
            decideMovement(nextX, nextY);
            nextCol1 = mg.pathF.pathList.get(0).col;
            nextRow1 = mg.pathF.pathList.get(0).row;
            if (mg.pathF.pathList.size() >= 2) {
                nextCol2 = mg.pathF.pathList.get(1).col;
                nextRow2 = mg.pathF.pathList.get(1).row;
            }
            if (mg.pathF.pathList.size() >= 3) {
                nextCol3 = mg.pathF.pathList.get(2).col;
                nextRow3 = mg.pathF.pathList.get(2).row;
            }
            if (mg.pathF.pathList.size() >= 4) {
                nextCol4 = mg.pathF.pathList.get(3).col;
                nextRow4 = mg.pathF.pathList.get(3).row;
            }
        }
    }

    /**
     * Tracks the next 4 tiles that have been saved through searchPath() without computing anymore paths
     */
    protected void trackPath() {
        int nextX = nextCol1 * 48;
        int nextY = nextRow1 * 48;
        if (activeTile.x == nextCol1 && activeTile.y == nextRow1) {
            nextX = nextCol2 * mg.tileSize;
            nextY = nextRow2 * mg.tileSize;
        } else if (activeTile.x == nextCol2 && activeTile.y == nextRow2) {
            nextX = nextCol3 * mg.tileSize;
            nextY = nextRow3 * mg.tileSize;
        } else if (activeTile.x == nextCol3 && activeTile.y == nextRow3) {
            nextX = nextCol4 * mg.tileSize + 24;
            nextY = nextRow4 * mg.tileSize + 24;
        }
        if (activeTile.x == mg.playerX && activeTile.y == mg.playerY) {
            onPath = false;
        } else if (nextX != 0 && nextY != 0) {
            decideMovement(nextX, nextY);
        }
    }


    public void playGetHitSound() {

    }


    public void moveToTileSuperVised(int x, int y) {
        searchPathUncapped(x, y, 100);
    }

    protected void standardSeekPlayer() {
        onPath = mg.pathF.search();
        if (onPath && searchTicks >= Math.random() * 15) {
            getNearestPlayer();
            searchPath(goalCol, goalRow, 16);
            searchTicks = 0;
        } else if (onPath) {
            trackPath();
        }
    }

    protected void getNearestPlayer() {
        /*  if (Math.abs(Player.worldX - this.worldX + Player.worldY - this.worldY) < Math.abs(mg.ENTPlayer2.worldX - this.worldX + mg.ENTPlayer2.worldY - this.worldY)) {

         */
        this.goalCol = mg.playerX;
        this.goalRow = mg.playerY;
     /*
        } else {


            this.goalCol = (int) ((mg.ENTPlayer2.worldX + mg.player.collisionBox.x) / mg.tileSize);
            this.goalRow = (int) ((mg.ENTPlayer2.worldY + mg.player.collisionBox.y) / mg.tileSize);

        }
      */
    }

    protected void getNearestPlayerMultiplayer() {
        if (Math.abs(Player.worldX - this.worldX + Player.worldY - this.worldY) < Math.abs(mg.ENTPlayer2.worldX - this.worldX + mg.ENTPlayer2.worldY - this.worldY)) {
            this.goalCol = mg.playerX;
            this.goalRow = mg.playerY;
        } else {
            this.goalCol = (int) ((mg.ENTPlayer2.worldX + mg.player.collisionBox.x) / mg.tileSize);
            this.goalRow = (int) ((mg.ENTPlayer2.worldY + mg.player.collisionBox.y) / mg.tileSize);
        }
    }


    protected void tickEffects() {
        Iterator<Effect> iter = BuffsDebuffEffects.iterator();
        while (iter.hasNext()) {
            Effect effect = iter.next();
            effect.tick(this);
            if (effect.rest_duration <= 0) {
                iter.remove();
                if (effect instanceof Effect_ArrayBased) {
                    updateEquippedItems();
                }
            }
        }
    }


    public float getHealth() {
        return this.health;
    }

    abstract public void draw(GraphicsContext gc);

    public void update() {
        tickEffects();
        activeTile.x = (int) ((worldX + 24) / 48);
        activeTile.y = (int) ((worldY + 24) / 48);
        if (health <= 0) {
            dead = true;
            playGetHitSound();
        }
        if (hpBarCounter >= 600) {
            hpBarOn = false;
            hpBarCounter = 0;
        } else if (hpBarOn) {
            hpBarCounter++;
        }
    }

    public void setHealth(float value) {
        health = value;
    }

    public void updateEquippedItems() {
        for (int i = 0; i < Player.effectsSizeTotal; i++) {
            effects[i] = 0;
        }
        for (Effect effect : BuffsDebuffEffects) {
            effects[effect.indexAffected] += effect.amount;
        }
    }

    public void getDamageFromPlayer(float damagePercent, DamageType type) {
        float flat_damage = MainGame.random.nextFloat(mg.player.weaponDamageLower, mg.player.weaponDamageUpper) * (damagePercent / 100.0f);
        float[] effectsDouble = mg.player.effects;
        switch (type) {
            case DarkMagic -> flat_damage += (flat_damage / 100.0f) * effectsDouble[2];
            case Fire -> flat_damage += (flat_damage / 100.0f) * effectsDouble[19];
            case Arcane -> flat_damage += (flat_damage / 100.0f) * effectsDouble[1];
            case Poison -> flat_damage += (flat_damage / 100.0f) * effectsDouble[18];
            case Ice -> flat_damage += (flat_damage / 100.0f) * effectsDouble[28];
        }
        switch (type) {
            case DarkMagic -> flat_damage += (flat_damage / 100.0f) * effects[42];
            case Fire -> flat_damage += (flat_damage / 100.0f) * effects[44];
            case Arcane -> flat_damage += (flat_damage / 100.0f) * effects[41];
            case Poison -> flat_damage += (flat_damage / 100.0f) * effects[43];
            case Ice -> flat_damage += (flat_damage / 100.0f) * effects[40];
        }
        amountedDamageSinceLastDamageNumber += flat_damage;
        if (MainGame.random.nextInt(0, 101) <= effectsDouble[21]) {
            flat_damage += (flat_damage / 100.0f) * effectsDouble[22];
            mg.damageNumbers.add(new DamageNumber((int) flat_damage, type, this, true));
            timeSinceLastDamageNumber = System.currentTimeMillis();
            amountedDamageSinceLastDamageNumber = 0;
        }
        if (System.currentTimeMillis() - timeSinceLastDamageNumber >= 100 && amountedDamageSinceLastDamageNumber > 0.99) {
            mg.damageNumbers.add(new DamageNumber((int) amountedDamageSinceLastDamageNumber, type, this, false));
            amountedDamageSinceLastDamageNumber = 0;
            timeSinceLastDamageNumber = System.currentTimeMillis();
        }
        this.health -= flat_damage;
    }

    public void getDamage(float flat_damage) {
        this.health -= flat_damage;
    }

    public void drawBuffsAndDeBuffs(GraphicsContext gc) {
        if (BuffsDebuffEffects.size() > 0) {
            int x = screenX - 35;
            int y = screenY - 40;
            for (Effect effect : BuffsDebuffEffects) {
                if (effect.effectType == EffectType.BUFF) {
                    gc.setStroke(Colors.map_green);
                    gc.strokeRoundRect(x, y, 16, 16, 6, 6);
                    effect.drawCooldownSmall(gc, x, y);
                    x += 20;
                    //effect.draw(gc, x += 20, y);
                }
            }
            x += 32;
            for (Effect effect : BuffsDebuffEffects) {
                if (effect.effectType == EffectType.DEBUFF) {
                    //gc.setStroke(Colors.Red);
                    //gc.strokeRoundRect(x, y, 16, 16, 6, 6);
                    gc.drawImage(Storage.effectImages[effect.indexAffected], x, y, 16, 16);
                    effect.drawCooldownSmall(gc, x, y);
                    x += 20;
                }
            }
        }
    }

    public void addEffects(Effect[] effects) {
        boolean found;
        for (Effect effect : effects) {
            if (effect != null) {
                found = false;
                for (Effect entityEffect : BuffsDebuffEffects) {
                    if (effect.indexAffected == entityEffect.indexAffected && effect.sourceProjectile == entityEffect.sourceProjectile) {
                        entityEffect.rest_duration = effect.full_duration;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    BuffsDebuffEffects.add(effect.clone());
                }
            }
        }
    }
}

