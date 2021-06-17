package application.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import application.config.Sounds;
import application.config.Utilities;
import application.view.Maps;

public class Game {

	private static Game game = null;
	private boolean pause;
	private Paddle paddle;
	private int firstHalfPaddle;
	private Ball ball;
	private ArrayList<Brick> bricks;
	private ArrayList<Powerups> pwr;
	private ArrayList<Boolean> pwrActivated;
	private ArrayList<Integer> pwrDuration;
	private ArrayList<Boolean> managerTimePwr;
	private int widthPaddle;
	private int velBall;
	private int level;
	private int lives;
	private int score;
	private Sounds sound;
	private Timer timer; 
	private Sounds loseLife;
	private Sounds paddleSound;
	
	public static Game getInstance() {
		if (game == null)
			game = new Game();
		return game;
	}

	private Game() {
		paddle = new Paddle();
		paddle.x = Utilities.WIDTH_SIZE / 2 - Utilities.DIM_X_PADDLE / 2;
		paddle.y = Utilities.HEIGHT_SIZE - 120;
		firstHalfPaddle = 30;
		paddle.speed = 25;
		widthPaddle = 0;
		ball = new Ball();
		ball.x = Utilities.WIDTH_SIZE / 2 - Utilities.DIM_BALL / 2;
		ball.y = Utilities.HEIGHT_SIZE - 140;
		ball.dirX = -1;
		ball.dirY = -1;
		pause = true;
		bricks = new ArrayList<Brick>();
		pwr = new ArrayList<Powerups>();
		pwrActivated = new ArrayList<Boolean>();
		pwrDuration = new ArrayList<Integer>();
		managerTimePwr = new ArrayList<Boolean>();
		sound = new Sounds("levelCompleted.wav");
		loseLife = new Sounds("loseLife.wav");
		paddleSound = new Sounds("paddle.wav");
		lives = 3;
		score = 0;
		velBall = 8;
		timer = new Timer();
		for (int i = 0; i < 6; i++) {
			pwrDuration.add(5);
			pwrActivated.add(false);
			managerTimePwr.add(false);
		}
	}

	public void showLevel() {
		showCurrentLevel(Maps.getIstance().ReadMap(getLevel()));
	}

	public void movePaddle(int direction) {
		if (!pause) {
			switch (direction) {
			case 0:
				if (paddle.x + paddle.speed <= Utilities.WIDTH_SIZE - (Utilities.DIM_X_PADDLE + widthPaddle)) {
					paddle.x += paddle.speed;
				}
				break;
			case 1:
				if (paddle.x - paddle.speed >= 0)
					paddle.x -= paddle.speed;
				break;
			default:
				return;
			}
		}
	}

	public void updateBall() {
		int bricksBroken = 0;
		for (int i = 0; i < bricks.size(); i++) {
			if (bricks.get(i).getDestroyed())
				bricksBroken++;
		}
		if (bricksBroken == bricks.size()) {
			timer.schedule(new Countdown(3,0), 0, 1000);
			sound.start();
			level++;
			score = 0;
			
			bricks.clear();
			showCurrentLevel(Maps.getIstance().ReadMap(getLevel()));
			resetGame();
		}
		if (!pause) {
			if ((ball.x <= 0 && ball.dirX < 0)
					|| (ball.x + Utilities.DIM_BALL >= Utilities.WIDTH_SIZE && ball.dirX > 0))
				ball.dirX = -ball.dirX;
			if ((ball.y <= 0 && ball.dirY < 0)
					|| (ball.y + Utilities.DIM_BALL >= Utilities.HEIGHT_SIZE && ball.dirY > 0))
				ball.dirY = -ball.dirY;
			if (ball.y <= 0 && ball.dirX == 0) {
				Random direction = new Random();
				int  dir = direction.nextInt(2) + 1;
				System.out.println(dir);
				if (dir == 1)
					ball.dirX = -1;
				else 
					ball.dirX = 1;
			}
			ballCollision();
			int cont = 0;
			while (cont != velBall) {
				ball.x += ball.dirX;
				ball.y += ball.dirY;
				cont++;
			}
		}
	}

	public void ballCollision() {
		paddleCollision();
		brickCollision();
	}

	private void paddleCollision() {
		if (ball.getRect().getMaxY() > Utilities.LIMIT_LINE) {
			lives--;
			loseLife.start();
			resetGame();
		}
		if (paddle.getRect().intersects(ball.getRect())) {
			int posPaddle = (int) paddle.getRect().getMinX();
			int posBall = (int) ball.getRect().getMinX();
			paddleSound.start();
			int firstHalf = posPaddle + firstHalfPaddle;
			int secondHalf = posPaddle + firstHalfPaddle*2;
			int thirdHalf = posPaddle + firstHalfPaddle*3;
			int fourthHalf = posPaddle + firstHalfPaddle*4;
			
			if (posBall < firstHalf) {
				if (ball.dirX > 0)
					ball.dirX = -ball.dirX;
				if (ball.dirX == 0)
					ball.dirX = -1;
				ball.dirY = -ball.dirY;
			} else if (posBall >= firstHalf && posBall < secondHalf) {
				if (ball.dirX > 0)
					ball.dirX = -ball.dirX;
				if (ball.dirX == 0)
					ball.dirX = -1;
				ball.dirY = -ball.dirY;
			} else if (posBall >= secondHalf && posBall < thirdHalf) {
				ball.dirX = 0;
				ball.dirY = -ball.dirY;
			} else if (posBall >= thirdHalf && posBall < fourthHalf) {
				if (ball.dirX < 0)
					ball.dirX = -ball.dirX;
				if (ball.dirX == 0)
					ball.dirX = 1;
				ball.dirY = -ball.dirY;
			} else if (posBall > fourthHalf) {
				if (ball.dirX < 0)
					ball.dirX = -ball.dirX;
				if (ball.dirX == 0)
					ball.dirX = 1;
				ball.dirY = -ball.dirY;
			}
		}
	}

	private void brickCollision() {
		for (int i = 0; i < bricks.size(); i++) {
			if (ball.getRect().intersects(bricks.get(i).getRect())) {
				int ballLeft = (int) ball.getRect().getMinX();
				int ballHeight = (int) ball.getRect().getHeight();
				int ballWidth = (int) ball.getRect().getWidth();
				int ballTop = (int) ball.getRect().getMinY();

				Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
				Point pointLeft = new Point(ballLeft - 1, ballTop);
				Point pointTop = new Point(ballLeft, ballTop - 1);
				Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

				if (!bricks.get(i).getDestroyed()) {
					if (managerTimePwr.get(Utilities.PWR_FIREBALL)) {
						bricks.get(i).setDestroyed(true);
						score++;
					} else {
						if (bricks.get(i).resistance == Utilities.BRICK_RES_1) {
							if (i % 2 == 0)
								spawnPwr();
							bricks.get(i).setDestroyed(true);
							score++;
						}
						bricks.get(i).resistance--;
						if (bricks.get(i).getRect().contains(pointRight))
							ball.dirX = -1;
						else if (bricks.get(i).getRect().contains(pointLeft))
							ball.dirX = 1;
						if (bricks.get(i).getRect().contains(pointTop)) {
							if (ball.dirX == 0)
								ball.dirX = -1;
							ball.dirY = 1;
						} else if (bricks.get(i).getRect().contains(pointBottom))
							ball.dirY = -1;
						addPwr(bricks.get(i));
					}
				}
			}
		}
	}

	public void addPwr(Brick b) {
		for (int i = 0; i < pwrActivated.size(); i++) {
			if (pwrActivated.get(i)) {
				Powerups p = new Powerups();
				p.x = b.x + Utilities.DIM_X_BRICK / 2;
				p.y = b.y;
				p.speed = 10;
				p.setPower(i);
				pwr.add(p);
			}
		}
	}

	public void pwrCollision() {
		if (!pause) {
			for (int i = 0; i < pwr.size(); i++) {
				pwr.get(i).y += pwr.get(i).speed;
				if (paddle.getRect().intersects(pwr.get(i).getRect())) {
					if (pwr.get(i).getPower() == Utilities.PWR_LIFE) {
					if (lives <= 5)
						lives++;
					}
					if (pwr.get(i).getPower() == Utilities.PWR_LARGE_PADDLE && !managerTimePwr.get(Utilities.PWR_LARGE_PADDLE)) 
						timer.schedule(new Countdown(pwrDuration.get(pwr.get(i).power), Utilities.PWR_LARGE_PADDLE), 0, 1000);
					
					if (pwr.get(i).getPower() == Utilities.PWR_FIREBALL && !managerTimePwr.get(Utilities.PWR_FIREBALL))
						timer.schedule(new Countdown(pwrDuration.get(pwr.get(i).power), Utilities.PWR_FIREBALL), 0, 1000);
					
					if (pwr.get(i).getPower() == Utilities.NERF_VEL_PADDLE) 
						timer.schedule(new Countdown(pwrDuration.get(pwr.get(i).power), Utilities.NERF_VEL_PADDLE), 0, 1000);
						
					if (pwr.get(i).getPower() == Utilities.NERF_VEL_BALL) 
						velBall = 14;
					
					pwr.remove(i);
				} else if (pwr.get(i).y > Utilities.LIMIT_LINE)
					pwr.remove(i);
			}
			for (int i = 0; i < pwrActivated.size(); i++)
				pwrActivated.set(i, false);
		}
	}

	public void showCurrentLevel(int[][] level) {
		int k = 0;
		for (int i = 0; i < level.length; i++) {
			for (int j = 0; j < level[i].length; j++) {
				if (level[i][j] >= Utilities.BRICK_RES_1) {
					Brick b = new Brick(j * Utilities.DIM_X_BRICK, i * Utilities.DIM_Y_BRICK);
					bricks.add(b);
					bricks.get(k).resistance = level[i][j];
					bricks.get(k).resistanceInit = level[i][j];
					k++;
				}
			}
		}
	}

	public void spawnPwr() {
		Random r = new Random();
		int rand = r.nextInt(100) + 1;
		if (rand <= 2 && level < 6)
			pwrActivated.set(Utilities.PWR_LIFE, true);
		else if (rand > 3 && rand <= 7)
			pwrActivated.set(Utilities.PWR_LARGE_PADDLE, true);
		else if (rand > 8 && rand <= 12)
			pwrActivated.set(Utilities.PWR_FIREBALL, true);
		else if (rand > 12 && rand <= 17)
			pwrActivated.set(Utilities.NERF_VEL_BALL, true);
		else if (rand > 18 && rand <= 23)
			pwrActivated.set(Utilities.NERF_VEL_PADDLE, true);
	}

	public void resetGame() {
		paddle.x = Utilities.WIDTH_SIZE / 2 - Utilities.DIM_X_PADDLE / 2;
		paddle.y = Utilities.HEIGHT_SIZE - 120;
		ball.x = Utilities.WIDTH_SIZE / 2 - Utilities.DIM_BALL / 2;
		ball.y = Utilities.HEIGHT_SIZE - 140;
		pwr.clear();
		paddle.speed = 25;
		velBall = 8;
		ball.dirX = -1;
		ball.dirY = -1;
	}

	public ArrayList<Brick> getBrick() {
		return bricks;
	}

	public Paddle getPaddle() {
		return paddle;
	}

	public Ball getBall() {
		return ball;
	}

	public void setPause(boolean p) {
		pause = p;
	}

	public boolean getPause() {
		return pause;
	}

	public void setLevel(int lvl) {
		level = lvl;
	}

	private int getLevel() {
		return level;
	}

	public int getLives() {
		return lives;
	}

	public int getScore() {
		return score;
	}

	public ArrayList<Powerups> getPwr() {
		return pwr;
	}
	
	public int dimBricks() {
		return bricks.size();
	}

	public void setManagerTimePwr(ArrayList<Boolean> managerTimePwr) {
		this.managerTimePwr = managerTimePwr;
	}

	public ArrayList<Boolean> getManagerTimePwr() {
		return managerTimePwr;
	}
	
	public void setWidthPaddle(int widthPaddle) {
		this.widthPaddle = widthPaddle;
	}

	public void setFirstHalfPaddle(int firstHalfPaddle) {
		this.firstHalfPaddle = firstHalfPaddle;
	}
}
