package gameworld.player.abilities;

import gameworld.entities.damage.DamageType;
import gameworld.player.PROJECTILE;
import gameworld.player.Player;
import javafx.scene.canvas.GraphicsContext;
import main.system.Storage;

import java.awt.Point;
import java.awt.geom.Point2D;

public class PRJ_AutoShot extends PROJECTILE {

    /**
     * What happens when you press main mouse button
     */
    public PRJ_AutoShot(int x, int y, float damage) {

        //-------VALUES-----------
        this.movementSpeed = 5;
        this.damage = damage;
        this.type = DamageType.PoisonDMG;
        this.projectileHeight = 16;
        this.projectileWidth = 16;
        this.collisionBox = Storage.box_primaryFire;
        this.direction = "downleftrightup";
        //------POSITION-----------
        this.worldPos = new Point2D.Double(Player.worldX + 24 - projectileWidth / 2.0f, Player.worldY + 24 - projectileHeight / 2.0f);
        this.endPos = new Point((int) (worldPos.x + 650), (int) (worldPos.y + 650));
        this.updateVector = getTrajectory(new Point(x, y));
        getPlayerImage();
    }

    @Override
    public void draw(GraphicsContext g2) {
        g2.drawImage(projectileImage1, (int) (worldPos.x - Player.worldX + Player.screenX), (int) (worldPos.y - Player.worldY + Player.screenY), projectileWidth, projectileHeight);
    }

    @Override
    public void update() {
        outOfBounds();
        tileCollision();
        worldPos.x += updateVector.x * movementSpeed;
        worldPos.y += updateVector.y * movementSpeed;
    }

    /**
     *
     */
    @Override
    public void playHitSound() {

    }

    private Point2D.Double getTrajectory(Point mousePosition) {
        double angle = Math.atan2(mousePosition.y - Player.screenY - 24, mousePosition.x - Player.screenX - 24);
        return new Point2D.Double(Math.cos(angle), Math.sin(angle));
    }


    private void getPlayerImage() {
        projectileImage1 = Storage.primaryFire1;
    }
}