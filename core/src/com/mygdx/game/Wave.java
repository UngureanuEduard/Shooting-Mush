package com.mygdx.game;

public class Wave {
    private final int waveNumber;
    private  int numEnemies;
    private final float enemySpawnInterval;
    private final int enemyHealth;
    private final int bulletDamage;

    public Wave(int waveNumber, int numEnemies, float enemySpawnInterval, int enemyHealth, int bulletDamage) {
        this.waveNumber = waveNumber;
        this.numEnemies = numEnemies;
        this.enemySpawnInterval = enemySpawnInterval;
        this.enemyHealth = enemyHealth;
        this.bulletDamage = bulletDamage;
    }


    public int getWaveNumber() {
        return waveNumber;
    }

    public int getNumEnemies() {
        return numEnemies;
    }

    public float getEnemySpawnInterval() {
        return enemySpawnInterval;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public void setNumEnemies(int numEnemies){
        this.numEnemies = numEnemies;
    }
}
