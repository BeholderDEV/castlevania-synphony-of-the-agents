/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.behavior;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import core.actors.CollisionHandler;
import core.actors.GameActor;
import core.actors.enemies.Enemy;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 5674867
 */
public class SwordAgentBehavior extends AgentBehavior{
    public static final float SWORD_DISTANCE_TO_ATK = 6f;
    private AID patronArcherAddress;
    private final Vector2 patronPosition = new Vector2();
    private boolean closeToPatron = false;
    private boolean alreadySendedConfirmationMsg = false;
    public static final int rangeToStayAwayFromPlayer = 5;
    private float definedDistance = 0f;
    
    public SwordAgentBehavior(Enemy container, Agent a, long period) {
        super(container, a, period, SWORD_DISTANCE_TO_ATK);
        this.definedDistance = this.rand.nextInt(rangeToStayAwayFromPlayer) * ((this.rand.nextInt(2) == 0) ? -1: 1);
    }
    
    @Override
    public void defineAction(){
        if(super.container.isIsBlinking()){
            super.updateHurted();
        }
        ACLMessage msg = super.myAgent.receive();
        if(msg != null){
            this.checkReceivedMsg(msg);
        }
        this.checkIfCloseToPatron();
        switch(super.container.getCurrentState()){
            case STANDING:
                this.defineActionStanding();
            break;
            case WALKING:
                this.defineActionWalking();
            break;
            case ATTACKING:
                super.updateAtk();
                this.container.getVelocity().set(0, 0);
            break;
            case JUMPING:
                super.container.fallFromJump();
            break;
            case DYING:
                this.realizeAgentTakeDown();
            break;
        }
    }
    
    private void defineActionStanding(){
        if(this.patronArcherAddress != null && super.container.foundPlayer() && !this.closeToPatron){
            this.lurePlayerToArcher();
            super.container.setFacingRight(super.container.getBody().x - super.player.getBody().x < 0);
            return;
        }
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().x = (super.container.foundPlayer()) ? super.container.getWalkingSpeed(): super.container.getWalkingSpeed() / 2f;
        super.container.setFacingRight(super.container.getBody().x - super.player.getBody().x < 0);
    }
        
    private void defineActionWalking(){
        if(super.checkIfCanAtk()){
            return;
        }
        if(super.container.getStateTime() >= 1f){
            super.container.setStateTime(0);
            super.container.setFacingRight(super.container.getBody().x - super.player.getBody().x < 0);
        }
        if(this.patronArcherAddress != null && super.container.foundPlayer() && !this.closeToPatron){
            this.lurePlayerToArcher();
        }
    }
    
    private void lurePlayerToArcher(){
        float dx = Math.abs(super.container.getBody().x - this.player.getBody().x);
        if(dx > 20 - this.definedDistance){
            super.container.getVelocity().setZero();
            super.container.setCurrentState(GameActor.State.STANDING);
            return;
        }
        this.definedDistance = this.rand.nextInt(rangeToStayAwayFromPlayer) * ((this.rand.nextInt(2) == 0) ? -1: 1);
        super.container.setCurrentState(GameActor.State.WALKING);
        super.container.getVelocity().x = super.container.getWalkingSpeed();
        super.container.setFacingRight(super.container.getBody().x - this.patronPosition.x < 0);
    }
    
    private void checkIfCloseToPatron(){
        if(this.patronArcherAddress == null){
            return;
        }
//        System.out.println(super.myAgent.getAID().getName() +" --- "+ this.patronArcherAddress);
        float dx = Math.abs(super.container.getBody().x - this.patronPosition.x);
        this.closeToPatron = dx < ArcherAgentBehavior.ARCHER_DISTANCE_TO_ATK / 2f;
    }

    @Override
    public void checkCollisions(){
        this.groundBehavior();
        this.wallBehavior();
        if(super.container.getCurrentState() == GameActor.State.ATTACKING && super.container.getStateTime() >= GameActor.STANDARD_ATK_FRAME_TIME * 2){
            this.updateWeaponHit();
        }
    }
    
    @Override
    protected void groundBehavior(){
        CollisionHandler.checkGroundCollision(super.container.getGameScreen().getMapHandler(), super.container);
        if(super.container.getCurrentState() == GameActor.State.JUMPING){
            if(super.container.getVelocity().y == 0){
                super.container.getVelocity().y = super.container.getJumpingSpeed();
                if(super.rand.nextInt(10) > 6){
                    return;
                }
                if(this.patronArcherAddress != null && super.container.foundPlayer() && !this.closeToPatron){
                    super.container.setFacingRight(super.container.getBody().x - this.patronPosition.x < 0);
                }
            }
        }else{
            super.container.getVelocity().y = 0;
        }
    }
    
    @Override
    public void checkStatus(){
        if(super.container.getLifePoints() <= 0 || super.container.getBody().y < 0){
            super.container.getVelocity().set(0, 0);
            super.container.setCurrentState(GameActor.State.DYING);
        }
    }
    
        
    @Override
    protected void realizeAgentTakeDown(){
        if(this.patronArcherAddress == null){
            try {
                DFService.deregister(super.myAgent);
            } catch (FIPAException ex) {
                Logger.getLogger(SwordAgentBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(this.patronArcherAddress);
            msg.setContent("Guardian died");
            super.myAgent.send(msg);
        }
        super.myAgent.doDelete();
    }

    private void updateWeaponHit(){
        Rectangle weaponArea = CollisionHandler.rectanglePool.obtain();
        float w = 5f;
        float x = (super.container.isFacingRight()) 
                  ? super.container.getBody().x + super.container.getBody().width
                  : super.container.getBody().x - w;
        float y = (super.container.getBody().y + super.container.getBody().height) - super.container.getBody().height * 0.35f;
        float h = 1;
        weaponArea.set(x, y, w, h);
        if(CollisionHandler.checkCollisionBetweenBodyAndObject(super.player, weaponArea)){
            super.player.receiveDamage(super.container.getBody(), 1);
        }
        CollisionHandler.rectanglePool.free(weaponArea);
    }
    
    private void checkReceivedMsg(ACLMessage msg){
        if(msg.getContent().startsWith("Requesting guard")){
            this.receiveRequestForGuard(msg);
        }
        if(msg.getContent().startsWith("Guard confirmation")){
            this.confirmGuard(msg);
        }
        if(msg.getContent().startsWith("Update Guard Position")){
            String[] params = msg.getContent().split(",");
            this.patronPosition.set(Float.parseFloat(params[1]), Float.parseFloat(params[2]));
        }
        if(msg.getContent().startsWith("Patron died")){
            this.patronArcherAddress = null;
            this.closeToPatron = false;
            this.alreadySendedConfirmationMsg = false;
        }
    }
    
    private void receiveRequestForGuard(ACLMessage msg){
        if(this.patronArcherAddress == null && !this.alreadySendedConfirmationMsg){
            this.sendAcceptanceMsg(msg);
            return;
        }
    }
    
    private void sendAcceptanceMsg(ACLMessage msg){
        this.alreadySendedConfirmationMsg = true;
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        reply.setContent("Accept");
        super.myAgent.send(reply);
    }
    
    private void confirmGuard(ACLMessage msg){
        try {
            this.patronArcherAddress = msg.getSender();
            String[] params = msg.getContent().split(",");
            this.patronPosition.set(Float.parseFloat(params[1]), Float.parseFloat(params[2]));
            DFService.deregister(super.myAgent);
        } catch (FIPAException ex) {
            Logger.getLogger(SwordAgentBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AID getPatronArcher() {
        return patronArcherAddress;
    }

    public void setPatronArcher(AID patronArcher) {
        this.patronArcherAddress = patronArcher;
    }
}
