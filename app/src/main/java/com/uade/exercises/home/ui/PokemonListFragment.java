package com.uade.exercises.home.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.uade.exercises.R;
import com.uade.exercises.auth.repository.TokenRepository;
import com.uade.exercises.home.model.Pokemon;
import com.uade.exercises.home.service.PokemonService;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokemonListFragment extends Fragment {

    @Inject
    PokemonService pokemonService;
    
    @Inject
    TokenRepository tokenRepository;
    
    private ProgressBar loadingProgressBar;
    private PokemonListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_pokemon_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView pokemonListView = view.findViewById(R.id.pokemon_list_view);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);
        TextView titleTextView = view.findViewById(R.id.title_text_view);
        Button showTokenButton = view.findViewById(R.id.show_token_button);

        titleTextView.setText("Pokemon list");
        
        showTokenButton.setOnClickListener(v -> showTokenDialog());

        adapter = new PokemonListAdapter(requireContext());
        pokemonListView.setAdapter(adapter);

        pokemonListView.setOnItemClickListener((parent, itemView, position, id) -> {
            Pokemon pokemon = adapter.getItem(position);
            navigateToPokemonDetail(pokemon.getId());
        });

        loadPokemonList();
    }

    private void loadPokemonList() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        
        pokemonService.getPokemonList(new PokemonService.PokemonListCallback() {
            @Override
            public void onSuccess(List<Pokemon> pokemonList) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        adapter.setPokemonList(pokemonList);
                        adapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void navigateToPokemonDetail(int pokemonId) {
        Bundle args = new Bundle();
        args.putInt("pokemonId", pokemonId);
        Navigation.findNavController(requireView()).navigate(R.id.action_pokemonListFragment_to_pokemonDetailFragment, args);
    }
    

    private void showTokenDialog() {
        String token = tokenRepository.getToken();
        String message = token != null ? 
                "Token: " + token : 
                "No hay token almacenado";
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Token de Autenticación")
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
