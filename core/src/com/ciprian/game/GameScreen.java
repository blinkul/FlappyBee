package com.ciprian.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private Flappee flappee;

    private static final float GAP_BETWEEN_FLOWERS = 200F;

    //scoring
    private int score = 0;
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;

    //textures
    private Texture background;
    private Texture flowerBottom;
    private Texture flowerTop;
    private Texture flappeeTexture;

    @Override
    public void render(float delta) {
        clearScreen();
        draw();
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
        flappeeTexture = new Texture(Gdx.files.internal("bee.png"));
        flappee = new Flappee(flappeeTexture);
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        background = new Texture(Gdx.files.internal("bg.png"));
        flowerBottom = new Texture(Gdx.files.internal("flowerBottom.png"));
        flowerTop = new Texture(Gdx.files.internal("flowerTop.png"));

    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void update(float delta) {
        updateFlappee(delta);
        updateFlowers(delta);
        updateScore();
        if(checkForCollision()) {
            restart();
        }
    }

    private void updateFlappee(float delta) {
        flappee.update(delta);
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            flappee.flyUp();
        }
        blockFlappeeLeavingTheWorld();
    }

    private void restart() {
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        flowers.clear();
        score = 0;
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
        Flower newFlower = new Flower(flowerBottom, flowerTop);
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

    private boolean checkForCollision() {
        for(Flower flower : flowers) {
            if(flower.isFlappeeColliding(flappee)) {
                return true;
            }
        }
        return false;
    }

    private void updateScore() {
        Flower flower = flowers.first();
        if(flower.getX() < flappee.getX()
            && !flower.isPointClaimed())
        {
            flower.markPointClaimed();
            score++;
        }
    }

    private void drawScore() {
        String scoreAsString = Integer.toString(score);
        glyphLayout.setText(bitmapFont,scoreAsString);
        bitmapFont.draw(batch,
                scoreAsString,
                (viewport.getWorldWidth() - glyphLayout.width) / 2,
                (4 * viewport.getWorldHeight() / 5) - glyphLayout.height / 2) ;
    }

    private void drawFlowers() {
        for (Flower flower : flowers) {
            flower.draw(batch);
        }
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(background, 0, 0);
        drawFlowers();
        flappee.draw(batch);
        drawScore();
        batch.end();
    }



}
