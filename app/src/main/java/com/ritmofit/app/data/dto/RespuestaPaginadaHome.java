package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RespuestaPaginadaHome<T> {
    @SerializedName("contenido") private List<T> contenido;

    @SerializedName("pagina") private int pagina;
    @SerializedName("tamanio") private int tamanio;
    @SerializedName("totalElementos") private long totalElementos;
    @SerializedName("totalPaginas") private int totalPaginas;

    @SerializedName("primera") private boolean primera;
    @SerializedName("ultima") private boolean ultima;
    @SerializedName("tieneSiguiente") private boolean tieneSiguiente;
    @SerializedName("tieneAnterior") private boolean tieneAnterior;

    public List<T> getContenido() { return contenido; }
    public int getPagina() { return pagina; }
    public int getTamanio() { return tamanio; }
    public long getTotalElementos() { return totalElementos; }
    public int getTotalPaginas() { return totalPaginas; }
    public boolean isPrimera() { return primera; }
    public boolean isUltima() { return ultima; }
    public boolean isTieneSiguiente() { return tieneSiguiente; }
    public boolean isTieneAnterior() { return tieneAnterior; }
}
