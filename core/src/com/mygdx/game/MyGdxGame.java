package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Iterator;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	TiledMap tiledMap;
	OrthogonalTiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	Character character;
	Array<Bullet> bullets;
	private float minCameraX;
	private float minCameraY;
	private float maxCameraX;
	private float maxCameraY;
	private float enemySpawnTimer = 0.0f;
	private int enemiesToSpawn = 10; // Adjust to control the number of enemies
	Array<Enemy> enemies;
	private float timeSinceLastShot = 0.0f;
	private Texture heartTexture;
	private Texture emptyHeartTexture;
	private int characterLives;

	@Override
	public void create() {
		batch = new SpriteBatch();

		// Load the TiledMap
		tiledMap = new TmxMapLoader().load("map.tmx");

		// Create a TiledMapRenderer
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		// Create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 1280); // Can change
		float zoomLevel;
		zoomLevel = 0.5f;
		camera.zoom = zoomLevel;

		// Create character
		character = new Character(new Vector2(800, 800));

		// Initialize bullets array
		bullets = new Array<>();

		// Initialize enemies array
		enemies = new Array<>();

		minCameraX = camera.viewportWidth / 2 - 320;
		minCameraY = camera.viewportHeight / 2 - 320;

		// 3200 = map size (w x h)
		maxCameraX = 3200 * tiledMapRenderer.getUnitScale() - camera.viewportWidth / 2 + 320;
		maxCameraY = 3200 * tiledMapRenderer.getUnitScale() - camera.viewportHeight / 2 + 320;

		heartTexture = new Texture("heart.png");
		emptyHeartTexture = new Texture("border.png");

		characterLives = 3; // Start with 3 lives

	}

	@Override
	public void render() {
		// Clear the screen
		ScreenUtils.clear(1, 0, 0, 1);

		// Update character and camera
		character.update(enemies);
		updateCamera();

		timeSinceLastShot += Gdx.graphics.getDeltaTime();

		// Handle shooting bullets if the cooldown has expired

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= 0.2f) {
			shootBullet();
			timeSinceLastShot = 0.0f;  // Reset the timer
		}

		// Set the camera's projection matrix
		batch.setProjectionMatrix(camera.combined);

		// Begin the batch
		batch.begin();

		// Render the map
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		// Render the character
		character.render(batch);

		// Render the bullets
		for (Iterator<Bullet> iter = bullets.iterator(); iter.hasNext(); ) {
			Bullet bullet = iter.next();
			bullet.update(Gdx.graphics.getDeltaTime());
			if (!bullet.isActive()) {
				iter.remove(); // Remove the inactive bullet
			} else {
				bullet.render(batch);
			}
		}

		// Render Enemies
		enemySpawnTimer += Gdx.graphics.getDeltaTime();
		// Adjust this to control spawn rate
		float enemySpawnInterval = 0.1f;
		if (enemiesToSpawn > 0 && enemySpawnTimer >= enemySpawnInterval) {
			spawnEnemy();
			enemySpawnTimer = 0.0f;
			enemiesToSpawn--;
		}

		// Render Enemies
		enemySpawnTimer += Gdx.graphics.getDeltaTime();
		if (enemiesToSpawn > 0 && enemySpawnTimer >= enemySpawnInterval) {
			spawnEnemy();
			enemySpawnTimer = 0.0f;
			enemiesToSpawn--;
		}

		// Update and render enemies
		for (Enemy enemy : enemies) {
			enemy.update(Gdx.graphics.getDeltaTime(),bullets,enemies);
			enemy.render(batch);
		}

		float heartX = camera.position.x - (camera.viewportWidth * camera.zoom) / 2 + 10 * camera.zoom;
		float heartY = camera.position.y + (camera.viewportHeight * camera.zoom) / 2 - 40 * camera.zoom;

		for (int i = 0; i < characterLives; i++) {
			// Calculate the position for each heart container
			float heartContainerX = heartX + i * 40 * camera.zoom;

			if (i >= characterLives - character.getLives()) {
				batch.draw(heartTexture, heartContainerX, heartY);
				batch.draw(emptyHeartTexture, heartContainerX, heartY);
			} else {
				batch.draw(emptyHeartTexture, heartContainerX, heartY);
			}
		}
		// End the batch
		batch.end();
	}

	private void updateCamera() {
		// Center the camera on the character's position
		camera.position.x = MathUtils.clamp(character.getPosition().x + character.getWidth() / 2, minCameraX, maxCameraX);
		camera.position.y = MathUtils.clamp(character.getPosition().y + character.getHeight() / 2, minCameraY, maxCameraY);

		// Update the camera's matrices
		camera.update();
	}

	@Override
	public void dispose() {
		batch.dispose();
		tiledMap.dispose();
		tiledMapRenderer.dispose();
		character.dispose();
		for (Bullet bullet : bullets) {
            bullet.dispose();
        }
	}

	private void shootBullet() {
		Vector2 bulletStartPosition = new Vector2(character.getPosition());
		Vector2 directionToCursor = calculateDirectionToCursor(bulletStartPosition);

		// Normalize the direction vector and scale it to the bullet's speed
		directionToCursor.nor().scl(800);

		Bullet bullet = new Bullet(bulletStartPosition, directionToCursor);

		// Set the bullet's damage
		bullet.setDamage(50);
		bullets.add(bullet);
	}

	private Vector2 calculateDirectionToCursor(Vector2 startingPoint) {
		// Get the cursor position in screen coordinates
		Vector2 cursorPositionScreen = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		// Convert the cursor position from screen coordinates to world coordinates
		Vector2 cursorPositionWorld = new Vector2(cursorPositionScreen.x / camera.zoom + camera.position.x - camera.viewportWidth / 2,
				cursorPositionScreen.y / camera.zoom + camera.position.y - camera.viewportHeight / 2);

		// Return the direction vector from the starting point to the cursor position
        return cursorPositionWorld.cpy().sub(startingPoint);
	}

	private void spawnEnemy() {
		// Generate a random position for the enemy
		Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));

		// Create an enemy instance and pass the player's position
		Enemy enemy = new Enemy(enemyPosition, character.getPosition());
		enemy.setHealth(100);

		// Add the enemy to a list or array to manage multiple enemies
		enemies.add(enemy);
	}

}
