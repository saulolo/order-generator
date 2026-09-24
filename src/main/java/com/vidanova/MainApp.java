package com.vidanova;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * VidanovaStore - Generador de Confirmación de Pedidos.
 *
 * <p>Diseño moderno minimalista con panel dual (captura + previsualización),
 * paleta de marca morado pastel / verde pastel, letras blancas y validaciones fail-fast.
 */
public class MainApp extends Application {

    // Expresión regular que admite solo letras del alfabeto español y espacios
    private static final String SOLO_LETRAS_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

    // Flag para evitar bucles infinitos en el listener del formateo de precio
    private boolean actualizandoPrecio = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🍃 Order Generator — VidanovaStore");

        // ════════════════════════════════════════════════════════════
        // 1. HEADER
        // ════════════════════════════════════════════════════════════
        Label lblBrand = new Label("🍃 VidanovaStore");
        lblBrand.getStyleClass().add("brand-title");

        Label lblSubtitulo = new Label("Generador de Confirmación de Pedidos");
        lblSubtitulo.getStyleClass().add("brand-subtitle");

        VBox headerBox = new VBox(3, lblBrand, lblSubtitulo);
        headerBox.getStyleClass().add("header-box");

        // ════════════════════════════════════════════════════════════
        // 2. PANEL IZQUIERDO: FORMULARIO (7 CAMPOS OBLIGATORIOS)
        // ════════════════════════════════════════════════════════════
        Label lblFormTitulo = new Label("📋 Datos del Pedido");
        lblFormTitulo.getStyleClass().add("section-title");

        // 1. Nombre
        Label lblNombre = new Label("Nombre del cliente");
        lblNombre.getStyleClass().add("field-label");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Eugenia");
        txtNombre.getStyleClass().add("form-field");

        // 2. Producto(s)
        Label lblProducto = new Label("Producto(s)");
        lblProducto.getStyleClass().add("field-label");
        TextField txtProducto = new TextField();
        txtProducto.setPromptText("Ej: Crema Despigmentante Arbutin 7.0% + TXA 4.0% Tosowoong® (50g)");
        txtProducto.getStyleClass().add("form-field");

        // 3. Dirección
        Label lblDireccion = new Label("Dirección de entrega");
        lblDireccion.getStyleClass().add("field-label");
        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Ej: Colombia 20# 19 .30 - barrio Capri -");
        txtDireccion.getStyleClass().add("form-field");

        // 4. Ciudad
        Label lblCiudad = new Label("Ciudad / Municipio");
        lblCiudad.getStyleClass().add("field-label");
        TextField txtCiudad = new TextField();
        txtCiudad.setPromptText("Ej: PASTO");
        txtCiudad.getStyleClass().add("form-field");

        // 5. Departamento
        Label lblDepartamento = new Label("Departamento");
        lblDepartamento.getStyleClass().add("field-label");
        TextField txtDepartamento = new TextField();
        txtDepartamento.setPromptText("Ej: NARIÑO");
        txtDepartamento.getStyleClass().add("form-field");

        // 6. Teléfono
        Label lblTelefono = new Label("Teléfono de contacto");
        lblTelefono.getStyleClass().add("field-label");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Ej: 3106714021");
        txtTelefono.getStyleClass().add("form-field");

        // 7. Total a pagar (con formateo en tiempo real)
        Label lblTotal = new Label("Total a pagar (COD — contraentrega)");
        lblTotal.getStyleClass().add("field-label");
        TextField txtTotal = new TextField();
        txtTotal.setPromptText("Ej: 89900  →  $89.900");
        txtTotal.getStyleClass().add("form-field");

        // Formateo reactivo en tiempo real al escribir el precio
        txtTotal.textProperty().addListener((obs, oldVal, newVal) -> {
            if (actualizandoPrecio) return;
            actualizandoPrecio = true;
            try {
                String digitos = newVal.replaceAll("[^0-9]", "");
                String formateado = digitos.isEmpty() ? "" : formatearPrecio(digitos);
                txtTotal.setText(formateado);
                txtTotal.positionCaret(formateado.length());
            } finally {
                actualizandoPrecio = false;
            }
        });

        // Contenedores individuales de campo
        VBox campoNombre = new VBox(4, lblNombre, txtNombre);
        VBox campoProducto = new VBox(4, lblProducto, txtProducto);
        VBox campoDireccion = new VBox(4, lblDireccion, txtDireccion);
        VBox campoCiudad = new VBox(4, lblCiudad, txtCiudad);
        VBox campoDepartamento = new VBox(4, lblDepartamento, txtDepartamento);
        VBox campoTelefono = new VBox(4, lblTelefono, txtTelefono);
        VBox campoTotal = new VBox(4, lblTotal, txtTotal);

        Separator separadorForm = new Separator();
        separadorForm.getStyleClass().add("form-separator");

        // Botones de acción del formulario
        Button btnGenerar = new Button("⚡ Generar Mensaje");
        btnGenerar.getStyleClass().add("btn-primary");
        btnGenerar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnGenerar, Priority.ALWAYS);

        Button btnLimpiar = new Button("🗑 Limpiar");
        btnLimpiar.getStyleClass().add("btn-secondary");

        HBox panelBotonesForm = new HBox(12, btnGenerar, btnLimpiar);

        VBox formContent = new VBox(12,
                lblFormTitulo,
                campoNombre,
                campoProducto,
                campoDireccion,
                campoCiudad,
                campoDepartamento,
                campoTelefono,
                campoTotal,
                separadorForm,
                panelBotonesForm
        );
        formContent.setPadding(new Insets(24, 26, 24, 26));

        // ScrollPane para asegurar que los 7 campos se vean perfecto en cualquier resolución
        ScrollPane scrollForm = new ScrollPane(formContent);
        scrollForm.setFitToWidth(true);
        scrollForm.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollForm.getStyleClass().addAll("form-panel", "form-scroll");
        scrollForm.setMinWidth(380);
        scrollForm.setPrefWidth(400);
        scrollForm.setMaxWidth(430);

        // ════════════════════════════════════════════════════════════
        // 3. PANEL DERECHO: PREVISUALIZACIÓN Y COPIA
        // ════════════════════════════════════════════════════════════
        Label lblPreviewTitulo = new Label("👁 Previsualización del Mensaje");
        lblPreviewTitulo.getStyleClass().add("section-title");

        TextArea txtResultado = new TextArea();
        txtResultado.setPromptText("Aquí se mostrará el mensaje generado...");
        txtResultado.setEditable(false);
        txtResultado.setWrapText(true);
        txtResultado.getStyleClass().add("preview-area");
        VBox.setVgrow(txtResultado, Priority.ALWAYS);

        Button btnCopiar = new Button("📋 Copiar al Portapapeles");
        btnCopiar.getStyleClass().add("btn-copy");
        btnCopiar.setDisable(true);

        Label copyStatusLabel = new Label("✅ ¡Copiado al portapapeles!");
        copyStatusLabel.getStyleClass().add("copy-status-label");
        copyStatusLabel.setVisible(false);

        HBox panelCopia = new HBox(14, btnCopiar, copyStatusLabel);
        panelCopia.setAlignment(Pos.CENTER_LEFT);

        VBox previewPanel = new VBox(14, lblPreviewTitulo, txtResultado, panelCopia);
        previewPanel.setPadding(new Insets(24, 26, 24, 26));
        previewPanel.getStyleClass().add("preview-panel");
        HBox.setHgrow(previewPanel, Priority.ALWAYS);

        // Divisor vertical entre paneles
        Separator panelDivider = new Separator(Orientation.VERTICAL);
        panelDivider.getStyleClass().add("panel-divider");

        HBox contentHBox = new HBox(0, scrollForm, panelDivider, previewPanel);
        contentHBox.getStyleClass().add("content-hbox");

        // ════════════════════════════════════════════════════════════
        // 4. FOOTER
        // ════════════════════════════════════════════════════════════
        Label lblFooter = new Label("© 2026 VidanovaStore  •  Todos los derechos reservados");
        lblFooter.getStyleClass().add("footer-label");

        HBox footerBox = new HBox(lblFooter);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.getStyleClass().add("footer-box");

        // ════════════════════════════════════════════════════════════
        // 5. ENSAMBLADO EN BORDERPANE
        // ════════════════════════════════════════════════════════════
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");
        root.setTop(headerBox);
        root.setCenter(contentHBox);
        root.setBottom(footerBox);

        // ════════════════════════════════════════════════════════════
        // 6. LÓGICA Y VALIDACIONES (EXACTAS DE ORDER-GENERATOR)
        // ════════════════════════════════════════════════════════════
        btnGenerar.setOnAction(e -> {
            copyStatusLabel.setVisible(false);

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
                txtNombre.selectAll();
                return;
            }

            // Validación 3: Ciudad solo texto
            if (!ciudad.matches(SOLO_LETRAS_REGEX)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Ciudad inválida", "El campo 'Ciudad' debe contener únicamente letras.");
                txtCiudad.requestFocus();
                txtCiudad.selectAll();
                return;
            }

            // Validación 4: Departamento solo texto
            if (!departamento.matches(SOLO_LETRAS_REGEX)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Departamento inválido", "El campo 'Departamento' debe contener únicamente letras.");
                txtDepartamento.requestFocus();
                txtDepartamento.selectAll();
                return;
            }

            // Validación 5: Teléfono solo números
            if (!telefono.matches("^[0-9]+$")) {
                mostrarAlerta(Alert.AlertType.ERROR, "Teléfono inválido", "El campo 'Teléfono' debe contener únicamente números (sin letras, espacios ni símbolos).");
                txtTelefono.requestFocus();
                txtTelefono.selectAll();
                return;
            }

            // Validación 6: Total a pagar numérico
            String soloDigitosTotal = total.replaceAll("[^0-9]", "");
            if (soloDigitosTotal.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Total inválido", "El campo 'Total a pagar' debe contener un valor numérico válido.");
                txtTotal.requestFocus();
                txtTotal.selectAll();
                return;
            }

            // Formateo automático del precio con separador de miles por puntos
            String totalFormateado = formatearPrecio(total);

            // Mensaje de salida oficial de order-generator (inalterado)
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
            btnCopiar.setDisable(false);
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

            // Feedback visual limpio
            copyStatusLabel.setText("✅ ¡Copiado al portapapeles!");
            copyStatusLabel.setVisible(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
            pause.setOnFinished(ev -> copyStatusLabel.setVisible(false));
            pause.play();
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
            btnCopiar.setDisable(true);
            copyStatusLabel.setVisible(false);
            txtNombre.requestFocus();
        });

        // ════════════════════════════════════════════════════════════
        // 7. ESCENA Y ESTILOS
        // ════════════════════════════════════════════════════════════
        Scene scene = new Scene(root, 980, 770);

        var estilo = getClass().getResource("/style.css");
        if (estilo != null) {
            scene.getStylesheets().add(estilo.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(860);
        primaryStage.setMinHeight(600);
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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}