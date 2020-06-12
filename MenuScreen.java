/**
 * К сожалению, катастрофически не хватает времени,
 * поэтому успел сделать только MenuScreen
 */

package com.dune.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dune.game.core.Assets;

public class MenuScreen extends AbstractScreen {
    Stage stage;

    public Stage getStage() {
        return stage;
    }

    public MenuScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        createMenuGUI();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    public void update(float dt) {
//        if (Gdx.input.justTouched()) {
//            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
//        }
        stage.act(dt);
    }

    @Override
    public void dispose() {
    }

    public void createMenuGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), ScreenManager.getInstance().getBatch());
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.getDrawable(
                "smButton"), null, null, font24);
        final TextButton btnStart = new TextButton("Start Game", style);
        btnStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });
        btnStart.setSize(200, 50);
        btnStart.setPosition(1280 / 2 - 100, 720 / 2 + 50);
        stage.addActor(btnStart);

        final TextButton btnExit = new TextButton("Exit Game", style);
        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        btnExit.setSize(200, 50);
        btnExit.setPosition(1280 / 2 - 100, 720 / 2 - 50);
        stage.addActor(btnExit);

        skin.dispose();
    }
}