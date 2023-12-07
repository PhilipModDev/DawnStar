package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;
import com.dawnfall.engine.Server.util.Utils;

public class LoadWorldMenu extends ScreenAdapter {
    //The stage for the sense.
    public Stage stage;
    //The table to add components to.
    public Table table;
    //The button to add to the sense.
    private TextButton forceStop;
    //This is for the pause menu screen.
    //Gives access to the dawnStar game loop.
    private final DawnStar main = DawnStar.getInstance();
    //The background image for the sense, this is used to block out the other senses.
    private Image backgroundImage;
    //The font to use for the chunk pre generator.
    private BitmapFont font;
    //The viewport to be rendered on.
    private final Viewport viewport = main.screenViewport;
    private final String fontMessage = "Loading World:";
    private final MenuManager menuManager;
    public LoadWorldMenu(MenuManager menuManager){
        this.menuManager = menuManager;
    }

    @Override
    public void show() {
        //Creates the components for the sense.
        font = new BitmapFont(Gdx.files.internal("ui/fonts/black.fnt"));
        font.getData().scale(0.3f);
        stage = menuManager.UI_FACTORY.createStage(viewport);
        Gdx.input.setInputProcessor(stage);
        table = menuManager.UI_FACTORY.createTable(false,true);
        table.setFillParent(true);
        forceStop = menuManager.UI_FACTORY.createTextButton("Force Stop");
        forceStop.setColor(menuManager.UI_FACTORY.getDefaultColor());
        backgroundImage = menuManager.UI_FACTORY.createImage("ui/backgrounds/Dawnfall_main_menu_background_image.png");
        backgroundImage.setWidth(Gdx.graphics.getWidth());
        backgroundImage.setHeight(Gdx.graphics.getHeight());
        //Adds the input events for the components.
        forceStop.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.GAME_STATE = Constants.GameState.STOP;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        //Adds the components to the stage.
        table.add(forceStop);
        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        //Renders the sense.
            Gdx.input.setInputProcessor(stage);
            stage.getViewport().apply();
            stage.draw();
            stage.act();
            main.spriteBatch.begin();
            if (main.loadingPercent >= 1000) {
                font.draw(main.spriteBatch,fontMessage+"Done.", 450, 500);
            } else {
                font.draw(main.spriteBatch,fontMessage + Utils.percentageCounter(main.loadingPercent) + "%", 430, 500);
                font.draw(main.spriteBatch,"\nOne story begins...", 430, 500);
            }
            main.spriteBatch.end();
    }
    //Dispose the current sense to free up memory.
    @Override
    public void dispose() {
        this.stage.dispose();
        super.dispose();
    }
    //Resizes the sense if needed.
    @Override
    public void resize(int width, int height) {
        backgroundImage.setWidth(width);
        backgroundImage.setHeight(height);
        stage.getViewport().update(width,height,true);
    }
}
