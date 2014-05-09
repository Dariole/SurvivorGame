package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.Timer;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener {

    private Geometry bulletGeo;
    private Geometry enemyGeo;
    private CameraNode camNode;
    private Node playerNode;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private RigidBodyControl enemy;
    private BetterCharacterControl player;
    private RigidBodyControl boxPhy;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    private boolean rotateLeft = false, rotateRight = false,
            forward = false, backward = false, shoot = false;
    private float speed = 20;
    private RigidBodyControl bulletPhy;
    //private Material bulletMat;
    private static final Sphere ballMesh = new Sphere(32, 32, 0.10f, true, false);
    Timer myTimer = getTimer();
    private int score = 0;
    private BitmapText scoreText;
    AudioNode audioGun;
    AudioNode audioPain;
    Geometry labDoorGeo;
    private Node doors;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Remove mouse
        org.lwjgl.input.Mouse.setGrabbed(true);

        // Add crosshair:
        initCrosshairs();

        // We re-use the flyby camera for rotation, while positioning is handled by physics
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setEnabled(false);

        // Remove stats
        setDisplayStatView(false);
        setDisplayFps(false);
        setUpKeys();
        setUpLight();

        // Adding score to GUI
        showScore();

        // Sound init
        initGunSound();
        initPainSound();
        
        // init door node
        doors = new Node("Doors");
        rootNode.attachChild(doors);
        playerNode = new Node("the player");
        playerNode.setLocalTranslation(new Vector3f(-10, 6, -10));
        rootNode.attachChild(playerNode);
        player = new BetterCharacterControl(1.5f, 4, 30f);
        player.setJumpForce(new Vector3f(0, 300, 0));
        player.setGravity(new Vector3f(0, -10, 0));
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4, -0.8f));
        Quaternion quat = new Quaternion();
        quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        camNode.setLocalRotation(quat);
        playerNode.attachChild(camNode);
        camNode.setEnabled(true);
        flyCam.setEnabled(false);

        createLevel();
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
        inputManager.addMapping("Forward",
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Back",
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Shoot",
                new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addMapping("Use", 
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Shoot");
        inputManager.addListener(this, "Rotate Left",
                "Rotate Right");
        inputManager.addListener(this, "Forward", "Back");
        inputManager.addListener(this, "Use");


        inputManager.setCursorVisible(true);
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
        // Get current forward and left vectors of the playerNode:
        Vector3f modelForwardDir =
                playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        // Determine the change in direction
        walkDirection.set(0, 0, 0);
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(speed));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.mult(speed).
                    negate());
        }
        player.setWalkDirection(walkDirection); // walk!
        // Determine the change in rotation
        if (rotateLeft) {
            Quaternion rotateL = new Quaternion().
                    fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        } else if (rotateRight) {
            Quaternion rotateR = new Quaternion().
                    fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        player.setViewDirection(viewDirection); // turn!


    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Rotate Left")) {
            rotateLeft = isPressed;
        } else if (binding.equals("Rotate Right")) {
            rotateRight = isPressed;
        } else if (binding.equals("Forward")) {
            forward = isPressed;
        } else if (binding.equals("Back")) {
            backward = isPressed;
        } else if (binding.equals("Use") && !isPressed) {
            System.out.println("Use virker");
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            doors.collideWith(ray, results);
            for (int i = 0; i < results.size(); i++) {
                System.out.println("use1");
                float dist = results.getCollision(i).getDistance();
                Vector3f pt = results.getCollision(i).getContactPoint();
                String hit = results.getCollision(i).getGeometry().getName();
                if (dist <= 10.0f){
                    System.out.println("use2");
                    if (hit.equals("labDoor")){
                        System.out.println("use3");
                        openDoor();
                    }
                }
            }
        } else if (binding.equals("Shoot") && !isPressed) {
            if (myTimer.getTimeInSeconds() > 5) {
                myTimer.reset();
                shoot();
            }
        }
    }

    public void spawnEnemy() {
        Box enemyBox = new Box(Vector3f.ZERO, 1, 3, 1);
        enemyGeo = new Geometry("enemy", enemyBox);
        Material enemyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        enemyMat.setColor("Color", ColorRGBA.Red);
        enemyGeo.setMaterial(enemyMat);
        Vector3f enemySpawnPosition = new Vector3f(-30, 3, -30);
        enemyGeo.setLocalTranslation(enemySpawnPosition);
        rootNode.attachChild(enemyGeo);
        enemy = new RigidBodyControl(0);
        enemyGeo.addControl(enemy);
        bulletAppState.getPhysicsSpace().add(enemy);
    }

    public void shoot() {
        Material bulletMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        bulletMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Metal.png"));
        bulletMat.setTexture("NormalMap", assetManager.loadTexture("Textures/Metal.png"));
        bulletMat.setFloat("Shininess", 5f);
        bulletGeo = new Geometry("bullet", ballMesh);
        bulletGeo.setMaterial(bulletMat);
        bulletGeo.setLocalTranslation(cam.getLocation().add(cam.getDirection().mult(3)));
        rootNode.attachChild(bulletGeo);
        audioGun.playInstance();
        SphereCollisionShape sceneShape = new SphereCollisionShape(0.25f);
        bulletPhy = new RigidBodyControl(sceneShape, 10f);
        bulletPhy.applyImpulse(cam.getDirection().mult(400), cam.getDirection());
        bulletGeo.addControl(bulletPhy);
        bulletAppState.getPhysicsSpace().add(bulletPhy);
        bulletPhy.setGravity(new Vector3f(0f, 0f, 0f));
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    private void initGunSound(){
    audioGun = new AudioNode(assetManager, "Sounds/gun_sound.wav");
    audioGun.setPositional(false);
    audioGun.setLooping(false);
    audioGun.setVolume(2);
    rootNode.attachChild(audioGun);
    }
    private void initPainSound(){
    audioPain = new AudioNode(assetManager, "Sounds/pain_sound.wav");
    audioPain.setPositional(false);
    audioPain.setLooping(false);
    audioPain.setVolume(2);
    rootNode.attachChild(audioPain);
    }
    
    
    private void showScore() {
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        scoreText.setText("Score: " + score);
        scoreText.setLocalTranslation( // center
                (settings.getWidth() - scoreText.getLineWidth() / 2) - 100, (settings.getHeight() + scoreText.getLineHeight() / 2) - 25, 0);
        guiNode.attachChild(scoreText);
    }

    private void openDoor(){
        labDoorGeo.setLocalTranslation(new Vector3f(-60.0f, 12.0f, -25.0f));
        //rootNode.detachChildNamed("labDoor");
        bulletAppState.getPhysicsSpace().remove(labDoorGeo);
    }
    
    public void collision(PhysicsCollisionEvent event) {
        if ("enemy".equals(event.getNodeA().getName()) || "enemy".equals(event.getNodeB().getName())) {
            if ("bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
                if ("bullet".equals(event.getNodeB().getName())) {
                    if (rootNode.getChild("bullet") != null) {
                        rootNode.detachChildNamed("bullet");
                        rootNode.detachChildNamed("enemy");
                        audioPain.playInstance();
                        score++;
                        System.out.println(score);
                        scoreText.setText("Score: " + score);
                    }
                }
            }
        }

        if ("Box".equals(event.getNodeA().getName()) || "Box".equals(event.getNodeB().getName())) {
            if ("bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
                if ("bullet".equals(event.getNodeB().getName())) {
                    if (rootNode.getChild("bullet") != null) {
                        rootNode.detachChildNamed("bullet");
                    }
                }
            }
        }
        if ("labDoor".equals(event.getNodeA().getName()) || "labDoor".equals(event.getNodeB().getName())) {
            if ("bullet".equals(event.getNodeA().getName()) || "bullet".equals(event.getNodeB().getName())) {
                if ("bullet".equals(event.getNodeB().getName())) {
                    if (rootNode.getChild("bullet") != null) {
                        rootNode.detachChildNamed("bullet");
                        openDoor();
                    }
                }
            }
        }
    }

    private void initCrosshairs() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    private void createLevel() {
        // Outter walls:
        Box wall1 = new Box(Vector3f.ZERO, 0.1f, 4, 50);
        Box wall2 = new Box(Vector3f.ZERO, 0.1f, 4, 50);
        Box wall3 = new Box(Vector3f.ZERO, 50, 4, 0.1f);
        Box wall4 = new Box(Vector3f.ZERO, 50, 4, 0.1f);
        Geometry geom1 = new Geometry("Box", wall1);
        Geometry geom2 = new Geometry("Box", wall2);
        Geometry geom3 = new Geometry("Box", wall3);
        Geometry geom4 = new Geometry("Box", wall4);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        mat1.setTexture("ColorMap", assetManager.loadTexture("Interface/Wall.jpg"));
        geom1.setMaterial(mat1);
        geom2.setMaterial(mat1);
        geom3.setMaterial(mat1);
        geom4.setMaterial(mat1);
        Vector3f wallPlacement1 = new Vector3f(0.0f, 4.0f, -50.0f);
        Vector3f wallPlacement2 = new Vector3f(-100.0f, 4.0f, -50.0f);
        Vector3f wallPlacement3 = new Vector3f(-50.0f, 4.0f, -100.0f);
        Vector3f wallPlacement4 = new Vector3f(-50.0f, 4.0f, 0.0f);
        geom1.setLocalTranslation(wallPlacement1);
        geom2.setLocalTranslation(wallPlacement2);
        geom3.setLocalTranslation(wallPlacement3);
        geom4.setLocalTranslation(wallPlacement4);
        rootNode.attachChild(geom1);
        rootNode.attachChild(geom2);
        rootNode.attachChild(geom3);
        rootNode.attachChild(geom4);

        // Start area:
        Box wall5 = new Box(Vector3f.ZERO, 0.1f, 4, 20);
        Geometry geom5 = new Geometry("Box", wall5);
        geom5.setMaterial(mat1);
        Vector3f wallPlacement5 = new Vector3f(-20.0f, 4.0f, -20.0f);
        geom5.setLocalTranslation(wallPlacement5);
        rootNode.attachChild(geom5);

        Box wall6 = new Box(Vector3f.ZERO, 20, 4, 0.1f);
        Geometry geom6 = new Geometry("Box", wall6);
        geom6.setMaterial(mat1);
        Vector3f wallPlacement6 = new Vector3f(-20.0f, 4.0f, -60.0f);
        geom6.setLocalTranslation(wallPlacement6);
        rootNode.attachChild(geom6);

        Box wall7 = new Box(Vector3f.ZERO, 10, 4, 0.1f);
        Geometry geom7 = new Geometry("Box", wall7);
        geom7.setMaterial(mat1);
        Vector3f wallPlacement7 = new Vector3f(-30.0f, 4.0f, -40.0f);
        geom7.setLocalTranslation(wallPlacement7);
        rootNode.attachChild(geom7);

        // Secret room
        Box wall8 = new Box(Vector3f.ZERO, 0.1f, 4, 10);
        Geometry geom8 = new Geometry("Box", wall8);
        geom8.setMaterial(mat1);
        Vector3f wallPlacement8 = new Vector3f(-40.0f, 4.0f, -90.0f);
        geom8.setLocalTranslation(wallPlacement8);
        rootNode.attachChild(geom8);

        Box wall9 = new Box(Vector3f.ZERO, 20, 4, 0.1f);
        Geometry geom9 = new Geometry("Box", wall9);
        geom9.setMaterial(mat1);
        Vector3f wallPlacement9 = new Vector3f(-20.0f, 4.0f, -80.0f);
        geom9.setLocalTranslation(wallPlacement9);
        rootNode.attachChild(geom9);

        // Labyrinth:
        Box wall10 = new Box(Vector3f.ZERO, 0.1f, 4, 35);
        Geometry geom10 = new Geometry("Box", wall10);
        geom10.setMaterial(mat1);
        Vector3f wallPlacement10 = new Vector3f(-60.0f, 4.0f, -65.0f);
        geom10.setLocalTranslation(wallPlacement10);
        rootNode.attachChild(geom10);
        
        Box wall16 = new Box(Vector3f.ZERO, 0.1f, 4, 10);
        Geometry geom16 = new Geometry("Box", wall16);
        geom16.setMaterial(mat1);
        Vector3f wallPlacement16 = new Vector3f(-60.0f, 4.0f, -10.0f);
        geom16.setLocalTranslation(wallPlacement16);
        rootNode.attachChild(geom16);

        Box wall11 = new Box(Vector3f.ZERO, 0.1f, 4, 30);
        Geometry geom11 = new Geometry("Box", wall11);
        geom11.setMaterial(mat1);
        Vector3f wallPlacement11 = new Vector3f(-70.0f, 4.0f, -50.0f);
        geom11.setLocalTranslation(wallPlacement11);
        rootNode.attachChild(geom11);

        Box wall12 = new Box(Vector3f.ZERO, 0.1f, 4, 25);
        Geometry geom12 = new Geometry("Box", wall12);
        geom12.setMaterial(mat1);
        Vector3f wallPlacement12 = new Vector3f(-80.0f, 4.0f, -55.0f);
        geom12.setLocalTranslation(wallPlacement12);
        rootNode.attachChild(geom12);

        // Button area:
        Box wall13 = new Box(Vector3f.ZERO, 15, 4, 0.1f);
        Geometry geom13 = new Geometry("Box", wall13);
        geom13.setMaterial(mat1);
        Vector3f wallPlacement13 = new Vector3f(-75.0f, 4.0f, -20.0f);
        geom13.setLocalTranslation(wallPlacement13);
        rootNode.attachChild(geom13);

        // Merry go round:
        Box wall14 = new Box(Vector3f.ZERO, 10, 4, 0.1f);
        Geometry geom14 = new Geometry("Box", wall14);
        geom14.setMaterial(mat1);
        Vector3f wallPlacement14 = new Vector3f(-90.0f, 4.0f, -30.0f);
        geom14.setLocalTranslation(wallPlacement14);
        rootNode.attachChild(geom14);

        Box wall15 = new Box(Vector3f.ZERO, 0.1f, 4, 20);
        Geometry geom15 = new Geometry("Box", wall15);
        geom15.setMaterial(mat1);
        Vector3f wallPlacement15 = new Vector3f(-90.0f, 4.0f, -60.0f);
        geom15.setLocalTranslation(wallPlacement15);
        rootNode.attachChild(geom15);

        // Labyrinth door
        Box doorBox = new Box(Vector3f.ZERO, 0.1f, 4, 5);
        labDoorGeo = new Geometry("labDoor", doorBox);
        Material doorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        doorMat.setColor("Color", ColorRGBA.Blue);
        labDoorGeo.setMaterial(doorMat);
        Vector3f doorPlacement1 = new Vector3f(-60.0f, 4.0f, -25.0f);
        labDoorGeo.setLocalTranslation(doorPlacement1);
        doors.attachChild(labDoorGeo);
        
        Spatial terrainGeo = assetManager.loadModel("Scenes/Level1.j3o");
        terrainGeo.setLocalScale(2f);

        spawnEnemy();

        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) terrainGeo);
        landscape = new RigidBodyControl(sceneShape, 0);
        terrainGeo.addControl(landscape);
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
        labDoorGeo.addControl(boxPhy);
        bulletAppState.getPhysicsSpace().add(boxPhy);
        rootNode.attachChild(terrainGeo);
        bulletAppState.getPhysicsSpace().add(landscape);
    }
}
