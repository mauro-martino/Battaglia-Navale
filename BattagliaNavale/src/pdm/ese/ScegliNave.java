package pdm.ese;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ScegliNave extends Activity {
    /** Called when the activity is first created. */
	//matrice della posizione delle navi
	char A[][] = new char[10][10];
	//matrice delle possibili posizioni
	char B[][] = new char[10][10];
	char E[] = new char[100];
	GridView grid;
    int i;            //coordinata delle righe
    int j;            //coordinata delle colonne
    int passo = 0;    //indice del passo da eseguire 0= scegli punto iniziale 1=scegli direzione
    int indice = 0;   //indice della nave da inserire sulla griglia
    Boolean thereIsRoom;  //booleano necessario per capire se la nave, dalla posizione selezionata, nonesca dalla griglia 
    Boolean nord;         //booleano che memorizza al passo 0 se la nave può essere depositata verso nord
    Boolean sud;          //booleano che memorizza al passo 0 se la nave può essere depositata verso sud
    Boolean est;          //booleano che memorizza al passo 0 se la nave può essere depositata verso est
    Boolean ovest;        //booleano che memorizza al passo 0 se la nave può essere depositata verso ovest
    Ship[] ships = new Ship[] { //crea il vettore che contiene le navi da posizionare
             new Ship(4),
             new Ship(3),
             new Ship(3),
             new Ship(2),
             new Ship(2),
             new Ship(2),
             new Ship(1),
             new Ship(1),
             new Ship(1),
             new Ship(1),
             new Ship(20)//dummy ship
             };
    EditText user;  //stringa contenente il nome utente
    EditText pass;  //stringa contenente la password
    EditText utente;//stringa contenente il nome dell'avversario
    Boolean logged = false;
    Intent intent;  //l'intent per andare alla seconda activity
    Context contesto;
    Ship ship;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!logged) {  //se non si è eseguita la fase di login al server, falla eseguire
        	setContentView(R.layout.login);  //imposta la view del login
        }
        contesto = this;
        user = (EditText)findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.pass);
        utente = (EditText)findViewById(R.id.utente);
        intent = new Intent(ScegliNave.this, Gioco.class);  //l'intent per passare alla fase di gioco
        
        
        Button next = (Button)findViewById(R.id.button1);   //bottone di ok per il login
        next.setOnClickListener(new OnClickListener() {     //listener sul click del bottone
        	
        	public void onClick(View v) {
        		String userstr = user.getText().toString();
        		String passstr = pass.getText().toString();
        		String utentestr = utente.getText().toString();
        		intent.putExtra("user",userstr);
        		intent.putExtra("pass",passstr);
        		intent.putExtra("utente",utentestr);           //prende i dati e li manda nella prossima activity
        		setContentView(R.layout.main);                 //passa alla view di posizionamento delle navi
        		grid = (GridView)findViewById(R.id.gridView1); //prende il riferimento alla gridview
            	grid.setAdapter(new Griglia(contesto));        //setta l'adapter customizzato per la griglia
            	grid.setOnItemClickListener(itemClickListener);//il listener per i click sulla griglia
        		logged = true;                                 //terminata la fase di login
        	}
        });
        //matrice A tiene memoria della posizione delle navi
        for (i=0; i < 10; i++){
        	for (j=0; j < 10; j++){
        		A[i][j]='-';                  //matrice di memoria della posizione delle navi inizialmente vuota
        		B[i][j]='-';                  //matrice di controllo del posizionamento inizialmente vuota
        	}
        }
    };
    private OnItemClickListener itemClickListener = new OnItemClickListener() {  //click sulla griglia
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //passo = 0 -> primo click passo = 1 -> secondo click
        //colonne matrice
        i = position % 10;
        //righe matrice
        j = (int) Math.floor(position/10);
        Log.d("TAG","Position Clicked ["+position+"]");
        Log.d("TAG","Position i ["+i+"]");
        Log.d("TAG","Position j ["+j+"]");
        ship = ships[indice];
        thereIsRoom = true;
        switch (passo){
        case 0:  //primo click
        	//in A N=Nave  in B Z = possibile posizione nave N = Nord S=Sud E=Est O=Ovest X=posizione occupata
        	if (B[j][i] != 'X') {  //se la posizione è libera
        		if (j - (ship.getSize()-1) < 0) { //la nave esce fuori dalla griglia a nord
            		thereIsRoom = false;          //non si può posizionare la nave
            		nord = false;}                //a Nord
            	else {
            		for (int  z = j - (ship.getSize() - 1); z <= j - 1 && thereIsRoom; z++) { //se per tutte le caselle a nord c'è spazio si può posizionare la nave
                        thereIsRoom = thereIsRoom & (B[z][i] == '-'); //si somma il valore attuale di thereIsRoom con il booleano associato alla casella libera
                        if (thereIsRoom) {
                        	B[j][i] = 'Z';
                        	nord = true;}     //la nave può essere posizionata a nord
                        else {
                        	nord = false;     //la nave non può essere posizionata a nord
                        }
                    }
            	}
            	thereIsRoom = true;       //resetta la variabile thereIsRoom a true
            	if (i + (ship.getSize()-1) >= 10) {//la nave esce fuori dalla griglia a ovest
            		thereIsRoom = false;
            		ovest = false;}
            	else {
            		for (int  z = i + (ship.getSize() - 1); z >= i + 1 && thereIsRoom; z--) {//se per tutte le caselle a ovest c'è spazio si può posizionare la nave
                        thereIsRoom = thereIsRoom & (B[j][z] == '-'); //si somma il valore attuale di thereIsRoom con il booleano associato alla casella libera
                        if (thereIsRoom) {
                        	B[j][i] = 'Z';     //Z è un char di appoggio
                        	ovest = true;}     //la nave può essere posizionata a ovest
                        else {
                        	ovest = false;     //la nave non può essere posizionata a ovest
                        }
                    }
            	}
            	thereIsRoom = true;        //resetta la variabile thereIsRoom a true
            	if (j + (ship.getSize()-1) >= 10) {//la nave esce fuori dalla griglia a sud
            		thereIsRoom = false;
            		sud = false;}
            	else {
            		for (int  z = j + (ship.getSize() - 1); z >= j + 1 && thereIsRoom; z--) {
                        thereIsRoom = thereIsRoom & (B[z][i] == '-'); //si somma il valore attuale di thereIsRoom con il booleano associato alla casella libera
                        if (thereIsRoom) {
                        	B[j][i] = 'Z';
                        	sud = true;}
                        else {
                        	sud = false;
                        }
                    }
            	}
            	thereIsRoom = true;       //resetta la variabile thereIsRoom a true
            	if (i - (ship.getSize()-1) < 0) {//la nave esce fuori dalla griglia a est
            		thereIsRoom = false;
            		est = false;}
            	else {
            		for (int  z = i - (ship.getSize() - 1); z <= i - 1 && thereIsRoom; z++) {
                        thereIsRoom = thereIsRoom & (B[j][z] == '-'); //si somma il valore attuale di thereIsRoom con il booleano associato alla casella libera
                        if (thereIsRoom) {
                        	B[j][i] = 'Z';
                        	est = true;}
                        else {
                        	est = false;
                        }
                    }
            	}
    	        if (B[j][i] == 'Z') {
    	        	if (nord) {//disegno possibile posizione nave a nord
    	        		for (int r = 1; r < ship.getSize(); r++) {
                    		B[j-r][i] = 'X';}
    	        		B[j-1][i] = 'N';
    	        	}
    	        	if (sud) {//disegno possibile posizione nave a sud
    	        		for (int r = 1; r < ship.getSize(); r++) {
                    		B[j+r][i] = 'X';}
    	        		B[j+1][i] = 'S';
    	        	}
    	        	if (est) {//disegno possibile posizione nave a est
    	        		for (int r = 1; r < ship.getSize(); r++) {
                    		B[j][i-r] = 'X';}
    	        		B[j][i-1] = 'E';
    	        	}
    	        	if (ovest) {//disegno possibile posizione nave a ovest
    	        		for (int r = 1; r < ship.getSize(); r++) {
                    		B[j][i+r] = 'X';}
    	        		B[j][i+1] = 'O';
    	        	}
                	B[j][i] = 'X';
    	        	A[j][i] = 'N';
    	        	passo+=1;}
    	        else {
    	        	if (ship.getSize() == 1) {  //elimina le posizioni intorno alla nave da 1
    	        		if (B[j][i] == '-') {   //se il punto cliccato è vuoto
    	        			A[j][i] = 'N';      //posiziona la nave nel punto cliccato
    	        			B[j][i] = 'X';      //invalida la posizione cliccata
    	        			if (j != 9){ //se la riga non è l'ultima
    	        				B[j+1][i] = 'X'; //invalida la posizione sotto la riga cliccata
    	        			}
    	        			if (i != 9){ //se la colonna non è l'ultima
    	        				B[j][i+1] = 'X'; //invalida la posizione accanto alla colonna cliccata
    	        			}
    	        			if (j != 0){ //se la riga non è la prima
    	        				B[j-1][i] = 'X'; //invalida la posizione sopra la riga cliccata
    	        			}
    	        			if (i != 0){ //se la colonna non è la prima
    	        				B[j][i-1] = 'X'; //invalida la posizione accanto alla colonna cliccata
    	        			}
    	        			if ((j != 9) & (i != 9)){ //se non mi trovo in basso a destra della griglia
    	        				B[j+1][i+1] = 'X';  //invalida la posizione in diagonale basso a destra
    	        			}
    	        			if ((j != 9) & (i != 0)){ //se non mi trovo in basso a sinistra della griglia
    	        				B[j+1][i-1] = 'X';  //invalida la posizione in diagonale basso a sinistra
    	        			}
    	        			if ((j != 0) & (i != 9)){ //se non mi trovo in alto a destra della griglia
    	        				B[j-1][i+1] = 'X';  //invalida la posizione in diagonale alto a destra
    	        			}
    	        			if ((j != 0) & (i != 0)){ //se non mi trovo in alto a sinistra della griglia
    	        				B[j-1][i-1] = 'X';  //invalida la posizione in diagonale alto a sinistra
    	        			}
    	        		}
    	        	}
    	        	else {// se non si può piazzare la nave in nessuna posizione avverti l'utente con un toast
    	        		Toast.makeText(getApplicationContext(), "Non hai cliccato una posizione valida", Toast.LENGTH_LONG).show();
    	        	}
    	        }
        	}
    	    else {// se la posizione è già occupata avverti l'utente con un toast
    	    	Toast.makeText(getApplicationContext(), "Non hai cliccato una posizione valida", Toast.LENGTH_LONG).show();
    	    }
	        if (ship.getSize() == 1) {  //se la dimensione della nave è 1
        		passo = 0;  //la prossima nave sicuramente partirà dal passo 0
        		if (indice < 10) {//inserite tutte le navi cambia activity
        			indice += 1;} //altrimenti passa alla nave successiva
        		else {
                	for (int n = 0; n < 10; n++) {   //converte la matrice A nel vettore E da passare nella prossima activity
            			for (int m = 0; m < 10; m++) {
            				E[(n*10)+m] = A[n][m];
            			}
            		}
            		intent.putExtra("Matrice Navi", E); //inserisce nell'intent il vettore E
            		startActivity(intent);              //fa partire l'activity gioco
        		}
        	}
	        break;
        case 1:                  //scegli la direzione della nave
        	switch (B[j][i]) {
        	case 'N':            //disponi a nord
        		indice +=1;      //passa alla nave successiva
        		passo = 0;       //ritorna allo stato 0
        		for (int r = 0; r < (ship.getSize()-1); r++) {
            		A[j-r][i] = 'N';
            		B[j-r][i] = 'X';
            		if (sud) {//Elimino possibile piazzamento nave a sud
            			B[(j+1)+(r+1)][i] = '-';
            		}
            		if (est) {//Elimino possibile piazzamento nave a est
            			B[j+1][i-(r+1)] = '-';
            		}
            		if (ovest) {//Elimino possibile piazzamento nave a ovest
            			B[j+1][i+(r+1)] = '-';
            		}
            		if (i != 0) {//Elimino posizioni a sinistra della nave
            			B[(j+1)-r][i-1] = 'X';
            			B[(j+1)-(r+1)][i-1] = 'X';//risparmio un ciclo for lungo r+1
            		}
            		if (i != 9) {//Elimino posizioni a destra della nave
            			B[(j+1)-r][i+1] = 'X';
            			B[(j+1)-(r+1)][i+1] = 'X';//risparmio un ciclo for lungo r+1
            		}
            	}
        		if (j-(ship.getSize()-1) >= 0) {//Elimino posizioni a sopra alla nave
        			B[j-(ship.getSize()-1)][i] = 'X';
        			if (i != 0) {//Elimino posizioni angolo in alto a sinistra della nave
            			B[j-(ship.getSize()-1)][i-1] = 'X';
            		}
            		if (i != 9) {//Elimino posizioni angolo in alto destra della nave
            			B[j-(ship.getSize()-1)][i+1] = 'X';
            		}
        		}
        		if (j+2 <= 9) {//Elimino posizioni a sotto alla nave
        			B[j+2][i] = 'X';
        			if (i != 0) {//Elimino posizioni angolo in basso a sinistra della nave
            			B[j+2][i-1] = 'X';
            		}
            		if (i != 9) {//Elimino posizioni angolo in basso destra della nave
            			B[j+2][i+1] = 'X';
            		}
        		}
        		break;
        	case 'S':
        		indice +=1;
        		passo = 0;
        		for (int r = 0; r < (ship.getSize()-1); r++) {
            		A[j+r][i] = 'N';
            		B[j+r][i] = 'X';
            		if (nord) {//Elimino possibile piazzamento nave a sud
            			B[(j-1)-(r+1)][i] = '-';
            		}
            		if (est) {//Elimino possibile piazzamento nave a est
            			B[j-1][i-(r+1)] = '-';
            		}
            		if (ovest) {//Elimino possibile piazzamento nave a ovest
            			B[j-1][i+(r+1)] = '-';
            		}
            		if (i != 0) {//Elimino posizioni a sinistra della nave
            			B[(j-1)+r][i-1] = 'X';
            			B[(j-1)+(r+1)][i-1] = 'X';//risparmio un ciclo for lungo r+1
            		}
            		if (i != 9) {//Elimino posizioni a destra della nave
            			B[(j-1)+r][i+1] = 'X';
            			B[(j-1)+(r+1)][i+1] = 'X';//risparmio un ciclo for lungo r+1
            		}
            	}
        		if (j+(ship.getSize()-1) <= 9) {//Elimino posizioni a sotto alla nave
        			B[j+(ship.getSize()-1)][i] = 'X';
        			if (i != 0) {//Elimino posizioni angolo in basso a sinistra della nave
            			B[j+(ship.getSize()-1)][i-1] = 'X';
            		}
            		if (i != 9) {//Elimino posizioni angolo in basso destra della nave
            			B[j+(ship.getSize()-1)][i+1] = 'X';
            		}
        		}
        		if (j-2 >= 0) {//Elimino posizioni a sopra alla nave
        			B[j-2][i] = 'X';
        			if (i != 0) {//Elimino posizioni angolo in alto a sinistra della nave
            			B[j-2][i-1] = 'X';
            		}
            		if (i != 9) {//Elimino posizioni angolo in alto destra della nave
            			B[j-2][i+1] = 'X';
            		}
        		}
        		break;
        	case 'E':
        		indice +=1;
        		passo = 0;
        		for (int r = 0; r < (ship.getSize()-1); r++) {
            		A[j][i-r] = 'N';
            		B[j][i-r] = 'X';
            		if (sud) {//Elimino possibile piazzamento nave a sud
            			B[j+(r+1)][i+1] = '-';
            		}
            		if (nord) {//Elimino possibile piazzamento nave a nord
            			B[j-(r+1)][i+1] = '-';
            		}
            		if (ovest) {//Elimino possibile piazzamento nave a ovest
            			B[j][(i+1)+(r+1)] = '-';
            		}
            		if (j != 0) {//Elimino posizioni sopra alla nave
            			B[j-1][(i+1)-r] = 'X';
            			B[j-1][(i+1)-(r+1)] = 'X';//risparmio un ciclo for lungo r+1
            		}
            		if (j != 9) {//Elimino posizioni sotto alla nave
            			B[(j+1)][(i+1)-r] = 'X';
            			B[(j+1)][(i+1)-(r+1)] = 'X';//risparmio un ciclo for lungo r+1
            		}
            	}
        		if (i-(ship.getSize()-1) >= 0) {//Elimino posizioni a sinistra della nave
        			B[j][i-(ship.getSize()-1)] = 'X';
        			if (j != 0) {//Elimino posizioni angolo in alto a sinistra della nave
            			B[j-1][i-(ship.getSize()-1)] = 'X';
            		}
            		if (j != 9) {//Elimino posizioni angolo in alto destra della nave
            			B[j+1][i-(ship.getSize()-1)] = 'X';
            		}
        		}
        		if (i+2 <= 9) {//Elimino posizioni a destra della nave
        			B[j][i+2] = 'X';
        			if (j != 0) {//Elimino posizioni angolo in basso a sinistra della nave
            			B[j-1][i+2] = 'X';
            		}
            		if (j != 9) {//Elimino posizioni angolo in basso destra della nave
            			B[j+1][i+2] = 'X';
            		}
        		}
        		break;
        	case 'O':
        		indice +=1;
        		passo = 0;
        		for (int r = 0; r < (ship.getSize()-1); r++) {
            		A[j][i+r] = 'N';
            		B[j][i+r] = 'X';
            		if (sud) {//Elimino possibile piazzamento nave a sud
            			B[j+(r+1)][i-1] = '-';
            		}
            		if (nord) {//Elimino possibile piazzamento nave a nord
            			B[j-(r+1)][i-1] = '-';
            		}
            		if (est) {//Elimino possibile piazzamento nave a ovest
            			B[j][(i-1)-(r+1)] = '-';
            		}
            		if (j != 0) {//Elimino posizioni sopra alla nave
            			B[j-1][(i-1)+r] = 'X';
            			B[j-1][(i-1)+(r+1)] = 'X';//risparmio un ciclo for lungo r+1
            		}
            		if (j != 9) {//Elimino posizioni sotto alla nave
            			B[(j+1)][(i-1)+r] = 'X';
            			B[(j+1)][(i-1)+(r+1)] = 'X';//risparmio un ciclo for lungo r+1
            		}
            	}
        		if (i+(ship.getSize()-1) <= 9) {//Elimino posizioni a sinistra della nave
        			B[j][i+(ship.getSize()-1)] = 'X';
        			if (j != 0) {//Elimino posizioni angolo in alto a sinistra della nave
            			B[j-1][i+(ship.getSize()-1)] = 'X';
            		}
            		if (j != 9) {//Elimino posizioni angolo in alto destra della nave
            			B[j+1][i+(ship.getSize()-1)] = 'X';
            		}
        		}
        		if (i-2 >= 0) {//Elimino posizioni a destra della nave
        			B[j][i-2] = 'X';
        			if (j != 0) {//Elimino posizioni angolo in basso a sinistra della nave
            			B[j-1][i-2] = 'X';
            		}
            		if (j != 9) {//Elimino posizioni angolo in basso destra della nave
            			B[j+1][i-2] = 'X';
            		}
        		}
        		break;
        	default:
        		Toast.makeText(getApplicationContext(), "Non hai cliccato una posizione valida", Toast.LENGTH_LONG).show();
        		break;
        	}
        	break;
        }
        grid.invalidateViews();  //aggiorna la view della griglia, funziona solo in un click item
        }
    };
    public class Griglia extends BaseAdapter{
    	private Context mContext;

        public Griglia(Context c) {
            mContext = c;
        }

        public int getCount() {
            return 100;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(40, 40));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(0, 0, 0, 0);}
            else{
                imageView = (ImageView) convertView;}
            int a = (int) Math.floor(position/10);
            int b = (int) position % 10;
			if (A[a][b] == 'N'){  //Se è presente una nave disegnala altrimenti disegna acqua o la situazione di B
		       	imageView.setImageResource(grafica[2]);}
			else {
				switch  (B[a][b]) {
				case 'N':  //grafica corrispondente al nord
					imageView.setImageResource(grafica[0]);
					break;
				case 'S': //grafica corrispondente al sud
					imageView.setImageResource(grafica[0]);
					break;
				case 'E': //grafica corrispondente al est
					imageView.setImageResource(grafica[0]);
					break;
				case 'O': //grafica corrispondente al ovest
					imageView.setImageResource(grafica[0]);
					break;
				case 'X': //grafica corrispondente ad un punto invalidato
					imageView.setImageResource(grafica[3]);
					break;
				default: //grafica di default(in realtà non serve)
					imageView.setImageResource(grafica[1]);
					break;
				}
			}
            return imageView;
        }
        
        // references to our images
        private Integer[] grafica = {
                R.drawable.vuoto, R.drawable.animated_sea,
                R.drawable.nave, R.drawable.colpito
        };
        
    }
    public void printBoard() {  //unicamente necessario in fase di debug per far disegnare nel logcat la matrice B
        for (int i = 0; i < 10; i++)
            System.out.println(Arrays.toString(B[i]));
        	//System.out.println(Arrays.toString(A[i]));
    }
}