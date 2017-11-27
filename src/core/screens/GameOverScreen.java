/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import core.actors.player.PlayerHandler;

/**
 *
 * @author Augustop
 */
public class GameOverScreen extends ImageScreen{
    
    public GameOverScreen(ScreenHandler game) {
        super(game, "assets/img/gameover_screen.png");
    }
    
    @Override
    public void show() {
        
    }

    @Override
    public void render(float f) {
        super.render(f);
        if(Gdx.input.isKeyPressed(Input.Keys.ENTER) || Gdx.input.isTouched()){
            super.game.setScreen(new GameScreen(super.game, new PlayerHandler()));
            this.dispose();
        }
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        super.dispose();
    }
    
}
