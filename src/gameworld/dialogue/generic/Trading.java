package gameworld.dialogue.generic;

import gameworld.dialogue.Dialog;
import main.MainGame;

public class Trading extends Dialog {
    /**
     * Trading dialog framework
     *
     * @param mg   maingame instance
     * @param type
     */
    protected Trading(MainGame mg, int type) {
        super(mg, type);
        this.type = type;
        this.mg = mg;
        stage = 1;
        load_text();
    }

    /**
     * allows the dialog to check for stages and update progress
     */
    @Override
    public void script() {

    }
}
