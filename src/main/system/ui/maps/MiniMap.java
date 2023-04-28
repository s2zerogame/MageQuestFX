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

package main.system.ui.maps;

import gameworld.entities.ENTITY;
import gameworld.player.PROJECTILE;
import gameworld.world.WorldController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import main.MainGame;
import main.system.rendering.WorldRender;
import main.system.ui.Colors;

import java.util.Objects;

public class MiniMap {
    private final MainGame mg;
    private final Image miniMapFrame = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/ui/inventory/minimap_frame.png")));


    public MiniMap(MainGame mg) {
        this.mg = mg;
    }

    public void draw(GraphicsContext gc) {
        int xTile = mg.playerX;
        int yTile = mg.playerY;
        gc.setFill(Colors.lightGreyMiddleAlpha);
        gc.fillRect(1_686, 45, 200, 200);
        int tileNum, tileNum2;
        int yTileOffset, xTileOffset, entityX, entityY;
        final int tileSize = 5;
        for (int y = 0; y < 40; y++) {
            int yTileOffsetStart = Math.max(yTile - 20 + y, 0);
            int yTileOffsetEnd = Math.min(yTile - 20 + y, mg.wRender.worldSize.x - 1);
            for (int x = 0; x < 40; x++) {
                int xTileOffsetStart = Math.max(xTile - 20 + x, 0);
                int xTileOffsetEnd = Math.min(xTile - 20 + x, mg.wRender.worldSize.x - 1);
                for (yTileOffset = yTileOffsetStart; yTileOffset <= yTileOffsetEnd; yTileOffset++) {
                    for (xTileOffset = xTileOffsetStart; xTileOffset <= xTileOffsetEnd; xTileOffset++) {
                        tileNum = WorldRender.worldData[xTileOffset][yTileOffset];
                        tileNum2 = WorldRender.worldData1[xTileOffset][yTileOffset];
                        if (WorldController.currentMapCover[xTileOffset][yTileOffset] == 1) {
                            if ((tileNum != -1 && mg.wRender.tileStorage[tileNum].collision) || (tileNum2 != -1 && mg.wRender.tileStorage[tileNum2].collision)) {
                                gc.setFill(Colors.darkBackground);
                                gc.fillRect(1_686 + x * tileSize, 45 + y * tileSize, tileSize, tileSize);
                            } else {
                                gc.setFill(Colors.map_green);
                                gc.fillRect(1_686 + x * tileSize, 45 + y * tileSize, tileSize, tileSize);
                            }
                        } else {
                            gc.setFill(Colors.black);
                            gc.fillRect(1_686 + x * tileSize, 45 + y * tileSize, tileSize, tileSize);
                        }
                    }
                }
            }
        }
        gc.setFill(Colors.Blue);
        gc.fillRect(1_686 + 100, 45 + 100, 5, 5);
        gc.setFill(Colors.Red);
        final int offset = 100;
        synchronized (mg.PROXIMITY_ENTITIES) {
            for (gameworld.entities.ENTITY entity : mg.PROXIMITY_ENTITIES) {
                entityX = (int) ((entity.worldX + 24) / 48);
                entityY = (int) ((entity.worldY + 24) / 48);
                if ((entityX - xTile) < 20 && xTile - entityX <= 20 && (entityY - yTile) < 20 && yTile - entityY <= 20) {
                    if (WorldController.currentMapCover[entityX][entityY] == 1) {
                        gc.fillRect(1_686 + offset + (entityX - xTile) * tileSize, 45 + offset + (entityY - yTile) * tileSize, tileSize, tileSize);
                    }
                }
            }
        }
        synchronized (mg.PROJECTILES) {
            for (PROJECTILE projectile : mg.PROJECTILES) {
                entityX = (int) ((projectile.worldPos.x + 24) / 48);
                entityY = (int) ((projectile.worldPos.y + 24) / 48);
                if (entityX < WorldController.currentMapCover.length && entityY < WorldController.currentMapCover.length && WorldController.currentMapCover[entityX][entityY] == 1) {
                    if ((entityX - xTile) < 20 && xTile - entityX <= 20 && (entityY - yTile) < 20 && yTile - entityY <= 20) {
                        gc.fillRect(1_686 + offset + (entityX - xTile) * tileSize, 45 + offset + (entityY - yTile) * tileSize, 2, 2);
                    }
                }
            }
        }
        gc.setFill(Colors.blue_npc);
        for (ENTITY entity : mg.npcControl.NPC_Active) {
            entityX = (int) ((entity.worldX + 24) / 48);
            entityY = (int) ((entity.worldY + 24) / 48);
            if (entity.zone == WorldController.currentWorld && WorldController.currentMapCover[entityX][entityY] == 1) {
                if ((entityX - xTile) < 20 && xTile - entityX <= 20 && (entityY - yTile) < 20 && yTile - entityY <= 20) {
                    gc.fillRect(1_686 + offset + (entityX - xTile) * tileSize, 45 + offset + (entityY - yTile) * tileSize, tileSize, tileSize);
                }
            }
        }

        gc.drawImage(miniMapFrame, 1_649, 20);
    }


}


