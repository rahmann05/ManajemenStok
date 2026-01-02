package com.jejekatering.jstok.model;

public class Bahan {
    private int idBahan;
    private String namaBahan;
    private String satuan;
    private int stokSaatIni;
    private int stokMinimum;

    // Enum untuk status stok sesuai SRS & SDD
    public enum StatusStok {
        NORMAL,  // Hijau - stok > minimum
        RENDAH,  // Kuning - stok <= minimum && stok > 0
        HABIS    // Merah - stok = 0
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

    /**
     * Menentukan status stok berdasarkan logika:
     * - HABIS (Merah): stok = 0
     * - RENDAH (Kuning): stok <= minimum && stok > 0
     * - NORMAL (Hijau): stok > minimum
     */
    public StatusStok getStatusStok() {
        if (stokSaatIni == 0) {
            return StatusStok.HABIS;
        } else if (stokSaatIni <= stokMinimum) {
            return StatusStok.RENDAH;
        } else {
            return StatusStok.NORMAL;
        }
    }

    /**
     * Mendapatkan warna hex berdasarkan status stok
     */
    public String getStatusColor() {
        return switch (getStatusStok()) {
            case HABIS -> "#FF3B30";  // Merah
            case RENDAH -> "#FF9500"; // Kuning/Oranye
            case NORMAL -> "#34C759"; // Hijau
        };
    }

    /**
     * Mendapatkan label status dalam bahasa Indonesia
     */
    public String getStatusLabel() {
        return switch (getStatusStok()) {
            case HABIS -> "Habis";
            case RENDAH -> "Rendah";
            case NORMAL -> "Normal";
        };
    }
}