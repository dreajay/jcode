package com.jcodes.nio.simulateQQ;
import java.awt.Color;  
import java.awt.Font;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.event.WindowAdapter;  
import java.awt.event.WindowEvent;  
import java.io.IOException;  
 
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JTextField;  
 
 
public class LoginFrame {  
      
    private JLabel   
        lblTitle = new JLabel("山寨版QQ"),  
        lblUserName = new JLabel("用户名："),  
        lblPassword = new JLabel("密     码：");  
      
    private JTextField   
        txtUserName = new JTextField(15),  
        txtPassword = new JTextField(15);  
      
    private JButton  
        btnSub = new JButton("提交"),  
        btnRes = new JButton("取消");  
      
    private JFrame  
        aFrame = new JFrame("登录山寨QQ");  
      
    private ClientServerBIz clientBiz;  
    public LoginFrame()  
    {  
        init();  
    }  
      
    private void init()  
    {  
        aFrame.setLayout(null);  
        aFrame.setBounds(300, 300, 200, 180);  
        lblTitle.setBounds(45, 10, 100, 40);  
        lblTitle.setForeground(new Color(120, 120, 120));  
        lblTitle.setFont(new Font("山寨版QQ", 1, 20));  
        aFrame.add(lblTitle);  
        lblUserName.setBounds(10, 50, 80, 20);  
        aFrame.add(lblUserName);  
        lblPassword.setBounds(10, 80, 80, 20);  
        aFrame.add(lblPassword);  
        txtUserName.setBounds(65, 50, 120, 20);  
        aFrame.add(txtUserName);  
        txtPassword.setBounds(65, 80, 120, 20);  
        aFrame.add(txtPassword);  
        btnSub.setBounds(10, 110, 80, 25);  
        aFrame.add(btnSub);  
        btnRes.setBounds(100, 110, 80, 25);  
        aFrame.add(btnRes);  
          
        btnSub.addActionListener(new ActionListener() {  
            @Override 
            public void actionPerformed(ActionEvent e) {  
                String userInfo = txtUserName.getText() + "-" + txtPassword.getText();  
                try {  
                    clientBiz = new ClientServerBIz();  
                    clientBiz.sendToServer(userInfo);  
                    Object obj = clientBiz.sendToClient();  
                    System.out.println(obj.toString());  
                    if (Boolean.parseBoolean(obj.toString()))  
                    {  
                        ChatFrame cf = new ChatFrame(clientBiz,txtUserName.getText());  
                        cf.show();  
                        aFrame.setVisible(false); 
                    }  
                    else 
                    {  
                        System.out.println("用户不存在或密码错误！");  
                    }  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                } catch (ClassNotFoundException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        });  
          
        btnRes.addActionListener(new ActionListener() {  
            @Override 
            public void actionPerformed(ActionEvent e) {  
                System.exit(0);  
            }  
        });  
          
        aFrame.addWindowListener(new WindowAdapter() {  
            @Override 
            public void windowClosing(WindowEvent e) {  
                System.exit(0);  
            }     
        });  
    }  
      
    public void show()  
    {  
        aFrame.setVisible(true);  
    }   
      
    public static void main(String[] args) {  
        LoginFrame login = new LoginFrame();  
        login.show();  
    }  
 
} 