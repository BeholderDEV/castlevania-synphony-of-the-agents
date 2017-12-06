/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.actors.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import core.actors.GameActor;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public class PlayerHandler extends GameActor{
    private final PlayerAnimation animationHandler;
    private final PlayerBehavior behaviorHandler;
    
    
    public PlayerHandler() {
        super(new Rectangle(0, 0, PlayerBehavior.NORMAL_WIDTH, PlayerBehavior.NORMAL_HEIGHT));
        this.animationHandler = new PlayerAnimation();
        this.behaviorHandler = new PlayerBehavior(this);
        this.spriteAdjustmentForCollision = new float[]{0.4f, 0.4f, 1.6f, 0.9f};
    }
    
    @Override
    public void updateActor(float deltaTime, MapHandler map, Array<GameActor> stageActors){
        super.stateTime += deltaTime;
        this.behaviorHandler.defineAction(deltaTime, map);
        super.updatePosition(deltaTime);
        this.behaviorHandler.checkCollisions(deltaTime, map, stageActors);
        if(this.currentState == State.STANDING && this.lifePoints <= 0){
            this.velocity.set(0, 0);
            this.currentState = State.DYING;
        }
    }
    
    @Override
    public void renderActor(SpriteBatch batch) {
        if(super.body.x < 0){
            super.body.setX(0);
        }
        TextureRegion currentFrame = this.getCurrentFrame();
        float[] renderValues = super.getSpriteRenderValues(currentFrame);
        batch.draw(currentFrame, renderValues[0], renderValues[1], renderValues[2], renderValues[3]);
    }
    
    @Override
    protected void adjustRenderCorrections(TextureRegion currentFrame){
        switch(currentFrame.getRegionX()){
            case 33:
                if(this.atkState != GameActor.Atk_State.CROUCH_ATK){
                    super.renderCorrection.x = 1.7f;
                    super.renderCorrection.y = -GameActor.DISTANCE_FROM_GROUND_LAYER;
                }else{
                    super.renderCorrection.x = 1.5f;
                }
            break;
            case 86:
                if(this.atkState != GameActor.Atk_State.CROUCH_ATK){
                    super.renderCorrection.x = 3.7f;
                }else{
                    super.renderCorrection.x = 3.4f;
                }
            break;
            case 157:
                if(this.atkState != GameActor.Atk_State.CROUCH_ATK){
                    super.renderCorrection.y = -0.2f;
                }else{
                    super.renderCorrection.x = -0.4f;
                }                
            break;
        }
    }
    
    @Override
    public TextureRegion getCurrentFrame(){
        switch(super.currentState){
            case WALKING:
                return this.animationHandler.getWalkAnimation().getKeyFrame(super.stateTime, true);
            case JUMPING:
                return this.animationHandler.getJumpImg();
            case CROUNCHING:
                return this.animationHandler.getCrouchImg();
            case ON_STAIRS:
                if(this.behaviorHandler.isUpstairs()) {
                    return this.animationHandler.getUpstairsAnimation().getKeyFrame(super.stateTime, true);
                }else{
                    return this.animationHandler.getDownstairsAnimation().getKeyFrame(super.stateTime, true);
                }
            case DYING:
                return this.defineDeathSprite();
            case ATTACKING:
                return this.defineAtkSprite(this.animationHandler.getCorrectAtkAnimation(this.atkState, this.behaviorHandler.isUpstairs()));
            case HURTED:
                return this.animationHandler.getHurtedImg();
            default:
                return this.animationHandler.getStandImg();
        }
    }
    
    private TextureRegion defineDeathSprite(){
        if(this.animationHandler.getDeathAnimation().isAnimationFinished(stateTime)){
            return this.animationHandler.getDeathAnimation().getKeyFrame(super.stateTime);
        }
        TextureRegion deathSprite = this.animationHandler.getDeathAnimation().getKeyFrame(super.stateTime);
        super.body.width = deathSprite.getRegionWidth() * MapHandler.unitScale;
        super.body.height = deathSprite.getRegionHeight() * MapHandler.unitScale;
        return deathSprite;
    }
    
    private TextureRegion defineAtkSprite(Animation<TextureRegion> atkAnimation){
        if(atkAnimation.isAnimationFinished(stateTime)){
            switch(this.atkState){
                case CROUCH_ATK:
                    super.body.setSize(PlayerBehavior.NORMAL_WIDTH, Math.round(PlayerBehavior.NORMAL_HEIGHT - PlayerBehavior.NORMAL_HEIGHT * 0.25));
                    super.currentState = GameActor.State.CROUNCHING;
                    return this.animationHandler.getCrouchImg();
                case JUMP_ATK:
                    super.body.setSize(PlayerBehavior.NORMAL_WIDTH, PlayerBehavior.NORMAL_HEIGHT);
                    super.currentState = GameActor.State.JUMPING;
                    return this.animationHandler.getJumpImg();
                case STAIRS_ATK:
                    super.body.setSize(PlayerBehavior.NORMAL_WIDTH, PlayerBehavior.NORMAL_HEIGHT);
                    super.currentState = GameActor.State.ON_STAIRS;
                    return (this.behaviorHandler.isUpstairs()) ? 
                            this.animationHandler.getUpStairsAtkAnimation().getKeyFrame(super.stateTime) :
                            this.animationHandler.getDownstairsAnimation().getKeyFrame(super.stateTime);
                default:
                    super.body.setSize(PlayerBehavior.NORMAL_WIDTH, PlayerBehavior.NORMAL_HEIGHT);
                    super.currentState = GameActor.State.STANDING;
                    return this.animationHandler.getStandImg();
            }
        }
        return atkAnimation.getKeyFrame(super.stateTime);
    }

    @Override
    public void receiveDamage(Rectangle dmgReason, int dmgPoints) {
        if(super.currentState == GameActor.State.HURTED || super.currentState == State.DYING){
            return;
        }
        this.behaviorHandler.receiveDamage(dmgReason, dmgPoints);
    }
    
    public boolean isDead(){
        return super.currentState == GameActor.State.DYING && this.animationHandler.getDeathAnimation().isAnimationFinished(stateTime);
    }

    
    public void drawDebugRec(SpriteBatch batch){
        this.behaviorHandler.drawRec(batch);
    }
    
    public float getStateTime() {
        return stateTime;
    }
        
    public Atk_State getCurrentAtkState(){
        return this.atkState;
    }
    
    public void changeStateTime(float delta){
        super.stateTime += delta;
    }

    

    
}
