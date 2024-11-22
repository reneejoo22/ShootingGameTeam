import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

// 네프 12주차 체크포인트
// 저장확인
// 3333... cnrkfdfd
public class ShootingGame1 extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Image playerImage, backgroundImage, monsterImage, missileImage, monsterWeapon;
    private int playerX, playerY, playerHealth;
    private final int SPEED = 5;
    private boolean[] keys;
    private List<Rectangle> missiles;
    private List<Rectangle> monsterWeapons;
    private Rectangle monster;
    private int monsterHealth;
    private boolean monsterAlive, monsterVisible;
    private int monsterDirectionX, monsterDirectionY;
    private final int MONSTER_WEAPON_SPEED = 5;
    private int monsterWeaponCooldown = 180;
    private int monsterWeaponTimer = 0;
    private int backgroundY;
    private int monsterRespawnDelay = 100;
    private int monsterRespawnCounter = 0;

    public ShootingGame1() {
        playerImage = new ImageIcon("images/spaceship5.png").getImage();
        backgroundImage = new ImageIcon("images/back2.png").getImage();
        monsterImage = new ImageIcon("images/monster1.png").getImage();
        missileImage = new ImageIcon("images/bullet1.png").getImage();
        monsterWeapon = new ImageIcon("images/virus1.png").getImage();

        keys = new boolean[256];
        missiles = new ArrayList<>();
        monsterWeapons = new ArrayList<>();
        monster = new Rectangle((int) (Math.random() * (400 - monsterImage.getWidth(null))), 0,
                                monsterImage.getWidth(null), monsterImage.getHeight(null));
        playerX = 180;
        playerY = 600;
        playerHealth = 3;
        monsterHealth = 3;
        monsterAlive = true;
        monsterVisible = true;
        monsterDirectionX = 1;
        monsterDirectionY = 1;

        backgroundY = 0;

        timer = new Timer(15, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(400, 800));
    }

    private void spawnMonster() {
        monsterAlive = true;
        monsterVisible = true;
        monster.setLocation((int) (Math.random() * (getWidth() - monsterImage.getWidth(null))), 0);
        monsterHealth = 3;
        monsterWeaponTimer = 0;
    }

    private void updateMonsterPosition() {
        if (monsterAlive) {
            monster.x += 2 * monsterDirectionX;
            monster.y += 1 * monsterDirectionY;

            if (monster.x <= 0 || monster.x + monster.width >= getWidth()) {
                monsterDirectionX *= -1;
            }
            if (monster.y <= 0 || monster.y + monster.height >= getHeight()) {
                monsterDirectionY *= -1;
            }
        }
    }

    private void updateMonsterWeapons() {
        for (Iterator<Rectangle> it = monsterWeapons.iterator(); it.hasNext();) {
            Rectangle weapon = it.next();
            weapon.y += MONSTER_WEAPON_SPEED;
            if (weapon.y > getHeight()) {
                it.remove();
            } else if (weapon.intersects(new Rectangle(playerX, playerY, playerImage.getWidth(null), playerImage.getHeight(null)))) {
                it.remove();
                playerHealth--;
                if (playerHealth <= 0) {
                    gameOver();
                }
            }
        }
    }

    private void updatePlayerPosition() {
        if (keys[KeyEvent.VK_LEFT]) playerX -= SPEED;
        if (keys[KeyEvent.VK_RIGHT]) playerX += SPEED;
        if (keys[KeyEvent.VK_UP]) playerY -= SPEED;
        if (keys[KeyEvent.VK_DOWN]) playerY += SPEED;

        playerX = Math.max(0, Math.min(playerX, getWidth() - playerImage.getWidth(null)));
        playerY = Math.max(0, Math.min(playerY, getHeight() - playerImage.getHeight(null)));
    }

    private void updateMissiles() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            missile.y -= 10;
            if (missile.y < 0) {
                it.remove();
            } else if (monsterAlive && monster.intersects(missile)) {
                it.remove();
                monsterHealth--;
                if (monsterHealth <= 0) {
                    monsterAlive = false;
                    monsterVisible = false;
                    monsterRespawnCounter = monsterRespawnDelay;
                }
            }
        }
    }

    private void checkPlayerMonsterCollision() {
        if (monsterAlive && new Rectangle(playerX, playerY, playerImage.getWidth(null), playerImage.getHeight(null)).intersects(monster)) {
            gameOver();
        }
    }

    private void gameOver() {
        timer.stop();
        System.out.println("Game Over!");
    }

    private void updateBackground() {
        backgroundY += 5;
        if (backgroundY > 0) {
            backgroundY = -backgroundImage.getHeight(null) + getHeight();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (monsterAlive) {
            monsterWeaponTimer++;
            if (monsterWeaponTimer >= monsterWeaponCooldown) {
                monsterWeapons.add(new Rectangle(monster.x + monster.width / 2 - monsterWeapon.getWidth(null) / 2,
                                                 monster.y + monster.height, monsterWeapon.getWidth(null), monsterWeapon.getHeight(null)));
                monsterWeaponTimer = 0;
            }
            updateMonsterPosition();
        } else if (monsterRespawnCounter > 0) {
            monsterRespawnCounter--;
            if (monsterRespawnCounter == 0) spawnMonster();
        }

        updateMonsterWeapons();
        updatePlayerPosition();
        updateMissiles();
        updateBackground();
        checkPlayerMonsterCollision();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, backgroundY, this);
        g.drawImage(playerImage, playerX, playerY, this);

        if (monsterAlive && monsterVisible) {
            g.drawImage(monsterImage, monster.x, monster.y, this);
        }

        for (Rectangle missile : missiles) {
            g.drawImage(missileImage, missile.x, missile.y, this);
        }

        for (Rectangle weapon : monsterWeapons) {
            g.drawImage(monsterWeapon, weapon.x, weapon.y, this);
        }

        g.setColor(Color.WHITE);
        g.drawString("Health: " + playerHealth, 10, 20);
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            missiles.add(new Rectangle(playerX + playerImage.getWidth(null) / 2 - missileImage.getWidth(null) / 2,
                                       playerY, missileImage.getWidth(null), missileImage.getHeight(null)));
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ShootingGame gamePanel = new ShootingGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}
