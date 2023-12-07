package com.dawnfall.engine.Client.handle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.StringBuilder;
import com.dawnfall.engine.Client.ClientManager;
import com.dawnfall.engine.Client.handle.ui.Hud;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.Deflater;

public class Inputs extends InputAdapter {
    private static Inputs input;
    public boolean noBlockPlacement;
    private final IntArray keysPressed = new IntArray(false, 16);
    private final IntArray keysJustPre = new IntArray(false, 16);
    private final GridPoint2 lastPos = new GridPoint2(), movePos = new GridPoint2(), tmp = new GridPoint2();
    private final ClientManager clientManager;
    public final StringBuilder stringBuilder;
    private final Hud hud;
    public Inputs(ClientManager clientManager) {
        this.clientManager = clientManager;
        input = this;
        this.stringBuilder = new StringBuilder();
        this.hud = this.clientManager.getClientHud();
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '/'){
            clientManager.getClientHud().showTextBox = true;
            noBlockPlacement = true;
        }
        return super.keyTyped(character);
    }

    public boolean keyDown (int keycode) {
        if (!keysPressed.contains(keycode)) {
            keysPressed.add(keycode);
        }
        if (!keysJustPre.contains(keycode)) {
            keysJustPre.add(keycode);
        }
        switch (keycode){
            case Input.Keys.ESCAPE :{
                DawnStar.getInstance().GAME_STATE = Constants.GameState.PAUSE;
            }
            break;
            case Input.Keys.F2 : {
                takeScreenshot();
            }
            break;
        }
        return false;
    }

    public boolean keyUp (int keycode) {
        keysPressed.removeValue(keycode);
        return false;
    }
    /** Must be called when windows has been resized. */
    public void clear() {
        keysPressed.clear();
        keysJustPre.clear();
    }

    /** Must be called on the end of <code>render()</code> */
    public void clearJustPressed() {
        keysJustPre.clear();
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        move(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        move(screenX, screenY);
        return false;
    }
    private void move(int screenX, int screenY) {
        movePos.add(lastPos.x - screenX, lastPos.y - screenY);
        lastPos.set(screenX, screenY);
    }

    public static GridPoint2 getMouseDelta() {
        input.lastPos.set(0, 0);
        input.tmp.set(input.movePos);
        input.movePos.set(0, 0);
        Gdx.input.setCursorPosition(0, 0);
        return input.tmp;
    }

    /** Resets the mouse's position to (0, 0). */
    public static void resetMouse() {
        input.lastPos.set(0, 0);
        input.movePos.set(0, 0);
        Gdx.input.setCursorPosition(0, 0);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (hud.bar.getSlotCounter() <= hud.bar.MAX_SLOTS && hud.bar.getSlotCounter() >= 0) {
            if (amountY == -1) hud.bar.addSlotCounter(-1);
            if (amountY == 1) hud.bar.addSlotCounter(1);
        }else {
            if (hud.bar.getSlotCounter() > hud.bar.MAX_SLOTS) hud.bar.setSlotCounter(0);
            if (hud.bar.getSlotCounter() < 0) hud.bar.setSlotCounter( hud.bar.MAX_SLOTS);
        }
        hud.bar.setChangedItem(true);
        return super.scrolled(amountX, amountY);
    }

    /** Returns whether the key has just been pressed.
     *  If pressed, than remove the pressed key (will return false on next call with same key).
     *
     * @param key The key code as found in {@link Input.Keys}.
     * @return true if key just pressed, else false. */
    public static boolean isKeyJustPressed(int key) {
        final int index = findAndGetIndex(input.keysJustPre, key);
        if (index == -1) return false;
        input.keysJustPre.removeIndex(index);
        return true;
    }

    /** Returns whether the key is pressed.
     *
     * @param key The key code as found in {@link Input.Keys}.
     * @return true or false. */
    public static boolean isKeyPressed(int key) {
        return input.keysPressed.contains(key);
    }

    /** @return -1 if it hasn't found. */
    private static int findAndGetIndex(IntArray array, int key) {
        final int size = array.size;
        final int[] ints = array.items;
        for (int i = 0; i < size; ++i) {
            if (ints[i] == key)	return i;
        }
        return -1;
    }

    public void takeScreenshot(){
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        ByteBuffer pixels = pixmap.getPixels();
        int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
        for (int i = 3; i < size; i += 4) {
            pixels.put(i, (byte) 255);
        }
        final Random random = new Random();
        PixmapIO.writePNG(Gdx.files.external("OneDrive/Desktop/screenshot"+random.nextInt(100)+".png"),
                pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
    }
    final int timer = 5000;
    int counter = 0;
    public void drawChatBox(int x, int y){
        if (!(counter >= timer)){
            clientManager.getClientHud().getTextFont().draw(clientManager.dawnStar.spriteBatch,stringBuilder.toString(),x,y);
            if (!stringBuilder.isEmpty()) counter += 1;
            return;
        }
        stringBuilder.clear();
        counter = 0;
    }
}
