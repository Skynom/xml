package controller;
import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.*;

import generated.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings("ALL")
public class VisitPlanController {

	public static void main(String[] args) {

		Itinerary i = new Itinerary();
		try {
			Itinerary i1 = sample("iti1.xml");
			Itinerary i2 = sample("iti2.xml");
			Itineraries iti=new Itineraries();
			ArrayList<Itinerary> ar=new ArrayList<>();
			ar.add(i1);
			ar.add(i2);
			iti.setItineraries(ar);
			persisteColl(ar);
			//System.out.println(storeXmlInFile("iti1.xml"));

			//persiste(i1);
			//persiste(i2);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		/*Itinerary it=new Itinerary();

		Budget b=new Budget();
		b.setDevise("Euro");
		b.setValue(BigInteger.valueOf(Integer.valueOf(5000)));
		//Budget
		it.setBudget(b);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		try {
			XMLGregorianCalendar beg = DatatypeFactory.newInstance()
					.newXMLGregorianCalendarDate(cal.get(
							Calendar.YEAR), 
							cal.get(Calendar.MONTH)+1, 
							cal.get(Calendar.DAY_OF_MONTH), 
							DatatypeConstants.FIELD_UNDEFINED);
			
		GregorianCalendar fin = new GregorianCalendar();
		fin.setTime(new Date(2020,9,13));
			XMLGregorianCalendar end = DatatypeFactory.newInstance()
					.newXMLGregorianCalendarDate(fin.get(
							Calendar.YEAR), 
							fin.get(Calendar.MONTH)+1, 
							fin.get(Calendar.DAY_OF_MONTH), 
							DatatypeConstants.FIELD_UNDEFINED);
		
		Periode p = new Periode();
		p.setStart(beg);
		p.setEnd(end);
		//Période
		it.setPeriode(p);
		String s1="bjr";
		String s2="bjrbjr";
		ArrayList <String> ls=new ArrayList<String>();
		ls.add(s1);ls.add(s2);

		Conditions c=new Conditions();
		c.setCondition(ls);

		Price price = new Price();
		price.setValue(BigInteger.valueOf(Integer.valueOf(300)));
		price.setConditions(c);

		Price price2 = new Price();
		price2.setValue(BigInteger.valueOf(Integer.valueOf(400)));
		price2.setConditions(c);

		Prices prices = new Prices();
		prices.

		Styles style = new Styles();
		style.setStyle("rouge");


		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		*/

	}

	public void truc(){
		Itinerary itinerary;
		Document document;
		String file="";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(file));
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
			Element root = document.getDocumentElement();
			Node node = root.getFirstChild();

			itinerary = (Itinerary) node;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void persisteColl(List<Itinerary> i) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Itineraries.class);
		Marshaller mar=jc.createMarshaller();
		mar.setProperty("jaxb.encoding", "UTF-8");
		mar.setProperty("jaxb.formatted.output", true);
		mar.marshal(i, new File("plan.xml"));
	}

	public static void persiste(Itinerary i) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(Itinerary.class);
		Marshaller mar=jc.createMarshaller();
		mar.setProperty("jaxb.encoding", "UTF-8");
		mar.setProperty("jaxb.formatted.output", true);
		mar.marshal(i, new File("plan.xml"));
	}

	public static void delete(Itinerary i ) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(Itinerary.class);
		Marshaller mar=jc.createMarshaller();
		mar.setProperty("jaxb.encoding", "UTF-8");
		mar.setProperty("jaxb.formatted.output", true);
		mar.marshal(null, new File("plan.xml"));
	}

	public static String storeXmlInFile(String file) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext jc = JAXBContext.newInstance(Itinerary.class);
		Marshaller mar = jc.createMarshaller();
		mar.setProperty("jaxb.encoding", "UTF-8");
		mar.setProperty("jaxb.formatted.output", true);
		Unmarshaller un = jc.createUnmarshaller();
		Itinerary i = (Itinerary) un.unmarshal(new File(file));
		mar.marshal(i,writer);
		return writer.toString();
	}

	public static void sample(){
		ObjectFactory of = new ObjectFactory();
	}


	public static Itinerary sample(String file) throws JAXBException{
		JAXBContext jc = JAXBContext.newInstance(Itinerary.class);
		Unmarshaller un = jc.createUnmarshaller();
		Itinerary i = (Itinerary) un.unmarshal(new File(file));
		return i;
	}

}
