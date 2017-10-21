package com.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class TankGame extends JFrame implements ActionListener{
	MyPanel pl;
	JMenuBar jmb = null;
	JMenu jm = null;
	JMenuItem jmi1 = null;
	JMenuItem jmi2 = null;
	JMenuItem jmi3 = null;
	public static void main(String[] args) {
			TankGame game = new TankGame();
	}
	
	public TankGame() {
		pl = new MyPanel();
		Thread tp = new Thread(pl);
		tp.start();
		jmb = new JMenuBar();
		jm = new JMenu("菜单");
		jmi1 = new JMenuItem("存档");
		jmi2 = new JMenuItem("打开上次游戏");
		jmi3= new JMenuItem("退出");
		
		jmb.add(jm);
		jm.add(jmi1);
		jm.add(jmi2);
		jm.add(jmi3);

		jmi1.addActionListener(this);
		jmi1.setActionCommand("save");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("open");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("exit");
		
		this.add(pl);
		
		this.setJMenuBar(jmb);
		
		this.addKeyListener(pl);
		this.setSize(700,500);
		this.setLocation(200, 200);
		this.setTitle("BigSky's坦克大战2.0");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("save")) {
			GameData gd = new GameData(pl.et, pl.tank);
			gd.writeData();
		}
		if(e.getActionCommand().equals("open")) {
			GameData gd = new GameData();
			gd.readData();
//			for(int i=0;i<gd.ecount;i++) {
//				System.out.println(gd.getEts().get(i).getX()+" "+gd.getEts().get(i).getY()+" "+gd.getEts().get(i).getDirection());
//			}
			MyPanel npl = new MyPanel(gd.getEts(),gd.getMt(),gd.ecount);
			Thread t =new Thread(npl);
			t.start();
			
			this.remove(pl);
			
			this.add(npl);
			this.addKeyListener(npl);
			this.setVisible(true);
		}
		if(e.getActionCommand().equals("exit")) {
			System.exit(0);
		}
	}
}

class MyPanel extends JPanel implements KeyListener,Runnable {
	Vector<EnemyTank> et = new Vector<EnemyTank>();
	int esize = 5;
	MyTank tank = new MyTank();
	public MyPanel() {
		super();
		tank = new MyTank();
		for(int i=0;i<esize;i++) {
			EnemyTank etk1 = new EnemyTank((i+1)*50,0);
			Thread t = new Thread(etk1);
			t.start();
			et.add(etk1);	
			}
	}
	public MyPanel(Vector<EnemyTank> et,MyTank tank,int esize) {
		super();
		this.et = et;
		this.tank = tank;
		this.esize = esize;
		
		for(int i=0;i<esize;i++) {
			EnemyTank etk1 = et.get(i);
			Thread t = new Thread(etk1);
			t.start();
			}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 600, 400);
		
		for(int i=0;i<et.size();i++) {
			EnemyTank etank1 = et.get(i);
			for(int j=0;j<etank1.bullets.size();j++) {
				Bullet bt1 = etank1.bullets.get(j);
				hitTank(tank, bt1);
			}
		}
		if(tank.islive == true&&tank!=null) {
			drawTank(tank.getX(),tank.getY(),g,tank.getDirection(),tank.getColor());
		}
		
		for(int i=0;i<et.size();i++) {
			EnemyTank etank = et.get(i);
			for(int j=0;j<tank.bullets.size();j++) {
				Bullet ebt = tank.bullets.get(j);
				hitTank(etank, ebt);
			}
			
			if(etank.islive == true) {
				drawTank(et.get(i).getX(),et.get(i).getY(),g,et.get(i).getDirection(),et.get(i).getColor());
				for(int k=0;k<etank.bullets.size();k++) {
					Bullet bt = etank.bullets.get(k);
					if(bt.islive == true) {
						g.draw3DRect(etank.bullets.get(k).x, etank.bullets.get(k).y, 2, 2, false);
					}
					else {
						etank.bullets.remove(bt);
					}
				}

			}
			else {
				et.remove(etank);
			}
		}
		if(tank!=null) {
			for(int i=0;i<tank.bullets.size();i++) {    //若子弹超出边界则移除
				Bullet bt0 = tank.bullets.get(i);
				if(bt0.islive == false) {
					tank.bullets.remove(bt0);
					continue;
				}
				if(bt0.x>600||bt0.x<0||bt0.y>400||bt0.y<0) {
					bt0.islive = false;
					tank.bullets.remove(bt0);
				}
			}
			for(int i=0;i<tank.bullets.size();i++) {
				g.fill3DRect(tank.bullets.get(i).x, tank.bullets.get(i).y, 2, 2, false);
			}
		}
	}
	
	public void drawTank(int x,int y,Graphics g,int direction,int color) {   //ctrl+f批量替换
		switch (color) {
			case 0:{
				g.setColor(Color.cyan);
				break;
			}
			case 1:{
				g.setColor(Color.yellow);
			}
		}
		
		switch (direction) {
			case 0:{
				g.fill3DRect(x, y, 10, 36,false);
				g.fill3DRect(x+10, y+12, 10, 12, false);
				g.fill3DRect(x+20, y, 10, 36, false);
				g.fillOval(x+10, y+12, 10, 12);
				g.drawLine(x+15,y+3, x+15, y+18);
				break;
			}
			case 1:{
				g.fill3DRect(x, y, 36, 10,false);
				g.fill3DRect(x+12, y+10, 10, 12, false);
				g.fill3DRect(x, y+20, 36, 10, false);
				g.fillOval(x+12, y+10, 12, 10);
				g.drawLine(x+18,y+15, x+33, y+15);
				break;
			}
			case 2:{
				g.fill3DRect(x, y, 10, 36,false);
				g.fill3DRect(x+10, y+12, 10, 12, false);
				g.fill3DRect(x+20, y, 10, 36, false);
				g.fillOval(x+10, y+12, 10, 12);
				g.drawLine(x+15,y+18, x+15, y+33);
				break;
			}
			case 3:{
				g.fill3DRect(x, y, 36, 10,false);
				g.fill3DRect(x+12, y+10, 12, 10, false);
				g.fill3DRect(x, y+20, 36, 10, false);
				g.fillOval(x+12, y+10, 12, 10);
				g.drawLine(x+3,y+15, x+18, y+15);
				break;
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
			tank.setDirection(0);
			tank.moveUp();
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			tank.setDirection(2);
			tank.moveDown();
		}
		else if(e.getKeyCode() == KeyEvent.VK_A) {
			tank.setDirection(3);
			tank.moveLeft();
		}
		else if(e.getKeyCode() == KeyEvent.VK_D) {
			tank.setDirection(1);
			tank.moveRight();
		}
		if (e.getKeyCode() == KeyEvent.VK_J) {
			tank.fire();
		}
		
		repaint();
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	public void hitTank(Tank tk,Bullet bt) {
		switch(tk.direction) {
		case 0:{
			if(bt.x>=tk.getX()&&bt.x<=tk.getX()+30&&bt.y<=tk.getY()+36&&bt.y>=tk.getY()) {
				tk.islive = false;
				bt.islive = false;
			}
			break;
		}
		case 1:{
			if(bt.x>=tk.getX()&&bt.x<=tk.getX()+36&&bt.y<=tk.getY()+30&&bt.y>=tk.getY()) {
				tk.islive = false;
				bt.islive = false;
			}
			break;
		}
		case 2:{
			if(bt.x>=tk.getX()&&bt.x<=tk.getX()+30&&bt.y<=tk.getY()+36&&bt.y>=tk.getY()) {
				tk.islive = false;
				bt.islive = false;
			}
			break;
		}
		case 3:{
			if(bt.x>=tk.getX()&&bt.x<=tk.getX()+36&&bt.y<=tk.getY()+30&&bt.y>=tk.getY()) {
				tk.islive = false;
				bt.islive = false;
			}
			break;
		}
		}
	}
	
	@Override
	public void run() {
		while(true) {
			repaint();
		}
	}
}



