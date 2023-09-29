import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class panel extends JFrame  {
    private MyComponent komponent;
    private Img img;
    private wykres[] wykresy;
    private Siec siec;
    private String zapis="idioty";
    JRadioButton lV;
    JRadioButton lP;
    JRadioButton lR;
    JRadioButton lF;
    JLabel wyjscie;
    JLabel[] ile;
    JLabel poprawnosc;
    int poprawneODP=0;
    int zadaneP=0;
    int epoka=0;

    //Panel do rysowania
    private class MyComponent extends JPanel implements MouseListener, MouseMotionListener {
        private boolean p;
        private boolean painting;
        private int px, py;
        private boolean[][] data;
        private int[] bit8x8;
        private boolean czyRysowane=false;

        protected void paint() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            bit8x8=new int[64];
            cleanMatrix();
            p = false;
            painting = false;
            px = 0;
            py = 0;
            data = new boolean[getWidth()][getHeight()];
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void mousePressed(MouseEvent e) {
            p = true;
            painting = true;
        }

        public void mouseDragged(MouseEvent e) {
            int x = e.getX(), y = e.getY();
            Graphics graphics = getGraphics();
            graphics.setColor(Color.BLACK);
            czyRysowane=true;
            if (painting && p) {
                graphics.drawLine(x, y, x, y);
                p = false;
            } else if (painting) {
                graphics.drawLine(px, py, x, y);
            }
            px = x;
            py = y;
            if (painting) data[x][y] = true;
        }

        public void mouseExited(MouseEvent e) {
            painting = false;
        }

        public void mouseEntered(MouseEvent e) {
            painting = true;
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void Clean() {
            data = new boolean[getWidth()][getHeight()];
            Graphics graphics = getGraphics();
            graphics.clearRect(0, 0, getWidth(), getHeight());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            czyRysowane=false;
            cleanMatrix();
        }

        private void cleanMatrix() {
            for(int i=0;i<64;i++)
                bit8x8[i]=0;
        }

        public void minimalData() {
            if(czyRysowane==true) {
                boolean[][] imgData;
                int px = getWidth(), py = getHeight(), kx = 0, ky = 0;
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < data[i].length; j++) {
                        if (data[i][j] == true) {
                            if (i < px)
                                px = i;
                            if (j < py)
                                py = j;
                            if (j > ky)
                                ky = j;
                            if (i > kx)
                                kx = i;
                        }
                    }
                }
                imgData = new boolean[kx - px+8][ky - py+8];
                for (int i = 0; i < imgData.length; i++) {
                    for (int j = 0; j < imgData[i].length; j++) {
                        if(data[i].length<j+py+4 || data.length<i+px+4 ||j+py-4<0 ||i+px-4<0 )
                            imgData[i][j]=false;

                        else
                            imgData[i][j] = data[i + px-4][j + py-4];
                    }
                }
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        bit8x8[i + j * 8] = getStatus(imgData.length * i / 8, imgData[0].length * j / 8, imgData.length * (i + 1) / 8, imgData[0].length * (j + 1) / 8, imgData);
                    }
                }
            }
        }

        private int getStatus(int x1, int y1, int x2, int y2, boolean[][] imgData) {
            for(int i=y1;i<y2;i++)
                for(int j=x1;j<x2;j++)
                {
                    if(imgData[j][i]==true)
                        return 1;
                }
            return 0;
        }
        public int[] getBit8x8()
        {
            minimalData();
            return bit8x8;
        }
    }

    //Graficzne przedstawienie wejścia do sieci
    private class Img extends JPanel{
        private int[] bit8x8;
        private boolean check=false;
        public void paint(Graphics g){
            int x=getWidth();
            int y=getHeight();
            if(check==true){
                for(int i=0;i<8;i++)
                    for(int j=0;j<8;j++) {
                        if (bit8x8[j+8*i] == 1)
                            g.setColor(Color.BLACK);
                        else
                            g.setColor(Color.WHITE);
                        g.fillRect(x*j/8,y*i/8,x*(j+1)/8,y*(i+1)/8);
                    }
            }
            else{
                g.setColor(Color.WHITE);
                g.fillRect(0,0,x,y);
            }
            g.setColor(Color.BLACK);
            g.drawRect(0,0,x-1,y-1);
            g.setColor(Color.green);
            for(int i=1;i<8;i++)
            {
                g.drawLine(x*i/8,0,x*i/8,y);
                g.drawLine(0,y*i/8,x,y*i/8);
            }
        }

        public void setBit8x8(int[] bit8x8) {
            this.bit8x8 = bit8x8;
            check=true;
        }
    }

    //Wykresy błędów sieci
    private class wykres extends JComponent {
        private ArrayList<Integer> dane=new ArrayList<>();
        String litera;

        public wykres(String litera) {
            this.litera=litera;
        }

        public void paint(Graphics g){
            int x=getWidth();
            int y=getHeight();
            g.drawLine(11,11,11,y-11);
            g.drawLine(11,y-11,x-11,y-11);
            g.drawString("Er",25,10);
            g.drawString("Ep",x-15,y-15);
            g.drawString(litera,x/2-5,10);
            for(int i=0;i<10;i++)
            {
                g.drawLine(11,(y-22)*i/10+11,21,(y-22)*i/10+11);
                g.drawString(String.valueOf(100*(10-i)/10),0,(y-22)*i/10+11);
            }
            int ile=dane.size();
            Object [] temp=dane.toArray();
            g.setColor(Color.red);
            for(int i=1;i<ile;i++)
            {
                try {
                    g.drawLine(x * (i - 1) / ile + 10, (y - 11) * (100 - (int) temp[i - 1]) / 100, x * (i) / ile + 10, (y - 11) * (100 - (int) temp[i]) / 100);
                }
                catch (Exception ex){}
            }
        }
        public void addElement(int dodaj){
            dane.add(dodaj);
        }

    }

    public panel(String string)
    {
        super(string);
        Toolkit kit= Toolkit.getDefaultToolkit();
        Dimension d=kit.getScreenSize();
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);
        this.setBounds(d.width/6,d.height/6,d.width*2/3,d.height*2/3);
        this.setSize(d.width*2/3,d.height*2/3);

//----------------------------Panele z Wykresami--------------------------//
        wykresy=new wykres[3];
        wykresy[0]=new wykres("V");
        wykresy[1]=new wykres("P");
        wykresy[2]=new wykres("R");
        wykresy[0].setBounds(getWidth()*2/5,getHeight()-2*getHeight()/3-60,(int)(getHeight()*0.3),(int)(getHeight()*0.3));
        wykresy[1].setBounds(getWidth()*2/5+getHeight()/3-5,getHeight()-2*getHeight()/3-60,(int)(getHeight()*0.3),(int)(getHeight()*0.3));
        wykresy[2].setBounds(getWidth()*2/5+getHeight()*2/3-10,getHeight()-2*getHeight()/3-60,(int)(getHeight()*0.3),(int)(getHeight()*0.3));
        add(wykresy[0]);
        add(wykresy[1]);
        add(wykresy[2]);
//-----------------------------------------------------------------------//

//--------------------------Panel do rysowania----------------------------//
        komponent=new MyComponent();
        komponent.setBounds(10,10,getWidth()*2/5-10,getHeight()-60);
        komponent.paint();
        add(komponent);
//-----------------------------------------------------------------------//

//-----------Panel z przedstawieniem graficznym wejścia------------------//
        img=new Img();
        img.setBounds(getWidth()*2/5+10,getHeight()-getHeight()/3-50,getHeight()/3,getHeight()/3);
        add(img);
//-----------------------------------------------------------------------//

//----------------------------Przyciski----------------------------------//
        JButton reset=new JButton("Wyczyść");
        reset.addActionListener(new Clean());
        reset.setBounds(getWidth()*2/5+10,10,getWidth()*1/7,25);
        add(reset);

        JButton test=new JButton("Sprawdź");
        test.addActionListener(new test());
        test.setBounds(getWidth()*2/5+10,100,getWidth()*1/7,25);
        add(test);

        JButton loadImg=new JButton("Ucz");
        loadImg.addActionListener(new load());
        loadImg.setBounds(getWidth()*2/5+getWidth()*1/7+10,10,getWidth()*1/7,25);
        add(loadImg);

        JButton CU=new JButton("CU");
        CU.addActionListener(new dodajDoCU());
        CU.setBounds(getWidth()*2/5+getWidth()*2/7+10,10,getWidth()*1/7,25);
        add(CU);

        JButton CT=new JButton("CT");
        CT.addActionListener(new dodajDoCT());
        CT.setBounds(getWidth()*2/5+getWidth()*3/7+10,10,getWidth()*1/7,25);
        add(CT);

//-------------------------------------------------------------------------//

//-------------------------------Radiobuttony------------------------------//
        lV=new JRadioButton("V");
        lP=new JRadioButton("P");
        lR=new JRadioButton("R");
        lF=new JRadioButton("Obcy");
        lV.addActionListener(new RadioV());
        lP.addActionListener(new RadioP());
        lR.addActionListener(new RadioR());
        lF.addActionListener(new RadioF());
        lV.setBounds(getWidth()-270,50,50,25);
        lP.setBounds(getWidth()-220,50,50,25);
        lR.setBounds(getWidth()-170,50,50,25);
        lF.setBounds(getWidth()-120,50,75,25);
        add(lV);
        add(lP);
        add(lR);
        add(lF);
//--------------------------------------------------------------------------//

// ----------------------------Wyniki --------------------------------------//
        wyjscie=new JLabel("Jaka litera?");
        poprawnosc=new JLabel("Poprawność?");
        ile=new JLabel[3];
        ile[0]=new JLabel("Podobieństwo V:");
        ile[1]=new JLabel("Podobieństwo P:");
        ile[2]=new JLabel("Podobieństwo R:");
        wyjscie.setBounds(getWidth()*2/5+50+getHeight()/3,getHeight()-getHeight()/3-50,300,40);
        poprawnosc.setBounds(getWidth()*2/5+getWidth()*1/7+10,35,300,65);
        ile[0].setBounds(getWidth()*2/5+50+getHeight()/3,getHeight()-getHeight()/3-20,300,40);
        ile[1].setBounds(getWidth()*2/5+50+getHeight()/3,getHeight()-getHeight()/3+10,300,40);
        ile[2].setBounds(getWidth()*2/5+50+getHeight()/3,getHeight()-getHeight()/3+40,300,40);
        add(wyjscie);
        add(poprawnosc);
        add(ile[0]);
        add(ile[1]);
        add(ile[2]);


//--------------------------------------------------------------------------//

//----------------------------------sie?------------------------------------//


        int [] tab=new int [3];
        tab[0]=20; tab[1]=15; tab[2]=3;
        siec=new Siec(64,3,tab);


//------------------------------------------------------------------------//
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        wykresy[0].paint(getGraphics());
        wykresy[1].paint(getGraphics());
        wykresy[2].paint(getGraphics());
    }//koniec konstuktora


    public static void main(String arg[])
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new panel("Projekt 1");
            }
        });
    }

    private class Clean implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            komponent.Clean();
        }
    }


    //Ważne-------------------------------------------------
    private class load implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Thread queryThread=new Thread(){
                public  void run(){
                    runQueries();
                }
            };
            queryThread.start();

        }

        private void runQueries() {
            //------------czytanie z pliku------------//
            File file;
            int[] wynik=new int[4];
            do {
                //Testowanie--------------------------------------------------------------------
                poprawneODP=0;
                zadaneP=0;
                for (int k = 0; k < 4; k++) {//ustawienie 4 testuje z nieznanymi elementami; ustawienie 3 testuje bez nieznanych elementów
                    if (k == 0)
                        file = new File("ciagTestowy_V.txt");
                    else if (k == 1)
                        file = new File("ciagTestowy_P.txt");
                    else if(k==2)
                        file = new File("ciagTestowy_R.txt");
                    else
                        file = new File("ciagTestowy_F.txt");
                    wynik[k] = 0;
                    try {
                        wynik[k] = spr(new Scanner(file), k);
                    } catch (FileNotFoundException ex) {
                        wyjscie.setText("Nie ma plików testowych");
                        break;
                    }

                }
                poprawnosc.setText("<html>Zadane pytania: "+zadaneP+"<br> Poprawne odpowiedzi: "+poprawneODP+"<br> Skutecznosc ok: "+ Math.round((double)poprawneODP/(double) zadaneP*10000)/100.0+"%<br> Epoki: "+epoka+"</html>");


                //Uczenie-----------------------------------------------------------------------
                for (int k = 0; k < 4; k++) {//ustawienie 4 uczy z nieznanymi elementami; ustawienie 3 uczy bez nieznanych element?w
                    if (k == 0)
                        file = new File("ciagUczacy_V.txt");
                    else if (k == 1)
                        file = new File("ciagUczacy_P.txt");
                    else if(k==2)
                        file = new File("ciagUczacy_R.txt");
                    else
                        file=new File("ciagUczacy_F.txt");

                    try {
                        wynik[k] = spr(new Scanner(file), k);//wyniki do wykres?w
                        ucz(new Scanner(file),k);
                    } catch (FileNotFoundException ex) {
                        wyjscie.setText("Nie ma plików uczących");
                    }
                    //Rysowanie wykresów-------------------------------------------
                    if(k<3){
                        wykresy[k].addElement(wynik[k]);
                        int finalK = k;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                wykresy[finalK].repaint();
                            }
                        });
                    }


                }
                siec.popraw();
                epoka++;
                //System.out.println(wynik[0]+"   "+wynik[1]+"   "+wynik[2]+"     "+wynik[3]);
            }while((wynik[0]>5 || wynik[1]>5 || wynik[2]>5 || wynik[3]>5) && epoka<10000);
            JOptionPane.showMessageDialog(null,"Nauka zakończona");

            //------------------------------------//
        }

        private int spr(Scanner in, int k) {
            double[] temp=new double[64];
            int wynik=0;
            int ile=0;
            while(in.hasNext()) {
                String[] zdanie = in.nextLine().split(";");
                for (int i = 0; i < 64; i++)
                    temp[i] = Double.parseDouble(zdanie[i]);

                double [] oWyjscie=siec.oblicz_wyjscie(temp);
                ile++;


                //Obliczanie b??du og?lnego

                //Sqrt((b??dW0^2+b??dW1^2+b??dW2^2)/3)*100
                if(k==0) {
                    wynik+=(int)((Math.sqrt(((1-oWyjscie[0])*(1-oWyjscie[0])+(0-oWyjscie[1])*(0-oWyjscie[1])+(0-oWyjscie[2])*(0-oWyjscie[2]))/3))*100);
                    if (oWyjscie[0] > 0.9 && oWyjscie[1] < 0.1 && oWyjscie[2] < 0.1)
                        poprawneODP++;
                }

                if(k==1)
                {
                    wynik+=(int)((Math.sqrt(((0-oWyjscie[0])*(0-oWyjscie[0])+(1-oWyjscie[1])*(1-oWyjscie[1])+(0-oWyjscie[2])*(0-oWyjscie[2]))/3))*100);
                    if(oWyjscie[0]<0.1 && oWyjscie[1]>0.9 && oWyjscie[2]<0.1)
                        poprawneODP++;
                }
                if(k==2)
                {
                    wynik+=(int)((Math.sqrt(((0-oWyjscie[0])*(0-oWyjscie[0])+(0-oWyjscie[1])*(0-oWyjscie[1])+(1-oWyjscie[2])*(1-oWyjscie[2]))/3))*100);
                    if(oWyjscie[0]<0.1 && oWyjscie[1]<0.1 && oWyjscie[2]>0.9)
                        poprawneODP++;
                }
                if(k==3) {
                    wynik+=(int)((Math.sqrt(((0-oWyjscie[0])*(0-oWyjscie[0])+(0-oWyjscie[1])*(0-oWyjscie[1])+(0-oWyjscie[2])*(0-oWyjscie[2]))/3))*100);
                    if(oWyjscie[0]<0.9 && oWyjscie[1]<0.9 && oWyjscie[2]<0.9)
                        poprawneODP++;
                }
                //System.out.println(oWyj?cie[0]+"    "+oWyj?cie[1]+"     "+oWyj?cie[2]);
                zadaneP++;
            }

            return wynik/ile;
        }

        private void ucz(Scanner in, int k){
            double[] temp=new double[64];
            while (in.hasNext()) {
                String[] zdanie = in.nextLine().split(";");
                for (int i = 0; i < 64; i++)
                    temp[i] = Double.parseDouble(zdanie[i]);


                //------------Uczenie------------//

                //wyliczanie b?edu dla neuron?w wyj?ciowych

                siec.liczDelta(siec.oblicz_wyjscie(temp),k);
                siec.liczPoprawe(temp,(epoka/100)+1);

                //------------------------------------//
            }
        }
    }
//-----------------------------------------
    private class dodajDoCU implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Path zapisz= Paths.get("ciagUczacy_" + zapis + ".txt");
                if(Files.notExists(zapisz))
                    Files.createFile(zapisz);
                int[] bit8x8=komponent.getBit8x8();
                byte[] bytes=Files.readAllBytes(zapisz);
                String content=new String(bytes, Charset.defaultCharset());
                for (int i=0;i<8;i++)
                {
                    for(int j=0;j<8;j++)
                        content+=bit8x8[j+i*8]+";";
                }
                content+="\n";
                Files.write(zapisz,content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    private class test implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int[] temp = komponent.getBit8x8();
                img.setBit8x8(temp);
                img.repaint();
                double[] tempD=new double[64];
                for (int i=0;i<64;i++)
                    tempD[i]=temp[i];
                double[] wynik = siec.oblicz_wyjscie(tempD);
                for (int i = 0; i < 3; i++) {
                    ile[i].setText("Podobieństwo " + ((i == 0) ? "V" : ((i == 1) ? "P" : "R")) + " ok: " + Math.round(wynik[i] * 100) / 100.0);
                }
                wyjscie.setText("Jest to litera " + ((wynik[0] > 0.65) ? "V" : ((wynik[1] > 0.65) ? "P" : ((wynik[2] > 0.65) ? "R" : "nie wiem jaka"))));
            }
            catch (Exception ex)
            {}

        }
    }

    private class dodajDoCT implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Path zapisz= Paths.get("ciagTestowy_" + zapis + ".txt");
                if(Files.notExists(zapisz))
                    Files.createFile(zapisz);
                int[] bit8x8=komponent.getBit8x8();
                byte[] bytes=Files.readAllBytes(zapisz);
                String content=new String(bytes, Charset.defaultCharset());
                for (int i=0;i<8;i++)
                {
                    for(int j=0;j<8;j++)
                        content+=bit8x8[j+8*i]+";";
                }
                content+="\n";
                Files.write(zapisz,content.getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class RadioV implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            zapis="V";
            lF.setSelected(false);
            lP.setSelected(false);
            lR.setSelected(false);
        }
    }
    private class RadioP implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            zapis="P";
            lF.setSelected(false);
            lV.setSelected(false);
            lR.setSelected(false);
        }
    }
    private class RadioR implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            zapis="R";
            lF.setSelected(false);
            lP.setSelected(false);
            lV.setSelected(false);
        }
    }

    private class RadioF implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            zapis="F";
            lV.setSelected(false);
            lP.setSelected(false);
            lR.setSelected(false);

        }
    }
}
