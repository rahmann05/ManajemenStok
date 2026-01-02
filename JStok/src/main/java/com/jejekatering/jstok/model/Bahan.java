package com.jejekatering.jstok.model;

public class Bahan {
    private int idBahan;
    private String namaBahan;
    private String satuan;
    private int stokSaatIni;
    private int stokMinimum;

    public enum StatusStok {
        NORMAL,
        RENDAH,
        HABIS
    }

    public Bahan(int idBahan, String namaBahan, String satuan, int stokSaatIni, int stokMinimum) {
        this.idBahan = idBahan;
        this.namaBahan = namaBahan;
        this.satuan = satuan;
        this.stokSaatIni = stokSaatIni;
        this.stokMinimum = stokMinimum;
    }

    public int getIdBahan() { return idBahan; }
    public void setIdBahan(int idBahan) { this.idBahan = idBahan; }

    public String getNamaBahan() { return namaBahan; }
    public void setNamaBahan(String namaBahan) { this.namaBahan = namaBahan; }

    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }

    public int getStokSaatIni() { return stokSaatIni; }
    public void setStokSaatIni(int stokSaatIni) { this.stokSaatIni = stokSaatIni; }

    public int getStokMinimum() { return stokMinimum; }
    public void setStokMinimum(int stokMinimum) { this.stokMinimum = stokMinimum; }

    public StatusStok getStatusStok() {
        if (stokSaatIni == 0) {
            return StatusStok.HABIS;
        } else if (stokSaatIni <= stokMinimum) {
            return StatusStok.RENDAH;
        } else {
            return StatusStok.NORMAL;
        }
    }

    public String getStatusColor() {
        return switch (getStatusStok()) {
            case HABIS -> "#FF3B30";
            case RENDAH -> "#FF9500";
            case NORMAL -> "#34C759";
        };
    }

    public String getStatusLabel() {
        return switch (getStatusStok()) {
            case HABIS -> "Habis";
            case RENDAH -> "Rendah";
            case NORMAL -> "Normal";
        };
    }
}