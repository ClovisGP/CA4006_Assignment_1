package test_GUI;

import java.awt.*;
import java.awt.event.*;
  
public class MyFrame extends Frame {
    public MyFrame()
    {
        setSize(300, 200);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        TextField b = new TextField();
        b.setBounds(10,30,150,40);
        this.add(b);
        Button a = new Button("Do SOMETHING DUMBASS");
        a.setBounds(150,150,120,40);
        this.add(a);
        this.setLayout(null);
        a.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(b.getText());
            }
        });
        setVisible(true);
    }
  
    public static void main(String[] args)
    {
        new MyFrame();
    }
}