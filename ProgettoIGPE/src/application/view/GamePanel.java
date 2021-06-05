package application.view;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import application.config.Utilities;
import application.model.Brick;
import application.model.Game;

public class GamePanel extends JPanel {
	
	private static final long serialVersionUID = -2171231079412649632L;
	
	private PaddleView paddle = new PaddleView();
	private BallView ball = new BallView();
	private BrickView brick = new BrickView();
	private Image background = null;
	
	public GamePanel() {
		// qui setter� il background //
		JLabel lives = new JLabel("Lives:");
		lives.setBounds(10, 850, 60, 20);
		lives.setFont(new Font("Helvetica Neue",Font.PLAIN, 15));
		this.add(lives);
		this.setLayout(null);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        var g2d = (Graphics2D) g;

        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        /*background = ImageIO.read(getClass().getResourceAsStream("/application/resources/backgrounds/start.jpeg"));
		g.drawImage(background, 0, 0, null);*/
		int x_paddle = Game.getInstance().getPaddle().getX();
		int y_paddle = Game.getInstance().getPaddle().getY();
		g.drawImage(paddle.img, x_paddle, y_paddle, paddle.dimX, paddle.dimY, null);
		
		int x_ball = Game.getInstance().getBall().getX();
		int y_ball = Game.getInstance().getBall().getY();
		g.drawImage(ball.img, x_ball, y_ball, ball.dimX, ball.dimY, null);
		Brick[] bricks = Game.getInstance().getBrick();
		for (int i = 0; i < bricks.length; i++) {
			if (!bricks[i].getDestroyed()) {
			g.fillRect(bricks[i].getX(), bricks[i].getY(), Utilities.DIM_X_BRICK,Utilities.DIM_Y_BRICK);
			//g.drawImage(brick.img,bricks[i].getX(),bricks[i].getY(), brick.dimX,brick.dimY , null);
			}
		}
	}
		
	public void update() {
		repaint();
	}

}