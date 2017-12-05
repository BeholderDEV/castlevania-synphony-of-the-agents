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
import core.actors.player.PlayerAnimation;
import core.map.MapHandler;
import core.objects.Arrow;
import core.screens.GameScreen;
import core.util.AnimationManager;
import core.util.AssetsManager;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adson Esteves
 */
public class ArcherSkeleton extends Enemy{
    
    List<Arrow> arrows = new ArrayList<>();
    TextureRegion arrowImg;
    

    public ArcherSkeleton(int walkingSpeed, Rectangle body, GameScreen gameScreen) {
        super(walkingSpeed, body, gameScreen);
        super.standImg = standImg = new TextureRegion(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class), 0, 52*2, 57, 52);
        super.movingAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class),  new int[]{57*0, 57*1, 57*2}, new int[]{52*2, 52*2, 52*2}, new int[]{57, 57, 57}, new int[]{52, 52, 52}, Animation.PlayMode.LOOP, 0.40f);
        super.atkAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class),  new int[]{57*0, 57*1, 57*2, 57*3, 57*4, 57*5, 57*6}, new int[]{0, 0, 0, 0, 0, 0, 0}, new int[]{57, 57, 57, 57, 57, 57, 57}, new int[]{52, 52, 52, 52, 52, 52, 52}, Animation.PlayMode.NORMAL, PlayerAnimation.STANDARD_ATK_FRAME_TIME);
        this.spriteAdjustmentForCollision = new float[]{0.4f, 0.4f, 1.6f, 0.9f};
        this.arrowImg = new TextureRegion(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class), 57*3, 52*2, 57, 52);
        AgentCreator.getInstance().createAgent(AgentCreator.AgentType.SKELETON_ARCHER, new Object[]{this});
    }

    @Override
    public void updateActor(float deltaTime, MapHandler map, Array<GameActor> stageActors) {
        super.stateTime += deltaTime;
    }
    
    @Override
    public void renderActor(SpriteBatch batch) {
        if(super.blinking && super.blinkPeriod >= Enemy.BLINK_INTERVAL){
            super.blinkPeriod = 0;
            return;
        }
        if(super.body.x < 0){
            super.body.setX(0);
        }
        TextureRegion currentFrame = this.getCurrentFrame();
        float[] renderValues = super.getSpriteRenderValues(currentFrame);
        batch.draw(currentFrame, renderValues[0], renderValues[1], renderValues[2], renderValues[3]);
        for (Arrow arrow : arrows) {
            float w = 6f;
            if(arrow.isFaceToRight()){
                arrow.positionX -= this.renderCorrection.x;
            }else{
                arrow.positionX += this.renderCorrection.x;
                w *= -1;
            }
            batch.draw(arrowImg, arrow.positionX, arrow.positionY, w, 6f);
        }
    }

    @Override
    protected void adjustRenderCorrections(TextureRegion currentFrame) {
        super.renderCorrection.x = -1f;
    }

    @Override
    public void drawDebugRec(SpriteBatch batch) {
        if(super.currentState == GameActor.State.ATTACKING && super.stateTime >= GameActor.STANDARD_ATK_FRAME_TIME * 2){
            float w = 3.5f;
            float x = (super.facingRight) 
                      ? super.body.x + super.body.width
                      : super.body.x - w;
            float y = (super.body.y + super.body.height) - super.body.height * 0.35f;
            float h = 1;
            batch.draw(AssetsManager.assets.get("assets/img/square.png", Texture.class), x, y, w, h);
        }
    }

    public List<Arrow> getArrows() {
        return arrows;
    }

    public void setArrows(List<Arrow> arrows) {
        this.arrows = arrows;
    }
    
    
}
