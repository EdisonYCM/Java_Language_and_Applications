package LoginPage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

//对应页面的属性
public class Server {
	String name;
    DataInputStream dis;
    DataOutputStream dos;
    public Server(String name, DataInputStream dis, DataOutputStream dos) {
        super();
        this.name = name;
        this.dis = dis;
        this.dos = dos;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public DataInputStream getDis() {
        return dis;
    }
    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }
    public DataOutputStream getDos() {
        return dos;
    }
    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }	
}
//对应棋盘的属性
class ChatServerChessBoard{
	String name;
    DataInputStream dis;
    DataOutputStream dos;
    public ChatServerChessBoard(String name,DataInputStream dis, DataOutputStream dos) {
        super();
        this.dis = dis;
        this.dos = dos;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public DataInputStream getDis() {
        return dis;
    }
    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }
    public DataOutputStream getDos() {
        return dos;
    }
    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }
}
//服务器端的读取接受数据
class ChatServer {
  int[] arr=new int[1];//双方点击之后就可以开始
  //请求客户端Client的连接:界面
  public void waitConnect01() {
      ArrayList<String> array_against=new ArrayList<>();//存储对战信息
      ArrayList<String> array_local=new ArrayList<>();//存储座位地址
      try {
          //将服务器对象写入集合
          Vector<Server> users = new Vector<>();
          ServerSocket ss = new ServerSocket(2345);
          while(true) {
              Socket server = ss.accept();
              DataInputStream dis = new DataInputStream(server.getInputStream());
              DataOutputStream dos = new DataOutputStream(server.getOutputStream());
              String userName = dis.readUTF();
              Server user = new Server(userName,dis,dos);
              users.add(user);
              MsgReader01 userReader = new MsgReader01(user,users,array_against,array_local,arr);
              userReader.start();
          }
      } catch (IOException e){
          e.printStackTrace();
      }
  }
  //请求客户端Client的连接:棋盘
  public void waitConnect02() {
      int[][] array=new int[15][15];
      int[] judge_start=new int[1];
      try {//将服务器对象写入集合
          Vector<ChatServerChessBoard> users = new Vector<>();
          ServerSocket ss = new ServerSocket(2365);
          while(true) {
              Socket server = ss.accept();
              DataInputStream dis = new DataInputStream(server.getInputStream());
              DataOutputStream dos = new DataOutputStream(server.getOutputStream());
              String userName = dis.readUTF();
              ChatServerChessBoard user = new ChatServerChessBoard(userName,dis,dos);
              users.add(user);
              MsgReader02 userReader = new MsgReader02(user,users,array,arr,judge_start);
              userReader.start();
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public static void main(String args[]) {
      ChatServer chatserver = new ChatServer();
      new Thread(chatserver::waitConnect01).start();
      new Thread(chatserver::waitConnect02).start();
  }
}
//界面:处理数据
class MsgReader01 extends Thread{
  Server user;
  Vector<Server> userList;
  ArrayList<String> array_against;
  ArrayList<String> array_local;

  int[] arr;

  public MsgReader01(Server user,Vector<Server> userList,ArrayList<String> array_against,ArrayList<String> array_local,int[] arr) {
      super();
      this.user = user;
      this.userList= userList;
      this.array_against=array_against;
      this.array_local=array_local;
      this.arr=arr;
  }

  public void run() {
      //通知所有人有新人来,遍历用户列表,向其他用户发送我进来的消息，向我发送其他用户存在的消息
      Iterator<Server> it;
      //开始读取消息，并显示数据
      while(true) {
          try {
              String info = user.getDis().readUTF();
              String[] str01 = info.split(":");//信息框信息
              String[] str02 = info.split(" ");
              String str03=info.substring(0,1);
              String[] str04 = info.split("::");//聊天框信息
              //一、UserHouse界面
              //1、服务器信息:(欢迎:张三:进入房间)
              if (str01[0].equals("欢迎")) {
                  it = userList.iterator();
                  while (it.hasNext()) {
                      Server uu = it.next();
                      uu.getDos().writeUTF(info);
                  }
              }
              //2、入座处理信息:(按钮位置,座位数,头像地址,名字)
              if(str02[0].startsWith("java")){
                  it = userList.iterator();
                  while (it.hasNext()) {
                      Server uu = it.next();
                      uu.getDos().writeUTF(info);
                  }
              }
              //3、进入房间键
              else if(info.startsWith("Start")){
                  it = userList.iterator();
                  while (it.hasNext()) {
                      Server uu = it.next();
                      uu.getDos().writeUTF(info);
                  }
              }
              //4、点击开始键后,将头像和名字传到对战框
              else if(str03.equals("C")) {
                  array_against.add(info);
                  it = userList.iterator();
                  if (userList.size() == 4) {//当两个人都进入了房间才显示
                      while (it.hasNext()) {
                          Server uu = it.next();
                          for (String s : array_against) {
                              uu.getDos().writeUTF(s);
                          }
                      }
                  }
              }
              //二、GameWindow界面
              //1、开始游戏按钮
              if(info.equals("start_game")){
                  arr[0]++;
              }
              if(arr[0]==2){
                  it = userList.iterator();
                  while (it.hasNext()) {
                      Server uu = it.next();
                      uu.getDos().writeUTF("start_game");
                  }
              }
              //2、聊天页面信息
              //String[] str04 = info.split("::");//聊天框信息
              if(str04[0].equals("info")){
                  System.out.println(info);
                  it = userList.iterator();
                  while (it.hasNext()) {
                      Server uu = it.next();
                      uu.getDos().writeUTF(info);
                  }
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
}
//棋盘:处理数据
class MsgReader02 extends Thread{
  ChatServerChessBoard user;
  Vector<ChatServerChessBoard> userList;
  int[][] array;
  int[] arr;
  int[] judge_start;

  public MsgReader02(ChatServerChessBoard user,Vector<ChatServerChessBoard> userList,int[][] array,int[] arr,int[] judge_start) {
      super();
      this.user = user;
      this.userList= userList;
      this.array=array;
      this.arr=arr;
      this.judge_start=judge_start;
  }

  public void run() {
      //通知所有人有新人来,遍历用户列表,向其他用户发送我进来的消息，向我发送其他用户存在的消息
      Iterator<ChatServerChessBoard> it;
      //开始读取消息，并显示数据
      while(true) {
          try {
              String info = user.getDis().readUTF();
              String[] str_01=info.split(" ");
              int x=Integer.parseInt(str_01[1]);
              int y=Integer.parseInt(str_01[2]);
              //1、开始游戏事件
              if(arr[0]>=2 && (str_01[0].equals("qizi"))){
                  //开始判断棋子,并将棋子坐标存入数组
                  if(judge_start[0]==0){
                      array[x][y]=1;
                      judge_start[0]=1;//换另一个人下棋
                  }else if(judge_start[0]==1){
                      array[x][y]=2;
                      judge_start[0]=0;//换另一个人下棋
                  }
                  //将棋子坐标全部遍历出去
                  for(int i=0;i<15;i++){
                      for(int j=0;j<15;j++){
                          if(array[i][j]==1){
                              it = userList.iterator();
                              while (it.hasNext()) {
                                  ChatServerChessBoard uu = it.next();
                                  uu.getDos().writeUTF("black"+" "+i+" "+j);
                              }
                          }else if(array[i][j]==2){
                              it = userList.iterator();
                              while (it.hasNext()) {
                                  ChatServerChessBoard uu = it.next();
                                  uu.getDos().writeUTF("white"+" "+i+" "+j);
                              }
                          }
                      }
                  }
              }
              //2、判断胜利,并将相应的信息传回给客户端
              if(winCol(array,x,y)==1){
                  it = userList.iterator();
                  for(int i=0;i<15;i++){
                      for(int j=0;j<15;j++){
                          array[i][j]=0;
                      }
                  }
                  judge_start[0]=0;//初始化黑棋先下
                  arr[0]=0;//还要点开始才能玩
                  while (it.hasNext()) {
                      ChatServerChessBoard uu = it.next();
                      uu.getDos().writeUTF("black_win"+" "+"end");
                  }
              }
              else if(winCol(array,x,y)==2){
                  it = userList.iterator();
                  for(int i=0;i<15;i++){
                      for(int j=0;j<15;j++){
                          array[i][j]=0;
                      }
                  }
                  judge_start[0]=0;//初始化黑棋先下
                  arr[0]=0;//还要点开始才能玩
                  while (it.hasNext()) {
                      ChatServerChessBoard uu = it.next();
                      uu.getDos().writeUTF("white_win"+" "+"end");
                  }
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
    public int winCol(int[][] all_chess, int row, int col) {
        // 检查传入参数的合法性，避免后续可能出现的数组越界等异常情况
        if (all_chess == null || row < 0 || row >= all_chess.length || col < 0 || col >= all_chess[0].length) {
            throw new IllegalArgumentException("传入的棋盘数据或坐标参数不合法");
        }
        // 定义四个方向的偏移量数组，分别对应垂直、水平、左上右下对角线、左下右上对角线方向
        int[][] directions = {
                {0, -1}, {0, 1}, // 垂直方向（向上、向下）
                {-1, 0}, {1, 0}, // 水平方向（向左、向右）
                {-1, -1}, {1, 1}, // 左上右下对角线方向（向左上、向右下）
                {-1, 1}, {1, -1} // 左下右上对角线方向（向左下、向右上）
        };
        for (int[] direction : directions) {
            int count = 1;
            int curRow = row + direction[0];
            int curCol = col + direction[1];
            //循环检查当前方向上连续相同棋子的数量
            while (curRow >= 0 && curRow < all_chess.length && curCol >= 0 && curCol < all_chess[0].length
                    && all_chess[curRow][curCol] == all_chess[row][col]) {
                count++;
                curRow += direction[0];
                curCol += direction[1];
                if (count == 5) {
                    return all_chess[row][col];
                }
            }
        }
        return 0;
    }
}
