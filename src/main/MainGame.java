/*
 * MIT License
 *
 * Copyright (c) 2023 Lukas Gilch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package main;

import gameworld.ENT_Control;
import gameworld.NPC_Control;
import gameworld.PRJ_Control;
import gameworld.entities.ENTITY;
import gameworld.entities.damage.dmg_numbers.DamageNumber;
import gameworld.entities.damage.effects.TileBasedEffects;
import gameworld.entities.loadinghelper.EntityPreloader;
import gameworld.entities.loadinghelper.ProjectilePreloader;
import gameworld.entities.monsters.ENT_SkeletonWarrior;
import gameworld.entities.multiplayer.ENT_Player2;
import gameworld.entities.props.ENT_TargetDummy;
import gameworld.player.PROJECTILE;
import gameworld.player.Player;
import gameworld.player.PlayerPrompts;
import gameworld.quest.dialog.DialogStorage;
import gameworld.world.WorldController;
import gameworld.world.effects.DayNightCycle;
import gameworld.world.generation.RandomMap;
import gameworld.world.maps.Map;
import gameworld.world.objects.DROP;
import gameworld.world.objects.DropManager;
import gameworld.world.objects.drops.DRP_DroppedItem;
import gameworld.world.objects.items.ITEM;
import input.InputHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import main.system.CollisionChecker;
import main.system.Storage;
import main.system.database.SQLite;
import main.system.enums.State;
import main.system.enums.Zone;
import main.system.pathfinding.PathFinder;
import main.system.rendering.WorldEnhancements;
import main.system.rendering.WorldRender;
import main.system.savegame.LoadGameState;
import main.system.sound.Sound;
import main.system.statistics.GameStatistics;
import main.system.ui.Effects;
import main.system.ui.FonT;
import main.system.ui.UI;
import main.system.ui.inventory.UI_InventoryPanel;
import main.system.ui.maps.GameMap;
import main.system.ui.maps.MiniMap;
import main.system.ui.questpanel.UI_QuestPanel;
import main.system.ui.skillbar.SKILL;
import main.system.ui.skillbar.UI_SkillBar;
import main.system.ui.skillbar.skills.SKL_EnergySphere;
import main.system.ui.skillbar.skills.SKL_PyroBlast;
import main.system.ui.skillbar.skills.SKL_RegenAura;
import main.system.ui.skillbar.skills.SKL_SolarFlare;
import main.system.ui.skillbar.skills.SKL_VoidEruption;
import main.system.ui.skillbar.skills.SKL_VoidField;
import main.system.ui.skillpanel.UI_SkillPanel;
import main.system.ui.statusmessages.StatusMessage;
import main.system.ui.talentpanel.UI_TalentPanel;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class MainGame {

    private static boolean CHEATS = false;
    public static int SCREEN_WIDTH = 1_920;
    public static int SCREEN_HEIGHT = 1_080;
    public final int HALF_WIDTH;
    public final int HALF_HEIGHT;
    public final List<ENTITY> ENTITIES = Collections.synchronizedList(new ArrayList<>());
    //---------VARIABLES----------
    public final List<PROJECTILE> PROJECTILES = Collections.synchronizedList(new ArrayList<>());
    public final List<ENTITY> PROXIMITY_ENTITIES = Collections.synchronizedList(new ArrayList<>());
    public final List<DamageNumber> damageNumbers = Collections.synchronizedList(new ArrayList<>());

    public static Random random;
    //ITEMS
    public final List<DROP> WORLD_DROPS = Collections.synchronizedList(new ArrayList<>());
    public final ArrayList<ITEM> AMULET = new ArrayList<>();
    public final ArrayList<ITEM> BOOTS = new ArrayList<>();
    public final ArrayList<ITEM> CHEST = new ArrayList<>();
    public final ArrayList<ITEM> HEAD = new ArrayList<>();
    public final ArrayList<ITEM> OFFHAND = new ArrayList<>();
    public final ArrayList<ITEM> ONEHAND = new ArrayList<>();
    public final ArrayList<ITEM> PANTS = new ArrayList<>();
    public final ArrayList<ITEM> RELICS = new ArrayList<>();
    public final ArrayList<ITEM> RINGS = new ArrayList<>();
    public final ArrayList<ITEM> TWOHANDS = new ArrayList<>();
    public final ArrayList<ITEM> BAGS = new ArrayList<>();
    public final ArrayList<ITEM> MISC = new ArrayList<>();


    public final int tileSize = 48;
    public final UI ui = new UI(this);


    //---------Input-----------
    public final GraphicsContext gc;
    //----------SCREEN SETTINGS---------------

    public String player2Information = "";
    public InputHandler inputH;
    public int playerX, playerY;
    public CollisionChecker collisionChecker;
    public WorldController wControl;
    public WorldRender wRender;
    public ENT_Player2 ENTPlayer2;
    public Player player;
    public State gameState;
    public PathFinder pathF;

    private Storage imageSto;
    public SQLite sqLite;
    private final Image vignette = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/vignette.png")));
    public boolean client = false, showBag, showChar, showTalents, loadingScreen, showMap;
    public PRJ_Control prj_control;
    public UI_InventoryPanel inventP;
    public UI_TalentPanel talentP;
    public NPC_Control npcControl;
    public GameMap gameMap;
    private final DayNightCycle cycle = new DayNightCycle(this);
    public UI_SkillBar sBar;
    public UI_QuestPanel qPanel;
    public boolean credits;
    public boolean drawVideoSettings, drawAudioSettings;
    public boolean drawKeybindings;
    public boolean drawGameplay;
    public DropManager dropManager;
    public boolean drawCodex;
    public WorldEnhancements wAnim;
    public LoadGameState loadGameState;
    public UI_SkillPanel skillPanel;
    public boolean showJournal;
    public int cutSceneX, cutSceneY;

    //---------System---------
    private MiniMap miniM;
    //private Multiplayer multiplayer;
    public TileBasedEffects tileBase;
    public ENT_Control ent_control;
    public Sound sound;
    public PlayerPrompts playerPrompts;
    private static int WORLD_SIZE;
    public boolean showAbilities;
    public RandomMap generator;
    public SecureRandom secureRandom;
    public StatusMessage statusMessage;
    public GameStatistics gameStatistics;

    /**
     * Main class for the game logic and center point for information
     */
    public MainGame(int width, int height, GraphicsContext gc) {
        this.gc = gc;
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        HALF_WIDTH = SCREEN_WIDTH / 2;
        HALF_HEIGHT = SCREEN_HEIGHT / 2;
        gameState = State.LOADING_SCREEN;
        cycle.start();
    }


    void run() {
        loadGame(gc);
    }

    /**
     * Starts the 4 game threads
     */
    private void startThreads() {
        Thread renderHelper = new Thread(() -> {
            long firstTimeGate1;
            long lastTime1 = System.currentTimeMillis();
            float difference = 0;
            float difference1 = 0;
            float difference2 = 0;
            float fastRenderCounter = 1_000 / 360.0f;
            float fastRenderCounter2 = 1_000 / 60.0f;
            try {
                while (true) {
                    firstTimeGate1 = System.currentTimeMillis();
                    difference += (firstTimeGate1 - lastTime1) / fastRenderCounter;
                    difference1 += (firstTimeGate1 - lastTime1) / 1_000.0f;
                    difference2 += (firstTimeGate1 - lastTime1) / fastRenderCounter2;
                    lastTime1 = firstTimeGate1;
                    if (difference >= 1) {
                        inventP.interactWithWindows();
                        difference = 0;
                    }
                    if (difference2 >= 1) {
                        if (gameState == State.PLAY || gameState == State.CUT_SCENE) {
                            if (showMap) {
                                gameMap.dragMap();
                                gameMap.getImage();
                            }
                            getPlayerTile();
                            tileBase.update();
                            player.pickupDroppedItem();
                            player.checkPlayerIsMoving();

                            wAnim.animateTiles();
                            wControl.uncoverWorldMap();
                            wControl.update();
                            qPanel.update();
                        }
                        difference2 = 0;
                    }
                    if (difference1 >= 0.5) {
                        synchronized (PROXIMITY_ENTITIES) {
                            proximitySorterENTITIES();
                        }
                        gameStatistics.updateGameStatistics();
                        tileBase.getNearbyTiles();
                        sound.update();
                        difference1 = 0;
                    }
                    Thread.sleep(2);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        renderHelper.start();
        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.008), ae -> drawGame(gc));
        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();
        Thread playerThread = new Thread(() -> {
            long firstTimeGate1;
            float logicCounter = 1_000 / 70.0f;
            long lastTime1 = System.currentTimeMillis();
            float difference = 0;
            try {
                while (true) {
                    firstTimeGate1 = System.currentTimeMillis();
                    difference += (firstTimeGate1 - lastTime1) / logicCounter;
                    lastTime1 = firstTimeGate1;
                    if (difference >= 1) {
                        if (gameState == State.PLAY) {
                            player.update();
                            sBar.update();
                        }
                        difference = 0;
                    }
                    Thread.sleep(4);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        playerThread.start();
        Thread updateThread = new Thread(() -> {
            long lastTime1 = System.currentTimeMillis();
            float logicCounter = 1_000.0f / 70.0f;
            float difference = 0;
            long firstTimeGate1;
            try {
                while (true) {
                    firstTimeGate1 = System.currentTimeMillis();
                    difference += (firstTimeGate1 - lastTime1) / logicCounter;
                    lastTime1 = firstTimeGate1;
                    if (difference >= 1) {
                        if (gameState == State.PLAY || gameState == State.OPTION || gameState == State.CUT_SCENE) {
                            prj_control.update();
                            ent_control.update();
                            npcControl.update();
                        }
                        difference = 0;
                    }
                    Thread.sleep(4);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        updateThread.start();
    }

    /**
     * The main painting method to draw the screen
     */

    private void drawGame(GraphicsContext gc) {
        /*
        ColorAdjust dark = new ColorAdjust();
            dark.setBrightness(-0.6);
            gc.setEffect(dark);
         */
        //Debug
        //long drawStart = System.nanoTime();
        //RENDER START
        if (gameState == State.PLAY || gameState == State.OPTION) {
            wRender.draw(gc);
            drawDroppedItems(gc);
            prj_control.draw(gc);
            ent_control.draw(gc);
            npcControl.draw(gc);
            //ENTPlayer2.draw(gc);
            player.draw(gc);
            drawDamageNumber(gc);
            wRender.drawSecondLayer(gc);
            playerPrompts.draw(gc);
            wAnim.drawLayerOneTwo(gc);
            gc.drawImage(vignette, 0, 0);
            npcControl.drawDialogs(gc);
            if (player.drawDialog) {
                player.dialog.drawDialog(gc);
            }
            qPanel.draw(gc);
            miniM.draw(gc);
            statusMessage.draw(gc);
            sBar.draw(gc);
            ui.draw(gc);
            if (showAbilities) {
                skillPanel.drawSkillPanel(gc);
                skillPanel.dragAndDropSkillBar(gc);
            }
            if (showMap) {
                gameMap.draw(gc);
            }
            if (showBag) {
                inventP.drawBagWindow(gc);
                inventP.drawBagTooltip(gc);
                inventP.drawDragAndDrop(gc);
            }
            if (showChar) {
                inventP.drawCharacterWindow(gc);
                inventP.drawCharTooltip(gc);
                inventP.drawDragAndDrop(gc);
            }
            if (showTalents) {
                talentP.drawTalentWindow(gc);
            }
        } else if (gameState == State.CUT_SCENE) {
            wRender.draw(gc);
            drawDroppedItems(gc);
            prj_control.draw(gc);
            ent_control.draw(gc);
            //ENTPlayer2.draw(gc);
            player.drawCutscene(gc, cutSceneX, cutSceneY);
            drawDamageNumber(gc);
            wRender.drawSecondLayer(gc);
            npcControl.draw(gc);
            playerPrompts.draw(gc);
            wAnim.drawLayerOneTwo(gc);
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, 1920, 200);
            gc.fillRect(0, 880, 1920, 200);
        } else if (gameState == State.TITLE || gameState == State.TITLE_OPTION || gameState == State.LOADING_SCREEN) {
            ui.draw(gc);
        }
        //RENDER END
        if (inputH.debugFps) {
            gc.setFont(FonT.minecraftBold30);
            gc.setFill(Color.BLACK);
            gc.setFont(ui.maruMonica30);
            //gc.fillText(("Draw Time" + (System.nanoTime() - drawStart)), 500, 600);
            gc.fillText((int) (Player.worldX + 24) / 48 + " " + (int) (Player.worldY + 24) / 48, 500, 700);
            gc.fillText(String.valueOf(TileBasedEffects.activeTile), 500, 750);
            if (CHEATS) {
                for (SKILL skill : sBar.skills) {
                    skill.actualCoolDown = skill.totalCoolDown;
                }
                player.setMana(player.maxMana);
            }
        }
    }


    /**
     * Loads the game and updates loading screen
     *
     * @param gc gc
     */
    private void loadGame(GraphicsContext gc) {
        gameStatistics = new GameStatistics(this);
        sqLite = new SQLite(this);
        sqLite.getConnection();
        dropManager = new DropManager(this);
        FonT.minecraftBold30 = Font.loadFont(FonT.class.getResourceAsStream("/Fonts/MinecraftBold-nMK1.otf"), 30);
        loadGameState = new LoadGameState(this);
        generator = new RandomMap(this);
        ProjectilePreloader.load();
        EntityPreloader.load();
        tileBase = new TileBasedEffects(this);
        qPanel = new UI_QuestPanel(this);
        sBar = new UI_SkillBar(this);
        wAnim = new WorldEnhancements(this);
        skillPanel = new UI_SkillPanel(this);
        ui.updateLoadingScreen(12, gc);
        secureRandom = new SecureRandom();
        long seed = secureRandom.nextLong();
        random = new Random(seed);
        statusMessage = new StatusMessage(this);
        // 0 %
        playerPrompts = new PlayerPrompts(this);
        sound = new Sound(this);
        sound.loadSounds();
        inventP = new UI_InventoryPanel(this);
        wControl = new WorldController(this);

        //12 %
        ui.updateLoadingScreen(24, gc);
        // ob_control = new OBJ_Control(this);
        wRender = new WorldRender(this);
        wControl.loadWorldData();
        //  wControl.makeOverWorldQuadrants();
        talentP = new UI_TalentPanel(this);
        DialogStorage.loadDialogs();

        //24%
        ui.updateLoadingScreen(36, gc);
        miniM = new MiniMap(this);
        ent_control = new ENT_Control(this);
        collisionChecker = new CollisionChecker(this);

        //36%
        ui.updateLoadingScreen(48, gc);
        imageSto = new Storage();
        imageSto.loadImages();
        prj_control = new PRJ_Control(this);
        player = new Player(this);

        //48%
        ui.updateLoadingScreen(58, gc);
        sqLite.readAllGameData();
        talentP.assignDescriptions();
        //60%
        ui.updateLoadingScreen(70, gc);
        ENTPlayer2 = new ENT_Player2(this);
        talentP.createTalentNodes();
        //72%
        ui.updateLoadingScreen(82, gc);
        pathF = new PathFinder(this);
        pathF.instantiateNodes();
        Effects.loadEffects();

        //84%
        ui.updateLoadingScreen(94, gc);
        //multiplayer = new Multiplayer(this, ENTPlayer2);
        npcControl = new NPC_Control(this);
        gameMap = new GameMap(this);
        FonT.loadFonts();
        //100%
        gameState = State.TITLE;
        //sqLite.resetGame();
        loadGameState.loadGame();
        ui.updateLoadingScreen(100, gc);
        countItems();
        gameMap.getImage();
        sound.setVolumeMusic(ui.musicSlider);
        sound.setVolumeAmbience(ui.ambientSlider);
        sound.setVolumeEffects(ui.effectsSlider);
        startThreads();
        sound.INTRO.setCycleCount(MediaPlayer.INDEFINITE);
        sound.INTRO.play();
    }

    private void debug() {
        CHEATS = true;
        player.level = 60;
        player.manaBarrier = 2000;
        player.weaponDamageUpper = 200;
        player.weaponDamageLower = 200;
        player.maxMana = 2000;
        player.intellect = 2000;
        for (int i = 0; i < 1; i++) {
            //ENTITIES.add(new ENT_SkeletonWarrior(this, 58 * 48, 44 * 48, 100, Zone.Tutorial));
        }
        player.coins = 2_000;
        //sound.setVolumeAmbience(0);
        //inventP.char_Slots[5].item = DRP_DroppedItem.cloneItemWithLevelQuality(TWOHANDS.get(2), 100, 60);
        sBar.skills[5] = new SKL_EnergySphere(this);
        sBar.skills[4] = new SKL_SolarFlare(this);
        sBar.skills[2] = new SKL_RegenAura(this);
        sBar.skills[0] = new SKL_PyroBlast(this);
        sBar.skills[1] = new SKL_VoidField(this);
        sBar.skills[3] = new SKL_VoidEruption(this);
        for (SKILL skill : skillPanel.allSkills) {
            skillPanel.addSKill(skill);
        }
        for (Map map : wControl.MAPS) {
            for (int i = 0; i < map.mapSize.x; i++) {
                for (int j = 0; j < map.mapSize.x; j++) {
                    //map.mapCover[i][j] = 1;
                }
            }
        }
        inventP.bag_Slots.get(2).item = DRP_DroppedItem.cloneItemWithLevelQuality(CHEST.get(8), 100, 60);
        inventP.char_Slots[8].item = DRP_DroppedItem.cloneItemWithLevelQuality(TWOHANDS.get(2), 100, 60);
        player.updateEquippedItems();
        player.maxMana = 2000;
        for (ITEM item : CHEST) {
            for (Float f : item.effects) {
                if (f != 0) {
                    System.out.println("ring" + item.name);
                }
            }
        }
        // inventP.bag_Slots.get(4).item = DRP_DroppedItem.cloneItemWithLevelQuality(BAGS.get(1), 100, 60);
        //ENTITIES.add(new ENT_Shooter(this, 35 * 48, 19 * 48, 111));
        // wControl.loadMap(Zone.Woodland_Edge, 74, 84);
        wControl.loadMap(Zone.Goblin_Cave, 51, 63);
        ENTITIES.add(new ENT_SkeletonWarrior(this, 160 * 48, 160 * 48, 2, Zone.The_Grove));
        for (int i = 0; i < 50; i++) {
            //  ENTITIES.add(new ENT_SkeletonSpearman(this, 56 * 48, 24 * 48, 30, Zone.Hillcrest));
        }
        for (int i = 0; i < 20; i++) {
            ITEM item = dropManager.getGuaranteedRandomItem(i);
            while (!(item.type == '2' || item.type == 'O' || item.type == 'W')) {
                item = dropManager.getGuaranteedRandomItem(5);
            }
            WORLD_DROPS.add(new DRP_DroppedItem((56 + i) * 48, 23 * 48, item, Zone.Hillcrest));
        }
        for (int i = 0; i < 2; i++) {
            //  dropI.dropEpicItem(this, (10 - i) * 48, 85 * 48, 1, Zone.Hillcrest);
        }
        // ENTITIES.add(new BOS_Slime(this, 490 * 48, 490 * 48, 1, 140));
        //testRoom();
    }

    private void testRoom() {
        wControl.loadMap(Zone.TestRoom, 25, 25);
        ENTITIES.add(new ENT_TargetDummy(this, 25, 25, 100000, Zone.TestRoom, false));
        ENTITIES.add(new ENT_TargetDummy(this, 10, 22, 100000, Zone.TestRoom, true));
    }

    /**
     * Prints out the total item count across all categories
     */
    private void countItems() {
        System.out.println(-12 + MISC.size() + BAGS.size() + AMULET.size() + BOOTS.size() + CHEST.size() + HEAD.size() + OFFHAND.size() + ONEHAND.size() + PANTS.size() + RELICS.size() + RINGS.size() + TWOHANDS.size() + " total Items!");
    }

    /**
     * Filters the bigger Entities array to only have objects that are less than 2000 worldPixels away
     * Only used for the gameMap
     *
     * @see GameMap
     */
    private void proximitySorterENTITIES() {
        synchronized (ENTITIES) {
            PROXIMITY_ENTITIES.clear();
            for (ENTITY entity : ENTITIES) {
                if (Math.abs(entity.worldX - Player.worldX) + Math.abs(entity.worldY - Player.worldY) < 2_000) {
                    PROXIMITY_ENTITIES.add(entity);
                }
            }
        }
    }

    public void getPlayerTile() {
        playerX = (int) ((Player.worldX + 24) / 48);
        playerY = (int) ((Player.worldY + 24) / 48);
    }

    private void drawDamageNumber(GraphicsContext gc) {
        synchronized (damageNumbers) {
            var iterator = damageNumbers.iterator();
            gc.setEffect(ui.shadow);
            while (iterator.hasNext()) {
                var dmgN = iterator.next();
                dmgN.draw(gc);
                if (dmgN.offSetY <= -30) {
                    iterator.remove();
                }
            }
            gc.setEffect(null);
        }
    }

    private void drawDroppedItems(GraphicsContext gc) {
        synchronized (WORLD_DROPS) {
            Iterator<DROP> iterator = WORLD_DROPS.iterator();
            while (iterator.hasNext()) {
                DROP drop = iterator.next();
                if (drop.zone == WorldController.currentWorld && Math.abs(drop.worldPos.x - Player.worldX) + Math.abs(drop.worldPos.y - Player.worldY) < 1_500) {
                    drop.draw(gc);
                } else if (WORLD_DROPS.size() > 100) {
                    iterator.remove();
                }
            }
        }
    }
}

/*





 */
