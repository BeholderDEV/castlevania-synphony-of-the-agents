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
import com.badlogic.gdx.math.Vector2;
import core.actors.GameActor;
import core.map.MapHandler;

/**
 *
 * @author Augustop
 */
public class PlayerHandler extends GameActor{
    private PlayerAnimation animationHandler;
    private PlayerBehavior behaviorHandler;
    private final Vector2 playerRenderCorrection = new Vector2(0, 0);
    
    public PlayerHandler() {
        super(new Rectangle(0, 0, PlayerBehavior.NORMAL_WIDTH, PlayerBehavior.NORMAL_HEIGHT));
        this.animationHandler = new PlayerAnimation();
        this.behaviorHandler = new PlayerBehavior(this);
    }
    
    @Override
    public void updateActor(float deltaTime, MapHandler map){
        super.stateTime += deltaTime;
        this.behaviorHandler.defineAction(deltaTime, map);
        this.behaviorHandler.updatePosition(deltaTime);
        this.behaviorHandler.checkCollisions(map);
    }
    
    @Override
    public void renderActor(SpriteBatch batch) {
        float x = super.body.x, y = super.body.y, w = super.body.width, h = super.body.height; 
        this.playerRenderCorrection.set(0, 0);
        Rectangle playerRec = super.body;
        TextureRegion currentFrame = this.getCurrentFrame();
        if(playerRec.x < 0){
            playerRec.setX(0);
        }
        if(!super.facingRight){
            x += w;
        }
        if(super.currentState == GameActor.State.ATTACKING){
            this.adjustPlayerRenderCorrections(currentFrame);
            w = currentFrame.getRegionWidth() * MapHandler.unitScale;
            h = currentFrame.getRegionHeight() * MapHandler.unitScale;
            if(this.behaviorHandler.getAtkState() == PlayerBehavior.Atk_State.CROUCH_ATK){
                h += 1.2f;
            }
        }

        batch.draw(currentFrame, 
                (super.facingRight) ? x - this.playerRenderCorrection.x: x + this.playerRenderCorrection.x,
                y + this.playerRenderCorrection.y, 
                (super.facingRight) ? w: -w,
                h);
    }
    
    private void adjustPlayerRenderCorrections(TextureRegion currentFrame){
        switch(currentFrame.getRegionX()){
            case 33:
                if(this.behaviorHandler.getAtkState() != PlayerBehavior.Atk_State.CROUCH_ATK){
                    this.playerRenderCorrection.x = 1.7f;
                    this.playerRenderCorrection.y = -PlayerBehavior.DISTANCE_FROM_GROUND_LAYER;
                }else{
                    this.playerRenderCorrection.x = 1.5f;
                }
            break;
            case 86:
                if(this.behaviorHandler.getAtkState() != PlayerBehavior.Atk_State.CROUCH_ATK){
                    this.playerRenderCorrection.x = 3.7f;
                }else{
                    this.playerRenderCorrection.x = 3.4f;
                }
            break;
            case 157:
                if(this.behaviorHandler.getAtkState() != PlayerBehavior.Atk_State.CROUCH_ATK){
                    this.playerRenderCorrection.y = -0.2f;
                }else{
                    this.playerRenderCorrection.x = -0.4f;
                }                
            break;
        }
    }
    
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
                return this.defineAtkSprite(this.animationHandler.getCorrectAtkAnimation(this.behaviorHandler.getAtkState(), this.behaviorHandler.isUpstairs()));
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
            switch(this.behaviorHandler.getAtkState()){
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
    
    public boolean isDead(){
        return super.currentState == GameActor.State.DYING && this.animationHandler.getDeathAnimation().isAnimationFinished(stateTime);
    }

    
    public void drawRecOnPlayer(SpriteBatch batch){
        this.behaviorHandler.drawRec(batch);
    }
    
    public Rectangle getPlayerBody(){
        return super.body;
    }

    public float getStateTime() {
        return stateTime;
    }
        
    public PlayerBehavior.Atk_State getCurrentAtkState(){
        return this.behaviorHandler.getAtkState();
    }
    
    public void setStateTime(float stateTime) {
        super.stateTime = stateTime;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
    public void changeStateTime(float delta){
        super.stateTime += delta;
    }

    

    
}
