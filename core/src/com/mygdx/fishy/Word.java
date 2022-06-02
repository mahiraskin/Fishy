package com.mygdx.fishy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Word {

    //Declare the fields
    private GlyphLayout layout;
    private BitmapFont font;

    private String text;

    private int posX;
    private int posY;
    private int next;

    private boolean passable = true;
    private boolean passed = false;


    public Word(String text, int posX, int posY, String next, BitmapFont font) {
        this.text = text;
        this.posX = posX;
        this.posY = posY;
        this.font = font;
        this.next = Integer.parseInt(next);
        if (this.next != 0)
            font.setColor(Color.RED);
        else
            font.setColor(Color.WHITE);
        layout = new GlyphLayout(font, text);

    }


    public void update(SpriteBatch batch, OrthographicCamera camera) {
        if(checkClicked(camera) && next == 0) {
            System.out.println(Fishy.inputs);
            this.passable = false;
            font.setColor(Color.GRAY);
            this.layout.setText(font, text);
        }
        font.draw(batch, layout, posX, posY);
    }


    private boolean checkClicked(OrthographicCamera camera) {
        if (Gdx.input.justTouched()) {
            //Get screen coordinates
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            //Transform screen touch to world coordinates using the camera you are drawing with
            camera.unproject(touch);

            //System.out.println(getRectangle());
            //System.out.println(touch);

            if (getRectangle().contains(touch.x, touch.y)) {
                System.out.println(text + " has been clicked. " + this.next);
                return true;
            }
        }
        return false;
    }


    private Rectangle getRectangle() {
        return new Rectangle(posX, posY - (int)layout.height, (int)layout.width, (int)layout.height);
    }
    public void pass() {
        font.setColor(Color.BLUE);
        this.layout.setText(font, text);
        this.passed = true;
        this.passable = false;
    }
    public void setUnPassable() {
        this.passable = false;
    }

    public boolean isPassable() {
        return passable;
    }
    public boolean isPassed() {return this.passed;}
    public int getNext() {return this.next;}

}