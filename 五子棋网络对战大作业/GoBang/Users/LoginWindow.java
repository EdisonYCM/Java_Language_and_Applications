package Users;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


//1.登陆界面
public class LoginWindow extends Component implements ActionListener{
	//DataOutputStream dos=null;//用于后续可能进行的网络数据输出操作
	String[] str=new String[1];
	JFrame jf=new JFrame("登录窗口");
	//标签
	JLabel info=new JLabel("请输入个人信息");
	JLabel username=new JLabel("用户名:");
    //JLabel server=new JLabel("服务器:");
    JLabel face=new JLabel("头像:");
    Random random = new Random();
    int randomNumber = random.nextInt(85) + 1;//随机初始头像
    JLabel use_face=new JLabel(new ImageIcon(LoginWindow.class.getResource("/resources/face/"+randomNumber+"-1.gif")));
    //文本框
	JTextField userName=new JTextField();
	JPasswordField Server=new JPasswordField();
	//按钮
	JButton Connect=new JButton("连接");
    JButton Reset=new JButton("重置");
    JButton Exit=new JButton("退出");
    
    public LoginWindow() {
		//初始化登录界面
    	//1、整体布局:边框布局管理器
    	str[0]="use_face.getText()";//第一个元素赋值了一个获取头像文本的表达式
    	jf.getContentPane().setLayout(new BorderLayout());//BorderLayout边框布局管理器
    	JPanel north=new JPanel();
        JPanel center=new JPanel();
        JPanel south=new JPanel();//整个界面划分为北、中、南三个区域
        
        //2、北边布局:
        north.setLayout(new BorderLayout());
        JPanel north_north=new JPanel();
        JPanel north_left=new JPanel();
        JPanel north_center=new JPanel();
        //北_北:
        north_north.add(info);//显示提示信息“请输入个人信息”
        //北_西:网格布局管理器
        north_left.setLayout(new GridLayout(2,1,1,1));//以2行1列的形式添加下面三个标签
        north_left.add(username);
        //north_left.add(server);
        north_left.add(face);
        //北_中:网格布局管理器
        north_center.setLayout(new GridLayout(2,1,1,1));
        north_center.add(userName);
        //north_center.add(Server);
        north_center.add(use_face);
        //将这三个子面板按照北、西、中的位置添加到north面板中
        north.add(north_north,"North");
        north.add(north_left,"West");
        north.add(north_center,"Center");
        //3、中区布局:不使用布局管理器
        center.setLayout(null);//采用绝对布局方式，需要手动指定每个组件的位置和大小
        int x=0,y=0;
        for(int i=1;i<=85;i++){//创建头像图标按钮
            if(x<420){//设置按钮位置，满420换行
                JButton jb=new JButton(new ImageIcon(LoginWindow.class.getResource("/resources/face/"+i+"-1.gif")));
                jb.setBounds(x,y,42,42);
                jb.addActionListener(this);
                center.add(jb);
                x+=42;
            }else{
                y+=42;
                x=0;
                JButton jb=new JButton(new ImageIcon(LoginWindow.class.getResource("/resources/face/"+i+"-1.gif")));
                jb.setBounds(x,y,42,42);
                jb.addActionListener(this);
                center.add(jb);
            }
        }
        //4、南区布局:流式布局管理器
        //并注册监听器
        south.setLayout(new FlowLayout());//按照添加顺序从左到右依次排列组件
        south.add(Connect);
        Connect.addActionListener(this);
        south.add(Reset);
        Reset.addActionListener(this);
        south.add(Exit);
        Exit.addActionListener(this);
        //将各个小布局加进整体布局中
        jf.getContentPane().add(north,"North");
        jf.getContentPane().add(center,"Center");
        jf.getContentPane().add(south,"South");
        //设置Swing窗口基本属性
        jf.setSize(440,550);
        jf.setVisible(true);//让窗口可见
        jf.setLocation(50,50);//指定窗口在屏幕上的初始位置
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置关闭窗口时的默认操作是退出整个程序
	}
	@Override
	public void actionPerformed(ActionEvent e) {//重写了ActionListener接口中的actionPerformed方法，用于处理各种按钮点击等动作事件
		//退出键
        if(e.getSource()==Exit){
            System.exit(0);
        }
        //重置键
        else if(e.getSource()==Reset){
            use_face.setIcon(new ImageIcon("/resources/face/1-1.gif"));
            userName.setText(null);
            Server.setText(null);//将头像恢复为默认，同时清空用户名和密码输入框中的内容
        }
        //连接键
        else if(e.getSource()==Connect){
            //告诉下个页面新用户来了
            isTextField(e);//合法性验证
        }
        else{
            //设置新头像
            JButton btn = (JButton)e.getSource();
            use_face.setIcon(btn.getIcon());
        }
	}
    //判断输入内容是否合法
	public void isTextField(ActionEvent e) {
		//1、判断姓名是否为空;
        String name=userName.getText();
        int name_long=name.length();
        //2、判断ip是否为空;
        //int ip=Server.getPassword().length;&& ip>0
        if(name_long>0 ){
            JOptionPane.showMessageDialog(this,"欢迎进入游戏大厅!","结果",JOptionPane.PLAIN_MESSAGE);
            Icon face=use_face.getIcon();
            new UserHouse(name,face);
            jf.setVisible(false);//将当前登录界面设置为不可见  && ip==0
        }else if(name_long==0 ){
            JOptionPane.showMessageDialog(this,"输入不能为空!","结果",JOptionPane.ERROR_MESSAGE);
        }
//            else if(name_long>0){
//            JOptionPane.showMessageDialog(this,"服务器ip地址不能为空！","结果",JOptionPane.ERROR_MESSAGE);
//        }else{
//            JOptionPane.showMessageDialog(this,"姓名不能为空！","结果",JOptionPane.ERROR_MESSAGE);
//        }	
	}
}