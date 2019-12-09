import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("3D Street");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PaintPanel paintPanel = new PaintPanel();
        paintPanel.setFocusable(true);
        frame.setContentPane(paintPanel);

        frame.setSize(1280,768);
//        frame.setLocation(-1400,100);
        frame.setVisible(true);



    }


}
