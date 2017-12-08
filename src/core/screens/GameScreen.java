/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import core.actors.CollisionHandler;
import core.util.AssetsManager;
import core.actors.GameActor;
import core.actors.enemies.Enemy;
import core.map.MapHandler;
import core.actors.player.PlayerHandler;
import static core.util.AssetsManager.assets;

/**
 *
 * @author Augustop
 */
public class GameScreen implements Screen {
    public static final boolean DEBUGGING = true;
    public static final int SCREEN_WIDTH = 50;
    public static final int SCREEN_HEIGHT = 20;
    private final ScreenHandler game;
    private Array<GameActor> actors = new Array<>();
    private final MapHandler mapHandler;
    private OrthographicCamera camera;
    private Vector2 oldCameraPosition = new Vector2(0, 0);
    private float debugCameraSpeed = 1f;
    private final TextureRegion heartImg;
    private float lastDelta;
    private Music bgm;
    
    public GameScreen(final ScreenHandler game, PlayerHandler player) {
        this.game = game;
        
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

//        this.mapHandler = new MapHandler("assets/map/mapadahora.tmx");
        this.heartImg = new TextureRegion(AssetsManager.assets.get("assets/img/heart.png", Texture.class), 16,16);
//        this.mapHandler = new MapHandler("assets/map/muitogrande.tmx");
        this.mapHandler = new MapHandler("assets/map/mapadahora2.tmx"); 
        
        this.camera.update();
        this.mapHandler.getMapRenderer().setView(camera);
        this.createActors();
        this.bgm = assets.get("assets/sound/bloodyTears.mp3", Music.class);
    }
    
    private void createActors(){
        PlayerHandler player = new PlayerHandler();
        player.getBody().setPosition(340, 3.4f);

        this.actors.add(player);
        MapObjects objects = this.mapHandler.getMapObjetcs();
        Rectangle rectObject;
        for (MapObject object : objects) {
            if(!(object instanceof RectangleMapObject)){
                continue;
            }
            rectObject = ((RectangleMapObject)object).getRectangle();
            switch(object.getName()){
                case "player":
                    player.getBody().setPosition(rectObject.x, rectObject.y);
                break;
                case "sword":
//                    this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.SWORD_SKELETON, 12, new Vector2(rectObject.x, rectObject.y), this));
                break;
                case "archer":
//                    this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.ARCHER_SKELETON, 6, new Vector2(rectObject.x, rectObject.y), this));
                break;
                case "bat":
//                    this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.BAT, 12, new Vector2(rectObject.x, rectObject.y), this));
                break;
            }
        }
//        this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.ARCHER_SKELETON, 6, new Vector2(153, 3.4f), this));
//        this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.ARCHER_SKELETON, 6, new Vector2(133, 3.4f), this));
//        this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.ARCHER_SKELETON, 6, new Vector2(143, 3.4f), this));
//        this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.SWORD_SKELETON, 14, new Vector2(84, 3.4f), this));
//        this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.SWORD_SKELETON, 14, new Vector2(83, 3.4f), this));
//        this.actors.add(EnemyFactory.createEnemy(EnemyFactory.enemyType.BAT, 14, new Vector2(84, 3.4f), this));
    }

    @Override
    public void render(float delta) {
        lastDelta = delta;
        Gdx.gl.glClearColor(0.5f, 0.5f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (GameActor actor : actors) {
            actor.setPossibleToRender(true);
            actor.updateActor(delta, mapHandler, this.actors);
        }
        this.updateCameraPosition();
        this.camera.update();

        this.mapHandler.getMapRenderer().setView(camera);
        this.mapHandler.getMapRenderer().render();
        this.game.batch.setProjectionMatrix(camera.combined);
        this.game.batch.begin();

        
        for (GameActor actor : actors) {
            actor.renderActor(this.game.batch);
            if(DEBUGGING){
                actor.drawRecOverBody(this.game.batch);
                actor.drawDebugRec(this.game.batch);
            }
        }
        for(int i=0; i<this.actors.get(0).getLifePoints(); i++){
            this.game.batch.draw(heartImg, this.camera.position.x-SCREEN_WIDTH/2f+2+i, this.camera.position.y+SCREEN_HEIGHT/2f-2,1f,1f);
        }
        this.game.batch.end();
        this.playSounds();
        this.verifyPlayerDeath();
        this.verifyEnemyDeath();
        this.verifyMenuInputs();
    }
    
    private void playSounds(){
        GameActor player = this.actors.get(0);
        this.playAtkSound(player);
        this.playHurtSound(player);
    }
    
    private void playAtkSound(GameActor player){
        Music whip = assets.get("assets/sound/whipSound.wav", Music.class);
        if(player.getCurrentState() == GameActor.State.ATTACKING){
            if(whip.isPlaying()){
                return;
            }
            if(player.getAtkState() != GameActor.Atk_State.JUMP_ATK && player.getStateTime() > GameActor.STANDARD_ATK_FRAME_TIME * 1.8f){
                assets.get("assets/sound/whipSound.wav", Music.class).play();
            }
            if(player.getAtkState() == GameActor.Atk_State.JUMP_ATK && player.getStateTime() >= 0.2f){
                assets.get("assets/sound/whipSound.wav", Music.class).play();
            }
        }else{
            whip.stop();
        }
    }
    
    private void playHurtSound(GameActor player){
        Music hurt = assets.get("assets/sound/hurtSound.wav", Music.class);
        if(player.getCurrentState() == GameActor.State.HURTED){
            if(!hurt.isPlaying()){
                hurt.play();
            }
        }else{
            hurt.stop();
        }
    }
        
    private void verifyPlayerDeath(){
        if(this.actors.get(0).getBody().y + this.actors.get(0).getBody().height < 0 || this.actors.get(0).isDead()){
            AssetsManager.assets.load("assets/img/gameover_screen.jpeg", Texture.class);
            for (GameActor actor : actors) {
                if(actor instanceof Enemy){
                    ((Enemy)actor).setForDelete();
                }
            }
            AssetsManager.assets.finishLoading();
            this.bgm.stop();
            this.game.setScreen(new GameOverScreen(game));
            this.mapHandler.disposeMap();
        }
    }
    
    private void verifyEnemyDeath(){
        for (int i = 1; i < this.actors.size; i++) {
            if(this.actors.get(i).isDead()){
                CollisionHandler.rectanglePool.free(this.actors.get(i).getBody());
                this.actors.removeIndex(i);
                i--;
                assets.get("assets/sound/enemyDeath.mp3", Sound.class).play(0.5f, 2f, 0);
            }
        }
    }
    
    private void updateCameraPosition(){
        //Debug
        if(Gdx.input.isKeyPressed(Input.Keys.J) || Gdx.input.isKeyPressed(Input.Keys.L) || Gdx.input.isKeyPressed(Input.Keys.K) || Gdx.input.isKeyPressed(Input.Keys.I) || Gdx.input.isKeyPressed(Input.Keys.BACKSPACE) || (this.oldCameraPosition.x != 0 && this.oldCameraPosition.y != 0)){
            if(this.oldCameraPosition.x == 0 && this.oldCameraPosition.y == 0){
                this.oldCameraPosition.set(this.camera.position.x, this.camera.position.y);
            }            
            this.moveCameraForDebug();
            return;
        }
        TiledMapTileLayer mapFrontLayer = (TiledMapTileLayer) this.mapHandler.getMapRenderer().getMap().getLayers().get("front");
        this.updateCameraX(mapFrontLayer);
        this.updateCameraY(mapFrontLayer);
    }
    
    private void moveCameraForDebug(){
        if(Gdx.input.isKeyPressed(Input.Keys.J)){
            this.camera.position.x -= this.debugCameraSpeed;
            if(this.camera.position.x - SCREEN_WIDTH / 2f < 0){
                this.camera.position.x += this.debugCameraSpeed;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.L)){
            this.camera.position.x += this.debugCameraSpeed;
            if(this.camera.position.x + SCREEN_WIDTH / 2f  > this.mapHandler.getMapWidth()){
                this.camera.position.x -= this.debugCameraSpeed;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.K)){
            this.camera.position.y -= this.debugCameraSpeed;
            if(this.camera.position.y - SCREEN_HEIGHT / 2f  < 0){
                this.camera.position.y += this.debugCameraSpeed;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.I)){
            this.camera.position.y += this.debugCameraSpeed;
            if(this.camera.position.y + SCREEN_HEIGHT / 2f  > this.mapHandler.getMapHeight()){
                this.camera.position.y -= this.debugCameraSpeed;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)){
            this.oldCameraPosition.set(0, 0);
        }
    }
    
    private void updateCameraX(TiledMapTileLayer mapFrontLayer){
        if(this.actors.get(0).getBody().x + this.actors.get(0).getBody().width / 2 <= this.SCREEN_WIDTH / 2){
            this.camera.position.x = this.SCREEN_WIDTH / 2;
            return;
        }
        if(this.actors.get(0).getBody().x + this.actors.get(0).getBody().width / 2 >= mapFrontLayer.getWidth() - this.SCREEN_WIDTH / 2){
            this.camera.position.x = mapFrontLayer.getWidth() - this.SCREEN_WIDTH / 2;
            return;
        }
        this.camera.position.x = this.actors.get(0).getBody().x + this.actors.get(0).getBody().width / 2;

    }
    private void updateCameraY(TiledMapTileLayer mapFrontLayer){
        if(this.actors.get(0).getBody().y + this.actors.get(0).getBody().height / 2 <= this.SCREEN_HEIGHT / 2){
            this.camera.position.y = this.SCREEN_HEIGHT / 2;
            return;
        }
        if(this.actors.get(0).getBody().y + this.actors.get(0).getBody().height / 2 >= mapFrontLayer.getHeight() - this.SCREEN_HEIGHT / 2){
            this.camera.position.y = mapFrontLayer.getHeight() - this.SCREEN_HEIGHT / 2;
            return;
        }
        this.camera.position.y = this.actors.get(0).getBody().y + this.actors.get(0).getBody().height / 2;
    }
    
    private void verifyMenuInputs(){
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            this.dispose();
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        this.bgm.setLooping(true);
        this.bgm.setVolume(0.5f);
        this.bgm.play();
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
        this.mapHandler.disposeMap();
        AssetsManager.assets.clear();
    }

    public Array<GameActor> getActors() {
        return actors;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getLastDelta() {
        return lastDelta;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }
    
}