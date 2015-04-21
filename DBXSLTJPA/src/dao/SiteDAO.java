package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import model.Equipment;
import model.Site;
import model.SiteList;
import model.Tower;

public class SiteDAO {

	EntityManagerFactory factory = Persistence
			.createEntityManagerFactory("DBXSLTJPA");
	EntityManager em = null;

	public Site findSite(@PathParam("ID") int siteId) {
		Site site = null;
		em = factory.createEntityManager();
		em.getTransaction().begin();
		site = em.find(Site.class, siteId);
		em.getTransaction().commit();
		em.close();
		return site;
	}

	public List<Site> findAllSites() {
		List<Site> sites = new ArrayList<Site>();
		em = factory.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createNamedQuery("findAllSites");
		sites = query.getResultList();
		em.getTransaction().commit();
		em.close();
		return sites;
	}

	public void exportSiteDatabaseToXmlFile(SiteList siteList,
			String xmlFileName) {
		File xmlFile = new File(xmlFileName);
		try {
			JAXBContext jaxb = JAXBContext.newInstance(SiteList.class);
			Marshaller marshaller = jaxb.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(siteList, xmlFile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void convertXmlFileToOutputFile(String inputXmlFileName,
			String outputXmlFileName, String xsltFileName) {
		File inputXmlFile = new File(inputXmlFileName);
		File outputXmlFile = new File(outputXmlFileName);
		File xsltFile = new File(xsltFileName);

		StreamSource source = new StreamSource(inputXmlFile);
		StreamSource xslt = new StreamSource(xsltFile);
		StreamResult output = new StreamResult(outputXmlFile);

		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer transformer = factory.newTransformer(xslt);
			transformer.transform(source, output);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SiteDAO dao = new SiteDAO();
		//Site site = dao.findSite(1);

		List<Site> sites = dao.findAllSites();
		for (Site s: sites) {
			System.out.println(s.getId()+":"+s.getName());
		}

		SiteList theSites = new SiteList();
		theSites.setSites(sites);

		dao.exportSiteDatabaseToXmlFile(theSites, "xml/sites.xml");

		dao.convertXmlFileToOutputFile("xml/sites.xml", "xml/sites.html",
				"xml/sites2html.xsl");
		dao.convertXmlFileToOutputFile("xml/sites.xml", "xml/equipments.html",
				"xml/sites2equipment.xsl");
	}
}
