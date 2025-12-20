package com.jejekatering.jstok.model;

public class Bahan {
    private int idBahan;
    private String namaBahan;
    private String satuan;
    private int stokSaatIni;
    private int stokMinimum;

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
}