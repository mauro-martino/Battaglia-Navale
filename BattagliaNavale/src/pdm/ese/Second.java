package pdm.ese;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class Second extends Activity{
	GridView grid;
	char A[][] = new char[10][10];
	char B[][] = new char[10][10];
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		grid = (GridView)findViewById(R.id.gridView1);
		char E[] = getIntent().getExtras().getCharArray("Matrice Navi");
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 10; i++) {
				A[j][i] = E[(j*10)+i];
				B[j][i] = '-';
			}
		}
        grid.setAdapter(new Griglia(this));
	}
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
                imageView.setScaleType(ImageView.ScaleType.FIT_END);
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

}
