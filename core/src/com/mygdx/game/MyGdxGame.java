package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
	Array<Enemy> enemies;
	private float timeSinceLastShot = 0.0f;
	private Texture heartTexture;
	private Texture emptyHeartTexture;
	private int characterLives;
	private int damage;
	Array<Wave> waves;

	private BitmapFont font;
	private GlyphLayout glyphLayout;

	Array<ParticleEffect> particleEffects;

	@Override
	public void create() {
		batch = new SpriteBatch();

		// Load the TiledMap
		tiledMap = new TmxMapLoader().load("map.tmx");

		// Create a TiledMapRenderer
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		// Create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		float zoomLevel;
		zoomLevel = 0.5f;
		camera.zoom = zoomLevel;

		// Create character
		character = new Character(new Vector2(800, 800));

		// Initialize bullets array
		bullets = new Array<>();

		// Initialize enemies array
		enemies = new Array<>();

		//Initialize waves add a few
		waves = new Array<>();
		waves.add(new Wave(1, 3, 0.5f, 200, 200));
		waves.add(new Wave(2, 6, 0.4f, 400, 200));

		minCameraX = camera.viewportWidth / 2-480 ;
		minCameraY = camera.viewportHeight / 2 - 268;

		// 3200 = map size (w x h)
		maxCameraX = 3200 * tiledMapRenderer.getUnitScale() - camera.viewportWidth / 2 +480 ;
		maxCameraY = 3200 * tiledMapRenderer.getUnitScale() - camera.viewportHeight / 2 +268;

		heartTexture = new Texture("Environment/heart.png");
		emptyHeartTexture = new Texture("Environment/border.png");

		characterLives = 3; // Start with 3 lives

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Kaph-Regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 24;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 1;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		font = generator.generateFont(parameter);
		generator.dispose();
		glyphLayout = new GlyphLayout();
		// Without this the font will shake
		font.setUseIntegerPositions(false);

		// Initialize particles array
		particleEffects=new Array<>();
	}

	@Override
	public void render() {
		// Clear the screen
		ScreenUtils.clear(1, 0, 0, 1);

		// Update character and camera
		character.update(enemies,tiledMap);
		updateCamera();

		timeSinceLastShot += Gdx.graphics.getDeltaTime();

		// Handle shooting bullets if the cooldown has expired , 0.2f = cooldown

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= 0.2f) {
			shootBullet();
			timeSinceLastShot = 0.0f;  // Reset the timer
		}

		// Set the camera's projection matrix
		batch.setProjectionMatrix(camera.combined);

		for (ParticleEffect particle:particleEffects) {
			particle.update(Gdx.graphics.getDeltaTime());
		}

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

		if (!waves.isEmpty()) {
			Wave currentWave = waves.first();
			enemySpawnTimer += Gdx.graphics.getDeltaTime();
			damage=currentWave.getBulletDamage();
			if (currentWave.getNumEnemies() > 0 && enemySpawnTimer >= currentWave.getEnemySpawnInterval()) {
				spawnEnemy(currentWave.getEnemyHealth());
				enemySpawnTimer = 0.0f;
				currentWave.setNumEnemies(currentWave.getNumEnemies() - 1);
			}

			// Check if the current wave is completed
			if (currentWave.getNumEnemies() == 0 && enemies.isEmpty()) {
				waves.removeIndex(0);
			}
		}

		// Update and render enemies
		for (Enemy enemy : enemies) {
			 Vector2 poz=enemy.update(Gdx.graphics.getDeltaTime(),bullets,enemies);
			// Check if the enemy died
			 if(!poz.epsilonEquals(-1,-1))
				 DeathParticles(poz,enemy.getHealthScale());
			enemy.render(batch);
		}

		float heartX = camera.position.x - (camera.viewportWidth * camera.zoom) / 2 + 10 * camera.zoom;
		float heartY = camera.position.y + (camera.viewportHeight * camera.zoom) / 2 - 40 * camera.zoom;

		for (int i = 0; i < characterLives; i++) {
			float heartContainerX = heartX + i * 40 * camera.zoom;
			if (i >= characterLives - character.getLives()) {
				batch.draw(heartTexture, heartContainerX, heartY);
				batch.draw(emptyHeartTexture, heartContainerX, heartY);
			} else {
				batch.draw(emptyHeartTexture, heartContainerX, heartY);
			}
		}

		// Draw wave number
		if(!waves.isEmpty())
		{
			drawCurrentWaveNumber(heartX,heartY);
		}
		// Draw the particle effects
		for (ParticleEffect particle:particleEffects) {
			particle.draw(batch);
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
		bullet.setDamage(damage);
		bullets.add(bullet);
	}

	private Vector2 calculateDirectionToCursor(Vector2 startingPoint) {
		// Get the cursor position in screen coordinates
		Vector2 cursorPositionScreen = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

		// Convert the cursor position from screen coordinates to world coordinates
		Vector2 cursorPositionWorld = new Vector2(cursorPositionScreen.x / camera.zoom + camera.position.x - camera.viewportWidth ,
				cursorPositionScreen.y / camera.zoom + camera.position.y - camera.viewportHeight );

		// Return the direction vector from the starting point to the cursor position
        return cursorPositionWorld.cpy().sub(startingPoint);
	}

	private void spawnEnemy(int health) {
		// Generate a random position for the enemy
		Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));

		// Create an enemy instance and pass the player's position
		Enemy enemy = new Enemy(enemyPosition, character.getPosition(),health);

		// Add the enemy to a list or array to manage multiple enemies
		enemies.add(enemy);
	}

	private void drawCurrentWaveNumber(float heartX, float heartY) {
		String text = "Wave:" + waves.first().getWaveNumber();

		glyphLayout.setText(font, text);

		float textX = heartX + camera.viewportWidth / 4 - 90 * camera.zoom;
		float textY = heartY + 15;

		// Draw the text
		font.setColor(Color.WHITE);
		font.draw(batch, text, textX, textY);
	}

	private void DeathParticles(Vector2 position,float scale)
	{
		particleEffects.add(new ParticleEffect());
		particleEffects.get(particleEffects.size-1).load(Gdx.files.internal("Environment/explosion/explosion.party"),Gdx.files.internal("Environment/explosion"));
		particleEffects.get(particleEffects.size-1).getEmitters().first().setPosition(position.x, position.y);
		particleEffects.get(particleEffects.size-1).getEmitters().first().scaleSize(scale);
		particleEffects.get(particleEffects.size-1).start();
	}


}
