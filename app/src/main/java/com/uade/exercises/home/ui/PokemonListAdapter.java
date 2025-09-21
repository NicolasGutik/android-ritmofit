package com.uade.exercises.home.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uade.exercises.R;
import com.uade.exercises.home.model.Pokemon;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PokemonListAdapter extends BaseAdapter {

    private List<Pokemon> pokemonList;
    private final LayoutInflater inflater;

    public PokemonListAdapter(Context context) {
        this.pokemonList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void setPokemonList(List<Pokemon> pokemonList) {
        this.pokemonList = pokemonList;
    }

    @Override
    public int getCount() {
        return pokemonList.size();
    }

    @Override
    public Pokemon getItem(int position) {
        return pokemonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.home_item_pokemon, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.pokemon_name_text_view);
        TextView idTextView = convertView.findViewById(R.id.pokemon_id_text_view);

        Pokemon pokemon = getItem(position);
        nameTextView.setText(pokemon.getFormattedName());
        idTextView.setText(MessageFormat.format("#{0}", pokemon.getId()));
        
        return convertView;
    }
}
