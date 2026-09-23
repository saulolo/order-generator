package com.vidanova;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {

    primaryStage.setTitle("🍃 VidanovaStore - Generador de Pedidos");

    // --- 1. Título principal ---
    Label lblTitulo = new Label("Generador de Confirmación de Pedidos");
    lblTitulo.getStyleClass().add("titulo-principal");

    // --- 2. Campos de entrada ---
    TextField txtNombre = new TextField();
    txtNombre.setPromptText("Ej: Eugenia Jojoa");

    TextField txtProducto = new TextField();
    txtProducto.setPromptText("Ej: 1 x Crema Despigmentante Coreana Arbutin + TXA (50g)");

    TextField txtTotal = new TextField();
    txtTotal.setPromptText("Ej: $89.900");

    TextField txtTelefono = new TextField();
    txtTelefono.setPromptText("Ej: 310 6714021");

    TextField txtDireccion = new TextField();
    txtDireccion.setPromptText("Ej: Calle/Carrera 20 # 19-30, barrio Capri (Pasto, Nariño)");

    // Grid para organizar los campos en dos columnas
    GridPane gridForm = new GridPane();
    gridForm.setHgap(12);
    gridForm.setVgap(12);

    gridForm.add(new Label("Nombre completo:"), 0, 0);
    gridForm.add(txtNombre, 1, 0);

    gridForm.add(new Label("Producto(s):"), 0, 1);
    gridForm.add(txtProducto, 1, 1);

    gridForm.add(new Label("Total a pagar:"), 0, 2);
    gridForm.add(txtTotal, 1, 2);

    gridForm.add(new Label("Teléfono:"), 0, 3);
    gridForm.add(txtTelefono, 1, 3);

    gridForm.add(new Label("Dirección de envío:"), 0, 4);
    gridForm.add(txtDireccion, 1, 4);

    // Hace que los campos de texto se estiren al ancho de la ventana
    ColumnConstraints colEtiquetas = new ColumnConstraints();
    ColumnConstraints colCampos = new ColumnConstraints();
    colCampos.setHgrow(Priority.ALWAYS);
    gridForm.getColumnConstraints().addAll(colEtiquetas, colCampos);

    // --- 3. Área de resultado (Markdown) ---
    TextArea txtResultado = new TextArea();
    txtResultado.setPromptText("Aquí se mostrará el mensaje generado...");
    txtResultado.setWrapText(true);
    txtResultado.setPrefRowCount(10);

    // --- 4. Botones de acción ---
    Button btnGenerar = new Button("⚡ Generar Mensaje");
    btnGenerar.getStyleClass().add("btn-primario");

    Button btnCopiar = new Button("📋 Copiar al Portapapeles");
    btnCopiar.getStyleClass().add("btn-secundario");

    Button btnLimpiar = new Button("Limpiar");
    btnLimpiar.getStyleClass().add("btn-limpiar");

    HBox panelBotones = new HBox(10, btnGenerar, btnCopiar, btnLimpiar);
    panelBotones.setAlignment(Pos.CENTER_LEFT);

    // --- 5. Lógica de los botones ---
    btnGenerar.setOnAction(e -> {
      if (txtNombre.getText().isBlank() || txtProducto.getText().isBlank() ||
          txtTotal.getText().isBlank() || txtTelefono.getText().isBlank() ||
          txtDireccion.getText().isBlank()) {

        mostrarAlerta(Alert.AlertType.WARNING, "Campos requeridos", "Por favor completa todos los campos antes de generar.");
        return;
      }

      // Text block de Java moderno con la plantilla exacta y emojis
      String mensaje = """
                    ¡Hola! Soy %s. Acabo de hacer un pedido en 🍃*VidanovaStore* con los siguientes detalles:

                    🛍️ *Productos:* %s
                    💰 *Total a pagar:* %s
                    📞 *Teléfono:* %s
                    🏠 *Dirección de envío:* %s

                    Confirmo que todos mis datos son correctos y que pagaré en efectivo al recibir mi pedido. 
                    En caso de no estar presente, dejaré el dinero en mi casa para que alguien más pueda recibirlo.

                    También entiendo que una vez generada la guía de mi pedido, no será posible cancelarlo y me comprometo a recibirlo. 🚚

                    Gracias y espero su confirmación. 😊
                    """.formatted(
          txtNombre.getText().trim(),
          txtProducto.getText().trim(),
          txtTotal.getText().trim(),
          txtTelefono.getText().trim(),
          txtDireccion.getText().trim()
      );

      txtResultado.setText(mensaje);
    });

    btnCopiar.setOnAction(e -> {
      if (txtResultado.getText().isBlank()) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Aviso", "Aún no has generado ningún mensaje.");
        return;
      }
      Clipboard clipboard = Clipboard.getSystemClipboard();
      ClipboardContent content = new ClipboardContent();
      content.putString(txtResultado.getText());
      clipboard.setContent(content);

      mostrarAlerta(Alert.AlertType.INFORMATION, "¡Listo!", "Mensaje copiado al portapapeles con éxito.");
    });

    btnLimpiar.setOnAction(e -> {
      txtNombre.clear();
      txtProducto.clear();
      txtTotal.clear();
      txtTelefono.clear();
      txtDireccion.clear();
      txtResultado.clear();
    });

    // --- 6. Ensamblado de la vista ---
    VBox layout = new VBox(15, lblTitulo, gridForm, panelBotones, new Label("Mensaje generado:"), txtResultado);
    layout.setPadding(new Insets(20));

    Scene scene = new Scene(layout, 650, 620);

    // Cargar hoja de estilos moderna
    var estilo = getClass().getResource("/style.css");
    if (estilo != null) {
      scene.getStylesheets().add(estilo.toExternalForm());
    }

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
    Alert alert = new Alert(tipo);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
  }


  public static void main(String[] args) {
    launch(args);
  }
}
