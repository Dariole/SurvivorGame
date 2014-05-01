package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(20);
        Spatial terrainGeo = assetManager.loadModel("Scenes/Level1.j3o");
        rootNode.attachChild(terrainGeo);
        
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
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Gray);
        geom5.setMaterial(mat2);
        Vector3f wallPlacement5 = new Vector3f(-20.0f, 3.0f, -20.0f);
        geom5.setLocalTranslation(wallPlacement5);
        rootNode.attachChild(geom5);
        
        Box wall6 = new Box(Vector3f.ZERO, 20, 3, 0.1f);
        Geometry geom6 = new Geometry("Box", wall6);
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.Gray);
        geom6.setMaterial(mat3);
        Vector3f wallPlacement6 = new Vector3f(-20.0f, 3.0f, -60.0f);
        geom6.setLocalTranslation(wallPlacement6);
        rootNode.attachChild(geom6);
        
        Box wall7 = new Box(Vector3f.ZERO, 10, 3, 0.1f);
        Geometry geom7 = new Geometry("Box", wall7);
        Material mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat4.setColor("Color", ColorRGBA.Gray);
        geom7.setMaterial(mat4);
        Vector3f wallPlacement7 = new Vector3f(-30.0f, 3.0f, -40.0f);
        geom7.setLocalTranslation(wallPlacement7);
        rootNode.attachChild(geom7);
        
        // Secret room
        Box wall8 = new Box(Vector3f.ZERO, 0.1f, 3, 10);
        Geometry geom8 = new Geometry("Box", wall8);
        Material mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat5.setColor("Color", ColorRGBA.Gray);
        geom8.setMaterial(mat5);
        Vector3f wallPlacement8 = new Vector3f(-40.0f, 3.0f, -90.0f);
        geom8.setLocalTranslation(wallPlacement8);
        rootNode.attachChild(geom8);
        
        Box wall9 = new Box(Vector3f.ZERO, 20, 3, 0.1f);
        Geometry geom9 = new Geometry("Box", wall9);
        Material mat6 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat6.setColor("Color", ColorRGBA.Gray);
        geom9.setMaterial(mat6);
        Vector3f wallPlacement9 = new Vector3f(-20.0f, 3.0f, -80.0f);
        geom9.setLocalTranslation(wallPlacement9);
        rootNode.attachChild(geom9);
        
        // Labyrinth:
        Box wall10 = new Box(Vector3f.ZERO, 0.1f, 3, 50);
        Geometry geom10 = new Geometry("Box", wall10);
        Material mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat7.setColor("Color", ColorRGBA.Gray);
        geom10.setMaterial(mat7);
        Vector3f wallPlacement10 = new Vector3f(-60.0f, 3.0f, -50.0f);
        geom10.setLocalTranslation(wallPlacement10);
        rootNode.attachChild(geom10);
        
        Box wall11 = new Box(Vector3f.ZERO, 0.1f, 3, 30);
        Geometry geom11 = new Geometry("Box", wall11);
        Material mat8 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat8.setColor("Color", ColorRGBA.Gray);
        geom11.setMaterial(mat8);
        Vector3f wallPlacement11 = new Vector3f(-70.0f, 3.0f, -50.0f);
        geom11.setLocalTranslation(wallPlacement11);
        rootNode.attachChild(geom11);
        
        Box wall12 = new Box(Vector3f.ZERO, 0.1f, 3, 25);
        Geometry geom12 = new Geometry("Box", wall12);
        Material mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat9.setColor("Color", ColorRGBA.Gray);
        geom12.setMaterial(mat9);
        Vector3f wallPlacement12 = new Vector3f(-80.0f, 3.0f, -55.0f);
        geom12.setLocalTranslation(wallPlacement12);
        rootNode.attachChild(geom12);
        
        // Button area:
        Box wall13 = new Box(Vector3f.ZERO, 15, 3, 0.1f);
        Geometry geom13 = new Geometry("Box", wall13);
        Material mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat10.setColor("Color", ColorRGBA.Gray);
        geom13.setMaterial(mat10);
        Vector3f wallPlacement13 = new Vector3f(-75.0f, 3.0f, -20.0f);
        geom13.setLocalTranslation(wallPlacement13);
        rootNode.attachChild(geom13);
        
        // Merry go round:
        Box wall14 = new Box(Vector3f.ZERO, 10, 3, 0.1f);
        Geometry geom14 = new Geometry("Box", wall14);
        Material mat11 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat11.setColor("Color", ColorRGBA.Gray);
        geom14.setMaterial(mat11);
        Vector3f wallPlacement14 = new Vector3f(-90.0f, 3.0f, -30.0f);
        geom14.setLocalTranslation(wallPlacement14);
        rootNode.attachChild(geom14);
        
        Box wall15 = new Box(Vector3f.ZERO, 0.1f, 3, 20);
        Geometry geom15 = new Geometry("Box", wall15);
        Material mat12 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat12.setColor("Color", ColorRGBA.Gray);
        geom15.setMaterial(mat12);
        Vector3f wallPlacement15 = new Vector3f(-90.0f, 3.0f, -60.0f);
        geom15.setLocalTranslation(wallPlacement15);
        rootNode.attachChild(geom15);
            
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.39f, -0.32f, -0.74f));
        rootNode.addLight(sun);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
