package pdm.ese;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
    int i;
    int j;
    int passo = 0;
    int indice = 0;
    Boolean thereIsRoom;
    Boolean nord;
    Boolean sud;
    Boolean est;
    Boolean ovest;
    Ship[] ships = new Ship[] {
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
    Ship ship;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        grid = (GridView)findViewById(R.id.gridView1);
        grid.setAdapter(new Griglia(this));
        //matrice A tiene memoria della posizione delle navi
        for (i=0; i < 10; i++){
        	for (j=0; j < 10; j++){
        		A[i][j]='-';
        		B[i][j]='-';
        	}
        }
        //inserire nave da 4
        grid.setOnItemClickListener(itemClickListener);
        
    };
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //passo = 0 -> primo click passo = 1 -> secondo click ecc.
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
        	//in A N=Nave   in B S = Nave N = Nord S=Sud E=Est O=Ovest
        	if (B[j][i] != 'X') {
        		if (j - (ship.getSize()-1) < 0) {//Nord
            		thereIsRoom = false;
            		nord = false;}
            	else {
            		for (int  z = j - (ship.getSize() - 1); z <= j - 1 && thereIsRoom; z++) {
                        thereIsRoom = thereIsRoom & (B[z][i] == '-');
                        if (thereIsRoom) {
                        	B[j][i] = 'X';
                        	nord = true;}
                        else {
                        	nord = false;
                        }
                    }
            	}
            	thereIsRoom = true;
            	if (i + (ship.getSize()-1) >= 10) {//Ovest
            		thereIsRoom = false;
            		ovest = false;}
            	else {
            		for (int  z = i + (ship.getSize() - 1); z >= i + 1 && thereIsRoom; z--) {
                        thereIsRoom = thereIsRoom & (B[j][z] == '-');
                        if (thereIsRoom) {
                        	B[j][i] = 'Z';
                        	ovest = true;}
                        else {
                        	ovest = false;
                        }
                    }
            	}
            	thereIsRoom = true;
            	if (j + (ship.getSize()-1) >= 10) {//Sud
            		thereIsRoom = false;
            		sud = false;}
            	else {
            		for (int  z = j + (ship.getSize() - 1); z >= j + 1 && thereIsRoom; z--) {
                        thereIsRoom = thereIsRoom & (B[z][i] == '-');
                        if (thereIsRoom) {
                        	B[j][i] = 'Z';
                        	sud = true;}
                        else {
                        	sud = false;
                        }
                    }
            	}
            	thereIsRoom = true;
            	if (i - (ship.getSize()-1) < 0) {//Est
            		thereIsRoom = false;
            		est = false;}
            	else {
            		for (int  z = i - (ship.getSize() - 1); z <= i - 1 && thereIsRoom; z++) {
                        thereIsRoom = thereIsRoom & (B[j][z] == '-');
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
    	        	if (ship.getSize() == 1) {
    	        		if (B[j][i] == '-') {	
    	        			A[j][i] = 'N';
    	        			B[j][i] = 'X';
    	        			if (j != 9){
    	        				B[j+1][i] = 'X';
    	        			}
    	        			if (i != 9){
    	        				B[j][i+1] = 'X';
    	        			}
    	        			if (j != 0){
    	        				B[j-1][i] = 'X';
    	        			}
    	        			if (i != 0){
    	        				B[j][i-1] = 'X';
    	        			}
    	        			if ((j != 9) & (i != 9)){
    	        				B[j+1][i+1] = 'X';
    	        			}
    	        			if ((j != 9) & (i != 0)){
    	        				B[j+1][i-1] = 'X';
    	        			}
    	        			if ((j != 0) & (i != 9)){
    	        				B[j-1][i+1] = 'X';
    	        			}
    	        			if ((j != 0) & (i != 0)){
    	        				B[j-1][i-1] = 'X';
    	        			}
    	        		}
    	        	}
    	        	else {
    	        		Toast.makeText(getApplicationContext(), "Non hai cliccato una posizione valida", Toast.LENGTH_LONG).show();
    	        	}
    	        }
        	}
    	    else {
    	    	Toast.makeText(getApplicationContext(), "Non hai cliccato una posizione valida", Toast.LENGTH_LONG).show();
    	    }
	        if (ship.getSize() == 1) {
        		
        		passo = 0;
        		if (indice < 9) {//inserite tutte le navi cambia activity
        			indice += 1;}
        		else {
        			Intent intent = new Intent(ScegliNave.this, Second.class);
                	for (int n = 0; n < 10; n++) {
            			for (int m = 0; m < 10; m++) {
            				E[(n*10)+m] = A[n][m];
            			}
            		}
            		intent.putExtra("Matrice Navi", E);
            		startActivity(intent);
        		}
        	}
	        printBoard();
	        break;
        case 1:
        	switch (B[j][i]) {
        	case 'N':
        		indice +=1;
        		passo = 0;
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
        grid.invalidateViews();
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
			if (A[a][b] == 'N'){  //Se è presente una nave disegnala altrimenti disegna acqua
		       	imageView.setImageResource(grafica[2]);}
			else {
				switch  (B[a][b]) {
				case 'N':
					imageView.setImageResource(grafica[0]);
					break;
				case 'S':
					imageView.setImageResource(grafica[0]);
					break;
				case 'E':
					imageView.setImageResource(grafica[0]);
					break;
				case 'O':
					imageView.setImageResource(grafica[0]);
					break;
				case 'X':
					imageView.setImageResource(grafica[3]);
					break;
				default:
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
    public void printBoard() {
        for (int i = 0; i < 10; i++)
            System.out.println(Arrays.toString(B[i]));
        	//System.out.println(Arrays.toString(A[i]));
    }
}