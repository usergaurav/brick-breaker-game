import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.io.*;




public class BB extends JFrame implements ActionListener{
	
	
	
	private GamePanel game;						
	private javax.swing.Timer myTimer;			
	
	JLabel bg, score, lives, level;				
	
	public BB(){
		//Constructor
			
		super("Brick Breaker");
		
		setLayout(null);
		setSize(800,600);
		
		game = new GamePanel();					
		game.setSize(500, 500);
		game.setLocation(25,25);
		add(game);
		
		Font f = new Font("Bebas Neue", Font.PLAIN, 45);		
		
		level = new JLabel("Level: 1");			
		level.setFont(f);
		level.setForeground(Color.WHITE);
        level.setSize(300,100);
        level.setLocation(550,340);
        add(level);
        
		score = new JLabel("Score: 0");
		score.setFont(f);
		score.setForeground(Color.WHITE);
        score.setSize(300,100);
        score.setLocation(545,400);
        add(score);
        
        lives = new JLabel("Lives 3");
		lives.setFont(f);
		lives.setForeground(Color.WHITE);
        lives.setSize(300,100);
        lives.setLocation(550,460);
        add(lives);
        
        
        
		bg = new JLabel(new ImageIcon("Game background, angrysnail.png"));				
		bg.setSize(800,600);
		add(bg);
		
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
		setVisible(true);										
		
		myTimer = new javax.swing.Timer(10,this);				
		myTimer.start();
		
		
	}
	
	public void actionPerformed(ActionEvent e){
		
		
		
		game.update(myTimer);
		score.setText("Score "+Integer.toString(game.getScore()));			
		lives.setText("Lives "+Integer.toString(game.getLives()));
		level.setText("Level "+Integer.toString(game.getLVL()));
		
	}
	
	public static void main(String[] args){
		
		new MainMenu();
		
	}
}

class GamePanel extends JPanel implements KeyListener{
	
	
	Brick[] bricks;					
	Paddle paddle;		
	Ball ball;					
	ArrayList<PowerUp> pUps;

	
	boolean[] keys;					
	boolean started;
	
	private int level, lives, score, left;		
	
	public GamePanel(){
		
		super();
		
		ball = new Ball(245, 450);
		paddle = new Paddle(220, 475);
		pUps = new ArrayList<PowerUp>();
		bricks = load(1);					

		
		keys = new boolean[2000];
		
		started = false;					
		
		lives = 3;							
		score = 0;
		left = bricks.length;				
		
		addKeyListener(this);				
        this.setFocusable(true);
        this.grabFocus();
			
	}
	
	public Brick[] load(int lvl){		
		
		
		Scanner inFile=null;			
		
		try{		
    			inFile = new Scanner (new BufferedReader (new FileReader("bricks"+Integer.toString(lvl-1)+".txt")));
    			
		} catch(IOException ex){
			
			System.out.println("File not available"+ex);
			System.exit(0);
			
		}
		
		String[] p = new String[] {"","","","","","","","","","","","","","shrink","expand","life","life","life","fast","slow"};	//Array of possible powerups
		Brick[] temp = new Brick[16];												//Empty Brick array
		
		for(int i = 0; i < temp.length; i++){										
            Brick b;
            int r = (int)(Math.random()*p.length);								
            int hp = (int)(Math.random()*3);										
            
          
	        b = new Brick(inFile.nextInt(), inFile.nextInt(), hp+1, p[r]);			
	        temp[i] = b;
	        pUps.add(new PowerUp(b.getX(), b.getY(), p[r]));						

        }
        
        return temp;											
	}
	
	private void checkContact(){
		
        if(ball.getX() + 10 >= 500 || ball.getX() <= 0){
            ball.setDX(-1 * ball.getDX());
        }
        
        if(ball.getY() < 0){
            ball.setDY(-1 * ball.getDY());
        }
        
        
        if(ball.getY() + 10 >= paddle.getY() && (ball.getX() >= paddle.getX() && ball.getX() <= (paddle.getX() + 60))){
            if(ball.getY() + 10 <= paddle.getY() + 10){
            	
            	double ballPos = ball.getX() - paddle.getX();			
				double newx = (ballPos/paddle.getWidth()) - .5;			
	
				ball.setDX(newx*5);									
				ball.setDY(-1 * ball.getDY());
            }
        }


        if(ball.getY() + 10 > paddle.getY() + 10){
   			
   			lives-=1;
   			reset();					
            
        }

      
        for(Brick b: bricks){
        	
            if(ball.getX() + 10 >= b.getX() && ball.getX() <= b.getX() + 55){
            	
                if(ball.getY() + 10 >= b.getY() && ball.getY() <= b.getY() + 20){
                	
                    if(b.getHP()>0){
                    	
                        if(ball.getX() + 10 - ball.getDX() <= b.getX() || ball.getX() - ball.getDX() >= b.getX() + 55){
   
							ball.setDX(-1 * ball.getDX());
                            b.setHP(b.getHP()-1);
                            
                            
                        } else{
                        	
                     		ball.setDY(-1 * ball.getDY());
                        	b.setHP(b.getHP()-1);
                       
                        } 
                        score += b.getHP()*5;
                    } 
                }
            }
          	
            if(b.getHP()<=0){
            	
            	for (PowerUp p: pUps){									
                	if (p.getX()==b.getX() && p.getY()==b.getY()){
                		
                		p.setDY(3);
                		p.setAct(true);
                	}
                }
                
            }
            
        }
        
        for (PowerUp p: pUps){											
        
			if ((p.getX()+50>paddle.getX() && p.getX()<=paddle.getX()+paddle.getWidth()) && p.getY()+20>=paddle.getY() && p.getY()<=paddle.getY()+10){					
				paddle.setpUp(p.getName());
				score += 50;
			}
		}
		
		int temp = 0;
		
		for(Brick b: bricks){											
			
			if(b.getHP()> 0){
				temp ++;
			}
		}
		
		left = temp;
        
        
        if(left == 0)
        {
        	
            level++;
            
            if(level>3){						
            	new MainMenu();
            	setVisible(false);
            }
            	
            reset();		
        }
        
        
        if(lives <= 0){
        	
        	new LoseFrame();
        	setVisible(false);
        	
        }
	
    }
    
    public void reset(){
		
		
		
    	ball.setX(245);
        ball.setY(460);
        ball.setDX(0);
        ball.setDY(0);
        
        paddle.setX(220);
        paddle.setY(475);
        
      	score = 0;
      	
      	if(lives>0){					
      		
      		pUps.clear();				
      		bricks = load(level+1);
      		score += 10;
      		
      	} else{							
      		
      		new MainMenu();
      		setVisible(false);
      		
      	}
      	
        started = false;				
    }
    
	public void keyPressed(KeyEvent event){			
		//Checks to see if keys are pressed
    	int i = event.getKeyCode();
    	keys[i] = true;
    }
	
	public void keyReleased(KeyEvent evt){
		//Checks to see if keys are released
		int i = evt.getKeyCode();
		keys[i]=false;
		
	}
	
	public void keyTyped(KeyEvent evt){} 
	
	public void paintComponent(Graphics g){
        
        
        
        g.setColor(new Color(222,222,222));				
		g.fillRect(0,0,getWidth(),getHeight());

        
        for(Brick b: bricks){
            if(b.getHP()>0){
            
                g.drawImage(b.getImg(), b.getX(), b.getY(), this);
            }
        }
		
		for (PowerUp p: pUps){
        	if (!p.getUse() && p.getAct()){
        		if (p.getY()<550 && !p.getName().equals("")){
        			g.drawImage(p.getImage(), p.getX(), p.getY(), this);
        		}	        		
        	}
        }
        
        
       
        g.setColor(Color.BLACK);
        g.fillRect(paddle.getX(), paddle.getY(), paddle.getWidth(), 10);

        
        g.drawImage(ball.getImage(),(int)(ball.getX()), (int)(ball.getY()), this);
    }
    
    public void update(javax.swing.Timer mT){
    	
    	
    	
    	if(keys[KeyEvent.VK_H]){		
    		
    		mT.stop();				
    		new HelpFrame();
    		setVisible(false);
    			
    	}
    	
    	if(keys[KeyEvent.VK_M]){		
    		
    		mT.stop();					
    		new MainMenu();
    		setVisible(false);
    		
    	}
    	
    	if(!started){						
    		
    		if(keys[KeyEvent.VK_SPACE]){	
    			
    			int i = (int)(Math.random()*6);		
    			ball.setDX(i);
    			ball.setDY(-5);
    			started = true;						
    			//mT.start();
    		}
    		
    	} else{
    		
    		checkContact();							
    		paddle.move(keys);						
    		paddle.powerUse(lives, ball);			
    		
    		ball.move();							
    		
    		for(PowerUp p: pUps){					
    			p.move();
    		}
    		
        	repaint();								
    		
    	}
    }
    
    public int getLVL(){
    	//Returns the current level	
    	return level;
    }
    
    public int getLives(){
    	//Returns the current amount of lives
    	return lives;
    }
    
    public int getScore(){
    	//Returns the curent score
    	return score;
    }
}



