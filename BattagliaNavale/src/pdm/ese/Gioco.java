package pdm.ese;

import java.util.Random;
import java.util.Timer;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Gioco extends Activity{
	GridView difesa;       //grigla per la difesa
	GridView attacco;      //grigla per l'attacco
	Connection connection; //dati di connessione
	String user;           //dati di connessione username
	String pass;           //dati di connessione password
	String utente;         //dati di connessione utente a cui connettersi
	String stato = "READY";  //stato iniziale
	char A[][] = new char[10][10];  //matrice di difesa
	char B[][] = new char[10][10];  //matrice di attacco
	String cella;
	String condizione;
	int j;           //coordinata j
	int i;           //coordinata i
	int wpixel;      //larghezza dello schemo in pixel
	int cellagriglia;       //dimensione della cella della griglia adattabile su ogni schermo
	int dimensionegriglia;  //dimensione della griglia adattabile su ogni schermo
	int number;    //numero random generato internamente
	int rnumber;   //primo numero random ricevuto
	int renumber;  //ripescaggio numero random
	GrigliaDifesa difadapt;
	GrigliaAttacco attadapt;
	TextView text;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gioco);  //setta il layout definito in gioco.xml
		wpixel = this.getResources().getDisplayMetrics().widthPixels;  //ritorda la larghezza dello schermo in pixel
		dimensionegriglia = (wpixel*7/10)-20;
		cellagriglia = (dimensionegriglia-20)/10;
		text = (TextView)findViewById(R.id.textView1);  //riferimento alla textview a destra delle griglie
		try { // tentativo di connessione
        	ConnectionConfiguration config = new ConnectionConfiguration("ppl.eln.uniroma2.it", 5222);
        	config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        	connection = new XMPPConnection(config);
        	connection.connect();
        	user = getIntent().getExtras().getString("user");
        	pass = getIntent().getExtras().getString("pass");
        	utente = getIntent().getExtras().getString("utente");
        	connection.login(user, pass);
        } catch (XMPPException e) {
        	e.printStackTrace();
        }
		difesa = (GridView)findViewById(R.id.gridView1);  //Riferimento alla grid view definita in gioco.xml
		attacco = (GridView)findViewById(R.id.gridView2); //Riferimento alla grid view definita in gioco.xml
		LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout1); //Riferimento al linear layout contente le griglie definita in gioco.xml
		ll.setLayoutParams(new LinearLayout.LayoutParams(dimensionegriglia,LinearLayout.LayoutParams.FILL_PARENT)); //setta la dimensione del linear layout per contenere le grigle in modo preciso
		char E[] = getIntent().getExtras().getCharArray("Matrice Navi"); //recupera dall'activity precedente il vettore delle navi E[]
		for (int b = 0; b < 10; b++) { // riempie la matrice di difesa e la matrice di attacco
			for (int a = 0; a < 10; a++) {
				A[b][a] = E[(b*10)+a]; //dati provenienti dall'activity ScegliNave
				B[b][a] = '0';         //riempie di spazi vuoti
			}
		}
		difadapt = new GrigliaDifesa(this);
		attadapt = new GrigliaAttacco(this);
        difesa.setAdapter(difadapt);    //imposta l'adapter della griglia di difesa
        attacco.setAdapter(attadapt);  //imposta l'adapter della griglia di attacco
        
        attacco.setOnItemClickListener(itemClickListener);  //imposta un listener su l'item della griglia di attacco
        if (stato.equals("READY")) {   //stato READY
        	Log.d("Btl",stato);
        	Message msg = new Message();
    		msg.setTo(utente+"@ppl.eln.uniroma2.it");
    		msg.setBody("READY");
			connection.sendPacket(msg);
			stato = "CHOSETURN";
        }
        connection.addPacketListener(new PacketListener() { //listener sui pacchetti in arrivo
			@Override
			public void processPacket(Packet pkt) {
				Message msg = (Message) pkt;
				String body = msg.getBody();
				Log.d("Btl","messaggio arrivato "+body);
				String delims = "[;]";    //imposta il delimitatore dei campi del messaggio ricevuto
				String[] rerandom = body.split(delims); //divide il messaggio ricevuto in campi all'interno di un vettore
				String reran = rerandom[0];  //header del messaggio
				if (reran.equals("RERANDOM")) {  //se arriva un messaggio RERANDOM (stato sempre accessibile)
					Log.d("Btl","Ricevuto il numero RERANDOM");
					Message msg1 = new Message();
			    	msg1.setTo(utente+"@ppl.eln.uniroma2.it");
					msg1.setBody("RANDOM;"+number);
					renumber = Integer.parseInt(rerandom[1]);
					if (renumber < number) {
						stato = "DIFESA";  //se il numero dell'avversario è minore del proprio si va in DIFESA
						Log.d("Btl",stato);
					}
					else {                 //se il numero dell'avversario è maggiore del proprio si va in ATC
						stato = "ATC";
						Log.d("Btl",stato);
					}
				}
				if (stato == "ATCWAIT") { //In fase di attacco si attende la ricezione del messaggio dell'avversario
					Log.d("Btl",stato);
					String[] atcmsgsplit = body.split(delims);
					String header = atcmsgsplit[0];
					if (header.equals("DIF")) {  //header del messaggio deve essere DIF
						Log.d("Btl","header msg difensore corretto");
						cella = atcmsgsplit[1];                 //valore della cella dell'avversario appena attaccata
						j = Integer.parseInt(atcmsgsplit[2]);   //un campo del messaggio è la j attaccata
						i = Integer.parseInt(atcmsgsplit[3]);   //un campo del messaggio è la i attaccata
						Log.d("Btl","la j ricevuta è "+j);
						Log.d("Btl","la i ricevuta è "+i);
						Log.d("Btl","stato cella avversario "+cella);
						if (cella.charAt(0) == '-') {  //se la cella dell'avversario è vuota
							B[j][i] = '.'; //sostituisce nella propria matrice di attacco lo stato della cella avversaria
						}
						else { //se nella cella dell'avversario c'è una nave
							B[j][i] = 'X'; //sostituisce nella propria matrice di attacco lo stato della cella avversaria
						}
					    stato = "ATCCONCLUSO";  //passa allo stato ATCONCLUSO in automatico
					    Log.d("Btl",stato);
					    Message msg2 = new Message();
			    		msg2.setTo(utente+"@ppl.eln.uniroma2.it");
						msg2.setBody("ENDTURN"); //invia il messaggio di ENDTURN
						connection.sendPacket(msg2);
						stato = "DIFESA";  //cambia lo stato in difesa
					}
				}
				if (stato == "DIFESA") {  //se si difende arriva il pacchetto con le coordinate attaccate
					Log.d("Btl",stato);
					String[] difmsgsplit = body.split(delims);
					String header = difmsgsplit[0];
					if (header.equals("ATC")) {  //l'header del pacchetto deve essere ATC
						Log.d("Btl","header pacchetto è ATC");
						j = Integer.parseInt(difmsgsplit[1]);  //ricevo la coordinata j attaccata 
						i = Integer.parseInt(difmsgsplit[2]);  //ricevo la coordinata i attaccata
						Log.d("Btl","la j ricevuta è"+j);
						Log.d("Btl","la i ricevuta è"+i);
						stato = "DIFESADONE";   //passa allo stato DIFESADONE in automatico
						Log.d("Btl",stato);
						Message msg3 = new Message();
				    	msg3.setTo(utente+"@ppl.eln.uniroma2.it");
				    	String valore = String.valueOf(A[j][i]); //prende il valore della cella nella propria matrice delle navi
						msg3.setBody("DIF;"+valore+";"+j+";"+i); //manda un messaggio di ack con header DIF valore della cella e coordinate attaccate
						if (A[j][i] == 'N') { //se nel punto attaccato c'è una nave
							A[j][i] = 'X';  //segnala che è stata colpita
						}
						else {
							A[j][i] = '.';  //altrimenti segnala che l'avversario è andato a vuoto
						}
						connection.sendPacket(msg3);
						stato = "DIFESAWAIT";  //passa allo stato DIFESAWAIT in automatico
					}
				}
				if (stato == "DIFESAWAIT") {  //Fase finale della difesa in attesa del messaggio ENDTURN dell'avversario
					Log.d("Btl",stato);
					String[] difmsgsplit = body.split(delims);
					String end = difmsgsplit[0];
					if (end.equals("ENDTURN")) { //l'header è corretto
						Log.d("Btl","Ricevuto messaggio di fine turno");
						stato = "ATC"; //si passa dalla difesa all'attacco
					}
				}
				if (stato == "CHOSETURN") { //in questo stato si crea ed invia il numero random per decidere chi inizia la partita
					String[] ready = body.split(delims);
					Log.d("Btl",ready[0]);
					String header = ready[0];
					Random rdn = new Random();
					number = rdn.nextInt();    //creazione numero random
					Message msg1 = new Message();
			    	msg1.setTo(utente+"@ppl.eln.uniroma2.it");
					msg1.setBody("RANDOM;"+number);  //invio del numero random
					connection.sendPacket(msg1);
					if (header.equals("READY")) {  //se anche l'avversario è pronto
						Log.d("Btl","ricevuto messaggio READY");
		    			stato = "WAITRANDOM";  //si passa allo stato WAITRANDOM
					}
				}
				if (stato == "WAITRANDOM") {  //in questo stato si attende il numero random dell'avversario
					String[] rrandom = body.split(delims);
					Log.d("Btl",rrandom[0]);
					String header = rrandom[0];
					if (header.equals("RANDOM")) {  //se il messaggio ha l'header RANDOM
						Log.d("Btl","arrivato messaggio RANDOM");
						rnumber = Integer.parseInt(rrandom[1]);  //intero random generato dall'avversario
						if (rnumber < number) {   //se il numero ricevuto è minore di quello generato
							stato = "DIFESA";    //si va in fase di difesa
							Log.d("Btl",stato);
						}
						else {    //se il numero ricevuto è maggiore di quello generato
							stato = "ATC";    //si va in fase di attacco
							Log.d("Btl",stato);
						}
					}
					else {  //se non arriva un messagio con header corretto vai nello stato RERANDOM
						Message msg1 = new Message();
						msg1.setTo(utente+"@ppl.eln.uniroma2.it");
						msg1.setBody("RERANDOM;"+number);
						connection.sendPacket(msg1);
					}
				}
			}
		}, new MessageTypeFilter(Message.Type.normal));
//		if (stato == "ATCDONE")  {
//			Message msg = new Message();
//    		msg.setTo(utente+"@ppl.eln.uniroma2.it");
//			msg.setBody(j+","+i);
//			connection.sendPacket(msg);
//			Log.d("Btl",stato);
//			stato = "ATCWAIT";
//		}
//		if (stato == "ATCCONCLUSO") {
//			Message msg = new Message();
//    		msg.setTo(utente+"@ppl.eln.uniroma2.it");
//			msg.setBody("ENDTURN");
//			connection.sendPacket(msg);
//			Log.d("Btl",stato);
//			stato = "DIFESA";
//		}
//		if (stato == "DIFESADONE") {
//			Log.d("Btl",stato);
//			Message msg = new Message();
//    		msg.setTo(utente+"@ppl.eln.uniroma2.it");
//    		String valore = String.valueOf(A[j][i]);
//			msg.setBody(valore);
//			difesa.invalidateViews();
//			connection.sendPacket(msg);
//			stato = "DIFESAWAIT";
//		}
	}
	public OnItemClickListener itemClickListener = new OnItemClickListener() {  // se viene cliccato un elemento della griglia
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        	if (stato == "ATC") {  //se sei nello stato ATC di attacco
        		i = position % 10;  //converte l'indice del vettore nella coordinata i della matrice
        		//righe matrice
        		j = (int) Math.floor(position/10); //converte l'indice del vettore nella coordinata j della matrice
        		Log.d("Btl",stato);
        		stato = "ATCDONE";  //passaggio automatico nello stato ATCDONE
        		Message msg = new Message();
        		msg.setTo(utente+"@ppl.eln.uniroma2.it");
        		msg.setBody("ATC;"+j+";"+i);  //messaggio d'attacco header ATC con appese le coordinate attaccate
    			connection.sendPacket(msg);
    			Log.d("Btl",stato);
    			stato = "ATCWAIT";  //dopo l'invio del messaggio di attacco si va nello stato ATCWAIT
        	}
        	else {  //se non si è in fase di attacco compare un toast con lo stato attuale
        		Toast.makeText(getApplicationContext(), "Sei in fase di "+stato+", attendi il tuo turno", Toast.LENGTH_LONG).show();
        	}
        }
	};
	public class GrigliaDifesa extends BaseAdapter{
    	private Context mContext;

        public GrigliaDifesa(Context c) {
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
                imageView.setLayoutParams(new GridView.LayoutParams(cellagriglia, cellagriglia));
                imageView.setScaleType(ImageView.ScaleType.FIT_END);
                imageView.setPadding(0, 1, 0, 1);}
            else{
                imageView = (ImageView) convertView;}
            int a = (int) Math.floor(position/10);
            int b = (int) position % 10;
            switch  (A[a][b]) {
			case 'N':
				imageView.setBackgroundResource(grafica[2]);
				break;
			case '.':
				imageView.setBackgroundResource(grafica[0]);
				break;
			case 'X':
				imageView.setBackgroundResource(grafica[3]);
				break;
			default:
				imageView.setBackgroundResource(grafica[1]);
				break;
			}
            return imageView;
        }
        
        // references to our images
        private Integer[] grafica = {
                R.drawable.vuoto, R.drawable.animated_sea,
                R.drawable.nave, R.drawable.colpito
        };
        
    }
	public class GrigliaAttacco extends BaseAdapter{   //custom adapter per la griglia di attacco
    	private Context mContext;

        public GrigliaAttacco(Context c) {
            mContext = c;
        }

        public int getCount() { //ritorna il numero di elementi totali in griglia
            return 100;
        }

        public Object getItem(int position) {//non utilizzato per cui assegnato a null
            return null;
        }

        public long getItemId(int position) {//non utilizzato per cui assegnato a null
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // se non è già stata utilizzata definisci alcuni attributi
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(cellagriglia, cellagriglia)); //setta la dimensione in base alla dimensione dello schermo
                imageView.setScaleType(ImageView.ScaleType.FIT_END);  //tipo di scaling sull'imamgine
                imageView.setPadding(0, 1, 0, 1);}  //spaziatura tra una immagine e l'altra nella griglia
            else{//già inizializzata
                imageView = (ImageView) convertView;}
            int a = (int) Math.floor(position/10);
            int b = (int) position % 10;
            switch  (B[a][b]) { //la matrice B è quella di attacco
			case '0':  //casella non controllata (default)
				imageView.setBackgroundResource(grafica[1]);
				break;
			case '.':  //casella attaccata ma vuota
				imageView.setBackgroundResource(grafica[0]);
				break;
			case 'X':  //casella attaccata e colpita
				imageView.setBackgroundResource(grafica[3]);
				break;
			default:
				imageView.setBackgroundResource(grafica[1]);
				break;
			}
            return imageView; //ritorna la view
        }
        
        // referenze alle immagini
        private Integer[] grafica = {
                R.drawable.vuoto, R.drawable.animated_sea,
                R.drawable.nave, R.drawable.colpito
        };
        
    }

}
