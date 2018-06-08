package com.ui.dialogs.JFXToast;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

// slides a frost pane in on scroll or swipe up; slides it out on scroll or swipe down.
public class Toasts extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static Tooltip JToast(String Title, String Text,
            String BackgroundColor,
            String Color,
            Duration duration) {
        Tooltip S = new Tooltip();
        Label X = new Label("X");
        X.setId("Close");
        X.setOnMouseClicked(value -> {
            S.hide();
        });

        Label Content = new Label(Text);
        Content.setAlignment(Pos.TOP_RIGHT);
        Content.setTextAlignment(TextAlignment.RIGHT);
        Content.setWrapText(true);
        Content.setId("Content");

        double MaxWidth = 800;
        Content.setMaxWidth(MaxWidth);

        Label TitleLable = new Label(Title);
        TitleLable.setAlignment(Pos.TOP_RIGHT);
        Content.setStyle("-fx-text-fill:" + Color);
        TitleLable.setStyle("-fx-text-fill:" + Color);

        GridPane GB = new GridPane();
        double Width = 0;
        if (Content.getText() != null) {
            Width = 150 + Content.getText().length() / 2.5;
        }
        if (Width > MaxWidth) {
            Width = MaxWidth;
        }
        double height = Content.getPrefHeight();

        GB.getColumnConstraints().setAll(new ColumnConstraints(30, 30, 30, Priority.NEVER, HPos.LEFT, true),
                new ColumnConstraints(Width, Width, MaxWidth, Priority.ALWAYS, HPos.RIGHT, true));

        GB.getRowConstraints().setAll(new RowConstraints(30, 30, 30), new RowConstraints(height, height, 750, Priority.ALWAYS, VPos.CENTER, true));
        GB.setId("msgtipbox");
        GB.setStyle("-fx-background-color:" + BackgroundColor);
        GB.setVgap(10);
        GB.setHgap(10);
        GB.add(X, 0, 0);
        GB.add(TitleLable, 1, 0);
        GB.add(Content, 0, 1, 2, 1);
        GridPane.setVgrow(Content, Priority.ALWAYS);
        GridPane.setHgrow(Content, Priority.ALWAYS);
        S.setGraphic(GB);
        S.setId("msg-tip");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double y = screenBounds.getMinY() + screenBounds.getHeight();
        S.show(PS, 0, y);

        //////Show and Hide
        //////Still showing in Hover Support
        SimpleBooleanProperty HoveProperty = new SimpleBooleanProperty(false);
        GB.setOnMouseEntered(v -> HoveProperty.set(true));
        GB.setOnMouseExited(v -> HoveProperty.set(false));
        PauseTransition wait = new PauseTransition(duration);
        wait.setOnFinished((e) -> {
            if (HoveProperty.get()) {
                wait.play();
            } else {
                S.hide();
            }
        });
        wait.play();

        HoveProperty.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            wait.playFromStart();
        });
        return S;
    }

    static public Tooltip NoticeToast(String Title, String Txt) {
        return JToast(Title, Txt, "rgba(255,255,255,0.8)", "#222", Duration.seconds(15));
    }

    static public Tooltip SucssesToast(String Title, String Txt) {
        return JToast(Title, Txt, "rgba(167, 255, 86, 0.8)", "#222", Duration.seconds(10));
    }

    static public Tooltip ErrorToast(String Title, String Txt) {
        return JToast(Title, Txt, "rgba(255, 25, 36, 0.8)", "#fff", Duration.seconds(18));
    }

    static Stage PS;

    @Override
    public void start(Stage primaryStage) throws Exception {
        PS = primaryStage;

        Button Success = new Button("Success");
        Button Notice = new Button("Notice");
        Button Error = new Button("Error");

        primaryStage.setScene(new Scene(new VBox(Success, Notice, Error)));

        PS.getScene().getStylesheets().add("com/ui/dialogs/JFXToast/ToastStyle.css");

        Success.setOnAction(va -> {
            SucssesToast("MSG From Application",
                    "Successfully Changes Made");
        });

        Notice.setOnAction(va -> {
            NoticeToast("MSG From Application",
                    "Note that: \n the application is now in the ...");
        });

        Error.setOnAction(va -> {
            ErrorToast("MSG From Application",
                    "Error No: 5054 \n Changes can't Made because of ...");
        });
        primaryStage.show();
    }
}
