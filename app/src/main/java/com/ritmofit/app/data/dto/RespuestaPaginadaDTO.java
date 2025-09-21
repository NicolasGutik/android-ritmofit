package com.ritmofit.app.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RespuestaPaginadaDTO<T> {
    @SerializedName("contenido")
    private List<T> contenido;
    
    @SerializedName("pagina")
    private Integer pagina;
    
    @SerializedName("tamanio")
    private Integer tamanio;
    
    @SerializedName("totalElementos")
    private Long totalElementos;
    
    @SerializedName("totalPaginas")
    private Integer totalPaginas;
    
    @SerializedName("primera")
    private Boolean primera;
    
    @SerializedName("ultima")
    private Boolean ultima;
    
    @SerializedName("tieneSiguiente")
    private Boolean tieneSiguiente;
    
    @SerializedName("tieneAnterior")
    private Boolean tieneAnterior;
    
    // Constructores
    public RespuestaPaginadaDTO() {}
    
    // Getters y Setters
    public List<T> getContenido() { return contenido; }
    public void setContenido(List<T> contenido) { this.contenido = contenido; }
    
    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }
    
    public Integer getTamanio() { return tamanio; }
    public void setTamanio(Integer tamanio) { this.tamanio = tamanio; }
    
    public Long getTotalElementos() { return totalElementos; }
    public void setTotalElementos(Long totalElementos) { this.totalElementos = totalElementos; }
    
    public Integer getTotalPaginas() { return totalPaginas; }
    public void setTotalPaginas(Integer totalPaginas) { this.totalPaginas = totalPaginas; }
    
    public Boolean getPrimera() { return primera; }
    public void setPrimera(Boolean primera) { this.primera = primera; }
    
    public Boolean getUltima() { return ultima; }
    public void setUltima(Boolean ultima) { this.ultima = ultima; }
    
    public Boolean getTieneSiguiente() { return tieneSiguiente; }
    public void setTieneSiguiente(Boolean tieneSiguiente) { this.tieneSiguiente = tieneSiguiente; }
    
    public Boolean getTieneAnterior() { return tieneAnterior; }
    public void setTieneAnterior(Boolean tieneAnterior) { this.tieneAnterior = tieneAnterior; }
    
    @Override
    public String toString() {
        return "RespuestaPaginadaDTO{" +
                "contenido=" + contenido +
                ", pagina=" + pagina +
                ", tamanio=" + tamanio +
                ", totalElementos=" + totalElementos +
                ", totalPaginas=" + totalPaginas +
                ", primera=" + primera +
                ", ultima=" + ultima +
                ", tieneSiguiente=" + tieneSiguiente +
                ", tieneAnterior=" + tieneAnterior +
                '}';
    }
}
