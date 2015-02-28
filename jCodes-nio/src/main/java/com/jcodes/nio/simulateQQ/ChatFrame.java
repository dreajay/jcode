package com.jcodes.nio.simulateQQ;
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.event.WindowAdapter;  
import java.awt.event.WindowEvent;  
import java.io.IOException;  
 
import javax.swing.DefaultListModel;  
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JList;  
import javax.swing.JOptionPane;  
import javax.swing.JTextArea;  
import javax.swing.event.ListSelectionEvent;  
import javax.swing.event.ListSelectionListener;  
 
 
public class ChatFrame {  
      
    //文本框  
    private JTextArea  
        readContext = new JTextArea(18,30),//显示信息  
        writeContext = new JTextArea(6,30);//发送信息  
      
    //列表框  
    private DefaultListModel modle = new DefaultListModel();//列表模型  
    private JList list = new JList(modle);//列表  
      
    //按钮  
    private JButton   
        btnSub = new JButton("提交"),//提交按钮  
        btnRes = new JButton("取消");//取消按钮  
      
    //窗体界面  
    private JFrame aFrame = new JFrame("ChatFrame");  
      
    //用户名  
    private String userName;  
      
    //Client业务类  
    private ClientServerBIz userBiz;  
      
    //设置线程是否运行  
    private boolean isConntext = false;  
      
    //构造方法  
    public ChatFrame(ClientServerBIz clientBiz,String userName)  
    {  
        //获取用户名  
        this.userName = userName;  
        userBiz = clientBiz;  
        //开启线程  
        isConntext = true;  
        new Thread(new ctUsers()).start();    
    }  
      
    //初始化界面  
    private void init() throws IOException, ClassNotFoundException  
    {  
        aFrame.setLayout(null);  
        aFrame.setTitle(userName+" 聊天窗口");  
        aFrame.setSize(500, 500);  
        aFrame.setLocation(400, 200);  
        readContext.setBounds(10, 10, 320, 285);  
        readContext.setEditable(false);  
        writeContext.setBounds(10, 305, 320, 100);  
        list.setBounds(340, 10, 140, 445);  
        aFrame.add(readContext);  
        aFrame.add(writeContext);  
        aFrame.add(list);  
        btnSub.setBounds(150, 415, 80, 30);  
        btnRes.setBounds(250, 415, 80, 30);  
          
        //frame的关闭按钮事件  
        aFrame.addWindowListener(new WindowAdapter() {  
            @Override 
            public void windowClosing(WindowEvent e) {  
                isConntext = false;  
                //发送关闭信息  
                userBiz.sendToServer("exit_" + userName);  
                System.exit(0);  
            }  
        });  
 
        //提交按钮事件  
        btnSub.addActionListener(new ActionListener() {  
            @Override 
            public void actionPerformed(ActionEvent e) {  
                //发送信息  
                userBiz.sendToServer(userName + "^" + writeContext.getText());  
                writeContext.setText(null);  
            }  
        });  
          
        //关闭按钮事件  
        btnRes.addActionListener(new ActionListener() {   
            @Override 
            public void actionPerformed(ActionEvent e) {  
                isConntext = false;  
                //发送关闭信息  
                userBiz.sendToServer("exit_" + userName);  
                System.exit(0);  
            }  
        });  
          
          
        list.addListSelectionListener(new ListSelectionListener() {   
            @Override 
            public void valueChanged(ListSelectionEvent e) {  
                JOptionPane.showMessageDialog(null,list.getSelectedValue().toString());  
            }  
        });  
          
        aFrame.add(btnSub);  
        aFrame.add(btnRes);  
        aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    }  
      
    //界面显示  
    public void show() throws IOException, ClassNotFoundException  
    {  
        init();  
        aFrame.setVisible(true);  
        userBiz.sendToServer("open");  
    }  
      
    class ctUsers extends Thread  
    {  
        public void run()   
        {  
            while(isConntext)  
            {  
                //获取服务器传过的值  
                Object obj = userBiz.sendToClient();  
                  
                //判断值是否有空  
                if(obj != null)  
                {  
                      
                    if(obj.toString().indexOf("[") != -1 && obj.toString().lastIndexOf("]") != -1)  
                    {  
                        obj = obj.toString().substring(1, obj.toString().length()-1);  
                        String [] userNames = obj.toString().split(",");  
                        modle.removeAllElements();  
                        for (int i = 0; i < userNames.length; i++) {  
                            modle.addElement(userNames[i].trim());  
                        }  
                    }  
                    else 
                    {  
                        String str = readContext.getText() + obj.toString();  
                        readContext.setText(str);  
                    }  
                }  
            }  
        }  
    }  
} 