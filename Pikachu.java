import java.awt.*;
import java.awt.event.*; 
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Pikachu extends JPanel implements ActionListener, KeyListener
{ 
    int boardWidth = 360; int boardHeight = 640;

    //images
    Image backgroundImg;
    Image pikaImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //pika
    int pikaX = boardWidth/8;
    int pikaY = boardHeight/2;
    int pikaWidth = 60;
    int pikaHeight = 50;

    class Pikathepokemon
    {
        int x = pikaX;
        int y = pikaY;
        int width = pikaWidth;
        int height = pikaHeight;
        Image img;

        Pikathepokemon(Image img)
        {
        this.img = img;
        }

    }

    //pipes
    int pipeX =boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; //scaled by 1/6
    int pipeHeight = 512;

    class Pipe
    {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img)
        {
             this.img = img;
        }
    }

    //game logic
    Pikathepokemon pika;
    int velocityX =-4; //move pipes to the left speed(simulates bied moving right)
    int velocityY = 0; // move pika up/down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    Pikachu() 
    {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

      //load images
      backgroundImg = new ImageIcon(getClass().getResource("./pokemon luna.jpeg")).getImage();
      pikaImg = new ImageIcon(getClass().getResource("./pika.png")).getImage();
      topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
      bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
    
      //pika
      pika = new Pikathepokemon(pikaImg);
      pipes = new ArrayList<Pipe>();

      //place pipes timer
      placePipesTimer = new Timer(1500,new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop = new Timer(1000/60,this);//1000/60 = 16.6
        gameLoop.start();
    }
    public void placePipes()
    {
    //(0-1 )* pipeHeight/2 ->(0-256) //128 //0 - 128 - (0-256) --> 1/4 pipeHeight ->3/4 pipeHeight


    int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
    int openingSpace = boardHeight/4;

    Pipe topPipe = new Pipe(topPipeImg);
    topPipe.y = randomPipeY;
    pipes.add(topPipe);

    Pipe bottomPipe = new Pipe(bottomPipeImg);
    bottomPipe.y = topPipe.y + pipeHeight +openingSpace;
    pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) 
    {
	    super.paintComponent(g);
	    draw(g);
    }
    public void draw(Graphics g) {
    
    //background
    g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

    //bird
    g.drawImage(pika.img,pika.x,pika.y,pika.width,pika.height,null);

    //pipes
    for(int i = 0; i< pipes.size();i++){
        Pipe pipe= pipes.get(i);
        g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
    }

    //score
    g.setColor(Color.white);
    g.setFont(new Font("Arial",Font.PLAIN,32)); 
    if(gameOver){
        g.drawString("Game Over: "+ String.valueOf((int) score),10,35);
    }
    else
    {
        g.drawString(String.valueOf((int) score),10,35);
    }
    
    }

    public void move(){
    //bird
    velocityY += gravity;
    pika.y +=velocityY;
    pika.y = Math.max(pika.y,0);
  
    //pipes 
    for(int i = 0; i< pipes.size();i++){
        Pipe pipe = pipes.get(i);
        pipe.x +=velocityX; 

        if(!pipe.passed && pika.x > pipe.x +pipe.width){
            pipe.passed = true;
            score +=0.5;//
        }

        if (collision(pika, pipe)){
            gameOver = true;
        }
    }

    if(pika.y > boardHeight){
        gameOver = true;
    }
    }


    public boolean collision(Pikathepokemon a, Pipe b){
        return a.x <b.x + b.width && //a's top left corner doesn't reach b's top right corner
           a.x +a.width >b.x &&  //a's top right corner passes b's top left corner
           a.y <b.y +b.height && //a's top left corner doesn't reach b's bottom left corner
           a.y + a.height > b.y; //a's bottom left corner passes b's top left corner
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        move();
        repaint();
        if(gameOver)
        {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        if(e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

