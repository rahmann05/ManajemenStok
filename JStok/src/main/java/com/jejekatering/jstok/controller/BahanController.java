package com.jejekatering.jstok.controller;

import animatefx.animation.FadeInUp;
import com.jejekatering.jstok.dao.BahanDAO;
import com.jejekatering.jstok.model.Bahan;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class BahanController {

    @FXML
    private TableView<Bahan> bahanTable;

    @FXML
    private TableColumn<Bahan, Integer> colId;
    @FXML
    private TableColumn<Bahan, String> colNama;
    @FXML
    private TableColumn<Bahan, String> colSatuan;
    @FXML
    private TableColumn<Bahan, Integer> colStok;
    @FXML
    private TableColumn<Bahan, Integer> colMin;

    private BahanDAO bahanDAO;

    @FXML
    public void initialize() {
        bahanDAO = new BahanDAO();
        loadData();
        playEntryAnimations();
    }

    private void loadData() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idBahan"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaBahan"));
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stokSaatIni"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("stokMinimum"));

        ObservableList<Bahan> listData = bahanDAO.getAllBahan();
        bahanTable.setItems(listData);
    }

    private void playEntryAnimations() {
        if (bahanTable != null) {
            new FadeInUp(bahanTable).setDelay(Duration.millis(100)).play();
        }
    }
}