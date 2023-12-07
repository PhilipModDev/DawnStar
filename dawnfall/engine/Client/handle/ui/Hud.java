package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dawnfall.engine.Client.ClientManager;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;

public class Hud extends ScreenAdapter {
    /*
     * Finish the HUD to the cmd line for the engine.
     * So far you have the text field bar step that you
     * need to do.
     */
    //The stage for the sense of the HUD.
    private BitmapFont textFont;
    private Stage stage;
    //The text field box to enter command lines in.
    private TextField textField;
    //The table for holding the components for the specific HUD.
    private Table table;
    //The access line to the dawnStar engine for software probabilities.
    private final ClientManager engine;
    //Tells whether if the command line should be displayed.
    public boolean showTextBox;
    //Checks whether if the user has it on the debug mode.
    private boolean isToggleDebug;
    public final HotBar bar;
    private final MenuManager menuManager = DawnStar.getInstance().getMenuManager();
    public Hud(ClientManager engine){
        this.engine = engine;
        bar = new HotBar(engine.dawnStar.player.getCamera());
        show();
    }

    @Override
    public void show() {
        textFont = new BitmapFont(Gdx.files.internal(Constants.UI_FONT));
        //Creates the components for the sense.
        stage = menuManager.UI_FACTORY.createStage(new ScreenViewport());
        table = menuManager.UI_FACTORY.createTable(false,true);
        textField = menuManager.UI_FACTORY.createTextField("/");
        table.add(textField).expand().growX().bottom().left();
        //Adds the event listeners.
        textField.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                /*
                 * Add command stuff here.
                 */
                if (keycode == Input.Keys.ENTER) {
                    if (textField.getText().equals("/stop")) System.exit(0);
                    engine.inputs.stringBuilder.append("\n").append(textField.getText());
                }
                return super.keyDown(event, keycode);
            }
        });
        //Adds HUD components to the stage for rendering.
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        /*
         * Renders the components as well as knowing
         * when to display the command line chat box.
         *
         * Also, you can add other render components to be
         * rendered.
         */
        // FIXME: 7/27/2023 Add the toggle debugging feature.
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            isToggleDebug = !isToggleDebug;
        }
        if (isToggleDebug) Debug.setRenderDebugMode(true, engine.dawnStar.player.getPerspectiveCamera(),engine);
        //Draw more ui stuff.
        engine.dawnStar.spriteBatch.begin();
        engine.dawnStar.spriteBatch.draw(engine.dawnStar.getTextureHolder().getCross(),530,350);
        if (showTextBox){
            engine.inputs.drawChatBox(50,500);
        }
        engine.dawnStar.spriteBatch.end();
        //draws the sense area using sense 2d ui.
        if (showTextBox) {
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER | Input.Keys.ESCAPE)){
                engine.inputs.noBlockPlacement = false;
                showTextBox = false;
            }
            Gdx.input.setInputProcessor(stage);
            Gdx.input.setCursorCatched(false);
            stage.act();
            stage.draw();
        }
        if (!showTextBox){
            bar.render(delta);
        }
    }

    public BitmapFont getTextFont(){
        return this.textFont;
    }

    @Override
    public void resize(int width, int height) {
        //Resizes the stage if needed.
        stage.getViewport().update(width,height,true);
        bar.resize(width, height);
    }

    @Override
    public void dispose() {
        //Disposes the resource for the stage and frees up memory.
        stage.dispose();
        bar.dispose();
    }
}
