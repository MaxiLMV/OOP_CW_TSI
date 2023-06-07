package com.example.oop_cw_st79404;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class registerSuccessPageController {
    @FXML
    private Button returnButton;

    public void returnToFirst() throws IOException {
        Main m = new Main();
        m.changeScene("firstPage.fxml");
    }
}
