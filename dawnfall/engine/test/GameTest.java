package com.dawnfall.engine.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameTest implements ApplicationListener {
    private Stage stage;
    private Table table;
    private Image image;
    private Skin skin;
    private TextButton button;
    private BitmapFont font;
    private SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("ui/fonts/default.fnt"),false);
        skin = new Skin(Gdx.files.internal("ui/ui_skin.json"));
        stage = new Stage(new ScreenViewport());
        table = new Table();
        table.setFillParent(true);
        image = new Image( new Texture(Gdx.files.internal("ui/backgrounds/Dawnfall_main_menu_background_image.png")));
        button = new TextButton("Text Button",skin);
        button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(true);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        table.add(button);
        stage.addActor(image);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        image.setWidth(width);
        image.setHeight(height);
        stage.getViewport().update(width, height,true);
    }

    @Override
    public void render() {
       stage.act();
       stage.draw();
       batch.begin();
       font.draw(batch,"Text from font",0,100);
       batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
      stage.dispose();
    }
}
