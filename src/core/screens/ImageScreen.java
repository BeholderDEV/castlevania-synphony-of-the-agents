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
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import core.AssetsManager;
import core.actors.player.PlayerHandler;

/**
 *
 * @author Augustop
 */
public abstract class ImageScreen implements Screen{
    protected final ScreenHandler game;
    protected Texture screenImage;
    protected OrthographicCamera camera;
    
    public ImageScreen(ScreenHandler game, String imgPath) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.screenImage = AssetsManager.assets.get(imgPath, Texture.class);
    }
    
    @Override
    public void show() {
    }

    @Override
    public void render(float f) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.camera.update();
        this.game.batch.setProjectionMatrix(camera.combined);

        this.game.batch.begin();
        this.game.batch.draw(this.screenImage, 0, 0, this.camera.viewportWidth, this.camera.viewportHeight);
        this.game.batch.end();
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            this.dispose();
            AssetsManager.assets.clear();
            Gdx.app.exit();
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
        AssetsManager.assets.unload(((FileTextureData)this.screenImage.getTextureData()).getFileHandle().path());
    }
    
}
