package com.engine.dawnstar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class CameraUtils {
    private final PerspectiveCamera camera3D;
    private final OrthographicCamera cameraGUI;

    public CameraUtils() {
        //Initiate default configurations for the cameras.
        camera3D = new PerspectiveCamera(70, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera3D.near = 0.1f;
        camera3D.far = 1000f;
        camera3D.update();

        cameraGUI = new OrthographicCamera(Constants.GUI_WIDTH,Constants.GUI_HEIGHT);
    }

    public void resize(int width,int height){
        //Resizing the viewports.
        camera3D.viewportWidth = width;
        camera3D.viewportHeight = height;
        camera3D.update();
        //Creates the aspect ratio and resize the gui.
        cameraGUI.viewportWidth = ((float) width / height) * Constants.GUI_HEIGHT;
        //Sets the viewport back into position where (0,0) is the bottom left.
        cameraGUI.position.set(cameraGUI.viewportWidth/2.0f,cameraGUI.viewportHeight/2.0f,0);
        cameraGUI.update();
    }

    //Updates the cameras.
    public void update(){
        camera3D.update();
        cameraGUI.update();
    }

    public OrthographicCamera getCameraGUI() {
        return cameraGUI;
    }

    public PerspectiveCamera getCamera3D() {
        return camera3D;
    }
}
