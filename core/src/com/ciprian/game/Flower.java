package com.ciprian.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import javax.xml.soap.Text;

public class Flower {

    private static final float COLLISION_RECTANGLE_WIDTH = 13F;
    private static final float COLLISION_RECTANGLE_HEIGHT = 447F;
    private static final float COLLISION_CIRCLE_RADIUS = 33F;
    private static final float DISTANCE_BETWEEN_FLOOR_AND_CEILING = 225F;
    private final Circle floorCollisionCircle;
    private final Rectangle floorCollisionRectangle;
    private final Circle ceilCollisionCircle;
    private final Rectangle ceilCollisionRectangle;

    private float x = 0;
    private float y = 0;

    private static final float MAX_SPEED_PER_SECOND = 100F;
    public static final float WIDTH = COLLISION_CIRCLE_RADIUS * 2;
    private static final float HEIGHT_OFFSET = -400F;

    private boolean pointClaimed = false;

    //Textures
    private final Texture floorTexture;
    private final Texture ceilTexture;

    public Flower(Texture floorTexture, Texture ceilTexture) {
        this.floorTexture = floorTexture;
        this.ceilTexture = ceilTexture;
        this.y = MathUtils.random(HEIGHT_OFFSET);
        this.floorCollisionRectangle = new Rectangle(x, y, COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        this.floorCollisionCircle = new Circle(x + floorCollisionRectangle.width / 2, y + floorCollisionRectangle.height, COLLISION_CIRCLE_RADIUS);

        this.ceilCollisionRectangle = new Rectangle(x, floorCollisionCircle.y + DISTANCE_BETWEEN_FLOOR_AND_CEILING, COLLISION_RECTANGLE_WIDTH, COLLISION_RECTANGLE_HEIGHT);
        this.ceilCollisionCircle = new Circle(x + ceilCollisionRectangle.width / 2, ceilCollisionRectangle.y, COLLISION_CIRCLE_RADIUS);
    }

    public void setPosition(float x) {
        this.x = x;
        updateCollisionCircle();
        updateCollisionRectangle();
    }

    private void updateCollisionCircle() {
        floorCollisionCircle.setX(x + floorCollisionRectangle.width / 2);
        ceilCollisionCircle.setX(x + ceilCollisionRectangle.width / 2);
    }

    private void updateCollisionRectangle() {
        floorCollisionRectangle.setX(x);
        ceilCollisionRectangle.setX(x);
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.circle(floorCollisionCircle.x, floorCollisionCircle.y, floorCollisionCircle.radius);
        shapeRenderer.rect(floorCollisionRectangle.x, floorCollisionRectangle.y, floorCollisionRectangle.width, floorCollisionRectangle.height);
        shapeRenderer.circle(ceilCollisionCircle.x, ceilCollisionCircle.y, ceilCollisionCircle.radius);
        shapeRenderer.rect(ceilCollisionRectangle.x, ceilCollisionRectangle.y, ceilCollisionRectangle.width, ceilCollisionRectangle.height);
    }

    public void update(float delta) {
        setPosition(x - (MAX_SPEED_PER_SECOND * delta));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isFlappeeColliding(Flappee flappee) {
        Circle flappeeCollisionCircle = flappee.getCollisionCircle();
        return Intersector.overlaps(flappeeCollisionCircle, ceilCollisionCircle)
                || Intersector.overlaps(flappeeCollisionCircle, floorCollisionCircle)
                || Intersector.overlaps(flappeeCollisionCircle, ceilCollisionRectangle)
                || Intersector.overlaps(flappeeCollisionCircle, floorCollisionRectangle);
    }


    public boolean isPointClaimed() {
        return pointClaimed;
    }

    public void markPointClaimed() {
        pointClaimed = true;
    }

    public void draw(SpriteBatch batch) {
        drawFloorFlower(batch);
        drawCeilFlower(batch);
//        batch.draw(floorTexture, floorCollisionRectangle.getX(), floorCollisionRectangle.getY());
//        batch.draw(ceilTexture, ceilCollisionRectangle.getX(), ceilCollisionRectangle.getY());
    }


    private void drawFloorFlower(SpriteBatch batch) {
        float textureX = floorCollisionCircle.x - floorTexture.getWidth() / 2;
        float textureY = floorCollisionRectangle.getY() + COLLISION_CIRCLE_RADIUS - 32;
        batch.draw(floorTexture, textureX, textureY);
    }

    private void drawCeilFlower(SpriteBatch batch) {
        float textureX = ceilCollisionCircle.x - ceilTexture.getWidth() / 2;
        float textureY = ceilCollisionRectangle.getY() + COLLISION_CIRCLE_RADIUS - 64;
        batch.draw(ceilTexture, textureX, textureY);
    }

}

