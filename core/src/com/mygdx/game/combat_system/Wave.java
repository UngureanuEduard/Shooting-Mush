package com.mygdx.game.combat_system;

public class Wave {
    private final int waveNumber;
    private  int numEnemies;
    private final float enemySpawnInterval;
    private final float enemyHealth;

    private int numBossEnemies;

    public Wave(int waveNumber,int numBossEnemies, int numEnemies, float enemySpawnInterval, float enemyHealth ) {
        this.waveNumber = waveNumber;
        this.numBossEnemies = numBossEnemies;
        this.numEnemies = numEnemies;
        this.enemySpawnInterval = enemySpawnInterval;
        this.enemyHealth = enemyHealth;
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

    public float getEnemyHealth() {
        return enemyHealth;
    }

    public void setNumEnemies(int numEnemies){
        this.numEnemies = numEnemies;
    }

    public int getNumBossEnemies() {
        return numBossEnemies;
    }
    public void setNumBossEnemies(int numBossEnemies) {
        this.numBossEnemies = numBossEnemies;
    }
}
