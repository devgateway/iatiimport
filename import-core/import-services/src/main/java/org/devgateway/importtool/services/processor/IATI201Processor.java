package org.devgateway.importtool.services.processor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.devgateway.importtool.services.processor.helper.Field;
import org.devgateway.importtool.services.processor.helper.ISourceProcessor;
import org.devgateway.importtool.services.processor.helper.InternalDocument;

public class IATI201Processor implements ISourceProcessor {
	
	private String DEFAULT_ID_FIELD = "iati-identifier";

	@Override
	public void setInput(InputStream input) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Field> getFields() {
		List<Field> list = new ArrayList<Field>();

		Field f1 = new Field();
		f1.setFieldName("iati-identifier");
		List<String> listValue1 = new ArrayList<String>();
		listValue1.add("1");
		listValue1.add("2");
		listValue1.add("3");
		f1.setPossibleValues(listValue1);
		f1.addFilter("2");
		list.add(f1);

		Field f2 = new Field();
		f2.setFieldName("title");
		List<String> listValue2 = new ArrayList<String>();
		listValue2.add("Title 1");
		listValue2.add("Title 2");
		listValue2.add("Title 3");
		f2.setPossibleValues(listValue2);
		f2.addFilter("Title 2");
		list.add(f2);

		Field f3 = new Field();
		f3.setFieldName("activity-status");
		List<String> listValue3 = new ArrayList<String>();
		listValue3.add("Status 1");
		listValue3.add("Status 2");
		listValue3.add("Status 3");
		f3.setPossibleValues(listValue3);
		f3.addFilter("Status 2");
		list.add(f3);

		return list;
	}

	@Override
	public List<InternalDocument> getDocuments() {
		List<InternalDocument> list = new ArrayList<InternalDocument>();
		InternalDocument doc1 = new InternalDocument();
		doc1.addStringField("iati-identifier", "1");
		doc1.addStringField("title", "Title 1");
		doc1.addStringField("activity-status", "Status 1");
		list.add(doc1);

		InternalDocument doc2 = new InternalDocument();
		doc2.addStringField("iati-identifier", "2");
		doc2.addStringField("title", "Title 2");
		doc2.addStringField("activity-status", "Status 2");
		list.add(doc2);

		InternalDocument doc3 = new InternalDocument();
		doc3.addStringField("iati-identifier", "3");
		doc3.addStringField("title", "Title 3");
		doc3.addStringField("activity-status", "Status 3");
		list.add(doc3);

		return list;
	}

	@Override
	public String getIdField() {
		return DEFAULT_ID_FIELD;
	}

}
