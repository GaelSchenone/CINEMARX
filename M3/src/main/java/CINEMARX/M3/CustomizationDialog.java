package CINEMARX.M3;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CustomizationDialog extends JDialog {
    
    private Producto producto;
    private String opcionSeleccionada = "";
    private boolean aceptado = false;
    
    public CustomizationDialog(Frame owner, Producto p) {
        super(owner, "Personalizar: " + p.getNombre(), true);
        this.producto = p;
        initComponents();
        setSize(450, 480); 
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(60, 60, 60));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("Personaliza tu " + producto.getNombre().toUpperCase(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(60, 60, 60));
        
        JComponent customOptions = createOptionsPanel(producto.getCategoria(), producto.getNombre());
        centerPanel.add(customOptions);
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        scrollPane.getViewport().setBackground(new Color(60, 60, 60));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botones de control
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(60, 60, 60));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> {
            aceptado = false;
            dispose();
        });
        
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setBackground(new Color(220, 50, 50));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.addActionListener(e -> {
            extractOption(customOptions); 
            aceptado = true;
            dispose();
        });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnAceptar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JComponent createOptionsPanel(String categoria, String nombreProducto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(60, 60, 60));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblOpcion = new JLabel();
        lblOpcion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblOpcion.setForeground(Color.LIGHT_GRAY);
        panel.add(lblOpcion);

        Map<String, String[]> optionsMap = new HashMap<>();
        String nombreLower = producto.getNombre().toLowerCase(); 
        
        // --- OPCIONES GLOBALES ---
        String[] pochocloOptions = new String[]{"Salado", "Dulce", "Mixto (Dulce y Salado)"};
        String[] bebidaOptions = new String[]{"Con Hielo", "Sin Hielo"};

        // 🛑 Lógica de EXCLUSIÓN
        if (nombreLower.contains("gomita") || nombreLower.contains("m&m")) {
             lblOpcion.setText("🚫 Este producto no requiere personalización.");
             optionsMap.put("Añadir", new String[]{"Añadir al carrito"});
        } 
        
        // 🍿 Lógica de personalización específica (Comida/Snacks)
        else if (nombreLower.contains("hot dog") || nombreLower.contains("pancho")) {
            optionsMap.put("Aderezo", new String[]{"Mayonesa", "Ketchup", "Mostaza", "Ninguno"});
            optionsMap.put("Acompañamiento", new String[]{"Con Papas Fritas", "Sin Papas Fritas"});
            lblOpcion.setText("Personaliza tu Hot Dog (Pancho):");
            
        } else if (nombreLower.contains("nachos") || nombreLower.contains("totopos")) {
            optionsMap.put("Extra", new String[]{"Con Bacon", "Sin Bacon"});
            lblOpcion.setText("Personaliza tus Nachos:");
            
        } else if (categoria.equals("Combos")) {
            // 🚨 Lógica de personalización de Combos 🚨
            
            // Palabras clave para el Combo FAMILIAR (2 Phoclos y 2 Gaseosas)
            final String COMBO_FAMILIAR_CLAVES = "familiar|cuatro|4"; 

            if (nombreLower.matches(".*(" + COMBO_FAMILIAR_CLAVES + ").*")) {
                
                // --- COMBO FAMILIAR (4 ITEMS): 2 Pochoclos y 2 Gaseosas ---
                lblOpcion.setText("Personaliza los 4 ítems de tu Combo Familiar:");
                
                optionsMap.put("Pochoclo 1", pochocloOptions);
                optionsMap.put("Pochoclo 2", pochocloOptions);
                
                optionsMap.put("Gaseosa 1", bebidaOptions);
                optionsMap.put("Gaseosa 2", bebidaOptions);
                
            } else if (nombreLower.contains("combo") || nombreLower.contains("pareja")) {
                
                // --- COMBO PAREJA/BÁSICO (3 ITEMS): 1 Pochoclo y 2 Gaseosas ---
                lblOpcion.setText("Personaliza los 3 ítems de tu Combo Pareja:");
                
                optionsMap.put("Pochoclo 1", pochocloOptions); // Usamos '1' para consistencia

                optionsMap.put("Gaseosa 1", bebidaOptions);
                optionsMap.put("Gaseosa 2", bebidaOptions);
                
            }
            
        } else {
            // Lógica de personalización general (para items individuales)
            switch (categoria) {
                case "Snacks":
                    lblOpcion.setText("¿Cómo quieres tus pochoclos?");
                    optionsMap.put("Sabor", pochocloOptions);
                    break;
                case "Bebidas":
                    lblOpcion.setText("Opciones de tu bebida:");
                    optionsMap.put("Extras", new String[]{"Con Hielo", "Sin Hielo", "Doble Vaso"});
                    break;
                case "Comida":
                    lblOpcion.setText("Añade un aderezo (Opcional):");
                    optionsMap.put("Aderezo", new String[]{"Mayonesa", "Ketchup", "Mostaza", "Ninguno"});
                    break;
                default:
                    lblOpcion.setText("No hay opciones de personalización para este producto.");
                    optionsMap.put("Añadir", new String[]{"Añadir sin personalización"});
                    break; 
            }
        }
        
        // Generar los grupos de Radio Buttons
        JPanel optionsContainer = new JPanel();
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setBackground(new Color(60, 60, 60));
        
        Map<String, ButtonGroup> groups = new HashMap<>();

        for (Map.Entry<String, String[]> entry : optionsMap.entrySet()) {
            String groupName = entry.getKey();
            String[] options = entry.getValue();
            
            JLabel lblGroup = new JLabel("⦿ " + groupName + ":");
            lblGroup.setFont(new Font("Arial", Font.BOLD, 14));
            lblGroup.setForeground(new Color(255, 180, 0)); 
            lblGroup.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
            optionsContainer.add(lblGroup);

            ButtonGroup group = new ButtonGroup();
            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            radioPanel.setBackground(new Color(60, 60, 60));
            
            for (String option : options) {
                JRadioButton radio = new JRadioButton(option);
                radio.setActionCommand(groupName + ": " + option); 
                radio.setBackground(new Color(60, 60, 60));
                radio.setForeground(Color.WHITE);
                radio.setFont(new Font("Arial", Font.PLAIN, 14));
                group.add(radio);
                radioPanel.add(radio);
            }
            
            if (group.getButtonCount() > 0) {
                group.getElements().nextElement().setSelected(true); 
            }
            
            optionsContainer.add(radioPanel);
            groups.put(groupName, group);
        }
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(optionsContainer);
        
        panel.putClientProperty("ButtonGroupsMap", groups); 
        return panel; 
    }
    
    private void extractOption(JComponent customOptions) {
        @SuppressWarnings("unchecked")
        Map<String, ButtonGroup> groups = (Map<String, ButtonGroup>) customOptions.getClientProperty("ButtonGroupsMap");
        
        StringBuilder selectedOptions = new StringBuilder();
        
        if (groups != null) {
            for (ButtonGroup group : groups.values()) {
                if (group.getSelection() != null) {
                    selectedOptions.append(group.getSelection().getActionCommand()).append("; ");
                }
            }
        }
        
        this.opcionSeleccionada = selectedOptions.length() > 0 
            ? selectedOptions.substring(0, selectedOptions.length() - 2)
            : "";
    }
    
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    public boolean isAceptado() {
        return aceptado;
    }
}

