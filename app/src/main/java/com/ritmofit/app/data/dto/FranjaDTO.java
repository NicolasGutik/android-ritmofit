package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;

/** El backend espera strings "HH:mm" (ej: "12:00") */
public class FranjaDTO {
    @SerializedName("horaInicial")
    private String horaInicial;

    @SerializedName("horaFinal")
    private String horaFinal;

    public String getHoraInicial() { return horaInicial; }
    public void setHoraInicial(String horaInicial) { this.horaInicial = horaInicial; }

    public String getHoraFinal() { return horaFinal; }
    public void setHoraFinal(String horaFinal) { this.horaFinal = horaFinal; }
}
