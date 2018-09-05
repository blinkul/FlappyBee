package com.ciprian.game;

import com.badlogic.gdx.Game;

public class FlappyBee extends Game {
	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
