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

package gameworld.world;


import gameworld.entities.ENTITY;
import gameworld.entities.boss.BOSS_Knight;
import gameworld.entities.npcs.generic.zonescripts.NPCScript;
import gameworld.player.Player;
import gameworld.player.PlayerPrompts;
import gameworld.quest.QUEST;
import gameworld.quest.QUEST_NAME;
import gameworld.quest.SpawnTrigger;
import gameworld.world.maps.Map;
import main.MainGame;
import main.system.enums.GameMapType;
import main.system.enums.State;
import main.system.enums.Zone;
import main.system.rendering.WorldRender;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class WorldController {
    private final MainGame mg;
    public static Zone currentWorld = Zone.Woodland_Edge;
    private State currentState;
    public static NPCScript currentScript = null;
    public static int[][] currentMapCover;
    public HashMap<Zone, Boolean> playerHasEnteredZone = new HashMap<>();
    private static final ArrayList<SpawnTrigger> globalTriggers = new ArrayList<>();
    public final ArrayList<Map> MAPS = new ArrayList<>();

    public WorldController(MainGame mg) {
        this.mg = mg;
    }

    public void loadWorldData() {
        MAPS.add(new Map("hermitCaveHillcrest", new Point(70, 70), Zone.Hillcrest_Hermit_Cave, GameMapType.NoMapCover));
        MAPS.add(new Map("goblinCave", new Point(120, 120), Zone.Goblin_Cave, GameMapType.NoMapCover));
        MAPS.add(new Map("TestRoom", new Point(50, 50), Zone.TestRoom, GameMapType.NoMapCover));
        MAPS.add(new Map("Tutorial", new Point(100, 100), Zone.Woodland_Edge, GameMapType.MapCover));
        MAPS.add(new Map("FirstDungeon", new Point(60, 60), Zone.Ruin_Dungeon, GameMapType.NoMapCover));
        MAPS.add(new Map("Hillcrest", new Point(100, 100), Zone.Hillcrest, GameMapType.MapCover));
        MAPS.add(new Map("GrassLands", new Point(500, 500), Zone.GrassLands, GameMapType.MapCover));
        MAPS.add(new Map("HillCrestPuzzleCellar", new Point(50, 50), Zone.Treasure_Cave, GameMapType.NoMapCover));
        MAPS.add(new Map("caveMarla", new Point(60, 60), Zone.Hillcrest_Mountain_Cave, GameMapType.NoMapCover));
        MAPS.add(new Map("TheGrove", new Point(200, 200), Zone.The_Grove, GameMapType.MapCover));
        MAPS.add(new Map("DeadPlains", new Point(200, 200), Zone.DeadPlains, GameMapType.MapCover));
        loadArray();
    }

    public void loadMap(Zone zone, int xTile, int yTile) {
        currentState = mg.gameState;
        mg.gameState = State.LOADING_SCREEN;
        for (Map map : MAPS) {
            if (map.zone == zone) {
                var thread = new Thread(() -> {
                    try {
                        mg.ui.setLoadingScreen(0);
                        Thread.sleep(100);
                        if (zone != Zone.EtherRealm) {
                            mg.ENTITIES.removeIf(entity -> entity.zone == Zone.EtherRealm);
                        }
                        mg.wRender.worldSize = map.mapSize;
                        mg.ui.setLoadingScreen(20);
                        currentWorld = zone;
                        mg.player.map = map;
                        mg.ui.setLoadingScreen(40);
                        mg.player.setPosition(xTile, yTile);
                        mg.getPlayerTile();
                        clearWorldArrays();
                        mg.ui.setLoadingScreen(60);
                        mg.wAnim.emptyAnimationLists();
                        currentMapCover = map.mapCover;
                        WorldRender.worldData = map.mapDataBackGround;
                        WorldRender.worldData1 = map.mapDataBackGround2;
                        mg.ui.setLoadingScreen(80);
                        WorldRender.worldData2 = map.mapDataForeGround;
                        mg.wAnim.cacheMapEnhancements();
                        mg.npcControl.loadGenerics(zone);
                        mg.ui.setLoadingScreen(100);
                        mg.gameState = currentState;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
                break;
            }
        }
    }

    public void loadMapNoDelay(Zone zone, int xTile, int yTile) {
        State currentState = mg.gameState;
        for (Map map : MAPS) {
            if (map.zone == zone) {
                //mg.gameState = State.LOADING_SCREEN;
                mg.wRender.worldSize = map.mapSize;
                currentWorld = zone;
                mg.player.map = map;
                mg.player.setPosition(xTile, yTile);
                mg.getPlayerTile();
                clearWorldArrays();
                mg.wAnim.emptyAnimationLists();
                currentMapCover = map.mapCover;
                WorldRender.worldData = map.mapDataBackGround;
                WorldRender.worldData1 = map.mapDataBackGround2;
                WorldRender.worldData2 = map.mapDataForeGround;
                mg.wAnim.cacheMapEnhancements();
                mg.npcControl.loadGenerics(zone);
                mg.gameState = currentState;
                break;
            }
        }
        mg.gameState = currentState;
    }

    public void loadMap(Map map, int xTile, int yTile) {
        State currentState = mg.gameState;
        mg.gameState = State.LOADING_SCREEN;
        Thread thread = new Thread(() -> {
            mg.wRender.worldSize = map.mapSize;
            currentWorld = map.zone;
            mg.player.map = map;
            mg.ui.setLoadingScreen(20);
            mg.player.setPosition(xTile, yTile);
            mg.getPlayerTile();
            clearWorldArrays();
            mg.ui.setLoadingScreen(40);
            mg.wAnim.emptyAnimationLists();
            currentMapCover = map.mapCover;
            mg.ui.setLoadingScreen(60);
            WorldRender.worldData = map.mapDataBackGround;
            WorldRender.worldData1 = map.mapDataBackGround2;
            mg.ui.setLoadingScreen(80);
            WorldRender.worldData2 = map.mapDataForeGround;
            mg.wAnim.cacheMapEnhancements();
            mg.npcControl.loadGenerics(map.zone);
            mg.ui.setLoadingScreen(100);
            mg.gameState = currentState;
        });
        thread.start();
    }

    private Map getMap(Zone zone) {
        for (Map map : MAPS) {
            if (map.zone == zone) {
                return map;
            }
        }
        return null;
    }

    public void makeOverWorldQuadrants() {
        int size = getMap(Zone.GrassLands).mapSize.x / 10;
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            for (int b = 0; b < 10; b++) {
                if (counter != 99) {
                    //getMap(Zone.GrassLands).mapQuadrants[counter] = new MapQuadrant(19 - (i + b), mg, size * i, size * b, size, 30, Zone.GrassLands);
                    counter++;
                }
                // getMap(Zone.GrassLands).mapQuadrants[counter] = new MapQuadrant(19 - (i + b), mg, size * i, size * b, size, 0, Zone.GrassLands);
            }
        }
    }

    private void clearWorldArrays() {
        synchronized (mg.PROJECTILES) {
            mg.PROJECTILES.clear();
        }
    }

    public void update() {
        for (SpawnTrigger trigger : globalTriggers) {
            if (trigger.zone == currentWorld && !trigger.triggered) {
                trigger.activate(mg);
            }
        }
        if (currentWorld == Zone.Woodland_Edge) {
            if (mg.playerX == 71 && mg.playerY == 56) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Ruin_Dungeon, 28, 4);
                }
            } else if (mg.playerX == 99 && mg.playerY == 99) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Hillcrest, 1, 1);
                }
            }
        } else if (currentWorld == Zone.GrassLands) {
            if (mg.playerX == 499 && mg.playerY == 499) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Hillcrest, 4, 94);
                }
            }
        } else if (currentWorld == Zone.City1) {
            if (mg.playerX == 32 && mg.playerY == 0 ||
                    mg.playerX == 33 && mg.playerY == 0 ||
                    mg.playerX == 34 && mg.playerY == 0 ||
                    mg.playerX == 35 && mg.playerY == 0 ||
                    mg.playerX == 36 && mg.playerY == 0 ||
                    mg.playerX == 37 && mg.playerY == 0) {
                loadMap(Zone.GrassLands, 495, 495);
            }
        } else if (currentWorld == Zone.Ruin_Dungeon) {
            if (mg.playerX == 27 && mg.playerY == 4) {
                loadMap(Zone.Woodland_Edge, 71, 55);
            }
        } else if (currentWorld == Zone.Hillcrest) {
            if (mg.playerX == 0 && mg.playerY == 99 && mg.sqLite.readQuestFacts(QUEST_NAME.IntoTheGrassLands.val, 1) == 5) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.GrassLands, 495, 495);
                }
            } else if (mg.playerX == 64 && mg.playerY == 66) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Treasure_Cave, 26, 18);
                }
            } else if (mg.playerX == 89 && mg.playerY == 1) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Hillcrest_Hermit_Cave, 35, 68);
                }
            } else if (mg.playerX == 99 && mg.playerY == 37) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.The_Grove, 1, 100);
                }
            } else if (mg.playerX == 75 && mg.playerY == 83) {
                loadMap(Zone.Hillcrest_Mountain_Cave, 3, 14);
            } else if (mg.playerX == 87 && mg.playerY == 94) {
                loadMap(Zone.Hillcrest_Mountain_Cave, 56, 57);
            }
        } else if (currentWorld == Zone.Treasure_Cave) {
            if (mg.playerX == 26 && mg.playerY == 18) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    loadMap(Zone.Hillcrest, 64, 65);
                }
            }
        } else if (currentWorld == Zone.Hillcrest_Mountain_Cave) {
            if (mg.playerY == 59 && (mg.playerX == 54 || mg.playerX == 55 || mg.playerX == 56 || mg.playerX == 57 || mg.playerX == 58)) {
                loadMap(Zone.Hillcrest, 88, 96);
            } else if (mg.playerX == 0 && (mg.playerY == 13 || mg.playerY == 14 || mg.playerY == 15 || mg.playerY == 16)) {
                loadMap(Zone.Hillcrest, 75, 85);
                if (!mg.qPanel.PlayerHasQuests(QUEST_NAME.TheFakeNecklace) || mg.sqLite.readQuestFacts(QUEST_NAME.TheFakeNecklace.val, 1) == -1) {
                    if (mg.prj_control.stoneKnightKilled == 0) {
                        synchronized (mg.ENTITIES) {
                            for (ENTITY entity : mg.ENTITIES) {
                                if (entity instanceof BOSS_Knight) {
                                    break;
                                }
                            }
                            mg.ENTITIES.add(new BOSS_Knight(mg, 56 * 48, 91 * 48, mg.player.level, 1, Zone.Hillcrest));
                        }
                    }
                }
            }
        } else if (currentWorld == Zone.The_Grove) {
            if (mg.playerX == 0 && (mg.playerY == 99 || mg.playerY == 100 || mg.playerY == 101)) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Hillcrest, 97, 37);
                }
            } else if (mg.playerX == 3 && mg.playerY == 5) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Goblin_Cave, 10, 26);
                }
            }
        } else if (currentWorld == Zone.Goblin_Cave) {
            if (mg.playerX == 12 && mg.playerY == 25) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.The_Grove, 3, 5);
                }
            }
            if (WorldRender.worldData1[53][92] != 1382) {
                QUEST.openRoundDoor(12, 67);
            }
            if (WorldRender.worldData1[73][37] != 1382) {
                QUEST.openSquareDoor(72, 20);
            }
        } else if (currentWorld == Zone.Hillcrest_Hermit_Cave) {
            if (mg.playerY == 69 && (mg.playerX == 34 || mg.playerX == 35 || mg.playerX == 36)) {
                PlayerPrompts.setETrue();
                if (mg.inputH.e_typed) {
                    mg.inputH.e_typed = false;
                    loadMap(Zone.Hillcrest, 89, 2);
                }
            }
        }
    }


    private void loadArray() {
        for (Map map : MAPS) {
            globalTriggers.addAll(map.spawnTriggers);
        }
        System.out.println(globalTriggers.size() + " total Spawn Triggers!");
    }

    /**
     * @param playerLocation in awt Point
     * @return true if the player is further than 500 pixels from the given points
     */
    public boolean player_went_away(Point playerLocation, int distance) {
        return Point.distance(playerLocation.x, playerLocation.y, Player.worldX, Player.worldY) > distance;
    }

    public void uncoverWorldMap() {
        int playerX = mg.playerX;
        int playerY = mg.playerY;
        int radius = 7;
        int xMin = Math.max(0, playerX - radius);
        int xMax = Math.min(currentMapCover.length - 1, playerX + radius);
        int yMin = Math.max(0, playerY - radius);
        int yMax = Math.min(currentMapCover[0].length - 1, playerY + radius);
        int radiusSquared = radius * radius;
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                int dx = x - playerX;
                int dy = y - playerY;
                if (dx * dx + dy * dy <= radiusSquared && Math.random() > 0.95) {
                    currentMapCover[x][y] = 1;
                }
            }
        }
    }

    private boolean playerInsideRectangle(Point p1, Point p2) {
        int x1 = Math.min(p1.x, p2.x);
        int x2 = Math.max(p1.x, p2.x);
        int y1 = Math.min(p1.y, p2.y);
        int y2 = Math.max(p1.y, p2.y);
        return mg.playerX >= x1 && mg.playerX <= x2 && mg.playerY >= y1 && mg.playerY <= y2;
    }
}


