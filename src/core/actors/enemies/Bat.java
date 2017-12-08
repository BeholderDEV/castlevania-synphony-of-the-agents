/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import ai.AgentCreator;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import core.actors.GameActor;
import core.map.MapHandler;
import core.screens.GameScreen;
import core.util.AnimationManager;
import core.util.AssetsManager;

/**
 *
 * @author Augustop
 */
public class Bat extends Enemy{    
    
    public Bat(int walkingSpeed, Rectangle body, GameScreen gameScreen) {
        super(walkingSpeed, body, gameScreen);
        super.standImg = standImg = new TextureRegion(AssetsManager.assets.get("assets/img/superIV_Enemies.png", Texture.class), 252, 2329, 17, 18);
        super.movingAnimation = AnimationManager.generateAnimation(new TextureRegion(AssetsManager.assets.get("assets/img/superIV_Enemies.png", Texture.class), 201, 2329, 51, 18), 17, 18, Animation.PlayMode.LOOP, 0.40f);
        super.atkAnimation = super.movingAnimation;
        this.spriteAdjustmentForCollision = new float[]{0.8f, 0.4f, -1.6f, -0.9f};
        this.body.setSize(super.standImg.getRegionWidth() * MapHandler.unitScale, super .standImg.getRegionHeight()* MapHandler.unitScale);
        AgentCreator.getInstance().createAgent(EnemyFactory.enemyType.BAT, new Object[]{this});
    }
    
    @Override
    public void updateActor(float deltaTime, MapHandler map, Array<GameActor> stageActors) {
        super.stateTime += deltaTime;
    }

    @Override
    public void renderActor(SpriteBatch batch) {
        super.renderActor(batch);
    }

    @Override
    protected void adjustRenderCorrections(TextureRegion currentFrame) {
        super.renderCorrection.x = -1f;
    }
    
    @Override
    public void drawDebugRec(SpriteBatch batch) {
        
    }


    
}
