//11week testing
import java.awt.Dimension;
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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

// ShootingGame 클래스
public class ShootingGame1 extends JPanel implements ActionListener, KeyListener {
    private Timer timer; // 게임 업데이트를 위한 타이머
    private Image playerImage; // 플레이어 이미지
    private Image backgroundImage; // 배경 이미지
    private Image monsterImage; // 몬스터 이미지
    private Image missileImage; // 미사일 이미지
    
    private Image monsterWeapon; // 몬스터 공격무기 이미지
    private List<Rectangle> monsterWeapons; // 몬스터 공격무기를 추적하는 리스트
    private final int MONSTER_WEAPON_SPEED = 5; // 몬스터 무기의 이동 속도
    private int monsterWeaponCooldown = 180; // 무기 발사 간격 (프레임 단위: 1초 = 60프레임)
    private int monsterWeaponTimer = 0; // 무기 발사 타이머
    private int playerHealth = 3; // 플레이어 체력

    private int playerX, playerY; // 플레이어 위치 좌표
    private final int SPEED = 5; // 플레이어 이동 속도
    private int backgroundY; // 배경 Y 좌표
    private boolean[] keys; // 키 입력 상태 배열
    private List<Rectangle> missiles; // 미사일을 추적하는 리스트
    private Rectangle monster; // 몬스터의 위치와 크기를 추적하는 직사각형
    
    private boolean monsterAlive; // 몬스터의 생존 상태를 나타내는 변수 추가
    private int monsterHealth; // 몬스터의 생명 수치
    private boolean monsterVisible; // 몬스터가 화면에 보이는지 여부 추가
    private int monsterRespawnDelay = 100; // 타이머 틱 기준 대기 시간
    private int monsterRespawnCounter = 0;  // 몬스터가 죽었을 때 다시 생성되기까지의 시간을 관리하는 변수
    private int monsterSpeedX = 2; // 몬스터의 이동 속도
    private int monsterSpeedY = 1; // 몬스터의 이동 속도
    //private int monsterDirection = 1; // 몬스터의 이동 방향 (1: 오른쪽, -1: 왼쪽)
    private int monsterDirectionX = 1; // 몬스터의 X 방향
    private int monsterDirectionY = 1; // 몬스터의 Y 방향
    //private int stage = 0;	//스테이지는 3까지 있음
    
    public ShootingGame1() {
        playerImage = new ImageIcon("images/spaceship5.png").getImage();
        backgroundImage = new ImageIcon("images/back2.png").getImage();
        monsterImage = new ImageIcon("images/monster1.png").getImage();
        missileImage = new ImageIcon("images/missile.png").getImage();
        monsterWeapon = new ImageIcon("images/monsterWeapon.png").getImage(); // 몬스터 공격무기 이미지
        
        keys = new boolean[256];
        missiles = new ArrayList<>();
        monster = new Rectangle((int) (Math.random() * (400 - monsterImage.getWidth(null))), 0, 
                                monsterImage.getWidth(null), monsterImage.getHeight(null));
        
        monsterAlive = true; // 게임 시작 시 몬스터가 살아있음
        monsterHealth = 3; // 몬스터의 초기 생명 수치
        monsterVisible = true; // 게임 시작 시 몬스터는 보이는 상태
        // 몬스터의 무기
        monsterWeapons = new ArrayList<>();
        //playerHealth = 3; // 플레이어 초기 체력
        
        playerX = 180;
        playerY = 600; 
        
        // 초기 배경 위치 설정 (이미지의 높이만큼 위로 올려 스크린 바깥에서 시작하도록)
        backgroundY = 0;

        timer = new Timer(15, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(400, 800));
    }

 // 몬스터 생성 메서드 수정
    private void spawnMonster() {
        monsterAlive = true; // 몬스터가 다시 살아남
        monsterVisible = true; // 화면에 다시 나타남
        monster.setLocation((int) (Math.random() * (getWidth() - monsterImage.getWidth(null))), 0);
        monsterHealth = 3; // 몬스터 체력 초기화
        monsterWeaponTimer = 0; // 타이머 초기화
    }

    //몬스터 좌우로 움직이기
    private void updateMonsterPosition() {
        if (monsterAlive) {
            // 몬스터의 X 위치 업데이트
            monster.x += monsterSpeedX * monsterDirectionX;

            // 화면 경계에 도달하면 방향 전환
            if (monster.x <= 0 || monster.x + monster.width >= getWidth()) {
                monsterDirectionX *= -1; // 방향 반전
            }
        }
    }
    
    //몬스터 상하좌우 움직이기
    private void updateMonsterPosition2() {
        if (monsterAlive) {
            // 몬스터의 X와 Y 위치 업데이트
            monster.x += monsterSpeedX * monsterDirectionX;
            monster.y += monsterSpeedY * monsterDirectionY;

            // 화면 경계에 도달하면 방향 전환
            if (monster.x <= 0 || monster.x + monster.width >= getWidth()) {
                monsterDirectionX *= -1; // X 방향 반전
            }
            if (monster.y <= 0 || monster.y + monster.height >= getHeight()) {
                monsterDirectionY *= -1; // Y 방향 반전
            }

            // 일정 확률로 랜덤한 방향으로 변경
            if (Math.random() < 0.01) { // 1% 확률로 방향 변경
                monsterDirectionX = (Math.random() < 0.5) ? 1 : -1; // 랜덤 X 방향
                monsterDirectionY = (Math.random() < 0.5) ? 1 : -1; // 랜덤 Y 방향
            }
        }
    }
    
    //몬스터와 닿을 시 게임 오버
    private void checkPlayerMonsterCollision() {
        if (monsterAlive) {
            Rectangle playerRect = new Rectangle(playerX, playerY, playerImage.getWidth(null), playerImage.getHeight(null));
            if (playerRect.intersects(monster)) {
            	playerHealth = 0;
                System.out.println("몬스터에게 물렸어요! 게임 오버");
            	gameOver(); // 게임 오버 처리
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, backgroundY, this); // 배경 그리기
        g.drawImage(playerImage, playerX, playerY, this); // 플레이어 그리기
        
        // 몬스터가 살아있고 화면에 보이는 상태일 때만 그리기
        if (monsterAlive && monsterVisible) {
            g.drawImage(monsterImage, monster.x, monster.y, this);
        }
        
        // 미사일 그리기
        for (Rectangle missile : missiles) {
            g.drawImage(missileImage, missile.x, missile.y, this);
        }
        
        for (Rectangle weapon : monsterWeapons) {
            g.drawImage(monsterWeapon, weapon.x, weapon.y, this);
        }

    }

   // 몬스터 위치에서 무기 발사
    private void spawnMonsterWeapon() {
        
        monsterWeapons.add(new Rectangle(
            monster.x + monsterImage.getWidth(null) / 2 - monsterWeapon.getWidth(null) / 2,
            monster.y + monsterImage.getHeight(null),
            monsterWeapon.getWidth(null),
            monsterWeapon.getHeight(null)
        ));
    }
    //몬스터 무기 이동
    private void updateMonsterWeapons() {
        for (Iterator<Rectangle> it = monsterWeapons.iterator(); it.hasNext();) {
            Rectangle weapon = it.next();
            weapon.y += MONSTER_WEAPON_SPEED; // 아래로 이동
            if (weapon.y > getHeight()) {
                it.remove(); // 화면 밖으로 나가면 제거
            }
        }
    }
    //플레이어와 몬스터의 무기 충돌 처리
    private void checkMonsterWeaponCollisions() {
        for (Iterator<Rectangle> it = monsterWeapons.iterator(); it.hasNext();) {
            Rectangle weapon = it.next();
            Rectangle player = new Rectangle(playerX, playerY, playerImage.getWidth(null), playerImage.getHeight(null));
            if (weapon.intersects(player)) {
                it.remove(); // 충돌한 무기 제거
                playerHealth--; // 플레이어 체력 감소
                System.out.printf("으악 플레이어가 맞았어요! 플레이어 현재 체력 = %d\n", playerHealth);
                if (playerHealth <= 0) {
                    gameOver(); // 체력이 0 이하가 되면 게임 종료
                }
            }
        }
    }
    
    //게임오버
    private void gameOver() {
        timer.stop();
        System.out.println("Game Over!"); // 간단한 메시지 출력 (UI 추가 가능)
    }

    //스테이지2: 몬스터가 좌우로 움직임/  스테이지3: 몬스터가 상하좌우로 움직임 
    public void actionPerformed(ActionEvent e) {
        if (monsterAlive) {
            monsterWeaponTimer++;
            if (monsterWeaponTimer >= monsterWeaponCooldown) {
                spawnMonsterWeapon(); // 무기 발사
                monsterWeaponTimer = 0;
            }
            
            //updateMonsterPosition(); // 스테이지2: 몬스터 좌우 이동 업데이트
            updateMonsterPosition2();  //스테이지3: 몬스터 상하좌우
            
        } else if (monsterRespawnCounter > 0) {
            monsterRespawnCounter--;
            if (monsterRespawnCounter == 0) {
                spawnMonster();
            }
        }

        updateMonsterWeapons();
        checkMonsterWeaponCollisions();
        updatePlayerPosition();
        updateMissiles();
        updateBackground();
        checkCollisions();
        checkPlayerMonsterCollision(); // 플레이어와 몬스터의 충돌 체크
        repaint();
    }


    // 플레이어의 위치를 업데이트하는 메소드
    private void updatePlayerPosition() {
        if (keys[KeyEvent.VK_LEFT]) {
            playerX -= SPEED; // 왼쪽 키가 눌리면 왼쪽으로 이동
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            playerX += SPEED; // 오른쪽 키가 눌리면 오른쪽으로 이동
        }
        if (keys[KeyEvent.VK_UP]) {
            playerY -= SPEED; // 위쪽 키가 눌리면 위로 이동
        }
        if (keys[KeyEvent.VK_DOWN]) {
            playerY += SPEED; // 아래쪽 키가 눌리면 아래로 이동
        }
        
        // 플레이어가 창의 경계를 넘지 않도록 위치를 조정
        playerX = Math.max(playerX, 0);
        playerX = Math.min(playerX, getWidth() - playerImage.getWidth(null));
        playerY = Math.max(playerY, 0);
        playerY = Math.min(playerY, getHeight() - playerImage.getHeight(null));
    }

    private void updateMissiles() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            missile.y -= 10; // 미사일 속도
            if (missile.y < 0) {
                it.remove(); // 화면 상단을 벗어난 미사일을 제거합니다.
            }
        }
    }

    private void checkCollisions() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            if (monsterAlive && monster.intersects(missile)) {
                it.remove(); // 미사일 제거
                monsterHealth--; // 몬스터 생명 감소
                if (monsterHealth <= 0) {
                    monsterAlive = false; // 몬스터 사망
                    monsterVisible = false; // 화면에서 사라짐
                    monsterRespawnCounter = monsterRespawnDelay; // 리스폰 타이머 시작
                }
                break; // 한 번의 충돌만 처리
            }
        }
    }

    // 배경의 위치를 업데이트하여 스크롤링 효과를 주는 메소드
    public void updateBackground() {
        backgroundY += 1; // 배경을 아래로 1픽셀씩 이동
        if (backgroundY > 0) {
            // 배경이 아래로 완전히 내려오면 다시 위로 설정
            backgroundY = -backgroundImage.getHeight(null) + getHeight();
        }
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 미사일 발사: 플레이어 위치에서 시작하는 새 미사일을 추가합니다.
            missiles.add(new Rectangle(playerX + playerImage.getWidth(null) / 2 - missileImage.getWidth(null) / 2,
                                       playerY, missileImage.getWidth(null), missileImage.getHeight(null)));
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
