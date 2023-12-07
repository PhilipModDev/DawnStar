package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;

public class MenuManager extends Game {
    public final DawnStar DAWN_STAR;
    public final UIComponentFactory UI_FACTORY;
    private final WorldCreationMenu worldCreationMenu;
    private final TitleScreenMenu titleScreenMenu;
    private final PauseMenu pauseMenu;
    public boolean FLAG_TITLE_SCREEN = false;
    public boolean FLAG_WORLD_CREATION = false;
    public boolean FLAG_RENDER_SENSE = false;

    public MenuManager(DawnStar dawnStar){
        this.DAWN_STAR = dawnStar;
        UI_FACTORY = new UIComponentFactory(Constants.UI_SKIN);
        titleScreenMenu = new TitleScreenMenu(this);
        worldCreationMenu = new WorldCreationMenu(this);
        pauseMenu = new PauseMenu(this,DAWN_STAR.clientManager);
    }

    @Override
    public void create() {
      setScreen(titleScreenMenu);
    }

    @Override
    public void render() {
        // TODO: 10/31/2023 Fix memory problem.
       if (FLAG_WORLD_CREATION){
           setScreen(worldCreationMenu);
           FLAG_WORLD_CREATION = false;
       }
       if (FLAG_RENDER_SENSE){
           setScreen(DAWN_STAR.clientManager);
           FLAG_RENDER_SENSE = false;
       }
       if (FLAG_TITLE_SCREEN){
           setScreen(titleScreenMenu);
           FLAG_TITLE_SCREEN = false;
       }
       screen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resume() {
        FLAG_RENDER_SENSE = true;
    }

    @Override
    public void pause() {
        Gdx.input.setCursorCatched(false);
       setScreen(pauseMenu);
       screen.render(Gdx.graphics.getDeltaTime());
    }
}
