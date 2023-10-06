package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class GameScene extends ScreenAdapter {

	Viewport viewport = new ExtendViewport(1920, 1080);
	Skin skin;
	private final Stage stage = new Stage(viewport);
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
	public float damage=5;
	Array<Wave> waves;
	Assets assets;
	Array<ParticleEffect> particleEffects;
	Boolean scaled=false;
	private int score=0;
	private int enemiesLeftToKill;
	Image imageActor;
	Music gameMusic;

	Music bossMusic;
	boolean isPaused=false;
	private WaveCompleteTable waveCompleteTable;

	private Texture healthBarTexture;
	private Texture healthFillTexture;
	private float healthBarWidth;
	private float healthBarHeight;
	private float maxBossHealth;

	private final Integer musicVolume;
	private final Integer soundVolume;

	public Integer critRate=15;

	MyGdxGame game;

	public GameScene(MyGdxGame game,Integer musicVolume,Integer soundVolume) {
		this.game=game;
		this.soundVolume=soundVolume;
		this.musicVolume=musicVolume;
	}

	@Override
    public void show()
    {

		assets = new Assets();
		assets.loadGameAssets();
		assets.getAssetManager().finishLoading();

		skin = assets.getAssetManager().get(Assets.skin);

        batch = new SpriteBatch();

		// Load the TiledMap
		tiledMap = assets.getAssetManager().get(Assets.tiledMap);

		// Create a TiledMapRenderer
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		// Create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		float zoomLevel;
		zoomLevel = 0.5f;
		camera.zoom = zoomLevel;

		// Create character
		character = new Character(new Vector2(800, 800),assets);

		// Initialize bullets array
		bullets = new Array<>();

		// Initialize enemies array
		enemies = new Array<>();

		//Initialize waves add a few
		waves = new Array<>();
		waves.add(new Wave(1, 3, 0.5f, 90, damage));
		waves.add(new Wave(2, 4, 0.4f, 95, damage));
		waves.add(new Wave(3, 5, 0.3f, 100, damage));
		waves.add(new Wave(4, 6, 0.3f, 110, damage));
		waves.add(new Wave(6, 6, 0.3f, 115, damage));
		waves.add(new Wave(7, 7, 0.3f, 120, damage));
		waves.add(new Wave(8, 1, 0.4f, 500, damage));

		minCameraX = camera.viewportWidth / 2-480 ;
		minCameraY = camera.viewportHeight / 2 - 268;

		// 3200 = map size (w x h)
		maxCameraX = 1920 * tiledMapRenderer.getUnitScale() - camera.viewportWidth / 2 +480 ;
		maxCameraY = 1920 * tiledMapRenderer.getUnitScale() - camera.viewportHeight / 2 +268;

		// Initialize particles array
		particleEffects=new Array<>();

		imageActor= new Image(assets.getAssetManager().get(Assets.skullTexture));
		imageActor.setPosition((float)Gdx.graphics.getWidth()/2-imageActor.getWidth(),(float)(Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10)+imageActor.getHeight()/3);
		imageActor.setSize(imageActor.getWidth()/1.5f,imageActor.getHeight()/1.5f);
		enemiesLeftToKill=waves.first().getNumEnemies();

		gameMusic = assets.getAssetManager().get(Assets.gameMusic);
		gameMusic.setLooping(true);
		gameMusic.setVolume(musicVolume/100f);
		gameMusic.play();

		bossMusic=assets.getAssetManager().get(Assets.bossMusic);
		bossMusic.setLooping(true);

		waveCompleteTable= new WaveCompleteTable(skin,assets,this);

		waveCompleteTable.center();

		// Calculate the position to center the table on the screen
		float centerX = Gdx.graphics.getWidth() / 2f;
		float centerY = Gdx.graphics.getHeight() / 2f;

		// Set the table's position
		waveCompleteTable.setPosition(centerX - waveCompleteTable.getWidth() / 2f, centerY - waveCompleteTable.getHeight() / 2f);

		healthBarTexture = assets.getAssetManager().get(Assets.BorderHealthTexture);
		healthFillTexture = assets.getAssetManager().get(Assets.HealthTexture);

		healthBarWidth = (float) Gdx.graphics.getWidth() /5;
		healthBarHeight = (float) Gdx.graphics.getHeight() /36;
		maxBossHealth=500;
	}

    @Override
	public void render(float delta) {

		// Clear the screen
		ScreenUtils.clear(1, 0, 0, 1);

		// Update character and camera
		character.update(enemies,tiledMap,isPaused,bullets);
		updateCamera();

		timeSinceLastShot += Gdx.graphics.getDeltaTime();

		// Handle shooting bullets if the cooldown has expired , 0.2f = cooldown

		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= 0.2f && !isPaused) {
			shootBullet();
			timeSinceLastShot = 0.0f;
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
			if (currentWave.getNumEnemies() > 0 && enemySpawnTimer >= currentWave.getEnemySpawnInterval() && !isPaused) {
				spawnEnemy(currentWave.getEnemyHealth());
				enemySpawnTimer = 0.0f;
				currentWave.setNumEnemies(currentWave.getNumEnemies() - 1);
			}

			// Check if the current wave is completed
			if (currentWave.getNumEnemies() == 0 && enemies.isEmpty()) {
				waves.removeIndex(0);
				scaled=false;
				if(!waves.isEmpty())
				{
					enemiesLeftToKill=waves.first().getNumEnemies();
				}
				isPaused=true;
				if(waves.size==1)
				{
					gameMusic.stop();
					bossMusic.play();
				}
			}
		}
		else {
			game.setScreen(new MainMenuScreen(game));
		}

		// Update and render enemies
		for (Enemy enemy : enemies) {
			Vector2 poz=enemy.update(Gdx.graphics.getDeltaTime(),bullets,enemies,isPaused);
			// Check if the enemy died
			if(!poz.epsilonEquals(-1,-1))
			{
				DeathParticles(poz,enemy.getHealthScale());
				scaled=true;
				score+= (int) waves.first().getEnemyHealth();
				enemiesLeftToKill-=1;
			}

			enemy.render(batch);
		}
		if (!waves.isEmpty()) {
			drawWaveNumberAndScore();
		}
		//draw Hearts
		character.drawHearts(batch,camera);

		// Draw the particle effects
		for (ParticleEffect particle:particleEffects) {
			particle.draw(batch);
		}

		if (!enemies.isEmpty() && enemies.first().isBoss) {
			Vector2 healthBarPosition = new Vector2(camera.position.x- (float) healthBarTexture.getWidth() /2, camera.position.y+ (float) healthBarTexture.getWidth() /3+healthBarTexture.getHeight());
			float bossHealthPercentage =enemies.first().getHealth() / maxBossHealth;
			float healthBarFillWidth = healthBarWidth * bossHealthPercentage;
			batch.draw(healthBarTexture, healthBarPosition.x, healthBarPosition.y, healthBarWidth, healthBarHeight);
			batch.draw(healthFillTexture, healthBarPosition.x, healthBarPosition.y, healthBarFillWidth, healthBarHeight);
		}
		// End the batch
		batch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		if(character.getLives()<=0||Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			gameMusic.dispose();
			bossMusic.dispose();
			game.setScreen(new MainMenuScreen(game));
		}

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
		stage.dispose();
		assets.dispose();
		gameMusic.dispose();
		bossMusic.dispose();
	}

	private void shootBullet() {
		Vector2 bulletStartPosition = new Vector2(character.getPosition());
		Vector2 directionToCursor = calculateDirectionToCursor(bulletStartPosition);

		// Normalize the direction vector and scale it to the bullet's speed
		directionToCursor.nor().scl(800);

		// Create a new Bullet and set the damage
		Bullet bullet = new Bullet(bulletStartPosition, directionToCursor,damage,assets,"Character",soundVolume);

		// Add bullet to array
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

	private void spawnEnemy(float health) {
		// Generate a random position for the enemy
		Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));

		// Create an enemy instance and pass the player's position
		Enemy enemy = new Enemy(enemyPosition, character.getPosition(),health,assets,500==health,soundVolume,critRate);

		// Add the enemy to a list or array to manage multiple enemies
		enemies.add(enemy);
	}

	private void drawWaveNumberAndScore() {

		if(!isPaused) {
			stage.clear();
			// Wave
			String Text = "Wave: " + waves.first().getWaveNumber();
			Label Label = new Label(Text, skin);
			float TextX = (float) Gdx.graphics.getWidth() / 2 - Label.getWidth() / 2;
			float TextY = Gdx.graphics.getHeight() - Label.getHeight() / 2;
			Label.setPosition(TextX, TextY);
			stage.addActor(Label);
			// Score
			Text = "Score: " + score;
			Label = new Label(Text, skin);
			TextX = Gdx.graphics.getWidth() - (float) Gdx.graphics.getWidth() / 6;
			TextY = Gdx.graphics.getHeight() - Label.getHeight() / 2;
			Label.setPosition(TextX, TextY);
			stage.addActor(Label);

			Text = ": " + enemiesLeftToKill;
			Label = new Label(Text, skin);
			TextX = imageActor.getX() + Label.getWidth();
			TextY = imageActor.getY();
			Label.setPosition(TextX, TextY);
			stage.addActor(Label);
			stage.addActor(imageActor);
		}
		else stage.addActor(waveCompleteTable);
		Gdx.input.setInputProcessor(stage);
	}

	private void DeathParticles(Vector2 position, float scale) {
		ParticleEffect particleEffect = assets.getAssetManager().get(Assets.explosionParticleEffect);
		ParticleEmitter emitter = particleEffect.getEmitters().first();
		emitter.setPosition(position.x, position.y);
		if (!scaled) {
			emitter.scaleSize(scale);
		}
		particleEffect.start();
		particleEffects.add(particleEffect);
	}

}
