module org.soh.x4.x4tress_analyzer {
    requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires java.base;
	requires java.xml;
	requires java.xml.crypto;
	requires org.slf4j;
	
	opens org.soh.x4.x4tress_analyzer.savegame.sax to javafx.base;
	
    exports org.soh.x4.x4tress_analyzer.gui;
}
