package main.system.ui.talentpane;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;

public class TalentNode {
    public final Rectangle boundBox;
    private final TALENT talent;
    private final Image nodeImage;
    public int toolTipTimer;
    int pointsSpent;
    public Point position;
    public boolean activated;
    public int id;
    public int size = 0;


    TalentNode(TALENT talent, int xCo, int yCo) {
        this.boundBox = new Rectangle(944 + xCo, 524 + yCo, 32, 32);
        this.nodeImage = setup();
        this.talent = talent;
        this.position = new Point(xCo, yCo);
        this.id = talent.i_id;
    }

    TalentNode(TALENT talent, int xCo, int yCo, int size) {
        this.boundBox = new Rectangle(944 + xCo, 524 + yCo, 32, 32);
        this.nodeImage = setup();
        this.talent = talent;
        this.position = new Point(xCo, yCo);
        this.id = talent.i_id;
        this.size = size;
    }


    public void drawNode(GraphicsContext gc, int x, int y, Image image) {
        int drawx = position.x + x;
        int drawy = position.y + y;
        gc.drawImage(image, drawx, drawy);
        if (size == 1) {
            talent.drawIcon(gc, drawx + 10, drawy + 10);
        } else {
            talent.drawIcon(gc, drawx + 8, drawy + 8);
        }
        boundBox.x = drawx;
        boundBox.y = drawy;
    }


    private Image setup() {
        return new Image((Objects.requireNonNull(getClass().getResourceAsStream("/ui/talents/talentnode.png"))));
    }
}


