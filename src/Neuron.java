import java.util.Random;


public class Neuron {
	double [] wagi;
	int liczba_wejsc;
	//dodane--------//
	double wynik;
	double pochodna;
	double[] poprawka;
	double delta=0;
	//--------------//

	public Neuron(){
		liczba_wejsc=0;
		wagi=null;
	}
	public Neuron(int liczba_wejsc){
		this.liczba_wejsc=liczba_wejsc;
		wagi=new double[liczba_wejsc+1];
		poprawka=new double[wagi.length];
		generuj();
	}
	private void generuj() {
		Random r=new Random();
		for(int i=0;i<=liczba_wejsc;i++) {
			//wagi[i]=(r.nextDouble()-0.5)*2.0*10;//do ogladania
			wagi[i]=(r.nextDouble()-0.5)*2.0*0.01;//do projektu
			poprawka[i]=0;
			 }
	}
	public double oblicz_wyjscie(double [] wejscia){
		double fi=wagi[0];
		//double fi=0.0;
		for(int i=1;i<=liczba_wejsc;i++)
			fi+=wagi[i]*wejscia[i-1];
		double wynik=1.0/(1.0+Math.exp(-fi));// funkcja aktywacji sigma -unip
		//double wynik=(fi>0.0)?1.0:0.0;//skok jednostkowy
		//double wynik=fi; //f.a. liniowa
		this.wynik=wynik;
		pochodna=wynik*(1-wynik);
		return wynik;
	}


	//-----------------funkcje dodane -----------------//

	public void setDelta(double delta){this.delta=delta;}
	public void setPoprawka(double [] poprawka){this.poprawka=poprawka;}
	public void setWagi(double[] wagi) {this.wagi=wagi;}

	//podanie ilości wejść (potrzebne do wylicznia błędów dla poprzedniej warstwy)
	public int getWejscia() {
		return liczba_wejsc;
	}
	//------------------------------------------------------//
}
