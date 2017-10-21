package com.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.util.Vector;

class Tank{
	int x = 200,y = 200;
	int direction = 0;
	int speed = 5;
	boolean islive = true;
	
	public Tank() {
	}
	
	public Tank(int speed) {
		this.speed = speed;
	}
	
	public Tank(int x,int y,int speed) {
		this.x = x;
		this.y = y;
		this.speed = speed;
	}
	
	public void moveUp() {
		y -= speed;
	}
	
	public void moveDown() {
		y += speed;
	}
	
	public void moveRight() {
		x += speed;
	}
	
	public void moveLeft() {
		x -= speed;
	}
	
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

}

class MyTank extends Tank {
	int color = 0;
	int direction = 0;
	static int speed = 5;
	Vector<Bullet> bullets = new Vector<Bullet>();   //定义坦克自己的弹夹
	
	public MyTank() {
		super();
	}
	
	public MyTank(int x,int y,int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	public MyTank(int x,int y) {
		super(x,y,speed);
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void fire() {
		Bullet bt = null;
		switch(direction) {
		case 0:{
			bt = new Bullet(this.x+15,this.y+3,direction);
			break;
		}
		case 1:{
			bt = new Bullet(this.x+33,this.y+15,direction);
			break;
		}
		case 2:{
			bt = new Bullet(this.x+15,this.y+33,direction);
			break;
		}
		case 3:{
			bt = new Bullet(this.x+3,this.y+15,direction);
			break;
		}
		}
		if(bullets.size()<=4 && bt.islive == true) {  //最多同时连发5颗子弹
			bullets.add(bt);
			Thread t1 = new Thread(bt);
			t1.start();
		}
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public static int getSpeed() {
		return speed;
	}

	public static void setSpeed(int speed) {
		MyTank.speed = speed;
	}
}

class EnemyTank extends Tank implements Runnable{
	int color = 1;
	static int speed = 3;
	Vector<Bullet> bullets = new Vector<Bullet>();  
	
	public EnemyTank(int x,int y,int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	public EnemyTank(int x,int y) {
		super(x,y,speed);
		Bullet bt = null;
		switch(direction) {
		case 0:{
			bt = new Bullet(this.x+15,this.y+3,direction);
			break;
		}
		case 1:{
			bt = new Bullet(this.x+33,this.y+15,direction);
			break;
		}
		case 2:{
			bt = new Bullet(this.x+15,this.y+33,direction);
			break;
		}
		case 3:{
			bt = new Bullet(this.x+3,this.y+15,direction);
			break;
		}
		}
		Thread t = new Thread(bt);
		t.start();
		bullets.add(bt);
	}
	
	public EnemyTank() {
		super(speed);
	}
	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public static int getSpeed() {
		return speed;
	}

	public static void setSpeed(int speed) {
		EnemyTank.speed = speed;
	}

	@Override
	public void run() { 					//让敌方坦克自己移动
		while (true) {
			switch(direction) {
			case 0:
				for(int i=0;i<30;i++) {                   //for循环是为了让坦克更平缓地移动
					if (y>0)
						this.y -= speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 1:
				for(int i=0;i<30;i++) {
					if(x<(600-30))
						this.x += speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 2:
				for(int i=0;i<30;i++) {
					if(y<400-36)
						this.y += speed;			
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case 3:
				for(int i=0;i<30;i++) {
					if (x>0)
						this.x -= speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
			this.direction = (int)(Math.random()*4);
			Bullet bt = null;
			for(int j=0;j<bullets.size();j++) {
				bt = this.bullets.get(j);
				bt.judgeLive();
				}
			
			if(this.islive) {
				if(this.bullets.size()<5) {     		 //敌方坦克最多连发五颗子弹
					bt = new Bullet(this);
					Thread t = new Thread(bt);
					t.start();
					this.bullets.add(bt);
				}
			}
			if(islive == false) {
				break;
			}
		}
		
	}
}

class Bullet implements Runnable{       //子弹运行为一个线程
	int x,y;
	int speed = 5;
	int direction;
	boolean islive = true;
	
	public Bullet(int x,int y,int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;      //发射子弹的方向
	}
	
	public Bullet(Tank tank) {
		switch(tank.getDirection()) {
		case 0:{
			this.x = tank.getX()+15;
			this.y = tank.getY()+3;
			break;
		}
		case 1:{
			this.x = tank.getX()+33;
			this.y = tank.getY()+15;
			break;
		}
		case 2:{
			this.x = tank.getX()+15;
			this.y = tank.getY()+33;
			break;
		}
		case 3:{
			this.x = tank.getX()+3;
			this.y = tank.getY()+15;
			break;
		}
		}
		this.direction = tank.direction;
	}
	
	public void judgeLive() {
		if (this.x>600||this.x<0||this.y>400||this.y<0)
			this.islive = false;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch (direction) {
			case 0:{
				y -= speed;
				break;
			}
			case 1:{
				x += speed;
				break;
			}
			case 2:{
				y += speed;
				break;
			}
			case 3:{
				x -= speed;
				break;
			}
			}
			
			if (islive == false)
				break;
		}
	}
}

class GameData{
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	MyTank mt = null;
	int ecount = 0;
	int mcount = 0;
	String file  = "D:/DataOfTankGame.txt";
	
	public GameData(Vector<EnemyTank> ets,MyTank mt) {
		this.ets = ets;
		this.mt = mt;
	}
	
	public GameData() {
		
	}
	
	public void writeData() {
		FileWriter fw = null;
		BufferedWriter bw = null;
		String s = "";
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for(int i=0;i<ets.size();i++) {
				EnemyTank et = ets.get(i);
				if(et.islive == true) {
					s = et.getX() + " " + et.getY() + " " + et.getDirection();
					s += "\r\n";
					bw.write(s);
				}
			}
			if (mt.islive) {
				s = "-10 " + mt.getX() + " " + mt.getY() +  " " + mt.getDirection();  //-10表示此行是mytank的数据 
			}
			bw.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void readData(){
		FileReader fr = null;
		BufferedReader br = null;
		String s = "";
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			while((s=br.readLine())!=null) {
				String[] x = s.split(" ");
				int[] y = new int[5];
				if(!x[0].equals("-10")) {
					for(int i=0;i<x.length;i++) {
						y[i] = Integer.parseInt(x[i]);
					}
					EnemyTank eti = new EnemyTank(y[0],y[1],y[2]);
					ets.add(eti);
					ecount++;
				}
				if(x[0].equals("-10")) {
					for(int j =1;j<x.length;j++) {
						y[j-1] = Integer.parseInt(x[j]);
					}
					mt = new MyTank(y[0],y[1],y[2]);
					mcount++;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	public Vector<EnemyTank> getEts() {
		return ets;
	}

	public void setEts(Vector<EnemyTank> ets) {
		this.ets = ets;
	}

	public MyTank getMt() {
		return mt;
	}

	public void setMt(MyTank mt) {
		this.mt = mt;
	}
}


















