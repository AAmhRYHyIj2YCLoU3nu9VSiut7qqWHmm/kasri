package com.amin.ui.main.wind;

import com.amin.analysis.Mapping;
import com.amin.config.C;
import com.amin.jsons.Date;
import com.amin.jsons.WindInfo;
import com.amin.ui.SceneJsonWindInfo;
import com.amin.ui.dialogs.Dialog;
import com.amin.ui.main.main.Charting;
import com.amin.ui.main.main.MainController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.time4j.PlainDate;
import net.time4j.calendar.PersianCalendar;
import net.time4j.ui.javafx.CalendarPicker;
import org.controlsfx.control.RangeSlider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * is created by aMIN on 6/1/2018 at 05:50
 */
public class WindMonthController implements Initializable {


    public GridPane rootNode;

    @FXML
    public JFXComboBox<Label> stationsCombo;
    @FXML
    public JFXComboBox<Label> countriesCombo;
    public JFXButton cancelBtn;
    public JFXButton Gobtn;

    public TextField height;
    public VBox vvv;
    public JFXComboBox monthCombo;
    public JFXComboBox dayofMonthCombo;
    public JFXCheckBox z00;
    public JFXTabPane jfxtab;
    @FXML
    CalendarPicker<PersianCalendar> persianCalendarCalendarPicker;

    public WindInfo windInfo;
    private Map<String, String> stationNumTOCities;
    private RangeSlider hSlider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        windInfo = new WindInfo();
//        rootNode.setAlignment(Pos.CENTER);
//        rootNode.setStyle("          -fx-padding: 10;\n" +
//                "            -fx-border-style: solid inside;\n" +
//                "            -fx-border-width: 6;\n" +
//                "            -fx-border-insets: 5;\n" +
//                "            -fx-border-radius: 5;\n" +
//                "            -fx-border-color: white;");

        GridPane.setMargin(monthCombo,new Insets(0,0,30,0));

        ArrayList<String> persianMonths=new ArrayList<String>( Arrays.asList("فروردین", "اردیبهشت", "خرداد","تیر","مرداد","شهریور","مهر","ابان","اذر","دی","بهمن","اسفند"));
        Map<String, Integer> persianMapMonth = new HashMap<>();

        for (int j = 0; j < persianMonths.size(); j++) {
            monthCombo.getItems().add(new Label((persianMonths.get(j))));
            persianMapMonth.put(persianMonths.get(j), j + 1);
        }



        monthCombo.valueProperty().addListener((observable, oldValue, newValue) -> {


            dayofMonthCombo.getItems().clear();
            int[] days={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
            for (int i = 1; i <= 31; i++) {
                    dayofMonthCombo.getItems().add(new Label(String.valueOf(i)));
            }

        });
        dayofMonthCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                windInfo.setDate(null);
                persianCalendarCalendarPicker = CalendarPicker.persianWithSystemDefaults();
                String text = ((Label) monthCombo.getValue()).getText();
                Integer intmonth = persianMapMonth.get(text);

                try {
                    persianCalendarCalendarPicker.valueProperty().setValue(PersianCalendar.of(1372, intmonth, (Integer.parseInt(((Label) newValue).getText()))));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }catch (java.lang.IllegalArgumentException ex){
                    Dialog.createExceptionDialog(ex);
                }
//                System.out.println(persianCalendarCalendarPicker.valueProperty().getValue().getMonth());
            PersianCalendar persianCalendar = persianCalendarCalendarPicker.valueProperty().getValue();
            PlainDate plainDate = persianCalendar.transform(PlainDate.class);
                System.out.println(String.format("%s-%s-%s", plainDate.getDayOfMonth(), plainDate.getMonth(), plainDate.getYear()));
            windInfo.setDate(new Date(plainDate.getMonth(), plainDate.getDayOfMonth(), plainDate.getYear()));
            }

            if (isReadyToFire(windInfo))
                Gobtn.setDisable(false);

        });





        countriesCombo = new JFXComboBox<>();
        try {
            ArrayList<String> countriesName = Mapping.getFileLines(C.COUNTRIES_CONFIG_PATH);
            countriesName.forEach(countryName -> countriesCombo.getItems().add(new Label(countryName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rootNode.add(countriesCombo, 1, 5);


        stationsCombo = new JFXComboBox<>();

        countriesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            stationsCombo.getItems().clear();
            try {
                windInfo.setStationNumber(null);
                windInfo.setCountry(newValue.getText());
                Mapping.createCSVFILEFORStations("config", newValue.getText() + ".conf");
                stationNumTOCities = Mapping.MapStationNumTOCities("config/" + newValue.getText() + ".conf.csv");
                for (Map.Entry<String, String> station : stationNumTOCities.entrySet()) {
                    if (!station.getValue().equals("&"))
                        stationsCombo.getItems().add(new Label(station.getKey()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isReadyToFire(windInfo))
                Gobtn.setDisable(false);


        });

//        stationsCombo.setPromptText("select station");
        stationsCombo.setMinWidth(200);
        countriesCombo.setMinWidth(200);
        height.setMinWidth(200);
        height.setMinHeight(32);


        GridPane.setMargin(stationsCombo, new Insets(12, 0, 12, 0));
        stationsCombo.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                for (Map.Entry<String, String> station : stationNumTOCities.entrySet()) {
                    if (station.getKey().equals(newValue.getText()))
                        windInfo.setStationNumber(station.getValue());

                }
                windInfo.setStationName(newValue.getText());

                if (isReadyToFire(windInfo))
                    Gobtn.setDisable(false);
            }
        });

        rootNode.add(stationsCombo, 1, 7);
        GridPane.setHalignment(stationsCombo, HPos.CENTER);

        cancelBtn.pressedProperty().addListener(observable -> {
        });

        cancelBtn.setOnAction(event -> {
            ((cancelBtn)).getParent().getScene().getWindow().hide();

        });
        cancelBtn.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER)
                cancelBtn.getOnAction().handle(null);
        });

        Gobtn.setOnKeyPressed(event -> {
            if(event.getCode()==KeyCode.ENTER)
                Gobtn.getOnAction().handle(null);
        });


        Gobtn.setOnAction(event -> {
//            try {
//                getResult(((Stage) rootNode.getScene().getWindow()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
            showChartAndAna();


        });


        height.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(""))
                windInfo.setHeight(null);
            else
                windInfo.setHeight(newValue);
            if (isReadyToFire(windInfo))
                Gobtn.setDisable(false);
        });


        hSlider = new RangeSlider(1100, 20000, 1100, 20000);

        hSlider.setShowTickMarks(true);
        hSlider.setShowTickLabels(true);
        hSlider.setBlockIncrement(1);

        hSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> {
            height.textProperty().setValue(String.valueOf(newValue));
        });

        height.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isReadyToFire(windInfo))
                Gobtn.setDisable(false);
        });


        rootNode.add(hSlider, 1, 11);

    }

    private void showChartAndAna() {

        int numDay = windInfo.Date.Day;
        String dayOfMonth = (numDay < 10 ? "0" : "") + numDay;
        int monthInt = windInfo.getDate().Month;
        Month month = Month.of(monthInt);
        String monthDisp = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        int year = windInfo.getDate().Year;

        String country = windInfo.getCountry();
        String stationNumber = windInfo.getStationNumber();
        String height = windInfo.getHeight();

        Charting charting = new Charting(1000, 30000, 1000,
                0, 190, 10, "height", "knot", Charting.LINE_CHART);
        final XYChart<Number, Number> sc = charting.getSc();

        final VBox vbox = new VBox();
        final HBox hbox = new HBox();
        vbox.setLayoutY(300);
        vbox.setLayoutX(400);
        vbox.setStyle("-fx-background-color: #fff");

        ArrayList<Double> knotslist=new ArrayList<>();
        int[] yearsknots=new int[3];
        int kkk=0;
        for (int i = 2015; i < 2018; i++) {
            String rootDir = C.THIRDY_PATH + File.separator + country + File.separator + "year_" + i + File.separator + "month_" + monthInt + File.separator + stationNumber;


            System.out.println(monthDisp);
            System.out.println(dayOfMonth);
            String fileName = "00Z_" + dayOfMonth + "_" + monthDisp + "_" + i + ".csv";
//            dayOfMonth = String.valueOf(Integer.parseInt(dayOfMonth) + 1);
//            dayOfMonth = (Integer.parseInt(dayOfMonth) < 10 ? "0" : "") + Integer.parseInt(dayOfMonth);


            ArrayList<ArrayList<String>> heightAndKnot;
            try {
                heightAndKnot = charting.addSeriesToChart(
                        fileName.replaceAll(".csv", "")
                        , fileName.replaceAll(".csv", ""),
                        rootDir + File.separator + fileName);

                double intrapolatedKnot = intrapolateKnot(height, heightAndKnot);
                knotslist.add(intrapolatedKnot);

                yearsknots[kkk++]=i;
                System.out.println("intrapolate " + intrapolatedKnot);

            } catch (IOException e) {
                Dialog.createExceptionDialog(e);
            }
        }
        try {

            Charting charting2 = new Charting(2014, 2018, 1,
                    0, 100, 10, "height", "knot", Charting.LINE_CHART);
            charting2.interpolateChart("interpolate years knot in "+height+" m","interpolate",knotslist,yearsknots);

            vbox.getChildren().addAll(sc,charting2.getSc());
            hbox.setPadding(new Insets(10, 10,
                    03.10, 10));
            Parent root = FXMLLoader.load(WindMonthController.class.getResource("/chart.fxml"));
            ((VBox) root).getChildren().add(vbox);
            ;
            Stage stage = new Stage();
            stage.setTitle("Title");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();

            // Hide this current window (if this is what you want)

        } catch (IOException e) {
            Dialog.createExceptionDialog(e);
        }


    }


    private double intrapolateKnot(String height, ArrayList<ArrayList<String>> heightAndKnotAll) {
        double knotdesire = -1;
        double heightdesire = Double.parseDouble(height);
        final Vector<Double> heigthsVector = new Vector<>();
        final Vector<Double> knotsVector = new Vector<>();

        heightAndKnotAll.forEach(strings -> {
            if (!strings.get(0).equals("HGHT")
                    && !strings.get(1).equals("SKNT")
                    && !strings.get(0).equals("m")
                    && !strings.get(1).equals("knot")
                    && !strings.get(0).equals("NULL")
                    && !strings.get(1).equals("NULL")


                    ) {
                double h = Double.parseDouble(strings.get(0));
                double knot = Double.parseDouble(strings.get(1));
                heigthsVector.add(h);
                knotsVector.add(knot);
            }
        });

        for (int i = 0; i < heigthsVector.size() - 1; i++) {
            double hi = heigthsVector.get(i);
            double hiplus = heigthsVector.get(i + 1);
            double knoti = knotsVector.get(i);
            double knotiplus = knotsVector.get(i + 1);
            if ((heightdesire - hi) * (heightdesire - hiplus) <= 0) {
                knotdesire = (knotiplus - knoti) * (heightdesire - hi) / (hiplus - hi) + (knoti);
                break;
            }

        }
        return knotdesire;
    }


    private void chartIntrapolates(ArrayList<Double> knotesinyears, int[] years) {


    }










































    private void getResult(Stage prStage) throws IOException, URISyntaxException {

        URL resource = getClass().getResource("/wind_result.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Parent root = ((Parent) fxmlLoader.load(resource));
        SceneJsonWindInfo scene = new SceneJsonWindInfo(root, 450, 350);
        (scene).setWindInfo(windInfo);

        String image = MainController.class.getResource("/fav.jpg").toURI().toString();
        root.setStyle("-fx-background-image: url('" + image + "'); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch;");
        prStage.hide();
        prStage.setScene(scene);
        prStage.show();

    }





































    public static final LocalDate LOCAL_DATE(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }

    private boolean isReadyToFire(WindInfo windInfo) {
        if (windInfo.getDate() == null || windInfo.getStationNumber() == null || windInfo.getCountry() == null || windInfo.getHeight() == null) {
            Gobtn.setDisable(true);
            return false;
        } else
            return true;
    }




}