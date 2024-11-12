package es.ifp.gestorpersonal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RutinaAdapter extends ArrayAdapter<Rutina> {
    public RutinaAdapter(Context context, List<Rutina> rutinas) {
        super(context, 0, rutinas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_rutina, parent, false);
        }

        Rutina rutina = getItem(position);
        TextView nombreRutina = convertView.findViewById(R.id.textViewItemRutina);

        nombreRutina.setText(rutina.getNombre());

        return convertView;
    }
}