package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    //Temporary vectors used on each frame.
    //They here to avoid instanciating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private RigidBodyControl boxPhy;
    
    public static void main(String[] args) {
        Main app = new Main();

        app.start();
    }

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // We re-use the flyby camera for rotation, while positioning is handled by physics
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(100);
        setUpKeys();
        setUpLight();

        // Outter walls:
        Box wall1 = new Box(Vector3f.ZERO, 0.1f, 3, 50);
        Box wall2 = new Box(Vector3f.ZERO, 0.1f, 3, 50);
        Box wall3 = new Box(Vector3f.ZERO, 50, 3, 0.1f);
        Box wall4 = new Box(Vector3f.ZERO, 50, 3, 0.1f);
        Geometry geom1 = new Geometry("Box", wall1);
        Geometry geom2 = new Geometry("Box", wall2);
        Geometry geom3 = new Geometry("Box", wall3);
        Geometry geom4 = new Geometry("Box", wall4);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        geom1.setMaterial(mat1);
        geom2.setMaterial(mat1);
        geom3.setMaterial(mat1);
        geom4.setMaterial(mat1);
        Vector3f wallPlacement1 = new Vector3f(0.0f, 3.0f, -50.0f);
        Vector3f wallPlacement2 = new Vector3f(-100.0f, 3.0f, -50.0f);
        Vector3f wallPlacement3 = new Vector3f(-50.0f, 3.0f, -100.0f);
        Vector3f wallPlacement4 = new Vector3f(-50.0f, 3.0f, 0.0f);
        geom1.setLocalTranslation(wallPlacement1);
        geom2.setLocalTranslation(wallPlacement2);
        geom3.setLocalTranslation(wallPlacement3);
        geom4.setLocalTranslation(wallPlacement4);
        rootNode.attachChild(geom1);
        rootNode.attachChild(geom2);
        rootNode.attachChild(geom3);
        rootNode.attachChild(geom4);

        // Start area:
        Box wall5 = new Box(Vector3f.ZERO, 0.1f, 3, 20);
        Geometry geom5 = new Geometry("Box", wall5);
        geom5.setMaterial(mat1);
        Vector3f wallPlacement5 = new Vector3f(-20.0f, 3.0f, -20.0f);
        geom5.setLocalTranslation(wallPlacement5);
        rootNode.attachChild(geom5);

        Box wall6 = new Box(Vector3f.ZERO, 20, 3, 0.1f);
        Geometry geom6 = new Geometry("Box", wall6);
        geom6.setMaterial(mat1);
        Vector3f wallPlacement6 = new Vector3f(-20.0f, 3.0f, -60.0f);
        geom6.setLocalTranslation(wallPlacement6);
        rootNode.attachChild(geom6);

        Box wall7 = new Box(Vector3f.ZERO, 10, 3, 0.1f);
        Geometry geom7 = new Geometry("Box", wall7);
        geom7.setMaterial(mat1);
        Vector3f wallPlacement7 = new Vector3f(-30.0f, 3.0f, -40.0f);
        geom7.setLocalTranslation(wallPlacement7);
        rootNode.attachChild(geom7);

        // Secret room
        Box wall8 = new Box(Vector3f.ZERO, 0.1f, 3, 10);
        Geometry geom8 = new Geometry("Box", wall8);
        geom8.setMaterial(mat1);
        Vector3f wallPlacement8 = new Vector3f(-40.0f, 3.0f, -90.0f);
        geom8.setLocalTranslation(wallPlacement8);
        rootNode.attachChild(geom8);

        Box wall9 = new Box(Vector3f.ZERO, 20, 3, 0.1f);
        Geometry geom9 = new Geometry("Box", wall9);
        geom9.setMaterial(mat1);
        Vector3f wallPlacement9 = new Vector3f(-20.0f, 3.0f, -80.0f);
        geom9.setLocalTranslation(wallPlacement9);
        rootNode.attachChild(geom9);

        // Labyrinth:
        Box wall10 = new Box(Vector3f.ZERO, 0.1f, 3, 50);
        Geometry geom10 = new Geometry("Box", wall10);
        geom10.setMaterial(mat1);
        Vector3f wallPlacement10 = new Vector3f(-60.0f, 3.0f, -50.0f);
        geom10.setLocalTranslation(wallPlacement10);
        rootNode.attachChild(geom10);

        Box wall11 = new Box(Vector3f.ZERO, 0.1f, 3, 30);
        Geometry geom11 = new Geometry("Box", wall11);
        geom11.setMaterial(mat1);
        Vector3f wallPlacement11 = new Vector3f(-70.0f, 3.0f, -50.0f);
        geom11.setLocalTranslation(wallPlacement11);
        rootNode.attachChild(geom11);

        Box wall12 = new Box(Vector3f.ZERO, 0.1f, 3, 25);
        Geometry geom12 = new Geometry("Box", wall12);
        geom12.setMaterial(mat1);
        Vector3f wallPlacement12 = new Vector3f(-80.0f, 3.0f, -55.0f);
        geom12.setLocalTranslation(wallPlacement12);
        rootNode.attachChild(geom12);

        // Button area:
        Box wall13 = new Box(Vector3f.ZERO, 15, 3, 0.1f);
        Geometry geom13 = new Geometry("Box", wall13);
        geom13.setMaterial(mat1);
        Vector3f wallPlacement13 = new Vector3f(-75.0f, 3.0f, -20.0f);
        geom13.setLocalTranslation(wallPlacement13);
        rootNode.attachChild(geom13);

        // Merry go round:
        Box wall14 = new Box(Vector3f.ZERO, 10, 3, 0.1f);
        Geometry geom14 = new Geometry("Box", wall14);
        geom14.setMaterial(mat1);
        Vector3f wallPlacement14 = new Vector3f(-90.0f, 3.0f, -30.0f);
        geom14.setLocalTranslation(wallPlacement14);
        rootNode.attachChild(geom14);

        Box wall15 = new Box(Vector3f.ZERO, 0.1f, 3, 20);
        Geometry geom15 = new Geometry("Box", wall15);
        geom15.setMaterial(mat1);
        Vector3f wallPlacement15 = new Vector3f(-90.0f, 3.0f, -60.0f);
        geom15.setLocalTranslation(wallPlacement15);
        rootNode.attachChild(geom15);

        Spatial terrainGeo = assetManager.loadModel("Scenes/Level1.j3o");
        //sceneModel = assetManager.loadModel("main.scene");
        terrainGeo.setLocalScale(2f);

        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) terrainGeo);
        landscape = new RigidBodyControl(sceneShape, 0);
        terrainGeo.addControl(landscape);

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // The CharacterControl offers extra settings for
        // size, stepheight, jumping, falling, and gravity.
        // We also put the player in its starting position.
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(-5, 10, 0));
        
        
        /** Make brick physical with a mass > 0.0f. */
        boxPhy = new RigidBodyControl(0f);
        geom1.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom2.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom3.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom4.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom5.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom6.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom7.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom8.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom9.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom10.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom11.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom12.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom13.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom14.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        boxPhy = new RigidBodyControl(0f);
        geom15.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        
        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        rootNode.attachChild(terrainGeo);
        bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
    }
    
    

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }

    /**
     * We over-write some navigational key mappings here, so we can add
     * physics-controlled walking and jumping:
     */
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    /**
     * This is the main event loop--walking happens here. We check in which
     * direction the player is walking by interpreting the camera direction
     * forward (camDir) and to the side (camLeft). The setWalkDirection()
     * command is what lets a physics-controlled player walk. We also make sure
     * here that the camera moves with player.
     */
    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * These are our custom actions triggered by key presses. We do not walk
     * yet, we just keep track of the direction the user pressed.
     */
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right")) {
            right = isPressed;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down")) {
            down = isPressed;
        } else if (binding.equals("Jump")) {
            if (isPressed) {
                player.jump();
            }
        }
    }
}
