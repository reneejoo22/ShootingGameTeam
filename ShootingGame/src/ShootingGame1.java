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
    //private Image backgroundImage; // 배경 이미지
    private Image monsterImage; // 몬스터 이미지
    private Image missileImage; // 미사일 이미지
    private Image monsterWeapon; // 몬스터 공격무기 이미지
    private Image heartImage; // 플레이어 체력 아이콘 이미지
    
    private List<Rectangle> monsterWeapons; // 몬스터 공격무기를 추적하는 리스트
    
    private final int MONSTER_WEAPON_SPEED = 5; // 몬스터 무기의 이동 속도
    private int monsterWeaponCooldown = 180; // 무기 발사 간격 (프레임 단위: 1초 = 60프레임)
    private int monsterWeaponTimer = 0; // 무기 발사 타이머
    
    private Player player; // 플레이어 객체 추가
    
    //private int backgroundY; // 배경 Y 좌표
    private boolean[] keys; // 키 입력 상태 배열
    private List<Rectangle> missiles; // 미사일을 추적하는 리스트
    
    private Monster monster;
    private int monsterRespawnDelay = 100; // 타이머 틱 기준 대기 시간
    private int monsterRespawnCounter = 0;  // 몬스터가 죽었을 때 다시 생성되기까지의 시간을 관리하는 변수
    
    
    private List<Stage> stages; // 스테이지 리스트
    private int currentStageIndex; // 현재 스테이지 번호
    private int elapsedTime; // 각 스테이지에서 경과한 시간
    
    private Background background; // Background 객체 추가

    
    public ShootingGame1() {
        playerImage = new ImageIcon("images/spaceship5.png").getImage();
        //backgroundImage = new ImageIcon("images/back2.png").getImage();
        monsterImage = new ImageIcon("images/monster1.png").getImage();
        missileImage = new ImageIcon("images/missile.png").getImage();
        monsterWeapon = new ImageIcon("images/monsterWeapon.png").getImage(); // 몬스터 공격무기 이미지
        heartImage = new ImageIcon("images/heart.png").getImage();
        background = new Background("images/back2.png");
        
     // Player 객체 초기화
        player = new Player(180, 600, playerImage, 3); // 위치, 이미지, 체력 전달
        
        keys = new boolean[256];
        missiles = new ArrayList<>();
        		
        //몬스터 객체 초기화
        monster = new Monster((int) (Math.random() * (400 - monsterImage.getWidth(null))), 0, 
                monsterImage.getWidth(null), monsterImage.getHeight(null), 3, 2, 1, monsterImage);
        
        // 몬스터의 무기
        monsterWeapons = new ArrayList<>();
        
        //스테이지_ test용 이라서 일단은 10초씩으로 설정
        stages = new ArrayList<>();
        stages.add(new Stage(1, 10*67)); // 스테이지 1, 3분(180초)
        stages.add(new Stage(2, 10*67)); // 스테이지 2, 3분
        stages.add(new Stage(3, 10*67)); // 스테이지 3, 3분
        currentStageIndex = 0; // 첫 번째 스테이지 시작
        elapsedTime = 0; // 경과 시간 초기화
        
        timer = new Timer(15, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setPreferredSize(new Dimension(400, 800));
    }

 // 몬스터 재생성 메서드 수정_(스테이지 추가하면 1,2,3 마다 다르게 조정 필요, 몬스터 체력, 속도, 이미지) 
    private void spawnMonster() {
        switch (currentStageIndex) {
            case 0: // 스테이지 1
                monster = new Monster(
                    (int) (Math.random() * (400 - monsterImage.getWidth(null))), 
                    0, 
                    monsterImage.getWidth(null), 
                    monsterImage.getHeight(null), 
                    3, 2, 1, monsterImage
                );
                break;

            case 1: // 스테이지 2
                monster = new Monster(
                    (int) (Math.random() * (400 - monsterImage.getWidth(null))), 
                    0, 
                    monsterImage.getWidth(null), 
                    monsterImage.getHeight(null), 
                    5, 2, 1, monsterImage
                );
                break;

            case 2: // 스테이지 3
                monster = new Monster(
                    (int) (Math.random() * (400 - monsterImage.getWidth(null))), 
                    0, 
                    monsterImage.getWidth(null), 
                    monsterImage.getHeight(null), 
                    7, 2, 1, monsterImage
                );
                break;
/*
            default: // 이후 추가적인 스테이지
                monster = new Monster(
                    (int) (Math.random() * (400 - monsterImage.getWidth(null))), 
                    0, 
                    monsterImage.getWidth(null), 
                    monsterImage.getHeight(null), 
                    10, 5, 2, monsterImage
                );
                break;
                */
        }
    }


    
    // 몬스터 좌우로만 움직이는 업데이트 메서드
    private void updateMonsterPosition() {
        if (monster.isAlive()) {
            // 몬스터의 위치를 업데이트
            monster.updatePosition(getWidth());
        }
    }
    
    //몬스터 상하좌우 움직이는 업데이트 메서드
    private void updateMonsterPosition2() {
        if (monster.isAlive()) {
            // 몬스터의 위치를 업데이트
            monster.updatePosition2(getWidth(), getHeight());
        }
    }
    
    //몬스터와 닿을 시 게임 오버
    private void checkPlayerMonsterCollision() {
        if (monster.isAlive()) {
            // Player 클래스에서 플레이어의 위치와 크기를 나타내는 getBounds() 호출
            Rectangle playerRect = player.getBounds();

         // Monster 클래스에서 몬스터의 위치와 크기를 나타내는 getBounds() 호출
            Rectangle monsterRect = monster.getBounds();
            
            // 몬스터와 플레이어 간 충돌 감지
            if (playerRect.intersects(monsterRect)) {
                player.deadHealth(); // Player 죽음
                System.out.println("몬스터에게 물렸어요! 게임 오버");
                gameOver(); // 게임 오버 처리
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경 그리기
        background.draw(g);
        
        // 현재 스테이지 번호 그리기
        g.setColor(java.awt.Color.WHITE); // 텍스트 색상 설정
        g.drawString("현재 스테이지: " + (currentStageIndex + 1), 10, 20); // 좌측 상단에 스테이지 번호 표시
        // 경과 시간 그리기
        int elapsedSeconds = elapsedTime / 67; // 프레임을 초로 변환
        g.drawString("경과 시간: " + elapsedSeconds + "초", 10, 40); // 경과 시간 표시
     // 플레이어 체력 표시
        drawPlayerHealth(g);
        
        // 플레이어 그리기 (Player 클래스의 getX(), getY(), getImage() 활용)
        g.drawImage(playerImage, player.getX(), player.getY(), this);

        // 몬스터 그리기 (몬스터가 살아 있고 화면에 보이는 상태일 때만)
        if (monster.isAlive() /*&& monsterVisible*/) {
            g.drawImage(monsterImage, monster.getBounds().x, monster.getBounds().y, this);
        }

        // 미사일 그리기
        for (Rectangle missile : missiles) {
            g.drawImage(missileImage, missile.x, missile.y, this);
        }
        
        // 몬스터 무기 그리기
        for (Rectangle weapon : monsterWeapons) {
            g.drawImage(monsterWeapon, weapon.x, weapon.y, this);
        }
        
    }

    private void drawPlayerHealth(Graphics g) {
        int heartSize = 30; // 하트 이미지의 크기
        int spacing = 10;  // 하트 간의 간격
        int xStart = 280;   // 하트 시작 X 좌표
        int yStart = 10;   // 하트 시작 Y 좌표

        for (int i = 0; i < player.getHealth(); i++) {
            g.drawImage(heartImage, xStart + i * (heartSize + spacing), yStart, heartSize, heartSize, this);
        }
    }


   // 몬스터 위치에서 무기 발사
 // 몬스터 무기 발사 타이밍 확인
    private void spawnMonsterWeapon() {
        monsterWeapons.add(new Rectangle(
            monster.getBounds().x + monsterImage.getWidth(null) / 2 - monsterWeapon.getWidth(null) / 2,
            monster.getBounds().y + monsterImage.getHeight(null),
            monsterWeapon.getWidth(null),
            monsterWeapon.getHeight(null)
            //MONSTER_WEAPON_SPEED
        ));
    }
    
 // 몬스터 무기 이동 및 화면에 그리기
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
            
            // Player 객체를 통해 충돌 검사
            Rectangle playerRect = new Rectangle(player.getX(), player.getY(), 
            		playerImage.getWidth(null), 
            		playerImage.getHeight(null));
            
            if (weapon.intersects(playerRect)) {
                it.remove(); // 충돌한 무기 제거
                player.decreaseHealth(); // Player 클래스에서 체력 감소 처리
                System.out.printf("으악 플레이어가 맞았어요! 플레이어 현재 체력 = %d\n", player.getHealth());
                
                if (player.getHealth() <= 0) {
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

    public void actionPerformed(ActionEvent e) {
        // 배경 업데이트
        background.update();

        if (monster.isAlive()) {
            monsterWeaponTimer++;
            if (monsterWeaponTimer >= monsterWeaponCooldown) {
                spawnMonsterWeapon(); // 무기 발사
                monsterWeaponTimer = 0;
            }
            
            // 몬스터 업데이트
            switch (currentStageIndex) {
                case 0: // 스테이지 1
                    // 몬스터의 움직임을 여기서 처리하거나 추가할 수 있습니다.
                    break;
                
                case 1: // 스테이지 2
                    updateMonsterPosition(); // 몬스터 좌우 이동 업데이트
                    break;
                
                case 2: // 스테이지 3
                    updateMonsterPosition2(); // 몬스터 상하좌우 움직임
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
        checkPlayerMonsterCollision(); // 플레이어와 몬스터의 충돌 체크

        // 스테이지 시간 업데이트
        elapsedTime++;

        // 현재 스테이지 클리어 처리
        if (elapsedTime >= stages.get(currentStageIndex).getTimeLimit()) {
            stages.get(currentStageIndex).setCleared(true);
            moveToNextStage(); // 다음 스테이지로 이동
        }

        repaint();
    }


    private void moveToNextStage() {
        currentStageIndex++;
        if (currentStageIndex >= stages.size()) {
            System.out.println("게임 승리!");
            gameOver(); // 모든 스테이지를 클리어한 경우 게임 종료
        } else {
        	
            System.out.printf("스테이지 %d 클리어!\n", currentStageIndex + 1);
            monster.killMonster();
            elapsedTime = 0; // 경과 시간 초기화
            player.restoreHealth();	//스테이지 넘어가면 플레이어 체력회복
            
            // 몬스터 리스폰 및 추가 설정을 여기에 작성할 수 있습니다.
            spawnMonster(); // 새로운 스테이지 시작 시 몬스터 생성
        }
    }
    
    
    // 플레이어의 위치를 업데이트하는 메소드
 // ShootingGame1 클래스
    private void updatePlayerPosition() {
        player.updatePosition(keys, getWidth(), getHeight());
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

    //몬스터와 플레이어의 총알이 닿은 경우
    private void checkCollisions() {
        for (Iterator<Rectangle> it = missiles.iterator(); it.hasNext();) {
            Rectangle missile = it.next();
            
            // 몬스터가 살아 있고, 미사일과 충돌한 경우
            if (monster.isAlive() && monster.checkCollision(missile)) {
                it.remove(); // 미사일 제거
                monster.decreaseHealth(); // 몬스터 체력 감소
                System.out.printf("몬스터를 맞췄어요!\n");
                // 몬스터 체력이 0 이하가 되면 몬스터 사망
                if (!monster.isAlive()) {
                    //monsterVisible = false; // 화면에서 사라짐
                    monsterRespawnCounter = monsterRespawnDelay; // 리스폰 타이머 시작
                }

                break; // 한 번의 충돌만 처리
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 미사일 발사: 플레이어 위치에서 시작하는 새 미사일을 추가합니다.
            missiles.add(new Rectangle(player.getX() + player.getWidth() / 2 - missileImage.getWidth(null) / 2,
                                       player.getY(), missileImage.getWidth(null), missileImage.getHeight(null)));
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
