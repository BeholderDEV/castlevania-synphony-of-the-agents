/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import core.AssetsManager;
import core.actors.player.PlayerHandler;

/**
 *
 * @author Augustop
 */
public class MainMenuScreen extends ImageScreen{

    public MainMenuScreen(ScreenHandler game) {
        super(game, "assets/img/titlescreen.jpg");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isTouched()){
            AssetsManager.loadFirstFaseAssets();
            super.game.setScreen(new GameScreen(super.game, new PlayerHandler()));
            this.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
