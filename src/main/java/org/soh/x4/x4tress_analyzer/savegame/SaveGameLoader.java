package org.soh.x4.x4tress_analyzer.savegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.DataStorage;
import org.soh.x4.x4tress_analyzer.model.ProcessedEvent;
import org.soh.x4.x4tress_analyzer.processor.EventProcessor;
import org.xml.sax.SAXException;

/**
 * Loads the SaveGame file
 * @author Son of Hubert
 *
 */
public class SaveGameLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveGameLoader.class);

	public DataStorage loadFile(File file) throws ParserConfigurationException, SAXException, IOException {
		FileInputStream originalInputStream = new FileInputStream(file);
		InputStream inputStream = originalInputStream;
		LOGGER.info("Loading file: " + file.getAbsolutePath());
		if (file.getName().endsWith(".gz")) {
			LOGGER.info("Unzipping file");
			inputStream = new GZIPInputStream(inputStream);
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saveGameParser = factory.newSAXParser();
		SaveGameHandler saveGameHandler = new SaveGameHandler();
		
		saveGameParser.parse(inputStream, saveGameHandler);
		
		DataStorage result = saveGameHandler.getData();
		System.out.println("Successfully parsed file " + file.getAbsolutePath());
		
		originalInputStream.close();
		inputStream.close();
		
		EventProcessor processor = new EventProcessor();
		List<ProcessedEvent> processGlobalEvents = processor.processGlobalEvents(result.getGlobalEvents());
		result.setProcessedEvents(processGlobalEvents);
		return result;
	}
	
}
