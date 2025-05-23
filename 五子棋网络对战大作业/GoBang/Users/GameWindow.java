package Users;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

//3.游戏界面
public class GameWindow implements ActionListener {
    Socket client = null;
    DataOutputStream dos = null;
    //存储上个页面传来的信息
    String[] str = new String[1];
    Icon[] ico = new Icon[1];
    String[] str_info = new String[1];//用于存储用户名、用户头像以及其他相关文本信息
    JFrame jf = new JFrame("五子棋游戏");
    //布局设置
    JTabbedPane jtp_1 = new JTabbedPane(SwingConstants.TOP);
    JSplitPane jsp_main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JSplitPane jsp_left = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    JSplitPane jsp_left_north = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    //设置按钮
    JButton start = new JButton("开始游戏");
//    JButton flatter = new JButton("求和");
//    JButton defeat = new JButton("认输");
    JButton exit = new JButton("返回房间");
    JButton jb_sent = new JButton("发送");
    //1-1、左上
    JTabbedPane jtp_2 = new JTabbedPane(SwingConstants.TOP);
    JPanel left_north = new JPanel();
    JPanel left_north_center = new JPanel();
    JPanel left_north_south = new JPanel();
    //1-2、左中
    JTabbedPane jtp_3 = new JTabbedPane(SwingConstants.TOP);
    JPanel left_center = new JPanel();
    JPanel left_center_center = new JPanel();
    JPanel left_center_south = new JPanel();
    //1-3、左下:
    JTabbedPane jtp_4 = new JTabbedPane();
    JPanel left_south = new JPanel();
    JTextArea jta = new JTextArea();
    JScrollPane left_south_center = new JScrollPane(jta);
    JPanel left_south_south = new JPanel();
    JPanel left_south_south_center = new JPanel();
    JPanel left_south_south_right = new JPanel();
    JTextField sent_info = new JTextField("", 9);
    //2、右边:
    JPanel right = new JPanel();
    JPanel right_north = new JPanel();
    JPanel right_south = new JPanel();
    JPanel right_up_left = new JPanel();
    //2-3、右下:
    JPanel right_down_center = new JPanel();
    //构造方法
    public GameWindow(String my_name, Icon my_face) {
        str[0] = my_name;
        ico[0] = my_face;
        JTextArea jta = new JTextArea();
        //设置 jspLeft 和 jspLeftUp 属性
        jsp_left.setDividerLocation(400);
        jsp_left.setDividerSize(3);
        jsp_left_north.setDividerLocation(200);
        jsp_left_north.setDividerSize(3);

        left_north.setLayout(new BorderLayout());
        left_north_center.add(new JLabel());    //todo
        left_north_center.add(new JLabel());    //todo
        left_north_south.add(new JLabel("剩余时间:"));//todo
        left_north.add(left_north_center, "Center");
        left_north.add(left_north_south, "South");

        jtp_2.addTab("自己", left_north);
        jsp_left_north.setTopComponent(jtp_2);

        left_center.setLayout(new BorderLayout());
        left_center_center.add(new JLabel());    //todo
        left_center_center.add(new JLabel());    //todo
        left_center_south.add(new JLabel("剩余时间:"));//todo
        left_center.add(left_center_center, "Center");
        left_center.add(left_center_south, "South");

        jtp_3.addTab("对手", left_center);
        jsp_left_north.setBottomComponent(jtp_3);

        left_south.setLayout(new BorderLayout());
        left_south_south_center.add(sent_info);
        left_south_south_right.add(jb_sent);
        left_south_south.add(left_south_south_center, "Center");
        left_south_south.add(left_south_south_right, "West");
        left_south.add(left_south_center, "Center");
        left_south.add(left_south_south, "South");

        jtp_4.addTab("聊天", left_south);
        //1、整合成左边
        jsp_left.setTopComponent(jsp_left_north);
        jsp_left.setBottomComponent(jtp_4);

        right.setLayout(new BorderLayout());
        //2-1、右上:
        right_north.setLayout(new BorderLayout());
        right_up_left.add(new JLabel("<<< 五子棋游戏——房间 >>>"));
        right_north.add(right_up_left, "West");
        right.add(right_north, "North");
        //2-2、右中:
        right.add(new ChessBoard(), "Center");
        //2-3、右下:
        right_south.setLayout(new BorderLayout());
        right_down_center.add(start);
//        right_down_center.add(flatter);
//        right_down_center.add(defeat);
        right_down_center.add(exit);
        //所有按钮的监听器
        jb_sent.addActionListener(this);
        start.addActionListener(this);
//        flatter.addActionListener(this);
//        defeat.addActionListener(this);
        exit.addActionListener(this);

        right_south.add(right_down_center, "Center");
        right.add(right_south, "South");
        //设置jsp_main
        jsp_main.setDividerLocation(200);
        jsp_main.setDividerSize(3);
        jsp_main.setLeftComponent(jsp_left);
        jsp_main.setRightComponent(right);
        //设置jtp_1属性
        jtp_1.addTab("游戏大厅", jsp_main);
        //设置jf属性
        jf.getContentPane().add(jtp_1);
        jf.setSize(800, 700);
        jf.setLocation(-10, 50);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connectServer();
        createReaderThread(jta);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb_sent) {
            try {
                dos = new DataOutputStream(client.getOutputStream());
                dos.writeUTF("info" + "::" + str[0] + "::" + sent_info.getText());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else if (e.getSource() == start) {
            try {
                System.out.println("点击开始了");
                dos = new DataOutputStream(client.getOutputStream());
                dos.writeUTF("start_game");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
//        else if (e.getSource() == flatter) {
//            System.out.println("求和");
//        } else if (e.getSource() == defeat) {
//            System.out.println("认输");
//        }
        else if (e.getSource() == exit) {
            JOptionPane.showMessageDialog(jf, "返回房间!", "返回", JOptionPane.PLAIN_MESSAGE);
            jf.setVisible(false);
            new UserHouse(str[0], ico[0]);
        }
    }
    //连接服务器:界面
    public void connectServer() {
        try {
            client = new Socket("127.0.0.1", 2345);
            dos = new DataOutputStream(client.getOutputStream());
            dos.writeUTF(str[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //创建线程:界面
    public void createReaderThread(JTextArea jta) {
        try {
            clientReader reader = new clientReader(new DataInputStream(client.getInputStream()), jta, this);
            reader.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //接收从服务器端传来的数据，并进行处理:界面
    public class clientReader extends Thread {
        DataInputStream dis;
        JTextArea jta;
        GameWindow gw;
        public clientReader(DataInputStream dis, JTextArea jta, GameWindow gw) {
            this.dis = dis;
            this.jta = jta;
            this.gw = gw;
        }
        public void run() {
            while (true) {
                try {
                    String info = dis.readUTF();
                    String[] str01 = info.split(" ");
                    String st01 = info.substring(0, 1);
                    String[] str02 = info.split("::");//聊天框信息
                    //1、出现自己和对手的信息
                    if (st01.equals("E")) {
                        if (str01[0].equals(ico[0].toString())) {
                            System.out.println("1:" + info);
                            left_north_center.add(new JLabel(new ImageIcon(str01[0])));
                            left_north_center.add(new JLabel(str01[1]));
                        } else {
                            left_center_center.add(new JLabel(new ImageIcon(str01[0])));
                            left_center_center.add(new JLabel(str01[1]));
                        }
                    }
                    //2、开始游戏
                    if (info.equals("start_game")) {
                        JOptionPane.showMessageDialog(jf, "双方准备完毕,游戏开始!", "结果", JOptionPane.PLAIN_MESSAGE);
                    }
                    //3、聊天框事件
                    if (str02[0].equals("info")) {
                        System.out.println("客户端运行!");
                        gw.jta.append(str02[1] + ":" + str02[2] + "\n");
                        gw.sent_info.setText("");
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}