//ShootingGame1.java
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class ShootingGame1 extends JPanel implements ActionListener, KeyListener {
    private Timer timer; // 게임 업데이트를 위한 타이머 
    private Image playerImage; // 플레이어 이미지
    private Image[] monsterImages; // 몬스터 이미지 배열
    private Image missileImage; // 미사일 이미지
    private Image monsterWeapon; // 몬스터 공격무기 이미지
    private Image heartImage; // 플레이어 체력 아이콘 이미지
    private Image bulletImage; // 총알 이미지

    private List<Rectangle> monsterWeapons; // 몬스터 공격무기를 추적하는 리스트
    
    private final int MONSTER_WEAPON_SPEED = 5; // 몬스터 무기의 이동 속도
    private int monsterWeaponCooldown = 180; // 무기 발사 간격 (프레임 단위: 1초 = 60프레임)
    private int monsterWeaponTimer = 0; // 무기 발사 타이머
    
    private Player player; // 플레이어 객체 추가
    
    private boolean[] keys; // 키 입력 상태 배열
    private List<Rectangle> missiles; // 미사일을 추적하는 리스트
    
    private Monster monster;
    private int monsterRespawnDelay = 100; // 타이머 틱 기준 대기 시간
    private int monsterRespawnCounter = 0;  // 몬스터가 죽었을 때 다시 생성되기까지의 시간을 관리하는 변수
    
    private List<Stage> stages; // 스테이지 리스트
    private int currentStageIndex; // 현재 스테이지 번호
    private int elapsedTime; // 각 스테이지에서 경과한 시간
    
    private Background background; // Background 객체 추가

    private int score = 0;
    private MainFrame mainFrame; // MainFrame 객체 추가

    private final int MISSILE_WIDTH = 25; // 미사일의 너비를 명시적으로 설정
    private final int MISSILE_HEIGHT = 30; // 미사일의 높이를 명시적으로 설정
    
    //private Client client;   // 클라이언트 객체
    
    public ShootingGame1(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        //this.client = client;
        
        playerImage = new ImageIcon("images/PlayerSoldier.png").getImage().getScaledInstance(150, 140, Image.SCALE_SMOOTH);
        monsterImages = new Image[] {
            new ImageIcon("images/stage 1 zombie.png").getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH),
            new ImageIcon("images/stage 2 zombie.png").getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH),
            new ImageIcon("images/stage 3 zombie.png").getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH)
        };
        missileImage = new ImageIcon("images/bullet1.png").getImage().getScaledInstance(MISSILE_WIDTH, MISSILE_HEIGHT, Image.SCALE_SMOOTH);
        monsterWeapon = new ImageIcon("images/zombie's fireball.png").getImage().getScaledInstance(50, 65, Image.SCALE_SMOOTH); // 몬스터 공격무기 이미지
        heartImage = new ImageIcon("images/heart.png").getImage();
        background = new Background("images/back2.png");
        bulletImage = new ImageIcon("images/bullet1.png").getImage().getScaledInstance(MISSILE_WIDTH, MISSILE_HEIGHT, Image.SCALE_SMOOTH); // 총알 이미지

        player = new Player(180, 600, playerImage, 3); // 위치, 이미지, 체력 전달
        
        keys = new boolean[256];
        missiles = new ArrayList<>();
                
        spawnMonster(); // 몬스터 객체 초기화
        
        monsterWeapons = new ArrayList<>();
        
        stages = new ArrayList<>();
        stages.add(new Stage(1, 180*67)); // 스테이지 1, 3분(180초)
        stages.add(new Stage(2, 180*67)); // 스테이지 2, 3분
        stages.add(new Stage(3, Integer.MAX_VALUE)); // 스테이지 3, 무한 시간
        
        currentStageIndex = 0; // 첫 번째 스테이지 시작
        elapsedTime = 0; // 경과 시간 초기화
        
        timer = new Timer(15, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(400, 800));
    }

    private void spawnMonster() {
        int stage = currentStageIndex % monsterImages.length;
        int x = (int) (Math.random() * (getWidth() - monsterImages[stage].getWidth(null))); // 좌우 랜덤 위치
        monster = new Monster(
            x, 
            0, 
            120, // 몬스터 이미지의 너비
            140, // 몬스터 이미지의 높이
            3 + 2 * stage, 2, 1, monsterImages[stage]
        );
    }

    private void updateMonsterPosition() {
        if (monster.isAlive()) {
            monster.updatePosition(getWidth());
        }
    }
    
    private void updateMonsterPosition2() {
        if (monster.isAlive()) {
            monster.updatePosition2(getWidth(), getHeight());
        }
    }
    
    private void updateMonsterPosition3() {
        if (monster.isAlive()) {
            monster.updatePosition3(getWidth(), getHeight());
        }
    }
    
    private void checkPlayerMonsterCollision() {
        if (monster.isAlive()) {
            Rectangle playerRect = player.getBounds();
            Rectangle monsterRect = monster.getBounds();
            
            if (playerRect.intersects(monsterRect)) { // 정확히 충돌했을 때만 데미지
                player.decreaseHealth();
                mainFrame.appendChatMessage("몬스터에게 물렸어요! 현재 체력: " + player.getHealth());
                if (player.getHealth() <= 0) {
                    //gameOver();
                }
            }
    }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        background.draw(g);
        
        g.setColor(java.awt.Color.WHITE);
        g.drawString("현재 스테이지: " + (currentStageIndex + 1), 10, 20);
        int elapsedSeconds = elapsedTime / 67;
        g.drawString("경과 시간: " + elapsedSeconds + "초", 10, 40);
        drawPlayerHealth(g);
        drawPlayerAmmo(g); // Draw player ammo
        
        g.drawImage(playerImage, player.getX(), player.getY(), this);

        if (monster.isAlive()) {
            g.drawImage(monster.getImage(), monster.getBounds().x, monster.getBounds().y, monster.getBounds().width, monster.getBounds().height, this);
        }

        for (Rectangle missile : missiles) {
            g.drawImage(missileImage, missile.x, missile.y, this);
        }
        
        for (Rectangle weapon : monsterWeapons) {
            g.drawImage(monsterWeapon, weapon.x, weapon.y, this);
        }
    }

    private void drawPlayerAmmo(Graphics g) {
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new Font("맑은 고딕", Font.PLAIN, 12));  // 텍스트 크기 줄이고 한글 폰트 설정
        g.drawString("탄창안에 남은 총알 수: ", 10, 60);

        int bulletSize = 25; // Size of the bullet image
        int spacing = 3; // Reduced spacing between bullets
        int xStart = 10; // Starting x-coordinate
        int yStart = 70; // Starting y-coordinate

        for (int i = 0; i < player.getAmmo(); i++) {
            g.drawImage(bulletImage, xStart + i * (bulletSize + spacing), yStart, bulletSize, bulletSize, this);
        }
    }

    private void drawPlayerHealth(Graphics g) {
        int heartSize = 30;
        int spacing = 10;
        int xStart = 280;
        int yStart = 10;

        for (int i = 0; i < player.getHealth(); i++) {
            g.drawImage(heartImage, xStart + i * (heartSize + spacing), yStart, heartSize, heartSize, this);
        }
    }

    private void spawnMonsterWeapon() {
        monsterWeapons.add(new Rectangle(
            monster.getBounds().x + monster.getBounds().width / 2 - monsterWeapon.getWidth(null) / 2,
            monster.getBounds().y + monster.getBounds().height,
            monsterWeapon.getWidth(null),
            monsterWeapon.getHeight(null)
        ));
    }
    
    private void updateMonsterWeapons() {
        for (Iterator<Rectangle> it = monsterWeapons.iterator(); it.hasNext();) {
            Rectangle weapon = it.next();
            weapon.y += MONSTER_WEAPON_SPEED;
            if (weapon.y > getHeight()) {
                it.remove();
            }
        }
    }

    private void checkMonsterWeaponCollisions() {
        for (Iterator<Rectangle> it = monsterWeapons.iterator(); it.hasNext();) {
            Rectangle weapon = it.next();
            Rectangle playerRect = player.getBounds();
            
            if (playerRect.intersects(weapon)) {
            	 it.remove();
            	 player.decreaseHealth();
                
                int playerHealth = player.getHealth();
                String message = String.format("으악 플레이어가 맞았어요!\n플레이어 현재 체력 = %d", playerHealth);

                mainFrame.appendChatMessage(message);
                
                if (player.getHealth() <= 0) {
                    gameOver();
                }
            }
        }
    }
    
    private void gameOver() {
        timer.stop();
        System.out.println("Game Over!");

        // 내 점수 표시
        JOptionPane.showMessageDialog(this, 
            "Game Over!\nYour Score: " + score, 
            "Game Over", 
            JOptionPane.INFORMATION_MESSAGE);

        // MainFrame에서 client 객체를 통해 점수 전송 및 결과 수신
        String response = mainFrame.getClient().sendGameOver(score);

        // 서버 결과에 따라 메시지 표시
        if (response.startsWith("RESULT:")) {
            String[] parts = response.split(":");
            String result = parts[1]; // WIN, LOSE, TIE
            int myScore = Integer.parseInt(parts[2]);
            int opponentScore = Integer.parseInt(parts[3]);

            // 팝업창으로 결과 표시
            JOptionPane.showMessageDialog(this, 
                "결과: " + result + "\n내 점수: " + myScore + "\n상대방 점수: " + opponentScore, 
                "게임 결과", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 오류 메시지 표시
            JOptionPane.showMessageDialog(this, 
                "Error: 서버에서 결과 확인 안됨", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }

        // 게임 종료
        System.exit(0);
    }



   
    public void actionPerformed(ActionEvent e) {
        background.update();

        if (monster.isAlive()) {
            monsterWeaponTimer++;
            if (monsterWeaponTimer >= monsterWeaponCooldown) {
                spawnMonsterWeapon();
                monsterWeaponTimer = 0;
            }
            
            switch (currentStageIndex) {
                case 0:
                    updateMonsterPosition();
                    break;
                
                case 1:
                    updateMonsterPosition2();
                    break;
                
                case 2:
                    updateMonsterPosition3();
                    break;
            }
        } else if (monsterRespawnCounter > 0) {
            monsterRespawnCounter--;
            if (monsterRespawnCounter == 0) {
                spawnMonster();
            }
        }

        checkMonsterWeaponCollisions();
        updateMonsterWeapons();
        updatePlayerPosition();
        updateMissiles();
        checkCollisions();
        checkPlayerMonsterCollision();

        elapsedTime++;

        if (elapsedTime >= stages.get(currentStageIndex).getTimeLimit()) {
            stages.get(currentStageIndex).setCleared(true);
            moveToNextStage();
        }

        repaint();
    }

    private void moveToNextStage() {
        currentStageIndex++;
        if (currentStageIndex >= stages.size()) {
            System.out.printf("\n게임 승리! 최종 점수 = %d\n", score);
            gameOver();
        } else {
            String message = String.format("=========스테이지 %d 클리어!=========", currentStageIndex + 1);
            mainFrame.appendChatMessage(message);

            monster.killMonster();
            elapsedTime = 0;
            player.restoreHealth();
            
            spawnMonster();
        }
    }
     
    private void updatePlayerPosition() {
        player.updatePosition(keys, getWidth(), getHeight());
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

    private void checkCollisions() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            
            if (monster.isAlive() && monster.checkCollision(missile)) {
                it.remove();
                monster.decreaseHealth();
                playSound("music/몬스터소리.wav"); // 몬스터가 맞았을 때 소리 재생
                score += 5;
                
                String message = String.format("몬스터를 맞췄어요! \n현재점수 = %d\n", score);
                mainFrame.appendChatMessage(message);
                
                if (!monster.isAlive()) {
                    monsterRespawnCounter = monsterRespawnDelay;
                }

                break;
            }
        }
    }
    
    public int getScore() {
        return score;
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!player.isReloading()) {
                if (player.shoot()) {
                    // 올바른 크기의 미사일 Rectangle 생성
                    Rectangle newMissile = new Rectangle(
                        player.getX() + player.getWidth() / 2 - MISSILE_WIDTH / 2,
                        player.getY(), 
                        MISSILE_WIDTH, 
                        MISSILE_HEIGHT
                    );
                    missiles.add(newMissile);
                    System.out.println("Missile fired: " + newMissile); // 디버깅 정보 추가
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) { }

    private void playSound(String filePath) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filePath)));
            Float volume = -10.0f; // Adjust volume here
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}