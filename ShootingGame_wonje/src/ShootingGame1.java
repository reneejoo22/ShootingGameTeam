import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;

public class ShootingGame1 extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Image playerImage, backgroundImage, monsterImage, missileImage, rockImage;
    private int playerX, playerY, score;
    private final int SPEED = 5;
    private int backgroundY;
    private boolean[] keys;
    private List<Rectangle> missiles;
    private List<Rectangle> rocks; // 몬스터가 던지는 돌 목록
    private Rectangle monster;
    private long lastRockThrowTime;

    public ShootingGame1() {
        playerImage = new ImageIcon("images/soldier.png")
        		.getImage()
        		.getScaledInstance(50, 50, Image.SCALE_SMOOTH);;
        backgroundImage = new ImageIcon("images/back2.png").getImage();
        monsterImage = new ImageIcon("images/stage 1 zombie.png")
        		.getImage()
        		.getScaledInstance(60, 60, Image.SCALE_SMOOTH);;
        missileImage = new ImageIcon("images/bullet1.png").getImage();
        rockImage = new ImageIcon("images/zombie's fireball.png").getImage();

        missileImage = new ImageIcon("images/bullet1.png")
                       .getImage()
                       .getScaledInstance(30, 40, Image.SCALE_SMOOTH); 

        rockImage = new ImageIcon("images/virus1.png")
                    .getImage()
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        
        keys = new boolean[256];
        missiles = new ArrayList<>();
        rocks = new ArrayList<>();
        monster = new Rectangle((int) (Math.random() * (400 - monsterImage.getWidth(null))), 0,
                                monsterImage.getWidth(null), monsterImage.getHeight(null));

        playerX = 180;
        playerY = 600;
        score = 0;
        backgroundY = 0;

        lastRockThrowTime = System.currentTimeMillis(); // 돌 던지기 타이머 초기화

        timer = new Timer(15, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(400, 800));
    }

    private void playSound(String filePath) {
        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void spawnMonster() {
        monster.setLocation((int) (Math.random() * (getWidth() - monsterImage.getWidth(null))), 0);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, backgroundY, this);
        g.drawImage(playerImage, playerX, playerY, this);
        g.drawImage(monsterImage, monster.x, monster.y, this);
        
        for (Rectangle missile : missiles) {
            g.drawImage(missileImage, missile.x, missile.y, this);
        }

        for (Rectangle rock : rocks) {
            g.drawImage(rockImage, rock.x, rock.y, this); // 돌을 화면에 그리기
        }
        
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    public void actionPerformed(ActionEvent e) {
        updatePlayerPosition();
        updateMissiles();
        updateRocks(); // 돌 업데이트
        checkCollisions();
        updateBackground();
        checkMonsterAttack();
        throwRock(); // 일정 시간마다 돌 던지기
        repaint();
    }

    private void updatePlayerPosition() {
        if (keys[KeyEvent.VK_LEFT]) {
            playerX -= SPEED;
        } else if (keys[KeyEvent.VK_RIGHT]) {
            playerX += SPEED;
        } else if (keys[KeyEvent.VK_UP]) {
            playerY -= SPEED;
        } else if (keys[KeyEvent.VK_DOWN]) {
            playerY += SPEED;
        }

        playerX = Math.max(playerX, 0);
        playerX = Math.min(playerX, getWidth() - playerImage.getWidth(null));
        playerY = Math.max(playerY, 0);
        playerY = Math.min(playerY, getHeight() - playerImage.getHeight(null));
    }

    private void updateMissiles() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            missile.y -= 10;
            if (missile.y < 0) {
                it.remove();
            }
        }
    }

    private void updateRocks() {
        for (Iterator<Rectangle> it = rocks.iterator(); it.hasNext();) {
            Rectangle rock = it.next();
            rock.y += 7; // 돌의 속도 설정
            if (rock.y > getHeight()) {
                it.remove(); // 화면을 벗어난 돌 제거
            } else if (rock.intersects(new Rectangle(playerX, playerY, playerImage.getWidth(null), playerImage.getHeight(null)))) {
                score -= 5;
                playSound("sounds/player_hit.wav"); // 플레이어가 돌에 맞을 때 소리
                it.remove();
            }
        }
    }

    private void throwRock() {
        if (System.currentTimeMillis() - lastRockThrowTime >= 3000) { // 3초마다 돌 던지기
            rocks.add(new Rectangle(monster.x + monster.width / 2 - rockImage.getWidth(null) / 2,
                                    monster.y + monster.height, rockImage.getWidth(null), rockImage.getHeight(null)));
            lastRockThrowTime = System.currentTimeMillis();
        }
    }

    private void checkCollisions() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            if (monster.intersects(missile)) {
                it.remove();
                score += 10;
                playSound("/audio/몬스터소리.wav"); // 몬스터가 미사일에 맞았을 때 소리
                spawnMonster();
                break;
            }
        }
    }

    private void checkMonsterAttack() {
        if (monster.intersects(new Rectangle(playerX, playerY, playerImage.getWidth(null), playerImage.getHeight(null)))) {
            score -= 5;
            playSound("sounds/플레이어 신음.wav"); // 몬스터와 충돌 시 소리
            spawnMonster();
        }
    }

    public void updateBackground() {
        backgroundY += 5;
        if (backgroundY > 0) {
            backgroundY = -backgroundImage.getHeight(null) + getHeight();
        }
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            missiles.add(new Rectangle(playerX + playerImage.getWidth(null) / 2 - missileImage.getWidth(null) / 2,
                                       playerY, missileImage.getWidth(null), missileImage.getHeight(null)));
            playSound("/audio/총소리.wav"); // 미사일 발사 소리
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) { }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ShootingGame1 gamePanel = new ShootingGame1();
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}