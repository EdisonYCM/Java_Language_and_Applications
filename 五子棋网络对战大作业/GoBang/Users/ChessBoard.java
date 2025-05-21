package Users;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//4.绘制棋盘
class ChessBoard extends JPanel implements MouseListener {
	Socket client = null;
    DataOutputStream dos = null;
    String color="qizi";//存储颜色
    //引入图片
    Image qipan;
    Image blackqizi;
    Image whiteqizi;
    //棋子坐标
    int row=-1;int line=-1;//记录的是鼠标点击时在面板上的原始坐标
    int arrayX=-1;int arrayY=-1;//经过换算后对应到棋盘数组中的坐标位置
    int[][] all_chess=new int[15][15];//二维数组用于记录棋盘上每个位置的棋子状态,0表示该位置为空，1表示有黑棋，2表示有白棋
    
    public ChessBoard(){
        super();
        //读取棋子和棋盘的文件
        try {
//            qipan= ImageIO.read(new File(".\\resources\\gobang\\board.jpg"));
//            blackqizi=ImageIO.read(new File(".\\resources\\gobang\\heiqi.jpg"));
//            whiteqizi= ImageIO.read(new File(".\\resources\\gobang\\baiqi.jpg"));
            URL qipanUrl = getClass().getClassLoader().getResource("resources/gobang/board.jpg");
            File qipanFile = Paths.get(qipanUrl.toURI()).toFile();
            qipan = ImageIO.read(qipanFile);
            URL blackqiziUrl = getClass().getClassLoader().getResource("resources/gobang/heiqi.jpg");
            File blackqiziFile = Paths.get(blackqiziUrl.toURI()).toFile();
            blackqizi = ImageIO.read(blackqiziFile);
            URL whiteqiziUrl = getClass().getClassLoader().getResource("resources/gobang/baiqi.jpg");
            File whiteqiziFile = Paths.get(whiteqiziUrl.toURI()).toFile();
            whiteqizi = ImageIO.read(whiteqiziFile);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        connectServer();
        createReaderThread();
        this.addMouseListener(this);
    }
    //画笔
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //画棋盘
        g.drawImage(qipan,0,0,this);
        //画棋子
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                if(all_chess[i][j]==1){
                    g.drawImage(blackqizi,i*35+10,j*35+10,this);
                }else if(all_chess[i][j]==2){
                    g.drawImage(whiteqizi,i*35+10,j*35+10,this);
                }
            }
        }
    }
    //鼠标点击响应
    @Override
    public void mouseClicked(MouseEvent e) {//五子棋的判断
        row=e.getX();
        line=e.getY();//记录下点击的原始位置
        arrayX=(row-10)/35;
        arrayY=(line-10)/35;//将原始坐标转换为对应棋盘数组中的坐标位置
        if(all_chess[arrayX][arrayY]==0){//为空，则表示可以放置棋子
            try{
                dos = new DataOutputStream(client.getOutputStream());
                dos.writeUTF(color+" "+arrayX+" "+arrayY);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    //连接服务器
    public void connectServer() {
        try {
            client = new Socket("127.0.0.1", 2365);
            dos = new DataOutputStream(client.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //创建线程
    public void createReaderThread() {
        try {
            clientReader reader = new clientReader(new DataInputStream(client.getInputStream()),this);
            reader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //接收从服务器端传来的数据，并进行处理
    class clientReader extends Thread {
        DataInputStream dis;
        ChessBoard cb;
        public clientReader(DataInputStream dis,ChessBoard cb) {
            this.dis = dis;
            this.cb=cb;
        }
        public void run() {
            while (true) {
                try {
                    String info = dis.readUTF();
                    String[] str_01=info.split(" ");
                    //储存棋子坐标
                    if(str_01[0].equals("black")) {
                        int x=Integer.parseInt(str_01[1]);
                        int y=Integer.parseInt(str_01[2]);
                        all_chess[x][y]=1;
                    }else if(str_01[0].equals("white")){
                        int x=Integer.parseInt(str_01[1]);
                        int y=Integer.parseInt(str_01[2]);
                        all_chess[x][y]=2;
                    }
                    //判断输赢
                    if(str_01[1].equals("end")){
                        if(str_01[0].equals("black_win")){
                            System.out.println("黑棋胜利!");
                            JOptionPane.showMessageDialog(cb,"黑棋胜!","返回",JOptionPane.PLAIN_MESSAGE);
                        }else if(str_01[0].equals("white_win")){
                            System.out.println("白棋胜利!");
                            JOptionPane.showMessageDialog(cb,"白棋胜!","返回",JOptionPane.PLAIN_MESSAGE);
                        }
                        for(int i=0;i<15;i++){
                            for(int j=0;j<15;j++){
                                all_chess[i][j]=0;
                            }
                        }
                    }
                    cb.repaint();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
}