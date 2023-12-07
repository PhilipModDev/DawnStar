package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.dawnfall.engine.Client.ClientManager;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;
import com.dawnfall.engine.Server.util.IO.OptionProvider;

import static com.dawnfall.engine.DawnStar.getInstance;

public class PauseMenu extends ScreenAdapter {
    //This is for the pause menu screen.
    //Gives access to the dawnStar game loop.
    private final DawnStar main = getInstance();
    //Creates a stage reference variable.
    public Stage stage;
    private final ClientManager clientManager;
    //The text buttons to add to the sense.
    private TextButton saveAndQuitButton;
    private TextButton debugButton;
    private TextButton resumeToAppButton;
    private TextButton portButton;
    //Adds the background image for the pause menu, this is used to cover up the black drop shadow.
    private Image backgroundImage;
    //The press button count for any buttons that need to have a toggle.
    private short buttonPressCount = 0;
    //checks whether if this screen was disposed.
    private final MenuManager menuManager;

    public PauseMenu(MenuManager menuManager,ClientManager clientManager){
        this.menuManager = menuManager;
        this.clientManager = clientManager;
    }
    private final InputListener inputListener = new InputListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (saveAndQuitButton.isPressed()){
                //Saves and Quit.
               saveAndQuit();
            }
            if (debugButton.isPressed()){
                //The debug option (Development only)
                debug();
            }
            if (resumeToAppButton.isPressed()) {
                //Resume to game after call to pause. This will dispose any resources to this menu.
                resume();
            }
            if (portButton.isPressed()){
                //Port a LAN server to the hosted computer.
                portLANServer();
            }
            return super.touchDown(event, x, y, pointer, button);
        }
    };

    @Override
    public void show() {
        //Creates the components.
        stage = menuManager.UI_FACTORY.createStage(main.screenViewport);
        Gdx.input.setInputProcessor(stage);
        Table table = menuManager.UI_FACTORY.createTable(false,true);
        saveAndQuitButton = menuManager.UI_FACTORY.createTextButton("Save And Quit");
        saveAndQuitButton.setColor(menuManager.UI_FACTORY.getDefaultColor());
        resumeToAppButton = menuManager.UI_FACTORY.createTextButton("Return to game");
        resumeToAppButton.setColor(menuManager.UI_FACTORY.getDefaultColor());
        portButton = menuManager.UI_FACTORY.createTextButton("Port to Server?");
        debugButton = menuManager.UI_FACTORY.createTextButton("Debug");
        backgroundImage = new Image(new Texture(Gdx.files.internal(Constants.UI_DAWNSTAR_BACKGROUND_IMAGE)));
        backgroundImage.setWidth(Gdx.graphics.getWidth());
        backgroundImage.setHeight(Gdx.graphics.getHeight());
        //Add listeners to the components.
        saveAndQuitButton.addListener(inputListener);
        debugButton.addListener(inputListener);
        portButton.addListener(inputListener);
        resumeToAppButton.addListener(inputListener);
        //Adds the components to the stage for rendering.
        table.add(saveAndQuitButton).expandX().fill(0.8f,0);
        table.add(resumeToAppButton).expandX().fill(0.8f,0);
        table.row();
        table.add(portButton).expandX().fill(0.8f,0);
        table.add(debugButton).expandX().fill(0.8f,0);
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        //Only renders the stage if tha game is pause.
        main.setGPUConfigFPS(60);
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    private void saveAndQuit(){
        //Stops the game.
        main.audio.getAudio((byte) 1).playMusic();
        main.GAME_STATE = Constants.GameState.RENDER;
        menuManager.FLAG_TITLE_SCREEN = true;
        clientManager.dispose();
    }

    private void debug(){
        //Debug options.
        main.audio.getAudio((byte) 1).playMusic();
        if (buttonPressCount >= 1){
            buttonPressCount = 0;
            Gdx.app.log("Debug","Debug mode is off.");
            OptionProvider.DEBUG_MODE = false;
            return;
        }
        buttonPressCount++;
        OptionProvider.DEBUG_MODE = true;
        Gdx.app.log("Debug","Debug mode is on.");
    }


    private void portLANServer(){
        //Checks whether if a server has already been ported. If not than prot.
        if (!OptionProvider.SERVER_PORTED){
            main.audio.getAudio((byte) 1).playMusic();
            OptionProvider.SERVER_PORTED = true;
        }
    }

    @Override
    public void resume() {
        //Resume the game state
        main.audio.getAudio((byte) 1).playMusic();
        main.GAME_STATE =  Constants.GameState.RESUME;
    }

    @Override
    public void resize(int width, int height) {
        //Resizes the stage with the window's current resolution.
        backgroundImage.setWidth(width);
        backgroundImage.setHeight(height);
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void hide() {
        //Dispose any resources to this stage.
        stage.dispose();
    }

    @Override
    public void dispose() {
        //Dispose any resources to this stage.
        stage.dispose();
    }
}

