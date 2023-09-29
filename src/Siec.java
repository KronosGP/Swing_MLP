
public class Siec {
	Warstwa [] warstwy;
	int liczba_warstw;
	
	public Siec(){
		warstwy=null;
		this.liczba_warstw=0;
	}
	public Siec(int liczba_wejsc,int liczba_warstw,int [] lnww){
		this.liczba_warstw=liczba_warstw;
		warstwy=new Warstwa[liczba_warstw];
		for(int i=0;i<liczba_warstw;i++)
			warstwy[i]=new Warstwa((i==0)?liczba_wejsc:lnww[i-1],lnww[i]);
	}
	double [] oblicz_wyjscie(double [] wejscia){
		double [] wyjscie=null;
		for(int i=0;i<liczba_warstw;i++)
			wejscia = wyjscie = warstwy[i].oblicz_wyjscie(wejscia);
		return wyjscie;
	}


	//-----------------funkcje dadane----------------------//

	//funkcja wyliczająca błąd
	public void liczDelta(double[] wynik,int r){
		double [] delta=new double[3];
		delta[0]=((r==0)?1:0)-wynik[0];
		delta[1]=((r==1)?1:0)-wynik[1];
		delta[2]=((r==2)?1:0)-wynik[2];

		for(int i=liczba_warstw-1;i>=0;i--)//wyliczenie błędów dla wszystkich neuronów w warstwach 2 1 0
		{
			int liczba_wejsc=warstwy[i].neurony[0].getWejscia();
			double [] sumaDelt= new double[liczba_wejsc];
			//sumaDelt-przechowuje sumy delt dla kolejnych neuronów
			for (int j=0;j<liczba_wejsc;j++){//
				sumaDelt[j]=0;
			}

			for(int j=0;j<warstwy[i].liczba_neuronow;j++)
			{
				warstwy[i].neurony[j].setDelta(delta[j]);//ustawianie delty dla neuronów poszczególnych warstw
				for (int k=0;k<liczba_wejsc;k++)
				{
					sumaDelt[k]+=(warstwy[i].neurony[j].wagi[k+1]*delta[j]);//obliczanie błędów dla neuronów poprzedniej warstwy
				}

			}
			delta=sumaDelt;
		}

	}

	//funkcja licząca poprawe
	public void liczPoprawe(double[] wejscia,int e) {
		for(int i=0;i<liczba_warstw;i++){//wyliczanie popraw dla warstwy 0 1 2
			double delta;
			double pochodna;
			double eta;
			/*if(e<=9)
				eta=0.1-(e/100.0);
			else
				eta=0.01;*/
			eta=0.1;
			//System.out.println(eta);
			int liczbaNeuronow=warstwy[i].liczba_neuronow;
			double [] poprawka;
			double [] wyjscie=new double[liczbaNeuronow];

			for(int j=0;j<liczbaNeuronow;j++) {
				delta = warstwy[i].neurony[j].delta;
				poprawka = warstwy[i].neurony[j].poprawka;
				pochodna = warstwy[i].neurony[j].pochodna;
				poprawka[0] += eta * delta * pochodna * 1;
				for (int k = 1; k < warstwy[i].neurony[j].getWejscia()+1; k++) {
					poprawka[k] += eta * delta * pochodna * wejscia[k-1];//w'=w+WU*D*P*WE
				}
				warstwy[i].neurony[j].setPoprawka(poprawka);
				wyjscie[j] = warstwy[i].neurony[j].wynik;
			}
			wejscia=wyjscie;
		}
		/*
		w'-poprawiona waga
		w- waga aktualna
		WU-współczynnik uczenia
		D-delta
		P-pochodna
		WE-wejścia dla wagi
		 */
	}

	//funkcja zatwierdzająca poprawy wag dla wszystkich warstw
	public void popraw() {
		for (int i=0;i<liczba_warstw;i++)
		{
			int liczba_neuronow=warstwy[i].liczba_neuronow;
			for (int j=0;j<liczba_neuronow;j++) {
				double wagi[]=warstwy[i].neurony[j].wagi;
				double poprawka[]=warstwy[i].neurony[j].poprawka;
				for (int k = 0; k < wagi.length; k++) {
					wagi[k] += poprawka[k];
				}
				for (int k = 0; k < poprawka.length; k++) {
					poprawka[k] = 0;
				}
				warstwy[i].neurony[j].delta=0;
				warstwy[i].neurony[j].setWagi(wagi);
			}
		}
	}


	//------------------------------------------------------//
}
