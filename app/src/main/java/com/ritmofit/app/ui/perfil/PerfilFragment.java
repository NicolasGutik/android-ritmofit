package com.ritmofit.app.ui.perfil;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.ritmofit.app.R;
import com.ritmofit.app.auth.ui.LoginActivity;
import com.ritmofit.app.data.dto.ApiResult;
import com.ritmofit.app.data.dto.FiltroClaseUserDTO;
import com.ritmofit.app.data.dto.FranjaDTO;
import com.ritmofit.app.data.dto.UserDTO;
import com.ritmofit.app.data.repository.AuthRepository;
import com.ritmofit.app.data.repository.PreferenciasRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PerfilFragment extends Fragment {

    @Inject AuthRepository authRepository;

    private ShapeableImageView ivFoto;
    private TextInputEditText etEmail, etFirst, etLast, etTel;
    private TextView tvError;
    private ProgressBar progress;
    private MaterialButton btnGuardarPerfil, btnGuardarPref, btnCambiarFoto, btnLogout;

    private UserDTO currentUser;
    private String fotoBase64Seleccionada;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    /*===========datos para el filtro===========*/
    @Inject
    PreferenciasRepository preferenciasRepository;

    private TextInputEditText etSede, etDisc, etHoraDesde, etHoraHasta;
    private ChipGroup chipsSedes, chipsDisciplinas;
    private MaterialButton btnAddSede, btnAddDisc, btnAddFranja;
    private RecyclerView rvFranjas;
    private FranjasAdapter franjasAdapter;
    private final ArrayList<String> sedes = new ArrayList<>();
    private final ArrayList<String> disciplinas = new ArrayList<>();
    private final ArrayList<FranjaDTO> franjas = new ArrayList<>();
    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

        etSede = v.findViewById(R.id.et_sede);
        etDisc = v.findViewById(R.id.et_disc);
        etHoraDesde = v.findViewById(R.id.et_hora_desde);
        etHoraHasta = v.findViewById(R.id.et_hora_hasta);
        btnAddSede = v.findViewById(R.id.btn_add_sede);
        btnAddDisc = v.findViewById(R.id.btn_add_disc);
        btnAddFranja = v.findViewById(R.id.btn_add_franja);
        chipsSedes = v.findViewById(R.id.chips_sedes);
        chipsDisciplinas = v.findViewById(R.id.chips_disc);
        rvFranjas = v.findViewById(R.id.rv_franjas);

        rvFranjas.setLayoutManager(new LinearLayoutManager(requireContext()));
        franjasAdapter = new FranjasAdapter(franjas, pos -> { franjas.remove(pos); franjasAdapter.notifyItemRemoved(pos); });
        rvFranjas.setAdapter(franjasAdapter);
        rvFranjas.setNestedScrollingEnabled(false);

        // pickers HH:mm
        etHoraDesde.setOnClickListener(_v -> pickTime(etHoraDesde));
        etHoraHasta.setOnClickListener(_v -> pickTime(etHoraHasta));

        // acciones agregar
        btnAddSede.setOnClickListener(_v -> addChipFromText(etSede, sedes, chipsSedes));
        btnAddDisc.setOnClickListener(_v -> addChipFromText(etDisc, disciplinas, chipsDisciplinas));
        btnAddFranja.setOnClickListener(_v -> addFranja());

        ivFoto = v.findViewById(R.id.iv_foto);
        etEmail = v.findViewById(R.id.et_email);
        etFirst = v.findViewById(R.id.et_first);
        etLast = v.findViewById(R.id.et_last);
        etTel = v.findViewById(R.id.et_tel);
        tvError = v.findViewById(R.id.tv_error);
        progress = v.findViewById(R.id.progress);
        btnGuardarPerfil = v.findViewById(R.id.btn_guardar_perfil);
        btnGuardarPref   = v.findViewById(R.id.btn_guardar_pref);
        btnCambiarFoto   = v.findViewById(R.id.btn_cambiar_foto);
        btnLogout        = v.findViewById(R.id.btn_logout);

        btnCambiarFoto.setOnClickListener(_v -> pickImage.launch("image/*"));
        btnGuardarPerfil.setOnClickListener(_v -> guardarPerfil());
        btnGuardarPref.setOnClickListener(_v -> guardarPreferencias());
        btnLogout.setOnClickListener(_v -> logout());

        cargarUsuario();
        return v;
    }

    private void pickTime(TextInputEditText target) {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int h = now.get(java.util.Calendar.HOUR_OF_DAY);
        int m = now.get(java.util.Calendar.MINUTE);
        new android.app.TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            LocalTime t = LocalTime.of(hourOfDay, minute);
            target.setText(t.format(HHMM));
        }, h, m, true).show();
    }

    private void addChipFromText(TextInputEditText et, ArrayList<String> list, ChipGroup group) {
        String val = textOf(et);
        if (val.isEmpty()) return;
        if (list.contains(val)) {
            Toast.makeText(requireContext(), "Ya agregado", Toast.LENGTH_SHORT).show();
            return;
        }
        list.add(val);

        Chip chip = new Chip(requireContext());
        chip.setText(val);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(true);
        // Opcionales de estilo
        chip.setEnsureMinTouchTargetSize(true);
        chip.setChipIconVisible(false);

        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            list.remove(val);
        });

        group.addView(chip);
        et.setText("");
    }

    private void addFranja() {
        String d = textOf(etHoraDesde);
        String h = textOf(etHoraHasta);
        if (!d.matches("^([01]\\d|2[0-3]):[0-5]\\d$") || !h.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            showError("Formato de hora inválido (HH:mm)");
            return;
        }
        if (LocalTime.parse(d, HHMM).isAfter(LocalTime.parse(h, HHMM))) {
            showError("La hora 'Desde' debe ser anterior a 'Hasta'");
            return;
        }
        FranjaDTO f = new FranjaDTO(); f.setHoraInicial(d); f.setHoraFinal(h);
        franjas.add(f);
        franjasAdapter.notifyItemInserted(franjas.size()-1);
        etHoraDesde.setText(""); etHoraHasta.setText("");
    }

    private FiltroClaseUserDTO buildFiltro() {
        FiltroClaseUserDTO dto = new FiltroClaseUserDTO();
        dto.setSedes(new ArrayList<>(sedes));
        dto.setDisciplinas(new ArrayList<>(disciplinas));
        dto.setFranjas(new ArrayList<>(franjas));
        return dto;
    }

    private void cargarUsuario() {
        showLoading(true);
        authRepository.obtenerUsuarioActual().observe(getViewLifecycleOwner(), result -> {
            showLoading(false);
            if (result instanceof ApiResult.Success) {
                currentUser = ((ApiResult.Success<UserDTO>) result).getData();
                bindUsuario(currentUser);
                if (currentUser != null && currentUser.getId() != null) {
                    cargarPreferencias(currentUser.getId());
                }
            } else if (result instanceof ApiResult.Error) {
                showError(((ApiResult.Error<UserDTO>) result).getMessage());
            }
        });
    }

    private void cargarPreferencias(Long userId) {
        preferenciasRepository.getPreferencias(userId).observe(getViewLifecycleOwner(), r -> {
            if (r instanceof ApiResult.Success) {
                FiltroClaseUserDTO f = ((ApiResult.Success<FiltroClaseUserDTO>) r).getData();
                // limpiar UI actual
                chipsSedes.removeAllViews(); sedes.clear();
                chipsDisciplinas.removeAllViews(); disciplinas.clear();
                franjas.clear(); franjasAdapter.notifyDataSetChanged();

                if (f != null) {
                    if (f.getSedes() != null) for (String s : f.getSedes()) {
                        etSede.setText(s); addChipFromText(etSede, sedes, chipsSedes);
                    }
                    if (f.getDisciplinas() != null) for (String d : f.getDisciplinas()) {
                        etDisc.setText(d); addChipFromText(etDisc, disciplinas, chipsDisciplinas);
                    }
                    if (f.getFranjas() != null) {
                        franjas.addAll(f.getFranjas());
                        franjasAdapter.notifyDataSetChanged();
                    }
                }
            } else if (r instanceof ApiResult.Error) {
                // si 404 o vacío, simplemente no mostramos nada
            }
        });
    }

    private void bindUsuario(UserDTO u) {
        if (u == null) return;
        safeSetText(etEmail, u.getEmail());
        safeSetText(etFirst, u.getFirstName());
        safeSetText(etLast, u.getLastName());
        safeSetText(etTel, u.getTelefono());

        if (u.getFoto() != null && !u.getFoto().isEmpty()) {
            if (u.getFoto().startsWith("http")) {
                // Glide/Picasso recomendado
                // Glide.with(this).load(u.getFoto()).placeholder(R.drawable.ic_person_24).into(ivFoto);
            } else {
                try {
                    byte[] bytes = Base64.decode(u.getFoto(), Base64.DEFAULT);
                    android.graphics.Bitmap bm = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ivFoto.setImageBitmap(bm);
                } catch (Exception ignored) {}
            }
        }
    }

    private void guardarPerfil() {
        if (currentUser == null || currentUser.getId() == null) {
            showError("Usuario no cargado.");
            return;
        }

        String email = textOf(etEmail);
        String first = textOf(etFirst);
        String last  = textOf(etLast);
        String tel   = textOf(etTel);

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { showError("Email inválido."); return; }
        if (first.isEmpty()) { showError("El nombre no puede estar vacío."); return; }
        if (last.isEmpty())  { showError("El apellido no puede estar vacío."); return; }

        UserDTO updated = new UserDTO();
        updated.setId(currentUser.getId());
        updated.setEmail(email);
        updated.setFirstName(first);
        updated.setLastName(last);
        updated.setTelefono(tel);
        updated.setFoto(fotoBase64Seleccionada != null ? fotoBase64Seleccionada : currentUser.getFoto());

        showLoading(true);
        authRepository.actualizarUsuario(currentUser.getId(), updated)
                .observe(getViewLifecycleOwner(), (ApiResult<String> res) -> {
                    showLoading(false);
                    if (res instanceof ApiResult.Success) {
                        Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        authRepository.guardarUsuario(updated);
                        if (!email.equalsIgnoreCase(currentUser.getEmail())) {
                            Toast.makeText(requireContext(),
                                    "Tu email fue actualizado. El próximo OTP llegará a " + email,
                                    Toast.LENGTH_LONG).show();
                        }
                        currentUser = updated;
                    } else if (res instanceof ApiResult.Error) {
                        showError(((ApiResult.Error<String>) res).getMessage());
                    }
                });
    }

    private void guardarPreferencias() {
        if (currentUser == null || currentUser.getId() == null) {
            showError("Usuario no cargado.");
            return;
        }
        // Validar franjas
        for (FranjaDTO f : franjas) {
            if (f.getHoraInicial() == null || f.getHoraFinal() == null ||
                    !f.getHoraInicial().matches("^([01]\\d|2[0-3]):[0-5]\\d$") ||
                    !f.getHoraFinal().matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
                showError("Formato de hora inválido (HH:mm) en una franja");
                return;
            }
            LocalTime d = LocalTime.parse(f.getHoraInicial(), HHMM);
            LocalTime h = LocalTime.parse(f.getHoraFinal(), HHMM);
            if (d.isAfter(h)) { showError("Hay una franja con 'Desde' > 'Hasta'"); return; }
        }

        FiltroClaseUserDTO filtro = buildFiltro();

        showLoading(true);
        preferenciasRepository.upsertPreferencias(currentUser.getId(), filtro)
                .observe(getViewLifecycleOwner(), r -> {
                    showLoading(false);
                    if (r instanceof ApiResult.Success) {
                        Toast.makeText(requireContext(), "Preferencias guardadas", Toast.LENGTH_SHORT).show();
                    } else if (r instanceof ApiResult.Error) {
                        showError("No se pudieron guardar las preferencias");
                    }
                });
    }

    private void logout() {
        authRepository.cerrarSesion();
        // Volver a Login y limpiar back stack
        startActivity(new android.content.Intent(requireContext(), LoginActivity.class)
                .setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void onImagePicked(@Nullable Uri uri) {
        if (uri == null) return;
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), uri));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            }
            ivFoto.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();
            fotoBase64Seleccionada = Base64.encodeToString(bytes, Base64.NO_WRAP);

        } catch (IOException e) {
            showError("No se pudo cargar la imagen");
        }
    }

    private @Nullable String base64UrlDecode(String s) {
        try {
            String pad = new String(new char[(4 - s.length() % 4) % 4]).replace("\0", "=");
            byte[] data = android.util.Base64.decode(s.replace('-', '+').replace('_', '/') + pad, Base64.DEFAULT);
            return new String(data, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) { return null; }
    }

    private @Nullable Long extractUserIdFromJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            String payloadJson = base64UrlDecode(parts[1]);
            org.json.JSONObject obj = new org.json.JSONObject(payloadJson);
            // Ajustá el nombre del claim: "id", "userId", o "sub"
            if (obj.has("id")) return obj.getLong("id");
            if (obj.has("userId")) return obj.getLong("userId");
            if (obj.has("sub")) return Long.valueOf(obj.getString("sub")); // si es numérico
        } catch (Exception ignored) {}
        return null;
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        if (btnGuardarPerfil != null) btnGuardarPerfil.setEnabled(!show);
        if (btnGuardarPref   != null) btnGuardarPref.setEnabled(!show);
        btnCambiarFoto.setEnabled(!show);
        btnLogout.setEnabled(!show);
        etEmail.setEnabled(!show ? true : etEmail.isEnabled()); // mantener editable
        etFirst.setEnabled(!show);
        etLast.setEnabled(!show);
        etTel.setEnabled(!show);
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void safeSetText(TextInputEditText et, String value) {
        et.setText(value == null ? "" : value);
    }

    private String textOf(TextInputEditText et) {
        return Objects.toString(et.getText(), "").trim();
    }
}


