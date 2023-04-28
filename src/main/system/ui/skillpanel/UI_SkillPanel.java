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

package main.system.ui.skillpanel;

import gameworld.entities.damage.DamageType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.MainGame;
import main.system.ui.Colors;
import main.system.ui.FonT;
import main.system.ui.skillbar.SKILL;
import main.system.ui.skillbar.skills.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Objects;

public class UI_SkillPanel {
    public final boolean[] whichPanel = new boolean[5];
    public final SKILL[] allSkills = new SKILL[50];
    private final Image a1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/spellbook_first.png")));
    private final Image a2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/spellbook_2.png")));
    private final Image a3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/spellbook_3.png")));
    private final Image a4 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/spellbook_4.png")));
    private final Image a5 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/spellbook_5.png")));
    private final MainGame mg;
    public final Rectangle wholeSkillWindow;
    public final Rectangle[] hitBoxesSideButtons = new Rectangle[5];
    public int skillPanelX = 500, skillPanelY = 250;
    public final Rectangle skillPanelMover = new Rectangle(409, 29);
    public final ArrayList<SKILL> arcaneSkills = new ArrayList<>();
    public final ArrayList<SKILL> fireSkills = new ArrayList<>();
    public final ArrayList<SKILL> poisonSkills = new ArrayList<>();
    public final ArrayList<SKILL> iceSkills = new ArrayList<>();
    public final ArrayList<SKILL> darkSkills = new ArrayList<>();
    public int toolTipNumber;
    public SKILL draggedSKILL;
    private int lastY;
    private int grabIndex;


    public UI_SkillPanel(MainGame mg) {
        this.mg = mg;
        whichPanel[0] = true;
        this.wholeSkillWindow = new Rectangle(skillPanelX, skillPanelY, 445, 524);
        for (int i = 0; i < 5; i++) {
            hitBoxesSideButtons[i] = new Rectangle(123, 123, 32, 41);
        }
        for (int i = 0; i < 10; i++) {
            arcaneSkills.add(new SKL_Filler(mg));
            fireSkills.add(new SKL_Filler(mg));
            poisonSkills.add(new SKL_Filler(mg));
            iceSkills.add(new SKL_Filler(mg));
            darkSkills.add(new SKL_Filler(mg));
        }
        allSkills[0] = new SKL_AutoShot(mg);
        allSkills[1] = new SKL_EnergySphere(mg);
        allSkills[2] = new SKL_EnergySphere_2(mg);
        allSkills[3] = new SKL_Filler(mg);
        allSkills[4] = new SKL_FrostNova(mg);
        allSkills[5] = new SKL_IceLance(mg);
        allSkills[6] = new SKL_Lightning(mg);
        allSkills[7] = new SKL_MagicShield(mg);
        allSkills[8] = new SKL_ManaShield(mg);
        allSkills[9] = new SKL_RegenAura(mg);
        allSkills[10] = new SKL_FireBurst(mg);
        allSkills[11] = new SKL_SolarFlare(mg);
        allSkills[12] = new SKL_ThunderSplash(mg);
        allSkills[13] = new SKL_ThunderStrike(mg);
        allSkills[14] = new SKL_VoidEruption(mg);
        allSkills[15] = new SKL_VoidField(mg);
        allSkills[16] = new SKL_PyroBlast(mg);
        allSkills[17] = new SKL_PowerSurge(mg);
        allSkills[18] = new SKL_BlastHammer(mg);
        allSkills[19] = new SKL_InfernoRay(mg);
        allSkills[20] = new SKL_FlameWhirl(mg);
        hideSkillPanelCollision();
        mg.sBar.showNoticeAbilities = false;
        addSKill(new SKL_AutoShot(mg));
    }


    public void drawSkillPanel(GraphicsContext gc) {
        drawBackGround(gc, skillPanelX, skillPanelY);
        drawSkills(gc, skillPanelX, skillPanelY);
    }

    private void drawBackGround(GraphicsContext gc, int x, int y) {
        if (whichPanel[0]) {
            gc.drawImage(a1, x, y);
        } else if (whichPanel[1]) {
            gc.drawImage(a2, x, y);
        } else if (whichPanel[2]) {
            gc.drawImage(a3, x, y);
        } else if (whichPanel[3]) {
            gc.drawImage(a4, x, y);
        } else if (whichPanel[4]) {
            gc.drawImage(a5, x, y);
        }
        gc.setFill(Colors.darkBackground);
        gc.setFont(FonT.minecraftBold16);
        gc.fillText("Skills", skillPanelX + 170, skillPanelY + 20);
        wholeSkillWindow.x = skillPanelX;
        wholeSkillWindow.y = skillPanelY;
        skillPanelMover.x = skillPanelX;
        skillPanelMover.y = skillPanelY;
        for (int i = 0; i < 5; i++) {
            hitBoxesSideButtons[i].x = skillPanelX + 407;
            hitBoxesSideButtons[i].y = skillPanelY + 51 + 45 * i;
            if (hitBoxesSideButtons[i].contains(mg.inputH.lastMousePosition)) {
                drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y, i);
            }
        }
    }

    private void drawSkills(GraphicsContext gc, int x, int y) {
        x += 25; //?
        y += 100;//?
        gc.setFont(FonT.minecraftItalic12);
        gc.setFill(Colors.darkBackground);
        if (whichPanel[0]) {
            for (int i = 0; i < arcaneSkills.size(); i++) {
                arcaneSkills.get(i).drawSkillSlot(gc, x - 5, y - 40);
                arcaneSkills.get(i).hitBox.x = x + 2;
                arcaneSkills.get(i).hitBox.y = y - 33;
                gc.fillText(arcaneSkills.get(i).name, x + 65, y - 25);
                arcaneSkills.get(i + 1).drawSkillSlot(gc, x + 190, y - 40);
                gc.fillText(arcaneSkills.get(i + 1).name, x + 260, y - 25);
                arcaneSkills.get(i + 1).hitBox.x = x + 197;
                arcaneSkills.get(i + 1).hitBox.y = y - 33;
                if (arcaneSkills.get(i).hitBox.contains(mg.inputH.lastMousePosition)) {
                    arcaneSkills.get(i).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                } else if (arcaneSkills.get(i + 1).hitBox.contains(mg.inputH.lastMousePosition)) {
                    arcaneSkills.get(i + 1).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                } else {
                    arcaneSkills.get(i).toolTipTimer = 0;
                    arcaneSkills.get(i + 1).toolTipTimer = 0;
                }
                i++;
                y += 85;
            }
        } else if (whichPanel[1]) {
            for (int i = 0; i < poisonSkills.size(); i++) {
                poisonSkills.get(i).drawSkillSlot(gc, x - 5, y - 40);
                gc.fillText(poisonSkills.get(i).name, x + 65, y - 25);
                poisonSkills.get(i).hitBox.x = x + 2;
                poisonSkills.get(i).hitBox.y = y - 33;
                poisonSkills.get(i + 1).drawSkillSlot(gc, x + 190, y - 40);
                gc.fillText(poisonSkills.get(i + 1).name, x + 260, y - 25);
                poisonSkills.get(i + 1).hitBox.x = x + 197;
                poisonSkills.get(i + 1).hitBox.y = y - 33;
                if (poisonSkills.get(i).hitBox.contains(mg.inputH.lastMousePosition)) {
                    poisonSkills.get(i).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                } else if (poisonSkills.get(i + 1).hitBox.contains(mg.inputH.lastMousePosition)) {
                    poisonSkills.get(i + 1).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                }
                i++;
                y += 85;
            }
        } else if (whichPanel[2]) {
            for (int i = 0; i < fireSkills.size(); i++) {
                fireSkills.get(i).drawSkillSlot(gc, x - 5, y - 40);
                gc.fillText(fireSkills.get(i).name, x + 65, y - 25);
                fireSkills.get(i).hitBox.x = x + 2;
                fireSkills.get(i).hitBox.y = y - 33;
                fireSkills.get(i + 1).drawSkillSlot(gc, x + 190, y - 40);
                gc.fillText(fireSkills.get(i + 1).name, x + 260, y - 25);
                fireSkills.get(i + 1).hitBox.x = x + 197;
                fireSkills.get(i + 1).hitBox.y = y - 33;
                if (fireSkills.get(i).hitBox.contains(mg.inputH.lastMousePosition)) {
                    fireSkills.get(i).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                } else if (fireSkills.get(i + 1).hitBox.contains(mg.inputH.lastMousePosition)) {
                    fireSkills.get(i + 1).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                }
                i++;
                y += 85;
            }
        } else if (whichPanel[3]) {
            for (int i = 0; i < iceSkills.size(); i++) {
                iceSkills.get(i).drawSkillSlot(gc, x - 5, y - 40);
                gc.fillText(iceSkills.get(i).name, x + 65, y - 25);
                iceSkills.get(i).hitBox.x = x + 2;
                iceSkills.get(i).hitBox.y = y - 33;
                iceSkills.get(i + 1).drawSkillSlot(gc, x + 190, y - 40);
                gc.fillText(iceSkills.get(i + 1).name, x + 260, y - 25);
                iceSkills.get(i + 1).hitBox.x = x + 197;
                iceSkills.get(i + 1).hitBox.y = y - 33;
                if (iceSkills.get(i).hitBox.contains(mg.inputH.lastMousePosition)) {
                    iceSkills.get(i).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                } else if (iceSkills.get(i + 1).hitBox.contains(mg.inputH.lastMousePosition)) {
                    iceSkills.get(i + 1).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                }
                i++;
                y += 85;
            }
        } else if (whichPanel[4]) {
            for (int i = 0; i < darkSkills.size(); i++) {
                darkSkills.get(i).drawSkillSlot(gc, x - 5, y - 40);
                gc.fillText(darkSkills.get(i).name, x + 65, y - 25);
                darkSkills.get(i).hitBox.x = x + 2;
                darkSkills.get(i).hitBox.y = y - 33;
                darkSkills.get(i + 1).drawSkillSlot(gc, x + 190, y - 40);
                gc.fillText(darkSkills.get(i + 1).name, x + 260, y - 25);
                darkSkills.get(i + 1).hitBox.x = x + 197;
                darkSkills.get(i + 1).hitBox.y = y - 33;
                if (darkSkills.get(i).hitBox.contains(mg.inputH.lastMousePosition)) {
                    darkSkills.get(i).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                } else if (darkSkills.get(i + 1).hitBox.contains(mg.inputH.lastMousePosition)) {
                    darkSkills.get(i + 1).drawToolTip(gc, mg.inputH.lastMousePosition.x, mg.inputH.lastMousePosition.y);
                }
                i++;
                y += 85;
            }
        }
    }

    private void drawToolTip(GraphicsContext gc, int x, int y, int i) {
        gc.setFill(Colors.lightGreyMiddleAlpha);
        gc.fillRoundRect(x + 25, y - 15, 75, 20, 10, 10);
        gc.setFill(Colors.darkBackground);
        gc.strokeRoundRect(x + 25, y - 15, 75, 20, 10, 10);
        gc.setFont(FonT.minecraftBold14);
        switch (i) {
            case 0 -> gc.fillText("Arcane Magic", x + 25, y);
            case 1 -> gc.fillText("Poison Magic", x + 25, y);
            case 2 -> gc.fillText("Fire Magic", x + 25, y);
            case 3 -> gc.fillText("Frost Magic", x + 25, y);
            case 4 -> gc.fillText("Dark Magic", x + 25, y);
        }
    }

    public void dragAndDropSkillBar(GraphicsContext gc) {
        Point mousePos = mg.inputH.lastMousePosition;
        if (draggedSKILL != null) {
            if (!(draggedSKILL instanceof SKL_Filler)) {
                gc.drawImage(draggedSKILL.icon, mg.inputH.lastMousePosition.x - 25, mg.inputH.lastMousePosition.y - 25);
                gc.setStroke(Colors.darkBackground);
                gc.setLineWidth(4);
                gc.strokeRoundRect(mg.inputH.lastMousePosition.x - 25, mg.inputH.lastMousePosition.y - 25, 50, 50, 10, 10);
            }
            if (!mg.inputH.mouse1Pressed) {
                for (int i = 0; i < 8; i++) {
                    if (mg.sBar.hitBoxes[i].contains(mousePos)) {
                        for (SKILL skill : mg.sBar.skills) {
                            if (draggedSKILL.name.equals(skill.name) || skill.actualCoolDown < skill.totalCoolDown) {
                                draggedSKILL = null;
                                grabIndex = -1;
                                return;
                            }
                        }
                        mg.sBar.skills[i] = draggedSKILL;
                        draggedSKILL = null;
                        grabIndex = -1;
                        return;
                    }
                }
                draggedSKILL = null;
                grabIndex = -1;
            }
        }
        if (draggedSKILL == null && mg.inputH.mouse1Pressed) {
            for (int i = 0; i < arcaneSkills.size(); i++) {
                if (mg.skillPanel.whichPanel[0]) {
                    SKILL skill = arcaneSkills.get(i);
                    if (skill.hitBox.contains(mousePos)) {
                        draggedSKILL = skill;
                        grabIndex = i;
                        return;
                    }
                }
            }
            for (int i = 0; i < poisonSkills.size(); i++) {
                if (mg.skillPanel.whichPanel[1]) {
                    SKILL skill = poisonSkills.get(i);
                    if (skill.hitBox.contains(mousePos)) {
                        draggedSKILL = skill;
                        grabIndex = i;
                        return;
                    }
                }
            }
            for (int i = 0; i < fireSkills.size(); i++) {
                if (mg.skillPanel.whichPanel[2]) {
                    SKILL skill = fireSkills.get(i);
                    if (skill.hitBox.contains(mousePos)) {
                        draggedSKILL = skill;
                        grabIndex = i;
                        return;
                    }
                }
            }
            for (int i = 0; i < iceSkills.size(); i++) {
                if (mg.skillPanel.whichPanel[3]) {
                    SKILL skill = iceSkills.get(i);
                    if (skill.hitBox.contains(mousePos)) {
                        draggedSKILL = skill;
                        grabIndex = i;
                        return;
                    }
                }
            }
            for (int i = 0; i < darkSkills.size(); i++) {
                if (mg.skillPanel.whichPanel[4]) {
                    SKILL skill = darkSkills.get(i);
                    if (skill.hitBox.contains(mousePos)) {
                        draggedSKILL = skill;
                        grabIndex = i;
                        return;
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                if (mg.sBar.hitBoxes[i].contains(mousePos)) {
                    draggedSKILL = mg.sBar.skills[i];
                    grabIndex = i;
                    return;
                }
            }
        }
    }

    private void removeSKill(SKILL draggedSKILL) {
        if (grabIndex != -1) {
            if (draggedSKILL.type == DamageType.Fire) {
                fireSkills.set(grabIndex, new SKL_Filler(mg));
            } else if (draggedSKILL.type == DamageType.Arcane) {
                arcaneSkills.set(grabIndex, new SKL_Filler(mg));
            } else if (draggedSKILL.type == DamageType.DarkMagic) {
                darkSkills.set(grabIndex, new SKL_Filler(mg));
            } else if (draggedSKILL.type == DamageType.Poison) {
                poisonSkills.set(grabIndex, new SKL_Filler(mg));
            } else if (draggedSKILL.type == DamageType.Ice) {
                iceSkills.set(grabIndex, new SKL_Filler(mg));
            }
        }
    }

    public void addSKill(SKILL newSkill) {
        if (newSkill != null) {
            mg.sBar.showNoticeAbilities = true;
            if (newSkill.type == DamageType.Fire) {
                for (int i = 0; i < fireSkills.size(); i++) {
                    if (fireSkills.get(i) instanceof SKL_Filler) {
                        for (SKILL skill : fireSkills) {
                            if (skill.name.equals(newSkill.name)) {
                                return;
                            }
                        }
                        fireSkills.set(i, newSkill);
                        return;
                    }
                }
            } else if (newSkill.type == DamageType.Arcane) {
                for (int i = 0; i < arcaneSkills.size(); i++) {
                    if (arcaneSkills.get(i) instanceof SKL_Filler) {
                        for (SKILL skill : arcaneSkills) {
                            if (skill.name.equals(newSkill.name)) {
                                return;
                            }
                        }
                        arcaneSkills.set(i, newSkill);
                        return;
                    }
                }
            } else if (newSkill.type == DamageType.DarkMagic) {
                for (int i = 0; i < darkSkills.size(); i++) {
                    if (darkSkills.get(i) instanceof SKL_Filler) {
                        for (SKILL skill : darkSkills) {
                            if (skill.name.equals(newSkill.name)) {
                                return;
                            }
                        }
                        darkSkills.set(i, newSkill);
                        return;
                    }
                }
            } else if (newSkill.type == DamageType.Poison) {
                for (int i = 0; i < poisonSkills.size(); i++) {
                    if (poisonSkills.get(i) instanceof SKL_Filler) {
                        for (SKILL skill : poisonSkills) {
                            if (skill.name.equals(newSkill.name)) {
                                return;
                            }
                        }
                        poisonSkills.set(i, newSkill);
                        return;
                    }
                }
            } else if (newSkill.type == DamageType.Ice) {
                for (int i = 0; i < iceSkills.size(); i++) {
                    if (iceSkills.get(i) instanceof SKL_Filler) {
                        for (SKILL skill : iceSkills) {
                            if (skill.name.equals(newSkill.name)) {
                                return;
                            }
                        }
                        iceSkills.set(i, newSkill);
                        return;
                    }
                }
            }
        }
    }

    public void hideSkillPanelCollision() {
        lastY = wholeSkillWindow.y;
        wholeSkillWindow.y = -1000;
    }

    public void resetSkillPanelCollision() {
        wholeSkillWindow.y = lastY;
        mg.sBar.showNoticeAbilities = false;
    }
}

