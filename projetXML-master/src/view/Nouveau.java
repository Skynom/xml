package view;

import controller.VisitPlanController;
import generated.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Nouveau extends Application {

	public Itinerary itinerary = new Itinerary();

	@Override
	@SuppressWarnings("Duplicates")
	public void start(Stage primaryStage) throws Exception {
		VBox vBox = new VBox();
		Periode periode = new Periode();

		//Start Itinerary.Periode
		Label startPeriode = new Label("Début période :");
		vBox.getChildren().add(startPeriode);

		DatePicker startPicker = new DatePicker();
		//If it is a modification
		if(itinerary.getPeriode() != null)
			startPicker.setValue(LocalDate.parse(itinerary.getPeriode().getStart().toXMLFormat()));
		vBox.getChildren().add(startPicker);
		startPicker.setOnAction(event -> {
			//Get selected value
			LocalDate localDate = startPicker.getValue();
			try {
				//Convert LocalDate to XMLGregorianCalendar
				XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
				periode.setStart(xcal);
			} catch (javax.xml.datatype.DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		});

		//End Itinerary.Periode
		Label endPeriode = new Label("\n Fin période :");
		vBox.getChildren().add(endPeriode);

		DatePicker endPicker = new DatePicker();
		//If it is a modification
		if(itinerary.getPeriode() != null)
			startPicker.setValue(LocalDate.parse(itinerary.getPeriode().getEnd().toXMLFormat()));
		vBox.getChildren().add(endPicker);
		endPicker.setOnAction(event -> {
			LocalDate localDate = endPicker.getValue();
			try {
				XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
				periode.setEnd(xcal);
			} catch (javax.xml.datatype.DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		});

		this.itinerary.setPeriode(periode);

		//Budget
		Budget budget = new Budget();

		//Budget.Value (only digits)
		Label labelBudget = new Label("\n Montant budget (Si non rempli, considéré comme 0) :");
		vBox.getChildren().add(labelBudget);
		TextField areaBudget = new TextField();
		//If it is a modification
		if (itinerary.getBudget() != null)
			areaBudget.setText(itinerary.getBudget().getValue().toString());
		vBox.getChildren().add(areaBudget);
		areaBudget.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent keyEvent) {
				if (!"0123456789".contains(keyEvent.getCharacter())) {
					keyEvent.consume();
				}
			}
		});

		//Budget.Devise
		Label labelDevise = new Label("\n Devise (Si non rempli, considéré comme Euro) :");
		vBox.getChildren().add(labelDevise);
		TextField areaDevise = new TextField();
		//If it is a modification
		if (itinerary.getBudget() != null)
			areaDevise.setText(itinerary.getBudget().getDevise());
		vBox.getChildren().add(areaDevise);

		Button next = new Button("Etape suivante");
		vBox.getChildren().add(next);

		Scene newScene = new Scene(vBox, 450, 500);

		next.setOnAction(event -> {
			if ((periode.getStart() != null) && (periode.getEnd() != null) && (periode.getEnd().compare(periode.getStart()) == DatatypeConstants.GREATER || periode.getEnd().compare(periode.getStart()) == DatatypeConstants.EQUAL)) {
				//Check if no copy-paste, if there is, value set to 0
				try {
					Integer.parseInt(areaBudget.getText());
					BigInteger value = new BigInteger(areaBudget.getText());
					budget.setValue(value);
				} catch(Exception e) {
					budget.setValue(BigInteger.ZERO);
				}
				if (areaBudget.getText().equals(""))
					budget.setValue(BigInteger.ZERO);


				if (areaDevise.getText().equals(""))
					budget.setDevise("Euro");
				else
					budget.setDevise(areaDevise.getText());

				this.itinerary.setBudget(budget);

				try {
					days(primaryStage, newScene);
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}
			}
		});

		primaryStage.setScene(newScene);
		primaryStage.setTitle("Nouvel Itinéraire");
		primaryStage.show();
	}

	private void days(Stage Stage, Scene scene) throws Exception {
		VBox vBox = new VBox();
		HBox hBox = new HBox();

		Label label = new Label("Configuration des jours");
		vBox.getChildren().add(label);

		//Converting XMLGregorianCalendar to LocalDate
		LocalDate start = LocalDate.parse(getPeriode().getStart().toXMLFormat());
		LocalDate end = LocalDate.parse(getPeriode().getEnd().toXMLFormat());

		//Difference between two LocalDate to have number of days in the Itinerary
		Period period = Period.between(start,end);
        int nbDays = period.getDays() + 1;

        ArrayList<Button> daysButtons = new ArrayList<>();
		for (int i = 0; i < nbDays; i++) {
			Days days = new Days();
			//If it is a modification
			if (itinerary.getDays().size() > 0 && itinerary.getDays().size() > i) {
				days.setDay(this.itinerary.getDays().get(i).getDay());
			}
			//Creating everything necessary in Days if this.itinerary is not modified
			else {
				Day day = new Day();
				Steps steps = new Steps();
				day.setId(UUID.randomUUID().toString());
				day.setSteps(steps);
				days.setDay(day);
				getDays().add(days);
			}

			Button button = new Button("Day " + (i + 1));
			daysButtons.add(button);

			button.setOnAction(event -> {
				Stage s = new Stage();
				try {
					steps(s, days, button.getText());
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}
			});
		}
		//HBox to get Days buttons horizontally
		hBox.getChildren().addAll(daysButtons);
		vBox.getChildren().add(hBox);

		Button finish = new Button("finish :");
		vBox.getChildren().add(finish);

		finish.setOnAction(event -> {
			try {
				VisitPlanController.persiste(this.itinerary);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		});

		scene = new Scene(vBox, 450, 500);

		Stage.setScene(scene);
		Stage.setTitle("Nouvel Itinéraire");
		Stage.show();
	}

	@SuppressWarnings("Duplicates")
	private void steps(Stage stage, Days days, String title) throws Exception {
		VBox vBox = new VBox();

		//Add Step
		Button addStep = new Button("Add Step");
		vBox.getChildren().add(addStep);
		addStep.setOnAction(event -> {
			days.getDay().getSteps().getStep().add(new Step());
			try {
				steps(stage, days, title);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});

		if (days.getDay().getSteps().getStep() != null) {
			int nbSteps = days.getDay().getSteps().getStep().size();
			System.out.println(nbSteps);

			//Loop creating everything necessary for step (might become buttons latter because overwhelming in a window)
			for (int i = 0; i < nbSteps; i++) {
				//Active step
				Step step = days.getDay().getSteps().getStep().get(i);

				Label label = new Label("\n Step " + (i + 1));
				vBox.getChildren().add(label);

				//XMLGregorianCalendar start for step
				DatePicker startStep = new DatePicker();
				vBox.getChildren().add(startStep);
				//If step.start is set, show the value in the DatePicker
				if (step.getStart() != null)
					startStep.setValue(LocalDate.parse(step.getStart().toXMLFormat()));

				//Desactivate all cells outside Itinerary.periode
				startStep.setDayCellFactory(param -> new DateCell() {
					public void updateItem(LocalDate date, boolean empty) {
						super.updateItem(date, empty);

						LocalDate start = LocalDate.parse(getPeriode().getStart().toXMLFormat());
						LocalDate end = LocalDate.parse(getPeriode().getEnd().toXMLFormat());

						setDisable(empty || date.compareTo(start) < 0 || date.compareTo(end) > 0);
					}
				});

				startStep.setOnAction(event -> {
					//Get selected value
					LocalDate localDate = startStep.getValue();
					try {
						//Convert LocalDate to XMLGregorianCalendar
						XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
						step.setStart(xcal);
					} catch (javax.xml.datatype.DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				});

				//XMLGregorianCalendar end for step
				DatePicker endStep = new DatePicker();
				vBox.getChildren().add(endStep);
				//If step.end is set, show the value in the DatePicker
				if (step.getEnd() != null)
					endStep.setValue(LocalDate.parse(step.getEnd().toXMLFormat()));

				//Desactivate all cells outside Itinerary.periode
				endStep.setDayCellFactory(param -> new DateCell() {
					public void updateItem(LocalDate date, boolean empty) {
						super.updateItem(date, empty);

						LocalDate start = LocalDate.parse(getPeriode().getStart().toXMLFormat());
						LocalDate end = LocalDate.parse(getPeriode().getEnd().toXMLFormat());

						setDisable(empty || date.compareTo(start) < 0 || date.compareTo(end) > 0);
					}
				});

				endStep.setOnAction(event -> {
					//Get selected value
					LocalDate localDate = startStep.getValue();
					try {
						//Convert LocalDate to XMLGregorianCalendar
						XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
						step.setEnd(xcal);
					} catch (javax.xml.datatype.DatatypeConfigurationException e) {
						e.printStackTrace();
					}
				});

				Button button = new Button("POI de step " + (i + 1));
				vBox.getChildren().add(button);
				button.setOnAction(event -> {
					if ((step.getStart() != null) && (step.getEnd() != null) && (step.getEnd().compare(step.getStart()) == DatatypeConstants.GREATER || step.getEnd().compare(step.getStart()) == DatatypeConstants.EQUAL)) {
						//If it isn't a modification, create a POI for Step
						if (step.getPOI() == null) {
							POI poi = new POI();
							step.setPOI(poi);
						}
						Stage s = new Stage();
						try {
							poi(s, step.getPOI(), button.getText());
						} catch (java.lang.Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}

		Scene scene = new Scene(vBox, 400, 450);

		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();
	}

	@SuppressWarnings("Duplicates")
	private void poi(Stage stage, POI poi, String title) throws Exception {
		VBox vBox = new VBox();

		ScrollPane scrollPane = new ScrollPane(vBox);
		scrollPane.setFitToHeight(true);

		BorderPane root = new BorderPane(scrollPane);
		root.setTop(vBox);

		ArrayList<TextField> fieldArrayList = new ArrayList<>();

		//POI name
		Label nameLabel = new Label("Nom POI :");
		vBox.getChildren().add(nameLabel);

		TextField nameField = new TextField();
		fieldArrayList.add(nameField);
		if (poi.getName() != null)
			nameField.setText(poi.getName());
		vBox.getChildren().add(nameField);


		//POI rating
		HBox hBoxRating = new HBox();
		Label ratingLabel = new Label("\n" + "Rating :");
		vBox.getChildren().add(ratingLabel);

		ToggleGroup group = new ToggleGroup();
		RadioButton b0 = new RadioButton("0");
		RadioButton b1 = new RadioButton("1");
		RadioButton b2 = new RadioButton("2");
		RadioButton b3 = new RadioButton("3");
		RadioButton b4 = new RadioButton("4");
		RadioButton b5 = new RadioButton("5");

		b0.setToggleGroup(group);
		b1.setToggleGroup(group);
		b2.setToggleGroup(group);
		b3.setToggleGroup(group);
		b4.setToggleGroup(group);
		b5.setToggleGroup(group);

		b0.setOnAction(event -> poi.setRating(new BigDecimal("0")));
		b1.setOnAction(event -> poi.setRating(new BigDecimal("1")));
		b2.setOnAction(event -> poi.setRating(new BigDecimal("2")));
		b3.setOnAction(event -> poi.setRating(new BigDecimal("3")));
		b4.setOnAction(event -> poi.setRating(new BigDecimal("4")));
		b5.setOnAction(event -> poi.setRating(new BigDecimal("5")));

		if (poi.getRating() != null) {
			ArrayList<RadioButton> list = new ArrayList<>();
			list.add(b0);
			list.add(b1);
			list.add(b2);
			list.add(b3);
			list.add(b4);
			list.add(b5);

			for (RadioButton b : list) {
				if (b.getText().contains(poi.getRating().toString())){
					b.setSelected(true);
				}
			}
		} else {
			b0.setSelected(true);
			poi.setRating(new BigDecimal("0"));
		}

		hBoxRating.getChildren().addAll(b0,b1,b2,b3,b4,b5);
		vBox.getChildren().add(hBoxRating);


		//POI Position
		Label latitudeLabel = new Label("\n" + "Latitude (digits only) :");
		vBox.getChildren().add(latitudeLabel);
		TextField latitudeField = new TextField();
		fieldArrayList.add(latitudeField);

		if (poi.getPosition() != null)
			latitudeField.setText(poi.getPosition().getLatitude().toString());

		latitudeField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent keyEvent) {
				if (!"0123456789.".contains(keyEvent.getCharacter())) {
					keyEvent.consume();
				}
			}
		});
		vBox.getChildren().add(latitudeField);


		Label longitudeLabel = new Label("Longitude (digits only) :");
		vBox.getChildren().add(longitudeLabel);
		TextField longitudeField = new TextField();

		fieldArrayList.add(longitudeField);

		if (poi.getPosition() != null)
			longitudeField.setText(poi.getPosition().getLongitude().toString());

		longitudeField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent keyEvent) {
				if (!"0123456789.".contains(keyEvent.getCharacter())) {
					keyEvent.consume();
				}
			}
		});
		vBox.getChildren().add(longitudeField);

		Label addressLabel = new Label("Addresse :");
		vBox.getChildren().add(addressLabel);
		TextField addressField = new TextField();

		fieldArrayList.add(addressField);

		if (poi.getPosition() != null) {
			addressField.setText(poi.getPosition().getAddress().toString());
		}
		vBox.getChildren().add(addressField);


		//Handicap
		Label handicapLabel = new Label("\n" + "Handicap :");
		vBox.getChildren().add(handicapLabel);

		HBox hBoxHandicap = new HBox();
		ToggleGroup groupHandicap = new ToggleGroup();
		RadioButton handicap = new RadioButton("oui");
		RadioButton noHandicap = new RadioButton("non");
		handicap.setToggleGroup(groupHandicap);
		handicap.setOnAction(event -> poi.setHandicap(true));
		noHandicap.setToggleGroup(groupHandicap);
		noHandicap.setOnAction(event -> poi.setHandicap(false));

		if (poi.getName() != null) {
			if (poi.isHandicap()) {
				handicap.setSelected(true);
			} else {
				noHandicap.setSelected(true);
				poi.setHandicap(false);
			}
		}

		hBoxHandicap.getChildren().addAll(handicap, noHandicap);
		vBox.getChildren().add(hBoxHandicap);

		//Contact
		Label phoneLabel = new Label("\n" + "Telephone :");
		vBox.getChildren().add(phoneLabel);
		TextField phoneField = new TextField();

		fieldArrayList.add(phoneField);

		if (poi.getContact() != null)
			phoneField.setText(poi.getContact().getTelephone());

		phoneField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent keyEvent) {
				if (!"0123456789".contains(keyEvent.getCharacter())) {
					keyEvent.consume();
				}
			}
		});
		vBox.getChildren().add(phoneField);

		Label mailLabel = new Label("Mail :");
		vBox.getChildren().add(mailLabel);
		TextField mailField = new TextField();

		fieldArrayList.add(mailField);

		if (poi.getContact() != null)
			mailField.setText(poi.getContact().getMail());
		vBox.getChildren().add(mailField);

		Label webLabel = new Label("Web :");
		vBox.getChildren().add(webLabel);
		TextField webField = new TextField();

		fieldArrayList.add(webField);

		if (poi.getContact() != null)
			webField.setText(poi.getContact().getWeb());
		vBox.getChildren().add(webField);


		//Description
		Label descriptionLabel = new Label("\n" + "Description :");
		vBox.getChildren().add(descriptionLabel);
		TextField descriptionField = new TextField();

		fieldArrayList.add(descriptionField);

		if (poi.getDescription() != null)
			descriptionField.setText(poi.getDescription());
		vBox.getChildren().add(descriptionField);


		//Styles
		Label styleLabel = new Label("\n" + "Style :");
		vBox.getChildren().add(styleLabel);
		TextField styleField = new TextField();

		fieldArrayList.add(styleField);

		if (poi.getStyles() != null)
			styleField.setText(poi.getStyles().getStyle());
		vBox.getChildren().add(styleField);


		//Theme
		Label themeLabel = new Label("\n" + "Theme (1 mot = 1 theme) :");
		vBox.getChildren().add(themeLabel);
		TextField themeField = new TextField();

		fieldArrayList.add(themeField);

		if (poi.getThemes() != null) {
			String s = "";
			for (String theme : poi.getThemes().getTheme()) {
				s += theme + " ";
			}
			themeField.setText(s);
		}
		vBox.getChildren().add(themeField);


		//Type
		Label typeLabel = new Label("\n" + "Type (1 mot = 1 type) :");
		vBox.getChildren().add(typeLabel);
		TextField typeField = new TextField();

		fieldArrayList.add(typeField);

		if (poi.getThemes() != null) {
			String s = "";
			for (String theme : poi.getThemes().getTheme()) {
				s += theme + " ";
			}
			typeField.setText(s);
		}
		vBox.getChildren().add(typeField);


		//Photo
		Label photoLabel = new Label("\n" + "Photo (1 mot = 1 photo) :");
		vBox.getChildren().add(photoLabel);
		TextField photoField = new TextField();

		fieldArrayList.add(photoField);

		if (poi.getPhotos() != null) {
			String s = "";
			for (String photo : poi.getPhotos().getPhoto()) {
				s += photo + " ";
			}
			photoField.setText(s);
		}
		vBox.getChildren().add(photoField);

		/*
		//XMLGregorianCalendar start for step
		DatePicker openDate = new DatePicker();
		vBox.getChildren().add(openDate);
		//If step.start is set, show the value in the DatePicker
		if (poi.getOpening() != null)
			openDate.setValue(LocalDate.parse(poi.getOpening().toString());
		openDate.setOnAction(event -> {
			//Get selected value
			LocalDate localDate = openDate.getValue();
			try {
				//Convert LocalDate to XMLGregorianCalendar
				Periode periode = new Periode();
				XMLGregorianCalendar xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
				periode.
			} catch (javax.xml.datatype.DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		});*/

		//Duration
		Label durationLabel = new Label("Duration :");
		vBox.getChildren().add(durationLabel);
		TextField durationField = new TextField();

		if (poi.getDuration() != null)
			durationField.setText(poi.getDuration().toString());
		vBox.getChildren().add(durationField);

		durationField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (!"0123456789".contains(keyEvent.getCharacter())) {
					keyEvent.consume();
				}
			}
		});

		durationField.setOnAction(event -> {
			BigInteger bigInteger = new BigInteger(durationField.getText());
			poi.setDuration(bigInteger);
		});

		//Prices
		//Add prices
		Button addPrices = new Button("Add Prices");
		vBox.getChildren().add(addPrices);
		addPrices.setOnAction(event -> {
			if (poi.getPrices() == null) {
				Prices prices = new Prices();
				poi.setPrices(prices);
			}
			poi.getPrices().getPrice().add(new Price());
			try {
				poi(stage, poi, title);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		});

		if (poi.getPrices() != null) {
			for (int i = 0; i < poi.getPrices().getPrice().size(); i++) {
				Price price = poi.getPrices().getPrice().get(i);

				Label priceLabel = new Label("\n" + "Price " + (i + 1) + " :");
				vBox.getChildren().add(priceLabel);

				Label valueLabel = new Label("Prix :");
				vBox.getChildren().add(valueLabel);
				TextField valueField = new TextField();

				if (poi.getPrices().getPrice().get(i).getValue() != null)
					valueField.setText(poi.getPrices().getPrice().get(i).getValue().toString());
				vBox.getChildren().add(valueField);

				valueField.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent keyEvent) {
						if (!"0123456789".contains(keyEvent.getCharacter())) {
							keyEvent.consume();
						}
					}
				});

				valueField.setOnAction(event -> {
					BigInteger bigInteger = new BigInteger(valueField.getText());
					price.setValue(bigInteger);
				});

				Label conditionLabel = new Label("Conditions :");
				vBox.getChildren().add(conditionLabel);
				TextField conditionField = new TextField();

				if (poi.getPrices().getPrice().get(i).getConditions() != null) {
					String s = "";
					for (String condition : poi.getPrices().getPrice().get(i).getConditions().getCondition()) {
						s += condition + " ";
					}
					conditionField.setText(s);
				}
				vBox.getChildren().add(conditionField);

				conditionField.setOnAction(event -> {
					Conditions conditions = new Conditions();
					String[] tabCondition = conditionField.getText().trim().split(" ");
					for (String condition : tabCondition) {
						conditions.getCondition().add(condition);
					}
					price.setConditions(conditions);
				});

			}
		}


		Button register = new Button("Enregistrer");
		vBox.getChildren().add(register);
		register.setOnAction(event -> {
			//if (stringVide(fieldArrayList)) {
				poi.setName(nameField.getText());

				Position position = new Position();
				BigDecimal latitude = new BigDecimal(latitudeField.getText());
				BigDecimal longitude = new BigDecimal(longitudeField.getText());
				position.setLatitude(latitude);
				position.setLongitude(longitude);
				position.setAddress(addressField.getText());
				poi.setPosition(position);
				System.out.println(poi.getPosition().getAddress());

				Contact contact = new Contact();
				contact.setTelephone(phoneField.getText());
				contact.setMail(mailField.getText());
				contact.setWeb(webField.getText());
				poi.setContact(contact);

				poi.setDescription(descriptionField.getText());

				Styles styles = new Styles();
				styles.setStyle(styleField.getText());
				poi.setStyles(styles);

				Themes themes = new Themes();
				String[] tabTheme = themeField.getText().trim().split(" ");
				for (String theme : tabTheme) {
					themes.getTheme().add(theme);
				}
				poi.setThemes(themes);

				Types types = new Types();
				String[] tabType = typeField.getText().trim().split(" ");
				for (String type : tabType) {
					types.getType().add(type);
				}
				poi.setTypes(types);

				Photos photos = new Photos();
				String[] tabPhoto = photoField.getText().trim().split(" ");
				for (String photo : tabPhoto) {
					photos.getPhoto().add(photo);
				}
				poi.setPhotos(photos);

				poi.setId(UUID.randomUUID().toString());
			//}
		});
		Scene scene = new Scene(root, 300, 600);

		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();
	}

	public Periode getPeriode(){
		return this.itinerary.getPeriode();
	}

	public List<Days> getDays(){
		return this.itinerary.getDays();
	}

	private boolean stringVide(ArrayList<TextField> list) {
		for (TextField field : list) {
			if (!field.getText().equals(""))
				return false;
		}
		return true;
	}
}
