package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FiltroClaseUserDTO {
    @SerializedName("disciplinas")
    private List<String> disciplinas;

    @SerializedName("sedes")
    private List<String> sedes;

    @SerializedName("franjas")
    private List<FranjaDTO> franjas;

    public List<String> getDisciplinas() { return disciplinas; }
    public void setDisciplinas(List<String> disciplinas) { this.disciplinas = disciplinas; }

    public List<String> getSedes() { return sedes; }
    public void setSedes(List<String> sedes) { this.sedes = sedes; }

    public List<FranjaDTO> getFranjas() { return franjas; }
    public void setFranjas(List<FranjaDTO> franjas) { this.franjas = franjas; }
}
