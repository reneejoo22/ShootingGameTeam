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
    private List<Rectangle> rocks;
    private Rectangle monster;
    private long lastRockThrowTime;
    private int bulletCount = 6; // 총알 개수
    private long lastReloadTime; // 마지막 재장전 시간
    private final long RELOAD_TIME = 2000; // 재장전 시간 (2초)

    public ShootingGame1() {
        playerImage = new ImageIcon("images/soldier.png")
                .getImage()
                .getScaledInstance(80, 100, Image.SCALE_SMOOTH);

        backgroundImage = new ImageIcon("images/back2.png").getImage();

        monsterImage = new ImageIcon("images/stage 1 zombie.png")
                .getImage()
                .getScaledInstance(150, 150, Image.SCALE_SMOOTH);

        missileImage = new ImageIcon("images/bullet1.png")
                .getImage()
                .getScaledInstance(30, 40, Image.SCALE_SMOOTH);

        rockImage = new ImageIcon("images/zombie's fireball.png")
                .getImage()
                .getScaledInstance(60, 60, Image.SCALE_SMOOTH);

        keys = new boolean[256];
        missiles = new ArrayList<>();
        rocks = new ArrayList<>();
        monster = new Rectangle((int) (Math.random() * (400 - 150)), 0, 150, 150);

        playerX = 180;
        playerY = 600;
        score = 0;
        backgroundY = 0;

        lastRockThrowTime = System.currentTimeMillis();
        lastReloadTime = 0;

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
        monster.setBounds(
                (int) (Math.random() * (getWidth() - 150)), // 몬스터 크기에 맞춤
                0,
                150,
                150
        );
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
            g.drawImage(rockImage, rock.x, rock.y, this);
        }

        g.setColor(Color.WHITE);
        g.drawString("플레이어 점수: " + score, 10, 20);
        g.drawString("남은 총알수: " + bulletCount, 10, 40); // 남은 총알 표시
    }

    public void actionPerformed(ActionEvent e) {
        updatePlayerPosition();
        updateMissiles();
        updateRocks();
        checkCollisions();
        updateBackground();
        checkMonsterAttack();
        throwRock();
        reloadBullets(); // 재장전 확인
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
        playerX = Math.min(playerX, getWidth() - 50); // 이미지 크기(50)에 맞춤
        playerY = Math.max(playerY, 0);
        playerY = Math.min(playerY, getHeight() - 50);
    }

    private void updateMissiles() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext(); ) {
            Rectangle missile = it.next();
            missile.y -= 10;
            if (missile.y < 0) {
                it.remove();
            }
        }
    }

    private void updateRocks() {
        for (Iterator<Rectangle> it = rocks.iterator(); it.hasNext(); ) {
            Rectangle rock = it.next();
            rock.y += 7;
            if (rock.y > getHeight()) {
                it.remove();
            } else if (rock.intersects(new Rectangle(playerX, playerY, 50, 50))) {
                score -= 5;
                playSound("/audio/플레이어 신음.wav");
                it.remove();
            }
        }
    }

    private void throwRock() {
        if (System.currentTimeMillis() - lastRockThrowTime >= 3000) {
            rocks.add(new Rectangle(monster.x + 10, monster.y + monster.height, 40, 40));
            lastRockThrowTime = System.currentTimeMillis();
        }
    }

    private void checkCollisions() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext(); ) {
            Rectangle missile = it.next();
            if (monster.intersects(missile)) {
                it.remove();
                score += 10;
                playSound("/audio/몬스터소리.wav");
                spawnMonster();
                break;
            }
        }
    }

    private void checkMonsterAttack() {
        if (monster.intersects(new Rectangle(playerX, playerY, 50, 50))) {
            score -= 5;
            playSound("/audio/플레이어 신음.wav");
            spawnMonster();
        }
    }

    private void reloadBullets() {
        if (bulletCount == 0 && System.currentTimeMillis() - lastReloadTime >= RELOAD_TIME) {
            bulletCount = 6;
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
        if (e.getKeyCode() == KeyEvent.VK_SPACE && bulletCount > 0) {
            missiles.add(new Rectangle(
                    playerX + 15, // 미사일 중심 좌표 조정
                    playerY,
                    30, // 미사일 너비
                    40  // 미사일 높이
            ));
            playSound("/audio/총소리.wav");
            bulletCount--;
            if (bulletCount == 0) {
                lastReloadTime = System.currentTimeMillis(); // 재장전 시작
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) {}

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
