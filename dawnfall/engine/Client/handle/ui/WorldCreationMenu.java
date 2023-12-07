package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;

public class WorldCreationMenu extends ScreenAdapter {
    //The table to use for adding components to the screen.
    public Table table;
    //The buttons to use for the sense.
    public TextButton launchGameButton, worldSettingsButton;
    //The Stage that the actors will be displayed on.
    public Stage stage;
    //Gets a reference to the dawnStar application for instance variables and operations.
    private final DawnStar main = DawnStar.getInstance();
    //The Viewport that the stage will be projected upon.
    private final Viewport viewport = main.screenViewport;
    //The background image of the sense, this is used to block out other senses.
    private Image backgoundImage;
    
    //The input listener for user input events.
    private final InputListener inputListener = new InputListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (worldSettingsButton.isPressed()){
                main.audio.getAudio((byte) 1).playMusic();
            }
            if (launchGameButton.isPressed()){
                launchWorld();
            }
            return super.touchDown(event, x, y, pointer, button);
        }
    };
    
    private final MenuManager menuManager;
    public WorldCreationMenu(MenuManager menuManager){
        this.menuManager = menuManager;
    }
    @Override
    public void show() {
        //Creates the components and their properties for the sense.
        stage = menuManager.UI_FACTORY.createStage(viewport);
        Gdx.input.setInputProcessor(stage);
        table = menuManager.UI_FACTORY.createTable(false,true);
        table.setFillParent(true);
        launchGameButton = new TextButton("Create World",menuManager.UI_FACTORY.getUi_skin());
        launchGameButton.setColor(menuManager.UI_FACTORY.getDefaultColor());
        worldSettingsButton = menuManager.UI_FACTORY.createTextButton("World Settings");
        worldSettingsButton.setColor(menuManager.UI_FACTORY.getDefaultColor());
        backgoundImage = menuManager.UI_FACTORY.createImage(Constants.UI_DAWNSTAR_BACKGROUND_IMAGE);
        backgoundImage.setWidth(Gdx.graphics.getWidth());
        backgoundImage.setHeight(Gdx.graphics.getHeight());
        worldSettingsButton.addListener(inputListener);
        launchGameButton.addListener(inputListener);
        //Adds the components to the stage.
        table.add(launchGameButton).expandX().fill(0.6f,0);
        table.add(worldSettingsButton).expandX().fill(0.6f,0);
        stage.addActor(backgoundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        //Applies the stage and renders it.
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //Resizes the components that require it.
        backgoundImage.setWidth(width);
        backgoundImage.setHeight(height);
        stage.getViewport().update(width,height,true);
    }
    //Call the game that the user wants to create a world.
    public void launchWorld(){
        main.audio.getAudio((byte) 1).playMusic();
        main.GAME_STATE =  Constants.GameState.RENDER;
        menuManager.FLAG_RENDER_SENSE = true;
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    //Disposes any resources from this sense.
    @Override
    public void dispose() {
        if (stage != null){
            stage.dispose();
        }
    }
}

