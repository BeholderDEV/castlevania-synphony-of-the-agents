/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jade.core.Runtime;

/**
 *
 * @author Augustop
 */

public class AgentCreator {
    public enum AgentType{
        SKELETON_SWORD,
        SKELETON_ARCHER
    }
    
    private static AgentCreator instance = null;
    private final HashMap<String, ContainerController> containerMap = new HashMap<>();
    public static final long BEHAVIOR_DELAY = 18; // milliseconds
    public static int swordNameCount = 1;
    public static int archerNameCount = 1;
    
    private AgentCreator(){
        Runtime rt = Runtime.instance();
	Profile p = new ProfileImpl();
	p.setParameter(Profile.MAIN_HOST, "localhost");
	p.setParameter(Profile.MAIN_PORT, "1099");
	p.setParameter(Profile.CONTAINER_NAME, "Main-Container");
//        p.setParameter(Profile.GUI, "true");
        this.containerMap.put("Main-Container", rt.createMainContainer(p));
    }
    
    public static AgentCreator getInstance(){
        if(instance == null){
            instance = new AgentCreator();
        }
        return instance;
    }
    
    public void createContainer(String host, String port, String name){
        Runtime rt = Runtime.instance();
	Profile p = new ProfileImpl();
	p.setParameter(Profile.MAIN_HOST, host);
	p.setParameter(Profile.MAIN_PORT, port);
	p.setParameter(Profile.CONTAINER_NAME, name);
        this.containerMap.put(name, rt.createAgentContainer(p));
    }
    
    public void createAgent(String containerName, AgentType agentType, Object[] args){
        ContainerController cc = this.containerMap.get(containerName);
        try {
            switch(agentType){
                case SKELETON_SWORD:
                    cc.createNewAgent("Skeleton_Sword_" + swordNameCount++, "ai.AgentCore", args).start();
                break;
                case SKELETON_ARCHER:
                    cc.createNewAgent("Skeleton_Archer_" + archerNameCount++, "ai.AgentCore", args).start();
                break;
            }
        } catch (StaleProxyException ex) {
            Logger.getLogger(AgentCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createAgent(AgentType agentType, Object[] args){
        this.createAgent("Main-Container", agentType, args);
    }
}
