package com.amin.ui.main.wind;

import com.amin.analysis.Mapping;
import com.amin.config.C;
import com.amin.jsons.Date;
import com.amin.jsons.Features;
import com.amin.jsons.FormInfo;
import com.amin.ui.SceneJson;
import com.amin.ui.dialogs.Dialog;
import com.amin.ui.main.main.Charting;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
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
import java.net.URL;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

/**
 * is created by aMIN on 6/1/2018 at 05:50
 */
public class FormDayController implements Initializable {

    public RangeSlider yearsSlider;
    public JFXSlider lowYearjfxslider;
    public JFXSlider highYearjfxslider;
    public HBox topOfgobtn;
    public JFXComboBox feturesCombo;
    private ArrayList<IOException> ioExceptions = new ArrayList<>();

    public GridPane rootNode;

    @FXML
    private JFXComboBox<Label> stationsCombo;
    @FXML
    private JFXComboBox<Label> countriesCombo;
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

    public FormInfo formInfo;
    private Map<String, String> stationNumTOCities;
    private RangeSlider hSlider;
    ArrayList<ArrayList<Object>> AllfeatureAndYear=new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formInfo = new FormInfo();

        String[] featursName = {"PRES", "HGHT", "TEMP", "DWPT", "RELH", "MIXR", "DRCT", "SKNT", "THTA", "THTE", "THTV"};
        for (int i = 0; i < featursName.length; i++) {
            feturesCombo.getItems().add(new Label(featursName[i]));
        }

        feturesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            formInfo.setFeaureName(((Label) newValue).getText());
            String ele = ".jfx-combo-box .label {-fx-text-fill: #9d8024;}";
            feturesCombo.getStyleClass().add(ele);




            if (isReadyToFire(formInfo))
                Gobtn.setDisable(false);
        });






        lowYearjfxslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int a = (int) Math.round((Double) newValue);
            formInfo.setLowerYear(a);
            highYearjfxslider.valueProperty().setValue(newValue);
            System.out.println(newValue);
            System.out.println(a);
            if (isReadyToFire(formInfo))
                Gobtn.setDisable(false);
        });

        highYearjfxslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int a = (int) Math.round((Double) newValue);
            formInfo.setHighYear(a);
            System.out.println(newValue);
            System.out.println(a);

            if (isReadyToFire(formInfo))
                Gobtn.setDisable(false);

        });


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

                formInfo.setDate(null);
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
                formInfo.setDate(new Date(plainDate.getMonth(), plainDate.getDayOfMonth(), plainDate.getYear()));
            }

            if (isReadyToFire(formInfo))
                Gobtn.setDisable(false);

        });


        try {
            ArrayList<String> countriesName = Mapping.getFileLines(C.COUNTRIES_CONFIG_PATH);
            countriesName.forEach(countryName -> countriesCombo.getItems().add(new Label(countryName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        countriesCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            stationsCombo.getItems().clear();
            try {
                formInfo.setStationNumber(null);
                formInfo.setCountry(newValue.getText());
                Mapping.createCSVFILEFORStations("config", newValue.getText() + ".conf");
                stationNumTOCities = Mapping.MapStationNumTOCities("config/" + newValue.getText() + ".conf.csv");
                for (Map.Entry<String, String> station : stationNumTOCities.entrySet()) {
                    if (!station.getValue().equals("&"))
                        stationsCombo.getItems().add(new Label(station.getKey()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isReadyToFire(formInfo))
                Gobtn.setDisable(false);


        });

//        stationsCombo.setPromptText("select station");
        stationsCombo.setMinWidth(200);
        countriesCombo.setMinWidth(200);
        height.setMinWidth(200);
        height.setMinHeight(32);


        stationsCombo.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                for (Map.Entry<String, String> station : stationNumTOCities.entrySet()) {
                    if (station.getKey().equals(newValue.getText()))
                        formInfo.setStationNumber(station.getValue());

                }
                formInfo.setStationName(newValue.getText());

                if (isReadyToFire(formInfo))
                    Gobtn.setDisable(false);
            }
        });

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
            if (formInfo.getHighYear().intValue() < formInfo.getLowerYear().intValue())
                Dialog.SnackBar.showSnack(rootNode, "high year is lower than low year");
            else
                showChartAndAna();


        });


        height.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(""))
                formInfo.setHeight(null);
            else
                formInfo.setHeight(newValue);
            if (isReadyToFire(formInfo))
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
            if (isReadyToFire(formInfo))
                Gobtn.setDisable(false);
        });


        rootNode.add(hSlider, 1, 11);

    }

    private void showChartAndAna() {
        int fromYear = formInfo.getLowerYear().intValue();
        int toYear = formInfo.getHighYear().intValue();
        String featureName = formInfo.getFeaureName();
        int featureIndexCSV = getfeatureIndex(featureName).getLevelCode() - 1;
        double lowrange = Double.parseDouble(getfeatureIndex(featureName).getLow_range());
        double highrange = Double.parseDouble(getfeatureIndex(featureName).getHigh_range());
        String unit = (getfeatureIndex(featureName).getUnit());


        int numDay = formInfo.Date.Day;
        String dayOfMonth = (numDay < 10 ? "0" : "") + numDay;
        int monthInt = formInfo.getDate().Month;
        Month month = Month.of(monthInt);
        String monthDisp = month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        String country = formInfo.getCountry();
        String stationNumber = formInfo.getStationNumber();
        String height = formInfo.getHeight();

        Charting charting = new Charting(1000, 30000, 1000,
                lowrange, highrange, 10, "geoHeight(m)", featureName+"("+unit+")", Charting.LINE_CHART);
        final XYChart<Number, Number> sc = charting.getSc();

        final VBox vbox = new VBox();
        final HBox hbox = new HBox();
        vbox.setLayoutY(300);
        vbox.setLayoutX(400);
        vbox.setStyle("-fx-background-color: #fff");

        ArrayList<Double> knotslist = new ArrayList<>();
        int[] yearsknots = new int[3];
        int kkk = 0;
        String[] z = {"00Z", "12Z"};
        for (int id = 0; id < 2; id++) {

            String Z = z[id];
            for (int i = fromYear; i <= toYear; i++) {
                ArrayList<Object> featureAndYear=new ArrayList<>();
                String rootDir = C.THIRDY_PATH + File.separator + country + File.separator + "year_" + i + File.separator + "month_" + monthInt + File.separator + stationNumber;
                System.out.println(monthDisp);
                System.out.println(dayOfMonth);
                String fileName = Z + "_" + dayOfMonth + "_" + monthDisp + "_" + i + ".csv";
//            dayOfMonth = String.valueOf(Integer.parseInt(dayOfMonth) + 1);
//            dayOfMonth = (Integer.parseInt(dayOfMonth) < 10 ? "0" : "") + Integer.parseInt(dayOfMonth);
                ArrayList<ArrayList<String>> heightAndKnot;
                try {
                    heightAndKnot = charting.addSeriesToChart(featureName
                            , fileName.replaceAll(".csv", ""),
                            rootDir + File.separator + fileName, 1, featureIndexCSV);

                    double intrapolatedKnot = intrapolateKnot(height, heightAndKnot);
                    knotslist.add(intrapolatedKnot);
                    featureAndYear.add(((Double) intrapolatedKnot));
                    featureAndYear.add(i);
                    AllfeatureAndYear.add(featureAndYear);

                    yearsknots[kkk++] = i;
                    System.out.println("intrapolate " + intrapolatedKnot);

                } catch (IOException e) {
//                    System.out.println(e.getMessage());
//                    Dialog.createExceptionDialog(e);
                    ioExceptions.add(e);
                }
            }
        }
        if (!ioExceptions.isEmpty()) {
            Dialog.createIOExceptionDialog(ioExceptions);
            ioExceptions.clear();
        }

        try {
            AllfeatureAndYear.forEach(objects -> {
                objects.forEach(o -> {
                    System.out.println(o);
                });
            });
            Charting charting2 = new Charting(fromYear, toYear ,1,
                    lowrange, highrange, 10, "years", featureName+"("+unit+")", Charting.LINE_CHART);
            charting2.interpolateChart("interpolate years for "+featureName+" in " + height + " m",
                    "interpolate", knotslist, yearsknots, "avg line val is on ",unit);


            vbox.getChildren().addAll(sc, charting2.getSc());
            hbox.setPadding(new Insets(10, 10,
                    03.10, 10));
            Parent root = FXMLLoader.load(FormDayController.class.getResource("/chart.fxml"));
            ((VBox) root).getChildren().add(vbox);
            Stage stage = new Stage();
            stage.setTitle("statistical analysis");

            SceneJson sceneJson = new SceneJson<ArrayList>(root, 450, 450);
            sceneJson.setJson(AllfeatureAndYear);

            stage.setScene(sceneJson);
            stage.show();

            // Hide this current window (if this is what you want)

        } catch (IOException e) {
            Dialog.createExceptionDialog(e);
        }


    }

    private Features getfeatureIndex(String featureName) {
        if (featureName.equals(Features.PRES.getName()))
            return Features.PRES;
        else if (featureName.equals(Features.HGHT.getName()))
            return Features.HGHT;
        else if (featureName.equals(Features.TEMP.getName()))
            return Features.TEMP;
        else if (featureName.equals(Features.DWPT.getName()))
            return Features.DWPT;
        else if (featureName.equals(Features.RELH.getName()))
            return Features.RELH;
        else if (featureName.equals(Features.MIXR.getName()))
            return Features.MIXR;
        else if (featureName.equals(Features.DRCT.getName()))
            return Features.DRCT;
        else if (featureName.equals(Features.SKNT.getName()))
            return Features.SKNT;
        else if (featureName.equals(Features.THTA.getName()))
            return Features.THTA;
        else if (featureName.equals(Features.THTE.getName()))
            return Features.THTE;
        else if (featureName.equals(Features.THTV.getName()))
            return Features.THTV;
        else
            return null;

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







    private boolean isReadyToFire(FormInfo formInfo) {
        if (formInfo.getFeaureName() == null || formInfo.getLowerYear() == null || formInfo.getHighYear() == null || formInfo.getDate() == null || formInfo.getStationNumber() == null || formInfo.getCountry() == null || formInfo.getHeight() == null) {
            Gobtn.setDisable(true);
            return false;
        } else
            return true;
    }




}