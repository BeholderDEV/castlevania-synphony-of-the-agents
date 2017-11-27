/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.AssetsManager;
import core.actors.player.PlayerHandler;

/**
 *
 * @author Augustop
 */
public class ScreenHandler extends Game{
    public SpriteBatch batch;
    public BitmapFont font;

    public void create() {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
//        AssetsManager.assets.load("assets/img/titlescreen.jpg", Texture.class);
//        AssetsManager.assets.finishLoading();
//        this.setScreen(new MainMenuScreen(this));
        AssetsManager.loadFirstFaseAssets();
        this.setScreen(new GameScreen(this, new PlayerHandler()));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        this.batch.dispose();
        this.font.dispose();
    }
}
