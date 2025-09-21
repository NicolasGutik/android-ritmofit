package com.uade.exercises.home.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.uade.exercises.R;
import com.uade.exercises.home.model.PokemonDetail;
import com.uade.exercises.home.service.PokemonService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PokemonDetailFragment extends Fragment {

    @Inject
    PokemonService pokemonService;

    private TextView nameTextView;
    private TextView orderTextView;
    private ProgressBar loadingProgressBar;
    private int pokemonId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pokemonId = getArguments().getInt("pokemonId", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_pokemon_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTextView = view.findViewById(R.id.pokemon_name_text_view);
        orderTextView = view.findViewById(R.id.pokemon_order_text_view);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);

        loadPokemonDetail();
    }

    private void loadPokemonDetail() {
        loadingProgressBar.setVisibility(View.VISIBLE);

        pokemonService.getPokemonDetail(pokemonId, new PokemonService.PokemonDetailCallback() {
            @Override
            public void onSuccess(PokemonDetail pokemonDetail) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        nameTextView.setText(pokemonDetail.getFormattedName());
                        orderTextView.setText(String.valueOf(pokemonDetail.getOrder()));
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
}
