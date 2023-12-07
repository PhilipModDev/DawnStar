package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dawnfall.engine.Server.util.Utils;

public class UIComponentFactory implements Disposable {
    private final Skin ui_skin;
    private Color defaultColor;
    private Color clickedButtonColor;
    //Looking for a .json file or a .atlas.
    public UIComponentFactory(String skinPath){
        ui_skin = new Skin(Utils.getInternalFile(skinPath));
        defaultColor = new Color(0.4f,0.5f,0.3f,1);
        clickedButtonColor = new Color(0.6f,0.5f,0.3f,1);
    }
    //Add the texture atlas for the components textures and the json file for the properties.
    public UIComponentFactory(FileHandle jsonFile, TextureAtlas textureAtlas){
        ui_skin = new Skin(jsonFile,textureAtlas);
    }
    //Adds the texture atlas for the components textures.
    public UIComponentFactory(TextureAtlas textureAtlas){
        ui_skin = new Skin(textureAtlas);
    }

    public Color createComponentColor(float r,float g, float b,float a){
        return new Color(r,g,b,a);
    }
    public Color getDefaultComponentColor(){
        return defaultColor;
    }

    public Color setClickedButtonColor(float r, float g, float b, float a){
          clickedButtonColor.set( r,g, b, a);
      return clickedButtonColor;
    }
    public Color getClickedButtonColor() {
        return clickedButtonColor;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }
    public Skin getUi_skin() {
        return ui_skin;
    }

    public TextButton createTextButton(String text){
        return new TextButton(text,ui_skin);
    }

    public Table createTable(Skin skin, Drawable drawable, boolean debug,boolean fill){
        Table table = new Table(skin);
        table.background(drawable);
        table.setFillParent(fill);
        table.setDebug(debug);
        return table;
    }

    public Table createTable(boolean debug,boolean fill){
      Table table = new Table();
      table.setFillParent(fill);
      table.setDebug(debug);
      return table;
    }

    public Stage createStage(Viewport viewport){
        return new Stage(viewport);
    }
    public Button createButton(){
        return new Button();
    }

    public TextField createTextField(String text){
        return new TextField(text,ui_skin);
    }
    public Image createImage(String path){
        Texture texture = new Texture(Utils.getFile(path));
        return new Image(texture);
    }
    public Label createLabel(String name){
        return new Label(name,ui_skin);
    }


    @Override
    public void dispose() {
        ui_skin.dispose();
    }
}
