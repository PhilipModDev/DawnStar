package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;

public class TitleScreenMenu extends ScreenAdapter {
   public Stage stage;
   private Table fontTable,backtable;
   private TextButton exitGame, playGame,options,mods;
   private Label dawnfall;
   private Label noteLabel;
   private Group group;
   private Image backgroundImage;
   private final DawnStar main = DawnStar.getInstance();
   private final MenuManager menuManager;
   
   public TitleScreenMenu(MenuManager menuManager){
       this.menuManager = menuManager;
   }

    @Override
    public void show() {
        stage = menuManager.UI_FACTORY.createStage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        fontTable =menuManager.UI_FACTORY.createTable(false,true);
        backtable = menuManager.UI_FACTORY.createTable(false,true);
        //Add components here.
        exitGame = menuManager.UI_FACTORY.createTextButton("Exit Game");
        exitGame.setColor(menuManager.UI_FACTORY.getDefaultColor());
        playGame = menuManager.UI_FACTORY.createTextButton("Play Game");
        playGame.setColor(menuManager.UI_FACTORY.getDefaultColor());
        options = menuManager.UI_FACTORY.createTextButton("Options");
        mods = menuManager.UI_FACTORY.createTextButton("Mods");
        dawnfall = menuManager.UI_FACTORY.createLabel("Dawnfall");
        noteLabel = menuManager.UI_FACTORY.createLabel("A story long forgotten.");
        group = new Group();
        backgroundImage = menuManager.UI_FACTORY.createImage(Constants.UI_DAWNSTAR_BACKGROUND_IMAGE);
        backgroundImage.setWidth(Gdx.graphics.getWidth());
        backgroundImage.setHeight(Gdx.graphics.getHeight());
        exitGame.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.GAME_STATE =  Constants.GameState.STOP;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        mods.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.audio.getAudio(1).playMusic();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        options.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.audio.getAudio(1).playMusic();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        playGame.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.audio.getAudio(1).playMusic();
                main.getMenuManager().FLAG_WORLD_CREATION = true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        group.addActor(noteLabel);
        group.setScale(0.5f,0.5f);
        stage.addActor(backgroundImage);
        backtable.add(dawnfall);
        stage.addActor(backtable);
        stage.addActor(group);
        fontTable.add(playGame).expandX().right();
        fontTable.row();
        fontTable.add(options).expandX().top().right();
        fontTable.row();
        fontTable.add(mods).right().top();
        fontTable.row();
        fontTable.add(exitGame).right().top();
        stage.addActor(fontTable);
        stage.addActor(backtable);
    }

    @Override
    public void render(float delta) {
        main.setGPUConfigFPS(60);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        backgroundImage.setWidth(width);
        backgroundImage.setHeight(height);
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
       stage.dispose();
    }
}
