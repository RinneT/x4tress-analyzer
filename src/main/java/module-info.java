module org.soh.x4.x4tress_analyzer {
    requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires java.base;
	requires java.xml;
	requires java.xml.crypto;
	requires org.slf4j;
	requires java.sql;
	
	opens org.soh.x4.x4tress_analyzer.model to javafx.base;
	
    exports org.soh.x4.x4tress_analyzer.gui;
}
