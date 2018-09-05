package com.ciprian.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    //Screen size and rendering
    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;

    private Array<Flower> flowers = new Array<Flower>();

    //Game
    private Flappee flappee = new Flappee();

    private static final float GAP_BETWEEN_FLOWERS = 200F;

    @Override
    public void render(float delta) {
        clearScreen();
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        flappee.drawDebug(shapeRenderer);
        drawDebug();
        shapeRenderer.end();
        update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    //Initializer
    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void update(float delta) {
        flappee.update();
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            flappee.flyUp();
        }
        blockFlappeeLeavingTheWorld();
        updateFlowers(delta);
    }

    private void drawDebug() {
        for(Flower flower : flowers) {
            flower.drawDebug(shapeRenderer);
        }
    }

    private void updateFlowers(float delta) {
        for (Flower flower : flowers) {
            flower.update(delta);
        }
        checkIfNewFlowerIsNeeded();
        removeFlowerIfPassed();
    }

    private void blockFlappeeLeavingTheWorld() {
        flappee.setPosition(flappee.getX(), MathUtils.clamp(flappee.getY(), 0, WORLD_HEIGHT));
    }

    private void createNewFlower() {
        Flower newFlower = new Flower();
        newFlower.setPosition(WORLD_WIDTH + Flower.WIDTH);
        flowers.add(newFlower);
    }

    private void checkIfNewFlowerIsNeeded() {
        if(flowers.size == 0) {
            createNewFlower();
        } else {
            Flower flower = flowers.peek();
            if(flower.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOWERS) {
                createNewFlower();
            }
        }
    }

    private void removeFlowerIfPassed() {
        if(flowers.size > 0) {
            Flower firstFlower = flowers.first();
            if(firstFlower.getX() < -Flower.WIDTH) {
                flowers.removeValue(firstFlower, true);
            }
        }
    }
}
