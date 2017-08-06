package beans;

import java.util.List;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class itemMaster {
	String name;
	int barcode;
	String style;
	String color;
	String description;
	String season;
	int seasonYear;
	String quality;
	double unitWeight;
	double unitVolume;
	String commodityLevelDesc;
	String uRL;
	SessionFactory sessionFactory;
	String selection;
	int pageno = 1;
	boolean disabled = false;
	boolean pdisabled = false;
	String prevLoc = null;

	public int getPageno() {
		return pageno;
	}

	public void setPageno(int pageno) {
		this.pageno = pageno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBarcode() {
		return barcode;
	}

	public void setBarcode(int barcode) {
		this.barcode = barcode;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;

	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public int getSeasonYear() {
		return seasonYear;
	}

	public void setSeasonYear(int seasonYear) {
		this.seasonYear = seasonYear;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public double getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(double unitWeight) {
		this.unitWeight = unitWeight;
	}

	public double getUnitVolume() {
		return unitVolume;
	}

	public void setUnitVolume(double unitVolume) {
		this.unitVolume = unitVolume;
	}

	public String getCommodityLevelDesc() {
		return commodityLevelDesc;
	}

	public void setCommodityLevelDesc(String commodityLevelDesc) {
		this.commodityLevelDesc = commodityLevelDesc;
	}

	public String getuRL() {
		return uRL;
	}

	public void setuRL(String uRL) {
		this.uRL = uRL;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	@SuppressWarnings("unchecked")
	public List<Item> getList() {
		int pagesize = 5;
		if (pageno == 1) {
			pdisabled = true;

		} else {
			pdisabled = false;
		}
		List<Item> items;
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			int pg = (pageno - 1) * pagesize;
			int totpages = session.createQuery("FROM Item").list().size();
			if (totpages == 0) {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "No Items Present",
						"Item List is empty");
				faces.addMessage(null, message);
				return null;
			} else {
				Query q = session.createQuery("FROM Item");
				q.setFirstResult(pg);
				q.setMaxResults(pagesize);
				items = (List<Item>) q.list();
				if (items.isEmpty() || (pg + pagesize) == totpages
						|| items.size() < 5) {
					disabled = true;
					pg = pg / 5;
				} else
					disabled = false;
				faces.getExternalContext().getSessionMap()
						.put("itemList", items);
				return items;
			}
		} finally {
			session.close();
		}

	}

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

	public String viewDetails() {
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {

			if (selection == null || selection == "") {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Please Select an  Item!",
						"no Items present");
				faces.addMessage(null, message);
				return null;
			} else {
				Item item = (Item) session.get(Item.class, selection);
				faces.getExternalContext().getSessionMap().put("item", item);
				return "show";
			}
		} catch (NullPointerException n) {
			return null;
		} finally {
			selection = null;
			session.close();
		}

	}

	public String clear() {
		name = null;
		barcode = 0;
		style = null;
		color = null;
		description = null;
		season = null;
		seasonYear = 0;
		quality = null;
		unitWeight = 0.0;
		unitVolume = 0.0;
		commodityLevelDesc = null;
		uRL = null;
		selection = null;
		return "add";
	}

	public String addItem() {
		FacesContext faces = FacesContext.getCurrentInstance();
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		Item check = (Item) session.get(Item.class, name);
		Location lcheck = (Location) session.get(Location.class, uRL);
		if (check != null) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Item Already Exists",
					"You cannot add Item Again");
			faces.addMessage(null, message);
			return null;
		} else if (lcheck == null) {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Location to add item does not exist",
					"You cannot add Item ");
			faces.addMessage(null, message);
			return null;
		} else {
			try {
				Item i = new Item();
				i.setName(name);
				i.setBarcode(barcode);
				i.setColor(color);
				i.setCommodityLevelDesc(commodityLevelDesc);
				i.setDescription(description);
				i.setQuality(quality);
				i.setSeason(season);
				i.setSeasonYear(seasonYear);
				i.setStyle(style);
				i.setUnitVolume(unitVolume);
				i.setUnitWeight(unitWeight);
				i.setuRL(uRL);
				session.save(i);
				session.getTransaction().commit();
				session.close();
				getList();
				return "done";
			} catch (HibernateException e) {

				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Item Barcode Already Exists",
						"Please add a new Item Barcode");
				faces.addMessage(null, message);
				return null;
			}

		}

	}

	public String getobj() {

		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {

			if (selection == null || selection == "") {
				FacesContext faces = FacesContext.getCurrentInstance();
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please select an Item to edit",
						"You cannot edit an Item withous Selecting it");
				faces.addMessage(null, message);
				return null;
			} else {
				Item selectedItem = (Item) session.get(Item.class, selection);
				name = selectedItem.getName();
				barcode = selectedItem.getBarcode();
				style = selectedItem.getStyle();
				color = selectedItem.getColor();
				description = selectedItem.getDescription();
				season = selectedItem.getSeason();
				seasonYear = selectedItem.getSeasonYear();
				quality = selectedItem.getQuality();
				unitWeight = selectedItem.getUnitWeight();
				unitVolume = selectedItem.getUnitVolume();
				commodityLevelDesc = selectedItem.getCommodityLevelDesc();
				uRL = selectedItem.getuRL();

				return "edit";
			}
		} finally {
			selection = null;
			session.close();
		}

	}

	public String updateItem() {

		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		Item i = (Item) session.get(Item.class, name);
		i.setBarcode(barcode);
		i.setColor(color);
		i.setDescription(description);
		i.setQuality(quality);
		i.setSeason(season);
		i.setSeasonYear(seasonYear);
		i.setStyle(style);
		i.setUnitVolume(unitVolume);
		i.setUnitWeight(unitWeight);
		session.update(i);
		session.getTransaction().commit();
		session.close();
		getList();

		return "back";

	}

	public String deleteItem() {
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		FacesContext faces = FacesContext.getCurrentInstance();
		if (selection == null || selection == "") {
			FacesMessage message = new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Please Select an Item",
					"You cannot delete without Selecting an Item");
			faces.addMessage(null, message);
			return null;
		} else {
			String hql = "DELETE FROM Item " + "WHERE item_name = :iname";
			Query query = session.createQuery(hql);
			query.setString("iname", selection);
			query.executeUpdate();
			session.getTransaction().commit();
			getList();
			session.close();
			selection = null;
			return "home";
		}
	}

	public String getlocnedit() {

		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		try {

			if (selection == null || selection == "") {
				FacesContext faces = FacesContext.getCurrentInstance();
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Please select an Item to change Location",
						"You cannot change the Location of an item without selecting it");
				faces.addMessage(null, message);
				return null;
			} else {
				Item selectedItem = (Item) session.get(Item.class, selection);
				name = selectedItem.getName();
				barcode = selectedItem.getBarcode();
				style = selectedItem.getStyle();
				color = selectedItem.getColor();
				description = selectedItem.getDescription();
				season = selectedItem.getSeason();
				seasonYear = selectedItem.getSeasonYear();
				quality = selectedItem.getQuality();
				unitWeight = selectedItem.getUnitWeight();
				unitVolume = selectedItem.getUnitVolume();
				commodityLevelDesc = selectedItem.getCommodityLevelDesc();
				uRL = selectedItem.getuRL();
				prevLoc = selectedItem.getuRL();
				return "editLoc";
			}
		} finally {
			selection = null;
			session.close();
		}

	}

	public String changeLocation() {
		Session session;
		session = sessionFactory.openSession();
		session.beginTransaction();
		Item nl = (Item) session.get(Item.class, name);
		nl.setCommodityLevelDesc(prevLoc);
		nl.setuRL(uRL);
		session.update(nl);
		session.getTransaction().commit();
		session.close();
		getList();
		return "back";

	}
}
