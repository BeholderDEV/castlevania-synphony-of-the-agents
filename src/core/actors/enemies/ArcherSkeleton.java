/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.enemies;

import ai.AgentCreator;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import core.actors.GameActor;
import core.map.MapHandler;
import core.objects.Arrow;
import core.screens.GameScreen;
import core.util.AnimationManager;
import core.util.AssetsManager;

/**
 *
 * @author Adson Esteves
 */
public class ArcherSkeleton extends Enemy{
    
    private Array<Arrow> arrows = new Array<>();
    private TextureRegion arrowImg;
    private float walkingRange;
    private Vector2 initialPosition;
    
    public ArcherSkeleton(int walkingSpeed, Rectangle body, GameScreen gameScreen, float walkingRange) {
        super(walkingSpeed, body, gameScreen);
        super.standImg = standImg = new TextureRegion(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class), 48, 114, 65, 44);
        super.movingAnimation = AnimationManager.generateAnimation(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class),  new int[]{48, 151, 249}, new int[]{114, 114, 114}, new int[]{65, 65, 65}, new int[]{44, 44, 44}, Animation.PlayMode.LOOP, 0.40f);
        super.atkAnimation = AnimationManager.generateAnimation(new TextureRegion(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class), 56, 1, 398, 51), 57, 51, Animation.PlayMode.NORMAL, GameActor.STANDARD_ATK_FRAME_TIME);
        super.spriteAdjustmentForCollision = new float[]{2.5f, 0.4f, -6f, -0.4f};
        this.arrowImg = new TextureRegion(AssetsManager.assets.get("assets/img/bone-archer.png", Texture.class), 332, 119, 33, 8);
        this.body.setSize(super.standImg.getRegionWidth() * MapHandler.unitScale, super .standImg.getRegionHeight()* MapHandler.unitScale);
        this.walkingRange = walkingRange;
        this.initialPosition = new Vector2(body.x, body.y);
        AgentCreator.getInstance().createAgent(EnemyFactory.enemyType.ARCHER_SKELETON, new Object[]{this});
    }
        
    @Override
    public void updateActor(float deltaTime, MapHandler map, Array<GameActor> stageActors) {
        super.stateTime += deltaTime;
        this.updateArrows();
    }
    
    @Override
    public void renderActor(SpriteBatch batch) {
        super.renderActor(batch);
        this.renderArrows(batch);
    }

    @Override
    protected void adjustRenderCorrections(TextureRegion currentFrame) {
        super.renderCorrection.x = -1f;
    }

    @Override
    public void drawDebugRec(SpriteBatch batch) {
        Rectangle arrowBody;
        for (int i = 0; i < this.arrows.size; i++) {
            arrowBody = this.arrows.get(i).getCollisionBody();
            batch.draw(AssetsManager.assets.get("assets/img/squarer.png", Texture.class), arrowBody.x, arrowBody.y + Arrow.DISTANCE_FROM_GROUND, (this.arrows.get(i).isFaceToRight()) ? arrowBody.width: -arrowBody.width, arrowBody.height);
        }
    }
    
    private void updateArrows(){
        Rectangle arrowBody;
        for (int i = 0; i < this.arrows.size; i++) {
            arrowBody = this.arrows.get(i).getCollisionBody();
            if(this.shouldArrowDisappear(arrowBody)){
                this.arrows.removeIndex(i);
                i--;
                continue;
            }
            arrowBody.x = (this.arrows.get(i).isFaceToRight()) ? arrowBody.x + Arrow.ARROW_SPEED: arrowBody.x - Arrow.ARROW_SPEED;
        }
    }
    
    private void renderArrows(SpriteBatch batch){
        Rectangle arrowBody;
        for (int i = 0; i < this.arrows.size; i++) {
            arrowBody = this.arrows.get(i).getCollisionBody();
            batch.draw(this.arrowImg, arrowBody.x, arrowBody.y + 3f, (this.arrows.get(i).isFaceToRight()) ? arrowBody.width: -arrowBody.width, arrowBody.height);
        }
    }
    
    private boolean shouldArrowDisappear(Rectangle arrow){
        OrthographicCamera camera = super.gameScreen.getCamera();
        if(arrow.x + arrow.width > (camera.position.x + camera.viewportWidth / 2f  + camera.viewportWidth * 0.2f)){
            return true;
        }
        if(arrow.x + arrow.width < camera.position.x - camera.viewportWidth / 2f - camera.viewportWidth * 0.2f){
            return true;
        }
        if(arrow.y + arrow.height > (camera.position.y + camera.viewportHeight / 2f  + camera.viewportHeight * 0.2f)){
            return true;
        }
        if(arrow.y + arrow.height < camera.position.y - camera.viewportHeight / 2f - camera.viewportHeight * 0.2f){
            return true;
        }
        return false;
    }

    public Array<Arrow> getArrows() {
        return arrows;
    }

    public float getWalkingRange() {
        return walkingRange;
    }

    public Vector2 getInitialPosition() {
        return initialPosition;
    }
    
    public void setArrows(Array<Arrow> arrows) {
        this.arrows = arrows;
    }
    
}
