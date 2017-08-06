package beans;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class locationMaster {
	boolean disabled = false;
	String dspLocn;
	int locationBarcode;
	String locnClass;
	String locnPickSeq;
	String area;
	String zone;
	String aisle;
	String bay;
	String lvl;
	SessionFactory sessionFactory;
	String selected;
	int pageno = 1;
	boolean pdisabled = false;

	public boolean isPdisabled() {
		return pdisabled;
	}

	public void setPdisabled(boolean pdisabled) {
		this.pdisabled = pdisabled;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getPageno() {
		return pageno;
	}

	public void setPageno(int pageno) {
		this.pageno = pageno;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getDspLocn() {
		return dspLocn;
	}

	public void setDspLocn(String dspLocn) {
		this.dspLocn = dspLocn;
	}

	public int getLocationBarcode() {
		return locationBarcode;
	}

	public void setLocationBarcode(int locationBarcode) {
		this.locationBarcode = locationBarcode;
	}

	public String getLocnClass() {
		return locnClass;
	}

	public void setLocnClass(String locnClass) {
		this.locnClass = locnClass;
	}

	public String getLocnPickSeq() {
		return locnPickSeq;
	}

	public void setLocnPickSeq(String locnPickSeq) {
		this.locnPickSeq = locnPickSeq;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getAisle() {
		return aisle;
	}

	public void setAisle(String aisle) {
		this.aisle = aisle;
	}

	public String getBay() {
		return bay;
	}

	public void setBay(String bay) {
		this.bay = bay;
	}

	public String getLvl() {
		return lvl;
	}

	public void setLvl(String lvl) {
		this.lvl = lvl;
	}

	public List<Location> getList() {

		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		List<Location> locations;
		int pagesize = 5;
		if (pageno == 1) {
			pdisabled = true;
		} else
			pdisabled = false;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {

			int pg = (pageno - 1) * pagesize;
			int totpages = session.createQuery("FROM Location").list().size();
			if (totpages == 0) {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "No Locations Present",
						"Location List is empty");
				faces.addMessage(null, message);
				return null;
			} else {
				Query q = session.createQuery("FROM Location");
				q.setFirstResult(pg);
				q.setMaxResults(pagesize);
				locations = (List<Location>) q.list();
				if (locations.isEmpty() || (pg + pagesize) == totpages
						|| locations.size() < 5) {
					disabled = true;
					pg = pg / 5;
				} else
					disabled = false;
				faces.getExternalContext().getSessionMap()
						.put("locationList", locations);
				return locations;
			}
		}

		finally {
			session.close();
		}

	}

	public String clear() {
		String dspLocn = null;
		locationBarcode = 0;
		locnClass = null;
		locnPickSeq = null;
		area = null;
		zone = null;
		aisle = null;
		bay = null;
		lvl = null;
		return "addLocation";
	}

	public String viewDetails() {
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {

			if (selected == null || selected == "") {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please Select a Location!", "No locations Selected");
				faces.addMessage(null, message);
				return null;
			} else {
				Location location = (Location) session.get(Location.class,
						selected);
				String hql = " from Item where url=:location";
				Query query = session.createQuery(hql);
				query.setString("location", location.getDspLocn());
				List<Item> items = (List<Item>) query.list();
				if (items.size() == 0) {
					FacesMessage message = new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"No Items Present in the location ",
							"Select another location");
					faces.addMessage(null, message);
					return null;
				} else {
					faces.getExternalContext().getSessionMap()
							.put("itemPresent", items);
					return "showLocation";
				}
			}
		} finally {
			selected = null;
			session.close();
		}

	}

	public String addLocation() {
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		Location lcheck = (Location) session.get(Location.class, dspLocn);
		session.beginTransaction();
		if (lcheck != null) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Location Already Exists",
					"Please add a new Location");
			faces.addMessage(null, message);
			return null;
		} else {
			try {
				Location l = new Location();
				l.setAisle(aisle);
				l.setArea(area);
				l.setBay(bay);
				l.setDspLocn(dspLocn);
				l.setLocationBarcode(locationBarcode);
				l.setLocnClass(locnClass);
				l.setLocnPickSeq(locnPickSeq);
				l.setLvl(lvl);
				l.setZone(zone);
				session.save(l);
				session.getTransaction().commit();
				session.close();
				getList();
			} catch (HibernateException e) {

				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Location Barcode Already Exists",
						"Please add a new Location Barcode");
				faces.addMessage(null, message);
				return null;
			}

			return "done";
		}
	}

	public String viewfullDetails() {
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			if (selected == null || selected == "") {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please Select a Location!", "No locations Selected");
				faces.addMessage(null, message);
				return null;
			}

			else

			{
				Location locations = (Location) session.get(Location.class,
						selected);
				faces.getExternalContext().getSessionMap()
						.put("location", locations);
				return "show";
			}
		} finally {
			selected = null;
			session.close();
		}

	}

	public String deleteLocation() {
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		if (selected == null || selected == "") {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Please Select a Location!",
					"No locations Selected");
			faces.addMessage(null, message);
			return null;
		}

		else {
			Location location = (Location) session
					.get(Location.class, selected);
			String hql = "select name from Item where url=:location";
			Query query = session.createQuery(hql);
			query.setString("location", location.getDspLocn());
			List items = query.list();
			if (items.isEmpty()) {
				hql = "DELETE FROM Location " + "WHERE dsp_locn = :lname";
				query = session.createQuery(hql);
				query.setString("lname", selected);
				query.executeUpdate();
				session.getTransaction().commit();
				getList();
			} else {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Cannot Delete Location!",
						"Items present in the Location");
				faces.addMessage(null, message);
			}
			session.close();
			return "afterdel";

		}

	}

	public String getobj() {

		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {

			if (selected == null || selected == "") {
				FacesContext faces = FacesContext.getCurrentInstance();
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please select a Location", "You cannot edit location");
				faces.addMessage(null, message);
				return null;
			} else {
				Location selectedLocation = (Location) session.get(
						Location.class, selected);
				dspLocn = selectedLocation.getDspLocn();
				locationBarcode = selectedLocation.getLocationBarcode();
				locnClass = selectedLocation.getLocnClass();
				locnPickSeq = selectedLocation.getLocnPickSeq();
				area = selectedLocation.getArea();
				zone = selectedLocation.getZone();
				aisle = selectedLocation.getAisle();
				bay = selectedLocation.getBay();
				lvl = selectedLocation.getLvl();

				return "edit";
			}
		} finally {
			selected = null;
			session.close();
		}

	}

	public String updateLocation() {

		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		Location l = (Location) session.get(Location.class, dspLocn);
		l.setAisle(aisle);
		l.setArea(area);
		l.setBay(bay);
		l.setLocationBarcode(locationBarcode);
		l.setLocnClass(locnClass);
		l.setLocnPickSeq(locnPickSeq);
		l.setLvl(lvl);
		l.setZone(zone);
		session.update(l);
		session.getTransaction().commit();
		session.close();
		getList();

		return "done";

	}
}
