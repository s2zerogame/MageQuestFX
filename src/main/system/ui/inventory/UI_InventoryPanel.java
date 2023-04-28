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

package main.system.ui.inventory;

import gameworld.entities.loadinghelper.ResourceLoaderEntity;
import gameworld.entities.npcs.trader.MERCHANT;
import gameworld.player.Player;
import gameworld.world.WorldController;
import gameworld.world.objects.drops.DRP_DroppedItem;
import gameworld.world.objects.items.ITEM;
import gameworld.world.objects.items.ITM_SpellBook;
import gameworld.world.objects.items.ITM_Usable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import main.MainGame;
import main.system.enums.State;
import main.system.ui.Colors;
import main.system.ui.FonT;
import main.system.ui.talentpanel.TalentNode;

import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class UI_InventoryPanel {
    private static final int SLOT_SIZE = 45;
    public final Rectangle combatStatsHitBox;
    private static final int CHAR_SLOTS = 10;
    private final ResourceLoaderEntity resource = new ResourceLoaderEntity("playerBig");
    public final UI_InventorySlot[] char_Slots;
    public final ArrayList<UI_InventorySlot> bag_Slots = new ArrayList<>();
    public final UI_InventorySlot[] bagEquipSlots;
    public final Rectangle wholeCharWindow;
    public final Rectangle wholeBagWindow;
    private final MainGame mg;
    private final DecimalFormat df = new DecimalFormat("#.#");
    public final Rectangle bagEquipSlotsBox;
    private final Rectangle charPanelMover;
    public final Rectangle secondPanelButton;
    public final Rectangle firstPanelButton;
    public final Rectangle bagPanelMover;
    public final Rectangle effectsHitBox;
    public boolean showCombatStats = true;
    private final Point previousMousePosition = new Point(300, 300);
    public MERCHANT activeTradingNPC;
    private int charPanelX = 300, grabbedIndexChar = -1, grabbedIndexBag = -1;
    private int charPanelY = 300;
    private final Point lastCharPosition = new Point(charPanelX, charPanelY);
    private int bagPanelX = 1_400;
    private int bagPanelY = 600;
    private final Point lastBagPosition = new Point(bagPanelX, bagPanelY);
    public ITEM grabbedITEM;
    public final Rectangle bagSortButton;
    public int activeCharacterPanel = 1;
    private int grabbedBagEquipIndex = -1;
    public boolean showBagEquipSlots;
    private final Image bag = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/bag.png")));
    private final Image sort = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/sort.png")));
    private final Image character_bottom = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/characterpanel_picture.png")));
    private final Image character_bottom2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/characterpanel_picture2.png")));
    private final Image helm = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/helm.png")));
    private final Image chest = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/chest.png")));
    private final Image pants = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/pants.png")));
    private final Image boots = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/boots.png")));
    private final Image ring = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/ring.png")));
    private final Image amulet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/amulet.png")));
    private final Image offhand = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/offhand.png")));
    private final Image relic = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/relic.png")));
    private final Image weapon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/weapon.png")));

    private final Image coin = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/coin.png")));

    private boolean showTutorial;


    public UI_InventoryPanel(MainGame mainGame) {
        mg = mainGame;
        resource.load();
        char_Slots = new UI_InventorySlot[CHAR_SLOTS];
        bagEquipSlots = new UI_InventorySlot[4];
        grabbedITEM = null;
        createCharSlots();
        createBagSlots();
        firstPanelButton = new Rectangle(charPanelX, charPanelY, 105, 20);
        secondPanelButton = new Rectangle(charPanelX, charPanelY, 90, 20);
        bagSortButton = new Rectangle(bagPanelX, bagPanelY, 24, 24);
        bagEquipSlotsBox = new Rectangle(charPanelX, charPanelY, 24, 24);
        charPanelMover = new Rectangle(charPanelX - 40, charPanelY - 75, 438, 25);
        bagPanelMover = new Rectangle(bagPanelX, bagPanelY, 355, 25);
        wholeCharWindow = new Rectangle(charPanelX - 30, charPanelY - 15, 445, 635);
        wholeBagWindow = new Rectangle(bagPanelX, bagPanelY, 365, 410);
        combatStatsHitBox = new Rectangle(bagPanelX, bagPanelY, 107, 15);
        effectsHitBox = new Rectangle(bagPanelX, bagPanelY, 80, 15);
        hideCharCollision();
        hideBagCollision();
    }

    public void drawCharacterWindow(GraphicsContext gc) {
        gc.setFont(mg.ui.maruMonica);
        drawCharPanel(gc, charPanelX, charPanelY);
        lastCharPosition.x = charPanelX;
        lastCharPosition.y = charPanelY;
    }

    public void drawBagWindow(GraphicsContext gc) {
        drawBagPanel(gc, bagPanelX, bagPanelY);
        lastBagPosition.x = bagPanelX;
        lastBagPosition.y = bagPanelY;
    }

    public void drawCharTooltip(GraphicsContext g2) {
        if (grabbedITEM == null && !mg.inputH.mouse1Pressed) {
            for (UI_InventorySlot invSlot : char_Slots) {
                if (invSlot.item != null && invSlot.toolTipTimer >= 30) {
                    getTooltip(g2, invSlot, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                }
                if (invSlot.boundBox.contains(mg.inputH.lastMousePosition)) {
                    invSlot.toolTipTimer++;
                    break;
                } else {
                    invSlot.toolTipTimer = 0;
                }
            }
        }
    }

    public void drawBagTooltip(GraphicsContext gc) {
        if (grabbedITEM == null && !mg.inputH.mouse1Pressed) {
            for (UI_InventorySlot bagSlot : bag_Slots) {
                if (bagSlot.item != null && bagSlot.toolTipTimer >= 30) {
                    getTooltip(gc, bagSlot, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                    if (mg.inputH.shift_pressed) {
                        for (UI_InventorySlot slot : char_Slots) {
                            if (slot.item != null && slot.item.type == bagSlot.item.type) {
                                getTooltip(gc, slot, (int) (mg.inputH.lastMousePosition.x - MainGame.SCREEN_HEIGHT * 0.232), (mg.inputH.lastMousePosition.y));
                            }
                        }
                    }
                }
                if (bagSlot.boundBox.contains(mg.inputH.lastMousePosition)) {
                    bagSlot.toolTipTimer++;
                    break;
                } else {
                    bagSlot.toolTipTimer = 0;
                }
            }
        }
    }

    private void drawCharPanel(GraphicsContext gc, int startX, int startY) {
        if (activeCharacterPanel == 1) {
            drawCharacterBackGroundMain(gc, startX, startY);
            drawCharacterSlots(gc, startX, startY);
        } else if (activeCharacterPanel == 2) {
            drawCharacterSecondPanel(gc, startX, startY);
        }
    }

    private void drawBagPanel(GraphicsContext gc, int startX, int startY) {
        drawBagBackground(gc, startX, startY);
        drawBagSlots(gc, startX, (int) (startY + MainGame.SCREEN_HEIGHT * 0.007f));
    }

    public void getTooltip(GraphicsContext gc, UI_InventorySlot invSlot, int startX, int startY) {
        //BACKGROUND
        gc.setFill(Colors.LightGrey);
        gc.fillRoundRect(startX - (MainGame.SCREEN_HEIGHT * 0.238), startY - (MainGame.SCREEN_HEIGHT * 0.314), MainGame.SCREEN_HEIGHT * 0.23, MainGame.SCREEN_HEIGHT * 0.324f, 15, 15);
        //OUTLINE
        setRarityColor(gc, invSlot);
        gc.setLineWidth(1);
        gc.strokeRoundRect(startX - (MainGame.SCREEN_HEIGHT * 0.235), startY - (MainGame.SCREEN_HEIGHT * 0.311), MainGame.SCREEN_HEIGHT * 0.224, MainGame.SCREEN_HEIGHT * 0.318f, 15, 15);
        //NAME
        setRarityColor(gc, invSlot);
        if (invSlot.item.name.length() < 23) {
            gc.setFont(FonT.minecraftRegular20);
        } else {
            gc.setFont(FonT.minecraftRegular18);
        }

        gc.fillText(invSlot.item.name, startX - MainGame.SCREEN_HEIGHT * 0.229f, startY - MainGame.SCREEN_HEIGHT * 0.268f);
        //Quality
        applyQualityColor(invSlot, gc);
        gc.setFont(FonT.minecraftItalic15);
        if (invSlot.item.quality < 100) {
            gc.fillText(invSlot.item.quality + "%", startX - MainGame.SCREEN_HEIGHT * 0.039_8f, startY - MainGame.SCREEN_HEIGHT * 0.299f);
        } else {
            gc.fillText(invSlot.item.quality + "%", startX - MainGame.SCREEN_HEIGHT * 0.047_3f, startY - MainGame.SCREEN_HEIGHT * 0.299f);
        }
        //STATS
        gc.setFill(Colors.darkBackground);
        if ((invSlot.item.type == 'M') || invSlot.item.type == 'X') {
            if (invSlot.item.type == 'X') {
                gc.setFont(FonT.minecraftItalic14);
                int stringY = (int) (startY - MainGame.SCREEN_HEIGHT * 0.22f);
                for (String string : invSlot.item.stats.split("\n")) {
                    gc.fillText(string, startX - MainGame.SCREEN_HEIGHT * 0.231f, stringY += MainGame.SCREEN_HEIGHT * 0.010f);
                }
            }
        } else if ((invSlot.item.type == 'G')) {
            gc.setFont(FonT.minecraftItalic15);
          /*  INT - Int
            WIS - Wis
            VIT - Vit
            AGI - Agi
            LUC - Luc
            CHA - Cha
            END - End
            STR - Str
            FOC - Foc
            MainGame.SCREEN_HEIGHT*0.8f
            x / 1080
           */
            //EFFECTS
            gc.fillText("Effects: ", startX - MainGame.SCREEN_HEIGHT * 0.229f, startY - MainGame.SCREEN_HEIGHT * 0.130f);
            gc.fillText("Slots: " + invSlot.item.stats, startX - MainGame.SCREEN_HEIGHT * 0.229f, startY - MainGame.SCREEN_HEIGHT * 0.110f);
        } else {
            gc.setFont(FonT.minecraftItalic15);
            gc.fillText("INT: " + invSlot.item.intellect, startX - MainGame.SCREEN_HEIGHT * 0.230f, startY - MainGame.SCREEN_HEIGHT * 0.236f);
            gc.fillText("VIT: " + invSlot.item.vitality, startX - MainGame.SCREEN_HEIGHT * 0.230f, startY - MainGame.SCREEN_HEIGHT * 0.222f);
            gc.fillText("WIS: " + invSlot.item.wisdom, startX - MainGame.SCREEN_HEIGHT * 0.230f, startY - MainGame.SCREEN_HEIGHT * 0.208f);

            gc.fillText("AGI: " + invSlot.item.agility, startX - MainGame.SCREEN_HEIGHT * 0.160f, startY - MainGame.SCREEN_HEIGHT * 0.236f);
            gc.fillText("LUC: " + invSlot.item.luck, startX - MainGame.SCREEN_HEIGHT * 0.160f, startY - MainGame.SCREEN_HEIGHT * 0.222f);
            gc.fillText("CHA: " + invSlot.item.charisma, startX - MainGame.SCREEN_HEIGHT * 0.160f, startY - MainGame.SCREEN_HEIGHT * 0.208f);

            gc.fillText("END: " + invSlot.item.endurance, startX - MainGame.SCREEN_HEIGHT * 0.086f, startY - MainGame.SCREEN_HEIGHT * 0.236f);
            gc.fillText("STR: " + invSlot.item.strength, startX - MainGame.SCREEN_HEIGHT * 0.086f, startY - MainGame.SCREEN_HEIGHT * 0.222f);
            gc.fillText("FOC: " + invSlot.item.focus, startX - MainGame.SCREEN_HEIGHT * 0.086f, startY - MainGame.SCREEN_HEIGHT * 0.208f);

          /*  INT - Int
            WIS - Wis
            VIT - Vit
            AGI - Agi
            LUC - Luc
            CHA - Cha
            END - End
            STR - Str
            FOC - Foc
            MainGame.SCREEN_HEIGHT*0.8f
            x / 1080
           */
            //EFFECTS
            gc.fillText("Effects: ", startX - MainGame.SCREEN_HEIGHT * 0.229f, startY - MainGame.SCREEN_HEIGHT * 0.130f);
            gc.setFont(FonT.minecraftItalic14);
            int counter = 0;
            for (int i = 0; i < Player.effectsSizeRollable; i++) {
                if (invSlot.item.effects[i] != 0) {
                    if (i == 23) {
                        gc.fillText(Player.effectNames[i] + invSlot.item.effects[i], startX - MainGame.SCREEN_HEIGHT * 0.221f, startY - MainGame.SCREEN_HEIGHT * (0.114f - 0.014 * counter));
                    } else {
                        gc.fillText(Player.effectNames[i] + invSlot.item.effects[i] + "%", startX - MainGame.SCREEN_HEIGHT * 0.221f, startY - MainGame.SCREEN_HEIGHT * (0.114f - 0.014 * counter));
                    }
                    counter++;
                }
            }
        }
        //DESCRIPTION
        gc.setFont(FonT.minecraftItalic12);
        int stringY = (int) (startY - MainGame.SCREEN_HEIGHT * 0.065f);
        for (String string : invSlot.item.description.split("\n")) {
            gc.fillText(string, startX - MainGame.SCREEN_HEIGHT * 0.231f, stringY += MainGame.SCREEN_HEIGHT * 0.010f);
        }
        gc.setFont(FonT.minecraftItalic14);
        //TYPE
        printItemType(gc, invSlot, startX, startY);
        //LEVEL
        gc.fillText("ilvl: " + invSlot.item.level, startX - 248, startY - 322);
        //Durability
        gc.fillText("D: " + invSlot.item.durability, startX - 251, startY + 3);
        //ID
        gc.fillText("ID: " + String.format("%04d", invSlot.item.i_id) + invSlot.item.type, startX - 78, startY + 3);
    }

    public void setRarityColor(GraphicsContext gc, UI_InventorySlot slot) {
        if (slot.item != null) {
            if (slot.item.rarity == 1) {
                gc.setStroke(Colors.NormalGrey);
                gc.setFill(Colors.NormalGrey);
            } else if (slot.item.rarity == 2) {
                gc.setStroke(Colors.rareColor);
                gc.setFill(Colors.rareColor);
            } else if (slot.item.rarity == 3) {
                gc.setStroke(Colors.epicColor);
                gc.setFill(Colors.epicColor);
            } else if (slot.item.rarity == 4) {
                gc.setStroke(Colors.legendaryColor);
                gc.setFill(Colors.legendaryColor);
            } else if (slot.item.rarity == 5) {
                gc.setStroke(Colors.setItem);
                gc.setFill(Colors.setItem);
            } else if (slot.item.rarity == 10) {
                gc.setStroke(Colors.legendaryColor);
                gc.setFill(Colors.legendaryColor);
            }
        } else {
            gc.setStroke(Colors.darkBackground);
            gc.setFill(Colors.darkBackground);
        }
    }

    private void printItemType(GraphicsContext gc, UI_InventorySlot slot, int x, int y) {
        int xPosition = (int) (x - MainGame.SCREEN_HEIGHT * 0.148f);
        int yPosition = (int) (y + MainGame.SCREEN_HEIGHT * 0.002_7f);
        switch (slot.item.type) {
            case 'H' -> gc.fillText("Helm", xPosition, yPosition);
            case 'C' -> gc.fillText("Chest", xPosition, yPosition);
            case 'P' -> gc.fillText("Pants", xPosition, yPosition);
            case 'B' -> gc.fillText("Boots", xPosition - MainGame.SCREEN_HEIGHT * 0.004, yPosition);
            case 'A' -> gc.fillText("Amulet", xPosition - MainGame.SCREEN_HEIGHT * 0.009f, yPosition);
            case 'R' -> gc.fillText("Ring", xPosition, yPosition);
            case 'T' -> gc.fillText("Relic", xPosition - MainGame.SCREEN_HEIGHT * 0.004, yPosition);
            case 'W' -> gc.fillText("One-Handed", xPosition - MainGame.SCREEN_HEIGHT * 0.017f, yPosition);
            case '2' -> gc.fillText("Two-Handed", xPosition - MainGame.SCREEN_HEIGHT * 0.019f, yPosition);
            case 'O' -> gc.fillText("Offhand", xPosition - MainGame.SCREEN_HEIGHT * 0.004, yPosition);
            case 'G' -> gc.fillText("Bag", xPosition, yPosition);

            default -> gc.fillText("Misc", xPosition, yPosition);
        }
    }

    public void drawDragAndDrop(GraphicsContext gc) {
        if (grabbedITEM != null) {
            gc.drawImage(grabbedITEM.icon, mg.inputH.lastMousePosition.x - SLOT_SIZE / 2.0f, mg.inputH.lastMousePosition.y - SLOT_SIZE / 2.0f, SLOT_SIZE, SLOT_SIZE);
        }
        if (grabbedITEM == null && mg.showChar) {
            for (int i = 0; i < char_Slots.length; i++) {
                if (char_Slots[i].boundBox.contains(mg.inputH.lastMousePosition) && char_Slots[i].item != null) {
                    if (mg.inputH.X_pressed) {
                        mg.WORLD_DROPS.add(new DRP_DroppedItem((int) (Player.worldX - 50), (int) Player.worldY, char_Slots[i].item, WorldController.currentWorld));
                        char_Slots[i].item = null;
                        mg.inputH.X_pressed = false;
                        mg.player.updateEquippedItems();
                    } else if (mg.inputH.mouse1Pressed) {
                        if (mg.inputH.shift_pressed && mg.showBag) {
                            for (UI_InventorySlot slot : bag_Slots) {
                                if (slot.item == null) {
                                    slot.item = char_Slots[i].item;
                                    char_Slots[i].item = null;
                                    mg.player.updateEquippedItems();
                                    mg.sound.playEffectSound(0);
                                    break;
                                }
                            }
                            continue;
                        }
                        char_Slots[i].grabbed = true;
                        grabbedITEM = char_Slots[i].item;
                        grabbedIndexChar = i;
                        char_Slots[i].item = null;
                        mg.player.updateEquippedItems();
                    }
                }
            }
        }
        if (grabbedITEM == null && mg.showBag) {
            for (int i = 0; i < bag_Slots.size(); i++) {
                if (bag_Slots.get(i).boundBox.contains(mg.inputH.lastMousePosition) && bag_Slots.get(i).item != null) {
                    if (mg.inputH.X_pressed) {
                        mg.WORLD_DROPS.add(new DRP_DroppedItem((int) (Player.worldX - 50), (int) Player.worldY, bag_Slots.get(i).item, WorldController.currentWorld));
                        bag_Slots.get(i).item = null;
                        mg.inputH.X_pressed = false;
                    } else if (mg.inputH.mouse1Pressed) {
                        if (mg.inputH.shift_pressed && mg.showChar) {
                            for (UI_InventorySlot slot : char_Slots) {
                                if (slot.item == null && slot.type.equals(String.valueOf(bag_Slots.get(i).item.type))) {
                                    slot.item = bag_Slots.get(i).item;
                                    bag_Slots.get(i).item = null;
                                    mg.player.updateEquippedItems();
                                    mg.sound.playEffectSound(0);
                                    break;
                                } else if (slot.type.contains(String.valueOf(bag_Slots.get(i).item.type))) {
                                    if (bag_Slots.get(i).item.type == '2') {
                                        for (UI_InventorySlot charItem : char_Slots) {
                                            if (charItem.item != null) {
                                                if (charItem.item.type == 'O') {
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                    ITEM placeholder = slot.item;
                                    slot.item = bag_Slots.get(i).item;
                                    bag_Slots.get(i).item = placeholder;
                                    mg.sound.playEffectSound(0);
                                    mg.player.updateEquippedItems();
                                    mg.inputH.mouse1Pressed = false;
                                    break;
                                }
                            }
                            break;
                        }
                        bag_Slots.get(i).grabbed = true;
                        grabbedITEM = bag_Slots.get(i).item;
                        grabbedIndexBag = i;
                        bag_Slots.get(i).item = null;
                    } else if (activeTradingNPC != null && activeTradingNPC.show_trade && mg.inputH.mouse2Pressed) {
                        if (!bag_Slots.get(i).type.equals("M") && activeTradingNPC.sellItem(bag_Slots.get(i).item)) {
                            bag_Slots.get(i).item = null;
                            return;
                        }
                    } else if (mg.inputH.e_typed && bag_Slots.get(i).item instanceof ITM_Usable) {
                        if (bag_Slots.get(i).item instanceof ITM_SpellBook) {
                            ((ITM_SpellBook) bag_Slots.get(i).item).activate(mg);
                            bag_Slots.get(i).item = null;
                            mg.sound.playEffectSound(10);
                            return;
                        }
                        mg.inputH.e_typed = false;
                    }
                }
            }
        }
        if (grabbedITEM == null && showBagEquipSlots) {
            for (int i = 0; i < bagEquipSlots.length; i++) {
                if (bagEquipSlots[i].boundBox.contains(mg.inputH.lastMousePosition) && bagEquipSlots[i].item != null) {
                    if (mg.inputH.X_pressed) {
                        mg.WORLD_DROPS.add(new DRP_DroppedItem((int) (Player.worldX - 50), (int) Player.worldY, bagEquipSlots[i].item, WorldController.currentWorld));
                        removeBagSlots(Integer.parseInt(bagEquipSlots[i].item.stats));
                        bagEquipSlots[i].item = null;
                        break;
                    } else if (mg.inputH.mouse1Pressed) {
                        bagEquipSlots[i].grabbed = true;
                        grabbedITEM = bagEquipSlots[i].item;
                        grabbedBagEquipIndex = i;
                        removeBagSlots(Integer.parseInt(bagEquipSlots[i].item.stats));
                        bagEquipSlots[i].item = null;
                    }
                }
            }
        }
        if (grabbedITEM == null && activeTradingNPC != null) {
            if (activeTradingNPC.show_trade) {
                for (int i = 0; i < activeTradingNPC.buySlots.size(); i++) {
                    if (mg.inputH.mouse2Pressed && activeTradingNPC.buySlots.get(i).boundBox.contains(mg.inputH.lastMousePosition)) {
                        if (activeTradingNPC.buySlots.get(i).item != null) {
                            if (activeTradingNPC.buyItem(activeTradingNPC.buySlots.get(i).item)) {
                                activeTradingNPC.buySlots.get(i).item = null;
                                return;
                            }
                        }
                    }
                }
            } else if (activeTradingNPC.show_buyback) {
                for (int i = 0; i < activeTradingNPC.soldSlots.size(); i++) {
                    if (activeTradingNPC.soldSlots.get(i).item != null && mg.inputH.mouse2Pressed && activeTradingNPC.soldSlots.get(i).boundBox.contains(mg.inputH.lastMousePosition)) {
                        if (activeTradingNPC.buyBackItem(activeTradingNPC.soldSlots.get(i).item)) {
                            activeTradingNPC.soldSlots.get(i).item = null;
                            return;
                        }
                    }
                }
            }
        }
        if (grabbedITEM != null && !mg.inputH.mouse1Pressed) {
            if (mg.showChar) {
                for (UI_InventorySlot invSlot : char_Slots) {
                    if (invSlot.boundBox.contains(mg.inputH.lastMousePosition) && invSlot.type.contains(String.valueOf(grabbedITEM.type))) {
                        mg.sound.playEffectSound(0);
                        if (invSlot.item != null) {
                            if (grabbedIndexChar != -1) {
                                if (grabbedITEM.type == '2') {
                                    for (UI_InventorySlot slot : char_Slots) {
                                        if (slot.item != null) {
                                            if (slot.item.type == 'O') {
                                                char_Slots[grabbedIndexChar].item = grabbedITEM;
                                                char_Slots[grabbedIndexChar].grabbed = false;
                                                grabbedIndexChar = -1;
                                                grabbedITEM = null;
                                                return;
                                            }
                                        }
                                    }
                                }
                                char_Slots[grabbedIndexChar].item = invSlot.item;
                            }
                            if (grabbedIndexBag != -1) {
                                if (grabbedITEM.type == '2') {
                                    for (UI_InventorySlot slot : char_Slots) {
                                        if (slot.item != null) {
                                            if (slot.item.type == 'O') {
                                                bag_Slots.get(grabbedIndexBag).item = grabbedITEM;
                                                bag_Slots.get(grabbedIndexBag).grabbed = false;
                                                grabbedITEM = null;
                                                grabbedIndexBag = -1;
                                                return;
                                            }
                                        }
                                    }
                                }
                                bag_Slots.get(grabbedIndexBag).item = invSlot.item;
                            }
                        }
                        if (grabbedITEM.type == '2') {
                            for (UI_InventorySlot slot : char_Slots) {
                                if (slot.item != null) {
                                    if (slot.item.type == 'O') {
                                        if (grabbedIndexBag != -1) {
                                            bag_Slots.get(grabbedIndexBag).item = grabbedITEM;
                                            bag_Slots.get(grabbedIndexBag).grabbed = false;
                                            grabbedITEM = null;
                                            grabbedIndexBag = -1;
                                            return;
                                        } else if (grabbedIndexChar != -1) {
                                            char_Slots[grabbedIndexChar].item = grabbedITEM;
                                            char_Slots[grabbedIndexChar].grabbed = false;
                                            grabbedITEM = null;
                                            grabbedIndexChar = -1;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        invSlot.item = grabbedITEM;
                        mg.player.updateEquippedItems();
                        grabbedITEM = null;
                    }
                    invSlot.grabbed = false;
                }
            }
            if (mg.showBag) {
                for (UI_InventorySlot bagSlot : bag_Slots) {
                    if (bagSlot.boundBox.contains(mg.inputH.lastMousePosition)) {
                        mg.sound.playEffectSound(0);
                        if (bagSlot.item != null) {
                            if (grabbedIndexChar != -1 && char_Slots[grabbedIndexChar].type.equals(String.valueOf(bagSlot.item.type))) {
                                char_Slots[grabbedIndexChar].item = bagSlot.item;
                            } else if (grabbedIndexChar != -1) {
                                char_Slots[grabbedIndexChar].item = grabbedITEM;
                                grabbedIndexChar = -1;
                                grabbedITEM = null;
                                return;
                            }
                            if (grabbedIndexBag != -1) {
                                bag_Slots.get(grabbedIndexBag).item = bagSlot.item;
                            }
                        }
                        mg.player.updateEquippedItems();
                        bagSlot.item = grabbedITEM;
                        grabbedITEM = null;
                    }
                    bagSlot.grabbed = false;
                }
            }
            if (showBagEquipSlots) {
                for (UI_InventorySlot equipBag : bagEquipSlots) {
                    if (equipBag.boundBox.contains(mg.inputH.lastMousePosition) && grabbedITEM != null) {
                        mg.sound.playEffectSound(0);
                        if (equipBag.item == null && grabbedITEM.type == 'G') {
                            equipBag.item = grabbedITEM;
                            addBagSlots(Integer.parseInt(grabbedITEM.stats));
                            grabbedITEM = null;
                        }
                    }
                    equipBag.grabbed = false;
                }
            }
            if (grabbedIndexChar != -1 && grabbedITEM != null) {
                char_Slots[grabbedIndexChar].item = grabbedITEM;
            }
            if (grabbedIndexBag != -1 && grabbedITEM != null) {
                bag_Slots.get(grabbedIndexBag).item = grabbedITEM;
            }
            if (grabbedBagEquipIndex != -1 && grabbedITEM != null) {
                bagEquipSlots[grabbedBagEquipIndex].item = grabbedITEM;
                addBagSlots(Integer.parseInt(bagEquipSlots[grabbedBagEquipIndex].item.stats));
            }
            grabbedIndexChar = -1;
            grabbedIndexBag = -1;
            grabbedBagEquipIndex = -1;
            if (grabbedITEM != null) {
                grabbedITEM = null;
                mg.player.updateEquippedItems();
            }
        }
    }

    public void interactWithWindows() {
        Point mousePos = mg.inputH.lastMousePosition;
        if (char_Slots[8].item != null && char_Slots[8].item.type == '2') {
            char_Slots[9].type = ",";
        } else {
            char_Slots[9].type = "O";
        }
        boolean node_focused = false;
        if ((mg.gameState == State.OPTION || mg.gameState == State.TITLE_OPTION) && mg.ui.musicSliderHitBox.contains(mousePos) && mg.inputH.mouse1Pressed) {
            mg.ui.musicSlider += (mousePos.x - previousMousePosition.x) / 2.0f;
            mg.ui.musicSlider = Math.max(Math.min(100, mg.ui.musicSlider), 0);
            mg.sound.setVolumeMusic(mg.ui.musicSlider);
            mg.ui.musicSliderHitBox.x = (int) (650 + mg.ui.musicSlider * 2 - 12);
        } else if ((mg.gameState == State.OPTION || mg.gameState == State.TITLE_OPTION) && mg.ui.effectsSliderHitBox.contains(mousePos) && mg.inputH.mouse1Pressed) {
            mg.ui.effectsSlider += (mousePos.x - previousMousePosition.x) / 2.0f;
            mg.ui.effectsSlider = Math.max(Math.min(100, mg.ui.effectsSlider), 0);
            mg.sound.setVolumeEffects(mg.ui.effectsSlider);
            mg.ui.effectsSliderHitBox.x = (int) (650 + mg.ui.effectsSlider * 2 - 12);
        } else if ((mg.gameState == State.OPTION || mg.gameState == State.TITLE_OPTION) && mg.ui.ambientSliderHitBox.contains(mousePos) && mg.inputH.mouse1Pressed) {
            mg.ui.ambientSlider += (mousePos.x - previousMousePosition.x) / 2.0f;
            mg.ui.ambientSlider = Math.max(Math.min(100, mg.ui.ambientSlider), 0);
            mg.sound.setVolumeAmbience(mg.ui.ambientSlider);
            mg.ui.ambientSliderHitBox.x = (int) (650 + mg.ui.ambientSlider * 2 - 12);
        } else if (mg.inputH.mouse1Pressed && charPanelMover.contains(mousePos)) {
            charPanelX += mousePos.x - previousMousePosition.x;
            charPanelY += mousePos.y - previousMousePosition.y;
            charPanelMover.x = charPanelX - 40;
            charPanelMover.y = charPanelY - 75;
        } else if (mg.inputH.mouse1Pressed && bagPanelMover.contains(mousePos)) {
            bagPanelX += mousePos.x - previousMousePosition.x;
            bagPanelY += mousePos.y - previousMousePosition.y;
            if (showBagEquipSlots) {
                bagSortButton.x = bagPanelX + 41;
                bagSortButton.y = bagPanelY + 1;
                wholeBagWindow.x = bagPanelX;
                wholeBagWindow.y = bagPanelY - 30;
                bagEquipSlotsBox.x = bagPanelX + 11;
                bagEquipSlotsBox.y = bagPanelY + 1;
                bagPanelMover.x = bagPanelX + 5;
                bagPanelMover.y = bagPanelY - 30;
            } else {
                bagSortButton.x = bagPanelX + 41;
                bagSortButton.y = bagPanelY + 31;
                wholeBagWindow.x = bagPanelX;
                wholeBagWindow.y = bagPanelY;
                bagEquipSlotsBox.x = bagPanelX + 11;
                bagEquipSlotsBox.y = bagPanelY + 31;
                bagPanelMover.x = bagPanelX + 5;
                bagPanelMover.y = bagPanelY - 2;
            }
        } else if (mg.talentP.wholeTalentWindow.contains(mousePos)) {
            for (TalentNode node : mg.talentP.talent_Nodes) {
                if (node != null) {
                    if (node.boundBox.contains(mousePos)) {
                        if (mg.inputH.mouse1Pressed) {
                            if (mg.talentP.checkValidTalent(node) && mg.talentP.pointsToSpend > 0 && !node.activated) {
                                node.activated = true;
                                mg.player.updateEquippedItems();
                                mg.talentP.spendTalentPoint();
                            }
                        }
                        node_focused = true;
                    }
                }
            }
            if (!node_focused && mg.inputH.mouse1Pressed) {
                mg.talentP.talentPanelX += mousePos.x - previousMousePosition.x;
                mg.talentP.talentPanelY += mousePos.y - previousMousePosition.y;
            }
        } else if (mg.skillPanel.wholeSkillWindow.contains(mousePos)) {
            if (mg.skillPanel.skillPanelMover.contains(mousePos)) {
                if (mg.inputH.mouse1Pressed) {
                    mg.skillPanel.skillPanelX += mousePos.x - previousMousePosition.x;
                    mg.skillPanel.skillPanelY += mousePos.y - previousMousePosition.y;
                    mg.skillPanel.skillPanelMover.x = mg.skillPanel.skillPanelX;
                    mg.skillPanel.skillPanelMover.y = mg.skillPanel.skillPanelY;
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (mg.skillPanel.draggedSKILL == null && mg.skillPanel.hitBoxesSideButtons[i].contains(mousePos)) {
                        if (mg.inputH.mouse1Pressed) {
                            for (int j = 0; j < 5; j++) {
                                mg.skillPanel.whichPanel[j] = false;
                            }
                            mg.skillPanel.whichPanel[i] = true;
                            mg.inputH.mouse1Pressed = false;
                            mg.sound.playEffectSound(11);
                            return;
                        } else {
                            mg.skillPanel.toolTipNumber = i;
                        }
                    }
                }
            }
        }
        previousMousePosition.x = mousePos.x;
        previousMousePosition.y = mousePos.y;
    }

    //TODO prevent windows off screen dragging
    private void drawCharacterBackGroundMain(GraphicsContext gc, int startX, int startY) {
        //inventory background
        wholeCharWindow.x = (int) (startX - 0.043f * MainGame.SCREEN_HEIGHT);
        wholeCharWindow.y = (int) (startY - 0.072_2f * MainGame.SCREEN_HEIGHT);
        firstPanelButton.x = (int) (startX - 0.027f * MainGame.SCREEN_HEIGHT);
        firstPanelButton.y = (int) (startY + 0.487f * MainGame.SCREEN_HEIGHT);
        secondPanelButton.x = (int) (startX + 0.083_3f * MainGame.SCREEN_HEIGHT);
        secondPanelButton.y = (int) (startY + 0.497f * MainGame.SCREEN_HEIGHT);
        //big background

        gc.setFill(Colors.LightGrey);
        gc.fillRoundRect(startX - 50, startY - 80, 450, 620, 35, 35);
        //outline
        gc.setStroke(Colors.darkBackground);
        gc.setLineWidth(5);
        gc.strokeRoundRect(startX - 45, startY - 75, 440, 620 - 10, 30, 30);
        gc.setFill(Colors.mediumLightGrey);
        gc.fillRoundRect(startX - 42, startY - 75, 434, 22, 15, 15);

        gc.setStroke(Colors.darkBackground);
        gc.strokeRoundRect(startX - 42, startY - 75, 434, 22, 15, 15);
        gc.setLineWidth(4);
        gc.drawImage(character_bottom, startX - 52, startY - 80 + 606);
        drawCharacterAnimation(gc, startX, startY);
        gc.setFill(Colors.darkBackground);
        gc.setTextAlign(TextAlignment.RIGHT);
        int distance1 = 15; // The distance between each line of text
        int startY2 = startY + 382; // The starting Y position for the first line of text

        gc.setFont(FonT.minecraftBold16);
        gc.fillText(String.valueOf(mg.player.intellect), startX + 172, startY2);
        gc.fillText(String.valueOf(mg.player.wisdom), startX + 172, startY2 + distance1);
        gc.fillText(String.valueOf(mg.player.vitality), startX + 172, startY2 + 2 * distance1);
        gc.fillText(String.valueOf(mg.player.agility), startX + 172, startY2 + 3 * distance1);
        gc.fillText(String.valueOf(mg.player.luck), startX + 172, startY2 + 4 * distance1);
        gc.fillText(String.valueOf(mg.player.charisma), startX + 172, startY2 + 5 * distance1);
        gc.fillText(String.valueOf(mg.player.endurance), startX + 172, startY2 + 6 * distance1);
        gc.fillText(String.valueOf(mg.player.strength), startX + 172, startY2 + 7 * distance1);
        gc.fillText(String.valueOf(mg.player.focus), startX + 172, startY2 + 8 * distance1);
        gc.setFont(FonT.minecraftBold15);
        gc.fillText(((Math.round(mg.player.weaponDamageLower * 10.0f) / 10.0f) + "-" + Math.round(mg.player.weaponDamageUpper * 10.0f) / 10.0f), startX + 172, startY2 + 9 * distance1);
        gc.setTextAlign(TextAlignment.LEFT);

        gc.fillText("Level " + mg.player.level, 135 + startX, startY - 15);
        gc.setFont(FonT.minecraftBold14);
        gc.fillText("Character", startX + 138, startY - 61);
        gc.fillText("Character", startX - 17, startY - 80 + 629);
        gc.fillText("Statistics", startX + 105, startY - 80 + 629);
        gc.fillText("WIP", startX + 218, startY - 80 + 629);

        //character image outline
        gc.setLineWidth(2);
        gc.strokeRoundRect(75 + startX, startY + 15, 200, 250, 25, 25);

        //Stats Text
        gc.strokeRoundRect(startX - 32, startY + 365, 207, 160, 15, 15);
        gc.strokeRoundRect(startX + 176, startY + 365, 207, 160, 15, 15);

        int startYtext = 367;
        int distance = 15;
        //stats
        gc.fillText("Intelligence: ", startX - 25, startY + startYtext + distance);
        gc.fillText("Wisdom: ", startX - 25, startY + startYtext + distance * 2);
        gc.fillText("Vitality: ", startX - 25, startY + startYtext + distance * 3);
        gc.fillText("Agility: ", startX - 25, startY + startYtext + distance * 4);
        gc.fillText("Luck: ", startX - 25, startY + startYtext + distance * 5);
        gc.fillText("Charisma: ", startX - 25, startY + startYtext + distance * 6);
        gc.fillText("Endurance: ", startX - 25, startY + startYtext + distance * 7);
        gc.fillText("Strength: ", startX - 25, startY + startYtext + distance * 8);
        gc.fillText("Focus: ", startX - 25, startY + startYtext + distance * 9);
        gc.fillText("Weapon DMG: ", startX - 25, startY + startYtext + distance * 10);

        // second panel stats
        gc.setFill(Colors.mediumLightGreyTransparent);
        gc.fillRoundRect(startX - 25, startY + 350, 107, 15, 10, 10);
        gc.setFill(Colors.darkBackground);
        gc.fillText("Base Stats", startX - 17, startY + 362);
        gc.strokeRoundRect(startX - 25, startY + 350, 107, 15, 10, 10);
        combatStatsHitBox.x = startX + 180;
        combatStatsHitBox.y = startY + 350;
        effectsHitBox.x = startX + 294;
        effectsHitBox.y = startY + 350;
        if (showCombatStats) {
            gc.setStroke(Colors.darkBackground);
            gc.strokeRoundRect(startX + 180, startY + 350, 114, 15, 10, 10);
            gc.fillText("Combat Stats", startX + 186, startY + 362);
            gc.setFill(Colors.mediumVeryLight);
            gc.fillRoundRect(startX + 294, startY + 350, 75, 15, 10, 10);
            gc.setFill(Colors.darkBackground);
            gc.strokeRoundRect(startX + 294, startY + 350, 75, 15, 10, 10);
            gc.fillText("Effects", startX + 301, startY + 362);
            drawCombatStats(gc, startX, startY);
        } else {
            gc.setFill(Colors.mediumVeryLight);
            gc.fillRoundRect(startX + 180, startY + 350, 114, 15, 10, 10);
            gc.setFill(Colors.darkBackground);
            gc.strokeRoundRect(startX + 180, startY + 350, 114, 15, 10, 10);
            gc.fillText("Combat Stats", startX + 186, startY + 362);
            gc.strokeRoundRect(startX + 294, startY + 350, 75, 15, 10, 10);
            gc.fillText("Effects", startX + 301, startY + 362);
            drawEffects(gc, startX, startY);
        }
    }

    private void drawCharacterAnimation(GraphicsContext gc, int startX, int startY) {
        if (mg.player.attack1) {
            switch (mg.player.spriteCounter % 80 / 10) {
                case 0 -> gc.drawImage(resource.attack1.get(0), startX + 50, startY - 50);
                case 1 -> gc.drawImage(resource.attack1.get(1), startX + 50, startY - 50);
                case 2 -> gc.drawImage(resource.attack1.get(2), startX + 50, startY - 50);
                case 3 -> gc.drawImage(resource.attack1.get(3), startX + 50, startY - 50);
                case 4 -> gc.drawImage(resource.attack1.get(4), startX + 50, startY - 50);
                case 5 -> gc.drawImage(resource.attack1.get(5), startX + 50, startY - 50);
                case 6 -> gc.drawImage(resource.attack1.get(6), startX + 50, startY - 50);
            }
        } else if (mg.player.attack2) {
            switch (mg.player.spriteCounter % 100 / 10) {
                case 0 -> gc.drawImage(resource.attack2.get(0), startX + 50, startY - 50);
                case 1 -> gc.drawImage(resource.attack2.get(1), startX + 50, startY - 50);
                case 2 -> gc.drawImage(resource.attack2.get(2), startX + 50, startY - 50);
                case 3 -> gc.drawImage(resource.attack2.get(3), startX + 50, startY - 50);
                case 4 -> gc.drawImage(resource.attack2.get(4), startX + 50, startY - 50);
                case 5 -> gc.drawImage(resource.attack2.get(5), startX + 50, startY - 50);
                case 6 -> gc.drawImage(resource.attack2.get(6), startX + 50, startY - 50);
                case 7 -> gc.drawImage(resource.attack2.get(7), startX + 50, startY - 50);
                case 8 -> gc.drawImage(resource.attack2.get(8), startX + 50, startY - 50);
            }
        } else if (mg.player.attack3) {
            switch (mg.player.spriteCounter % 170 / 10) {
                case 0 -> gc.drawImage(resource.attack3.get(0), startX + 50, startY - 50);
                case 1 -> gc.drawImage(resource.attack3.get(1), startX + 50, startY - 50);
                case 2 -> gc.drawImage(resource.attack3.get(2), startX + 50, startY - 50);
                case 3 -> gc.drawImage(resource.attack3.get(3), startX + 50, startY - 50);
                case 4 -> gc.drawImage(resource.attack3.get(4), startX + 50, startY - 50);
                case 5 -> gc.drawImage(resource.attack3.get(5), startX + 50, startY - 50);
                case 6 -> gc.drawImage(resource.attack3.get(6), startX + 50, startY - 50);
                case 7 -> gc.drawImage(resource.attack3.get(7), startX + 50, startY - 50);
                case 8 -> gc.drawImage(resource.attack3.get(8), startX + 50, startY - 50);
                case 9 -> gc.drawImage(resource.attack3.get(9), startX + 50, startY - 50);
                case 10 -> gc.drawImage(resource.attack3.get(10), startX + 50, startY - 50);
                case 11 -> gc.drawImage(resource.attack3.get(11), startX + 50, startY - 50);
                case 12 -> gc.drawImage(resource.attack3.get(12), startX + 50, startY - 50);
                case 13 -> gc.drawImage(resource.attack3.get(13), startX + 50, startY - 50);
                case 14 -> gc.drawImage(resource.attack3.get(14), startX + 50, startY - 50);
                case 15 -> gc.drawImage(resource.attack3.get(15), startX + 50, startY - 50);
            }
        } else {
            if (mg.player.isMoving) {
                if (mg.player.movingLeft) {
                    switch (mg.player.spriteCounter % 136 / 17) {
                        case 0 -> gc.drawImage(resource.runMirror.get(7), startX + 90, startY - 50);
                        case 1 -> gc.drawImage(resource.runMirror.get(6), startX + 90, startY - 50);
                        case 2 -> gc.drawImage(resource.runMirror.get(5), startX + 90, startY - 50);
                        case 3 -> gc.drawImage(resource.runMirror.get(4), startX + 90, startY - 50);
                        case 4 -> gc.drawImage(resource.runMirror.get(3), startX + 90, startY - 50);
                        case 5 -> gc.drawImage(resource.runMirror.get(2), startX + 90, startY - 50);
                        case 6 -> gc.drawImage(resource.runMirror.get(1), startX + 90, startY - 50);
                        case 7 -> gc.drawImage(resource.runMirror.get(0), startX + 90, startY - 50);
                    }
                } else {
                    switch (mg.player.spriteCounter % 136 / 17) {
                        case 0 -> gc.drawImage(resource.run.get(0), startX + 70, startY - 50);
                        case 1 -> gc.drawImage(resource.run.get(1), startX + 70, startY - 50);
                        case 2 -> gc.drawImage(resource.run.get(2), startX + 70, startY - 50);
                        case 3 -> gc.drawImage(resource.run.get(3), startX + 70, startY - 50);
                        case 4 -> gc.drawImage(resource.run.get(4), startX + 70, startY - 50);
                        case 5 -> gc.drawImage(resource.run.get(5), startX + 70, startY - 50);
                        case 6 -> gc.drawImage(resource.run.get(6), startX + 70, startY - 50);
                        case 7 -> gc.drawImage(resource.run.get(7), startX + 70, startY - 50);
                    }
                }
            } else {
                switch (mg.player.spriteCounter % 200 / 25) {
                    case 0 -> gc.drawImage(resource.idle.get(0), startX + 50, startY - 50);
                    case 1 -> gc.drawImage(resource.idle.get(1), startX + 50, startY - 50);
                    case 2 -> gc.drawImage(resource.idle.get(2), startX + 50, startY - 50);
                    case 3 -> gc.drawImage(resource.idle.get(3), startX + 50, startY - 50);
                    case 4 -> gc.drawImage(resource.idle.get(4), startX + 50, startY - 50);
                    case 5 -> gc.drawImage(resource.idle.get(5), startX + 50, startY - 50);
                    case 6 -> gc.drawImage(resource.idle.get(6), startX + 50, startY - 50);
                    case 7 -> gc.drawImage(resource.idle.get(7), startX + 50, startY - 50);
                }
            }
        }
    }


    private void drawCenteredText(GraphicsContext gc, String text, float y, int x) {
        Text textNode = new Text(text);
        textNode.setFont(gc.getFont());
        gc.fillText(text, x, y);
    }

    //FIRST TAB COMBAT STATS
    private void drawCombatStats(GraphicsContext gc, int startX, int startY) {
        gc.setFill(Colors.darkBackground);
        gc.setFont(FonT.minecraftBold14);
        int yInterval = (int) (MainGame.SCREEN_HEIGHT * 0.0148f);
        startY -= 10;
        startX += 190;
        double xOffset = 188;
        gc.fillText("Max-Health: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f);
        gc.fillText("Max-Mana: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + yInterval);
        gc.fillText("ManaREG: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 2 * yInterval);
        gc.fillText("HealthREG: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 3 * yInterval);
        gc.fillText("Armour: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 4 * yInterval);
        gc.fillText("Crit-Chance: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 5 * yInterval);
        gc.fillText("Crit-Damage: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 6 * yInterval);
        gc.fillText("Carry-weight: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 7 * yInterval);
        gc.fillText("MovementSpeed: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 8 * yInterval);
        gc.fillText("CD-Reduction: ", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 9 * yInterval);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(FonT.minecraftBold16);
        gc.fillText(String.valueOf(mg.player.maxHealth), startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f);
        gc.fillText(String.valueOf(mg.player.maxMana), startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + yInterval);
        gc.fillText(Math.round(mg.player.manaRegeneration * 60 * 100.0f) / 100.0f + "/s", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 2 * yInterval);
        gc.fillText(Math.round(mg.player.healthRegeneration * 60 * 100.0f) / 100.0f + "/s", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 3 * yInterval);
        gc.fillText(String.valueOf(mg.player.armour), startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 4 * yInterval);
        gc.fillText(mg.player.critChance + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 5 * yInterval);
        gc.fillText(mg.player.effects[22] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 6 * yInterval);
        gc.fillText(String.valueOf(mg.player.carryWeight), startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 7 * yInterval);
        gc.fillText(String.valueOf(mg.player.playerMovementSpeed), startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 8 * yInterval);
        gc.fillText(mg.player.effects[17] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 9 * yInterval);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    // SECOND TAB IN CHARACTER WINDOW
    private void drawCharacterSecondPanel(GraphicsContext gc, int startX, int startY) {
        wholeCharWindow.x = startX - 47;
        wholeCharWindow.y = startY - 78;
        firstPanelButton.x = startX - 30;
        firstPanelButton.y = startY - 80 + 615;
        secondPanelButton.x = startX + 90;
        secondPanelButton.y = startY - 80 + 615;
        //big background

        gc.setFill(Colors.LightGrey);
        gc.fillRoundRect(startX - 50, startY - 80, 450, 620, 35, 35);

        //outline
        gc.setStroke(Colors.darkBackground);
        gc.setLineWidth(5);
        gc.strokeRoundRect(startX - 45, startY - 75, 440, 620 - 10, 30, 30);
        gc.setFill(Colors.mediumLightGrey);
        gc.fillRoundRect(startX - 42, startY - 75, 434, 22, 15, 15);

        gc.setStroke(Colors.darkBackground);
        gc.strokeRoundRect(startX - 42, startY - 75, 434, 22, 15, 15);
        gc.setLineWidth(4);
        gc.drawImage(character_bottom2, startX - 52, startY - 80 + 606);
        gc.setFill(Colors.darkBackground);
        gc.setFont(FonT.minecraftBold14);
        gc.fillText("Character", startX + 138, startY - 61);
        gc.fillText("Character", startX - 17, startY - 80 + 629);
        gc.fillText("Statistics", startX + 103, startY - 80 + 629);
        gc.fillText("WIP", startX + 225, startY - 80 + 629);

        gc.fillText("Total Playtime: " + mg.gameStatistics.getPlayTimeFormatted(), startX + 20, startY + 50);
        gc.fillText("Killed Monsters: " + mg.gameStatistics.getTOTAL_MONSTERS_KILLED(), startX + 20, startY + 65);
        gc.fillText("Distance travelled: " + mg.gameStatistics.getDISTANCE_TRAVELLED(), startX + 20, startY + 80);
        gc.fillText("Abilities used: " + mg.gameStatistics.getABILITIES_USED(), startX + 20, startY + 95);
    }

    //FIRST TAB EFFECTS STATS (SECOND SUB TAB)
    private void drawEffects(GraphicsContext gc, int startX, int startY) {
        gc.setFill(Colors.darkBackground);
        gc.setFont(FonT.minecraftBold14);
        int yInterval = (int) (MainGame.SCREEN_HEIGHT * 0.0148f);
        startY -= 10;
        startX += 190;
        double xOffset = 188;
        gc.fillText("Arcane Dmg:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f);
        gc.fillText("Dark Dmg:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + yInterval);
        gc.fillText("Poison Dmg:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 2 * yInterval);
        gc.fillText("Fire Dmg:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 3 * yInterval);
        gc.fillText("Ice Dmg :", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 4 * yInterval);
        gc.fillText("Magic Find", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 5 * yInterval);
        gc.fillText("Mana Cost Reduc.", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 6 * yInterval);
        gc.fillText("DoT Dmg:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 7 * yInterval);
        gc.fillText("DoT Length:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 8 * yInterval);
        gc.fillText("Buff Length:", startX - 5, startY + MainGame.SCREEN_HEIGHT * 0.363f + 9 * yInterval);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(FonT.minecraftBold16);
        gc.fillText(mg.player.effects[1] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f);
        gc.fillText(mg.player.effects[2] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + yInterval);
        gc.fillText(mg.player.effects[18] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 2 * yInterval);
        gc.fillText(mg.player.effects[19] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 3 * yInterval);
        gc.fillText(mg.player.effects[28] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 4 * yInterval);
        gc.fillText(mg.player.effects[27] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 5 * yInterval);
        gc.fillText(mg.player.effects[26] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 6 * yInterval);
        gc.fillText(mg.player.effects[4] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 7 * yInterval);
        gc.fillText(mg.player.effects[5] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 8 * yInterval);
        gc.fillText(mg.player.effects[3] + "%", startX + xOffset, startY + MainGame.SCREEN_HEIGHT * 0.363f + 9 * yInterval);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void drawCharacterSlots(GraphicsContext gc, int startX, int startY) {
        gc.setLineWidth(2);
        for (int i = 0; i <= 3; i++) {
            char_Slots[i].boundBox.x = 15 + startX;
            char_Slots[i].boundBox.y = (i * 50 + 45 + startY);
            gc.setFill(Colors.mediumVeryLight);
            gc.fillRoundRect(15 + startX, i * 50 + 45 + startY, 45, 45, 20, 20);
            setRarityColor(gc, char_Slots[i]);
            char_Slots[i].drawSlot(gc, 15 + startX, (i * 50 + 45 + startY));
            if (char_Slots[i].item != null && !char_Slots[i].grabbed) {
                char_Slots[i].drawIcon(gc, 15 + startX, ((i * 50) + 45 + startY), SLOT_SIZE);
            } else {
                if (char_Slots[i].type.equals("H")) {
                    gc.drawImage(helm, 15 + startX + 2, ((i * 50) + 45 + startY) + 2);
                } else if (char_Slots[i].type.equals("C")) {
                    gc.drawImage(chest, 15 + startX + 2, ((i * 50) + 45 + startY));
                } else if (char_Slots[i].type.equals("P")) {
                    gc.drawImage(pants, 15 + startX + 2, ((i * 50) + 45 + startY) + 2);
                } else if (char_Slots[i].type.equals("B")) {
                    gc.drawImage(boots, 15 + startX + 2, ((i * 50) + 45 + startY) + 2);
                }
            }
        }
        for (int i = 4; i <= 7; i++) {
            char_Slots[i].boundBox.x = 289 + startX;
            char_Slots[i].boundBox.y = (((i - 4) * 50) + 45 + startY);
            gc.setFill(Colors.mediumVeryLight);
            gc.fillRoundRect(289 + startX, ((i - 4) * 50) + 45 + startY, 45, 45, 20, 20);
            setRarityColor(gc, char_Slots[i]);
            char_Slots[i].drawSlot(gc, 289 + startX, (((i - 4) * 50) + 45 + startY));
            if (char_Slots[i].item != null && !char_Slots[i].grabbed) {
                char_Slots[i].drawIcon(gc, 289 + startX, ((i - 4) * 50) + 45 + startY, SLOT_SIZE);
            } else {
                if (char_Slots[i].type.equals("R")) {
                    gc.drawImage(ring, 289 + startX + 2, ((i - 4) * 50) + 45 + startY + 2);
                } else if (char_Slots[i].type.equals("A")) {
                    gc.drawImage(amulet, 289 + startX + 2, ((i - 4) * 50) + 45 + startY + 2);
                } else if (char_Slots[i].type.equals("T")) {
                    gc.drawImage(relic, 289 + startX + 2, ((i - 4) * 50) + 45 + startY + 2);
                }
            }
        }
        for (int i = 8; i <= 9; i++) {
            char_Slots[i].boundBox.x = ((i - 8) * 57) + 124 + startX;
            char_Slots[i].boundBox.y = 275 + startY;
            gc.setFill(Colors.mediumVeryLight);
            gc.fillRoundRect(((i - 8) * 57) + 124 + startX, 275 + startY, 45, 45, 20, 20);
            setRarityColor(gc, char_Slots[i]);
            char_Slots[i].drawSlot(gc, ((i - 8) * 57) + 124 + startX, 275 + startY);
            if (char_Slots[i].item != null && !char_Slots[i].grabbed) {
                char_Slots[i].drawIcon(gc, ((i - 8) * 57) + 124 + startX, 275 + startY, SLOT_SIZE);
            } else {
                if (char_Slots[i].type.contains("2") || char_Slots[i].type.contains("W")) {
                    gc.drawImage(weapon, ((i - 8) * 57) + 124 + startX + 2, 275 + startY + 2);
                } else if (char_Slots[i].type.equals("O")) {
                    gc.drawImage(offhand, ((i - 8) * 57) + 124 + startX + 2, 275 + startY + 2);
                }
            }
        }
    }

    private void drawBagBackground(GraphicsContext gc, int startX, int startY) {
        //background
        if (showBagEquipSlots) {
            bagSortButton.x = startX + 41;
            bagSortButton.y = startY + 1;
            bagEquipSlotsBox.x = startX + 11;
            bagEquipSlotsBox.y = startY + 1;
            wholeBagWindow.x = startX;
            wholeBagWindow.y = startY - 30;
            bagPanelMover.x = startX + 5;
            bagPanelMover.y = startY - 30;
            gc.setFill(Colors.LightGrey);
            gc.fillRoundRect(startX, startY - 30, 365, 440, 25, 25);
            //background header
            gc.setFont(FonT.minecraftBold14);
            gc.setFill(Colors.mediumLightGrey);
            gc.fillRoundRect(startX + 5, startY - 25, 355, 20, 15, 15);
            gc.setLineWidth(3);
            gc.setStroke(Colors.darkBackground);
            //header outline
            gc.strokeRoundRect(startX + 5, startY - 25, 355, 20, 15, 15);
            //outline
            gc.strokeRoundRect(startX + 5, startY - 25, 355, 430, 15, 15);
            gc.setLineWidth(2);
            //feature pane
            gc.setFill(Colors.darkBackground);
            gc.fillText("Bags", startX + 160, startY - 11);
            gc.strokeRoundRect(startX + 10, startY, 25, 25, 5, 5);
            gc.strokeRoundRect(startX + 40, startY, 25, 25, 5, 5);
            gc.drawImage(bag, startX + 11, startY + 1);
            gc.drawImage(sort, startX + 41, startY + 1);

            for (int i = 0; i < 4; i++) {
                bagEquipSlots[i].boundBox.x = i * 26 + startX + 11;
                bagEquipSlots[i].boundBox.y = startY + 30;
                gc.setFill(Colors.mediumVeryLight);
                gc.fillRoundRect(i * 26 + startX + 11, startY + 30, 25, 25, 15, 15);
                setRarityColor(gc, bagEquipSlots[i]);
                bagEquipSlots[i].drawSlot(gc, i * 26 + startX + 11, startY + 30);
                if (bagEquipSlots[i].item != null && !bagEquipSlots[i].grabbed) {
                    bagEquipSlots[i].drawIcon(gc, i * 26 + startX + 11, startY + 30, 25);
                }
            }
        } else {
            bagSortButton.x = startX + 41;
            bagSortButton.y = startY + 31;
            bagEquipSlotsBox.x = startX + 11;
            bagEquipSlotsBox.y = startY + 31;
            wholeBagWindow.x = startX;
            wholeBagWindow.y = startY;
            bagPanelMover.x = startX + 5;
            bagPanelMover.y = startY - 2;
            for (int i = 0; i < 4; i++) {
                bagEquipSlots[i].boundBox.x = i * 26 + startX + 11 + 1_000;
                bagEquipSlots[i].boundBox.y = startY + 26 + 1_000;
            }
            gc.setFill(Colors.LightGrey);
            gc.fillRoundRect(startX, startY, 365, 410, 25, 25);
            //background header
            gc.setFont(FonT.minecraftBold14);
            gc.setFill(Colors.mediumLightGrey);
            gc.fillRoundRect(startX + 5, startY + 5, 355, 20, 15, 15);

            gc.setLineWidth(3);
            gc.setStroke(Colors.darkBackground);
            //header outline
            gc.strokeRoundRect(startX + 5, startY + 5, 355, 20, 15, 15);
            //outline
            gc.strokeRoundRect(startX + 5, startY + 5, 355, 400, 15, 15);
            gc.setLineWidth(2);
            //feature pane
            // gc.strokeRoundRect(startX + 5, startY + 31, 355, 18, 15, 15);
            gc.setFill(Colors.darkBackground);
            gc.fillText("Bags", startX + 160, startY + 19);
            gc.strokeRoundRect(startX + 10, startY + 30, 25, 25, 5, 5);
            gc.strokeRoundRect(startX + 40, startY + 30, 25, 25, 5, 5);
            gc.drawImage(bag, startX + 11, startY + 31);
            gc.drawImage(sort, startX + 41, startY + 31);
        }
        gc.drawImage(coin, startX + 340, startY + 380);
        gc.fillText(String.valueOf(mg.player.coins), startX + 290, startY + 393);
    }

    private void drawBagSlots(GraphicsContext gc, int startX, int startY) {
        int i = 0;
        int y;
        gc.setLineWidth(2);
        for (UI_InventorySlot slot : bag_Slots) {
            y = 50 * (i / 7);
            slot.boundBox.x = i % 7 * 50 + startX + 10;
            slot.boundBox.y = 60 + y + startY;
            gc.setFill(Colors.mediumVeryLight);
            gc.fillRoundRect(i % 7 * 50 + 10 + startX, 60 + y + startY, 45, 45, 20, 20);
            setRarityColor(gc, slot);
            slot.drawSlot(gc, i % 7 * 50 + startX + 10, 60 + y + startY);
            if (slot.item != null && !slot.grabbed) {
                slot.drawIcon(gc, i % 7 * 50 + startX + 10, 60 + y + startY, SLOT_SIZE);
            }
            i++;
        }
    }

    public void addItemToBag(ITEM item) {
        for (UI_InventorySlot slot : bag_Slots) {
            if (slot.item == null) {
                slot.item = item;
                return;
            }
        }
    }

    public void hideCharCollision() {
        wholeCharWindow.x = -1_000;
        wholeCharWindow.y = -1_000;
        charPanelMover.y = -1000;
    }

    private void applyQualityColor(UI_InventorySlot invSlot, GraphicsContext gc) {
        if (invSlot.item.quality < 90) {
            gc.setFill(Colors.NormalGrey);
        }
        if (invSlot.item.quality >= 90) {
            gc.setFill(Colors.mediumQuality);
        }
        if (invSlot.item.quality == 100) {
            gc.setFill(Colors.highQuality);
        }
    }

    public void hideBagCollision() {
        wholeBagWindow.x = -1_000;
        wholeBagWindow.y = -1_000;
        bagPanelMover.y = -1000;
    }

    public void resetCharCollision() {
        wholeCharWindow.x = lastCharPosition.x;
        wholeCharWindow.y = lastCharPosition.y;
        charPanelMover.y = wholeCharWindow.y - 75;
        mg.sBar.showNoticeChar = false;
    }

    public void resetBagCollision() {
        wholeBagWindow.x = lastBagPosition.x;
        wholeBagWindow.y = lastBagPosition.y;
        bagPanelMover.y = wholeBagWindow.y;
        mg.sBar.showNoticeBag = false;
    }

    private void createCharSlots() {
        for (int i = 0; i <= 3; i++) {
            char_Slots[i] = new UI_InventorySlot(null, 40 + 260, (i * 50) + 310);
        }
        for (int i = 4; i <= 7; i++) {
            char_Slots[i] = new UI_InventorySlot(null, 40 + 270 + 260, ((i - 4) * 50) + 310);
        }
        for (int i = 8; i <= 9; i++) {
            char_Slots[i] = new UI_InventorySlot(null, ((i - 8) * 50) + 160 + 260, 550);
        }
        char_Slots[0].type = "H";
        char_Slots[1].type = "C";
        char_Slots[2].type = "P";
        char_Slots[3].type = "B";
        char_Slots[4].type = "A";
        char_Slots[5].type = "R";
        char_Slots[6].type = "R";
        char_Slots[7].type = "T";
        char_Slots[8].type = "W2";
        char_Slots[9].type = "O";
    }

    private void createBagSlots() {
        for (int i = 0; i < 14; i++) {
            bag_Slots.add(new UI_InventorySlot(null, (i * 50) + 810, 330));
        }
        for (int i = 0; i < 4; i++) {
            bagEquipSlots[i] = new UI_InventorySlot(null, 700, 700, 25);
        }
    }

    private void addBagSlots(int size) {
        for (int i = 0; i < size; i++) {
            bag_Slots.add(new UI_InventorySlot(null, 1, 1));
        }
    }

    private void removeBagSlots(int size) {
        for (int i = 1; i <= size; i++) {
            if (bag_Slots.get(bag_Slots.size() - 1).item != null) {
                mg.WORLD_DROPS.add(new DRP_DroppedItem((int) (Player.worldX - 50), (int) Player.worldY, bag_Slots.get(bag_Slots.size() - 1).item, WorldController.currentWorld));
            }
            bag_Slots.remove(bag_Slots.size() - 1);
        }
    }

    public void updateItemEffects() {
        for (UI_InventorySlot slot : bagEquipSlots) {
            if (slot.item != null) {
                addBagSlots(Integer.parseInt(slot.item.stats));
            }
        }
    }

    public void sortBagsRarity() {
        int n = bag_Slots.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (bag_Slots.get(j).item != null && bag_Slots.get(j + 1).item != null && bag_Slots.get(j).item.rarity < bag_Slots.get(j + 1).item.rarity) {
                    UI_InventorySlot temp = bag_Slots.get(j);
                    bag_Slots.set(j, bag_Slots.get(j + 1));
                    bag_Slots.set(j + 1, temp);
                }
                if (bag_Slots.get(j).item == null && bag_Slots.get(j + 1).item != null) {
                    UI_InventorySlot temp = bag_Slots.get(j);
                    bag_Slots.set(j, bag_Slots.get(j + 1));
                    bag_Slots.set(j + 1, temp);
                }
            }
        }
    }
}