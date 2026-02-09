package view;

public enum VisualizationMode {
    EXPLORATION("Exploración"),    // muestra todo el grafo y caminos explorados
    FINAL_ROUTE("Ruta Final");     // muestra solo ruta final
    
    private final String displayName;
    
    VisualizationMode(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}