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
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainApp extends Application {

  // Expresión regular que admite solo letras del alfabeto español y espacios
  private static final String SOLO_LETRAS_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("🍃 VidanovaStore - Generador de Pedidos");

    // --- 1. Título principal ---
    Label lblTitulo = new Label("Generador de Confirmación de Pedidos");
    lblTitulo.getStyleClass().add("titulo-principal");

    // --- 2. Campos de entrada (7 campos) ---
    TextField txtNombre = new TextField();
    txtNombre.setPromptText("Ej: Eugenia");

    TextField txtProducto = new TextField();
    txtProducto.setPromptText("Ej: Crema Despigmentante Arbutin 7.0% + TXA 4.0% Tosowoong® (50g)");

    TextField txtDireccion = new TextField();
    txtDireccion.setPromptText("Ej: Colombia 20# 19 .30 - barrio Capri -");

    TextField txtCiudad = new TextField();
    txtCiudad.setPromptText("Ej: PASTO");

    TextField txtDepartamento = new TextField();
    txtDepartamento.setPromptText("Ej: NARIÑO");

    TextField txtTelefono = new TextField();
    txtTelefono.setPromptText("Ej: 3106714021");

    TextField txtTotal = new TextField();
    txtTotal.setPromptText("Ej: 79000 o $79.000");

    // Grid para organizar los campos en dos columnas
    GridPane gridForm = new GridPane();
    gridForm.setHgap(12);
    gridForm.setVgap(10);

    gridForm.add(new Label("Nombre:"), 0, 0);
    gridForm.add(txtNombre, 1, 0);

    gridForm.add(new Label("Producto(s):"), 0, 1);
    gridForm.add(txtProducto, 1, 1);

    gridForm.add(new Label("Dirección:"), 0, 2);
    gridForm.add(txtDireccion, 1, 2);

    gridForm.add(new Label("Ciudad:"), 0, 3);
    gridForm.add(txtCiudad, 1, 3);

    gridForm.add(new Label("Departamento:"), 0, 4);
    gridForm.add(txtDepartamento, 1, 4);

    gridForm.add(new Label("Teléfono:"), 0, 5);
    gridForm.add(txtTelefono, 1, 5);

    gridForm.add(new Label("Total a pagar:"), 0, 6);
    gridForm.add(txtTotal, 1, 6);

    ColumnConstraints colEtiquetas = new ColumnConstraints();
    ColumnConstraints colCampos = new ColumnConstraints();
    colCampos.setHgrow(Priority.ALWAYS);
    gridForm.getColumnConstraints().addAll(colEtiquetas, colCampos);

    // --- 3. Área de resultado (Markdown) ---
    TextArea txtResultado = new TextArea();
    txtResultado.setPromptText("Aquí se mostrará el mensaje generado...");
    txtResultado.setWrapText(true);
    txtResultado.setPrefRowCount(9);

    // --- 4. Botones de acción ---
    Button btnGenerar = new Button("⚡ Generar Mensaje");
    btnGenerar.getStyleClass().add("btn-primario");

    Button btnCopiar = new Button("📋 Copiar al Portapapeles");
    btnCopiar.getStyleClass().add("btn-secundario");

    Button btnLimpiar = new Button("Limpiar");
    btnLimpiar.getStyleClass().add("btn-limpiar");

    HBox panelBotones = new HBox(10, btnGenerar, btnCopiar, btnLimpiar);
    panelBotones.setAlignment(Pos.CENTER_LEFT);

    // --- 5. Lógica de los botones y Validaciones ---
    btnGenerar.setOnAction(e -> {
      String nombre = txtNombre.getText().trim();
      String producto = txtProducto.getText().trim();
      String direccion = txtDireccion.getText().trim();
      String ciudad = txtCiudad.getText().trim();
      String departamento = txtDepartamento.getText().trim();
      String telefono = txtTelefono.getText().trim();
      String total = txtTotal.getText().trim();

      // Validación 1: Campos vacíos
      if (nombre.isBlank() || producto.isBlank() || direccion.isBlank() ||
          ciudad.isBlank() || departamento.isBlank() || telefono.isBlank() || total.isBlank()) {
        mostrarAlerta(Alert.AlertType.WARNING, "Campos requeridos", "Por favor completa todos los campos antes de generar.");
        return;
      }

      // Validación 2: Nombre solo texto
      if (!nombre.matches(SOLO_LETRAS_REGEX)) {
        mostrarAlerta(Alert.AlertType.ERROR, "Nombre inválido", "El campo 'Nombre' debe contener únicamente letras.");
        txtNombre.requestFocus();
        return;
      }

      // Validación 3: Ciudad solo texto
      if (!ciudad.matches(SOLO_LETRAS_REGEX)) {
        mostrarAlerta(Alert.AlertType.ERROR, "Ciudad inválida", "El campo 'Ciudad' debe contener únicamente letras.");
        txtCiudad.requestFocus();
        return;
      }

      // Validación 4: Departamento solo texto
      if (!departamento.matches(SOLO_LETRAS_REGEX)) {
        mostrarAlerta(Alert.AlertType.ERROR, "Departamento inválido", "El campo 'Departamento' debe contener únicamente letras.");
        txtDepartamento.requestFocus();
        return;
      }

      // Validación 5: Teléfono solo números
      if (!telefono.matches("^[0-9]+$")) {
        mostrarAlerta(Alert.AlertType.ERROR, "Teléfono inválido", "El campo 'Teléfono' debe contener únicamente números (sin letras, espacios ni símbolos).");
        txtTelefono.requestFocus();
        return;
      }

      // Validación 6: Total a pagar numérico
      String soloDigitosTotal = total.replaceAll("[^0-9]", "");
      if (soloDigitosTotal.isEmpty()) {
        mostrarAlerta(Alert.AlertType.ERROR, "Total inválido", "El campo 'Total a pagar' debe contener un valor numérico válido.");
        txtTotal.requestFocus();
        return;
      }

      // Formateo automático del precio con separador de miles por puntos
      String totalFormateado = formatearPrecio(total);

      String mensaje = """
                    ¡Hola %s! 🌟

                    Acabamos de recibir tu compra del producto -- %s -- en nuestra tienda 🍃VidanovaStore

                    El cual será entregado en:

                    *Dirección:* %s
                    *Ciudad:* %s
                    *Departamento:* %s
                    *Teléfono:* %s
                    *Total a Pagar:* %s

                    El *envío* es totalmente *Gratis* y el pago es *Contra Entrega* para tu seguridad 🛍️🤍
                    🚚 Por favor *confírmanos* si tus datos son correctos para despachar tu pedido *ahora* 🤍
                    """.formatted(
          nombre,
          producto,
          direccion,
          ciudad.toUpperCase(),
          departamento.toUpperCase(),
          telefono,
          totalFormateado
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
      txtDireccion.clear();
      txtCiudad.clear();
      txtDepartamento.clear();
      txtTelefono.clear();
      txtTotal.clear();
      txtResultado.clear();
    });

    // --- 6. Ensamblado de la vista ---
    VBox layout = new VBox(14, lblTitulo, gridForm, panelBotones, new Label("Mensaje generado:"), txtResultado);
    layout.setPadding(new Insets(20));

    Scene scene = new Scene(layout, 650, 700);

    var estilo = getClass().getResource("/style.css");
    if (estilo != null) {
      scene.getStylesheets().add(estilo.toExternalForm());
    }

    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private String formatearPrecio(String entrada) {
    try {
      String soloNumeros = entrada.replaceAll("[^0-9]", "");
      if (soloNumeros.isEmpty()) {
        return entrada;
      }

      long valor = Long.parseLong(soloNumeros);

      DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.getDefault());
      simbolos.setGroupingSeparator('.');

      DecimalFormat formato = new DecimalFormat("#,###", simbolos);
      return "$" + formato.format(valor);
    } catch (Exception ex) {
      return entrada;
    }
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