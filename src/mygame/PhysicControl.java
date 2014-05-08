/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author Ejer
 */
public class PhysicControl extends RigidBodyControl 
    implements PhysicsCollisionListener {
    
    public PhysicControl(){
    }

    public void collision(PhysicsCollisionEvent event) {
        if ("Box".equals(event.getNodeA().getName()) || "Box".equals(event.getNodeB().getName())){
            if ("Bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
                if ("Bullet".equals(event.getNodeA().getName())){
                }
            }
        }
    }
    
}
//public class PhysicsControl extends RigidBodyControl
//        implements PhysicsCollisionListener {
//
//    public PhysicsControl() {
//    }
//
//    public void collision(PhysicsCollisionEvent event) {
//        if (event.getNodeB() != null && event.getNodeA() != null) {
//            if (event.getNodeB().getControl(MissileControl.class) != null) {
//                event.getNodeB().getControl(MissileControl.class).hitAsteroid(event.getNodeA().getControl(AsteroidControl.class));
//            } else if (event.getNodeA().getControl(MissileControl.class) != null) {
//                event.getNodeA().getControl(MissileControl.class).hitAsteroid(event.getNodeA().getControl(AsteroidControl.class));
//            } else if (event.getNodeA().getControl(PlayerControl.class) != null || event.getNodeB().getControl(PlayerControl.class) != null) {
//                System.out.println("AV FOR HELVEDE!!!");
//            }
//
//        }
//    }
//}