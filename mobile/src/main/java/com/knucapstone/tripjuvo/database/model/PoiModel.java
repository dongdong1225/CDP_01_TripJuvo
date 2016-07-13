package com.knucapstone.tripjuvo.database.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="pois")
public class PoiModel implements ClusterItem, Comparable<PoiModel>
{
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_CATEGORY_ID = "category_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_INTRO = "intro";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_LINK = "link";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_FAVORITE = "favorite";
	public static final String COLUMN_CITY = "city";

	@DatabaseField(columnName=COLUMN_ID, generatedId=true) private long id;
	@DatabaseField(foreign=true, index=true) private CategoryModel category;
	@DatabaseField(columnName=COLUMN_NAME) private String name;
	@DatabaseField(columnName=COLUMN_INTRO) private String intro;
	@DatabaseField(columnName=COLUMN_DESCRIPTION) private String description;
	@DatabaseField(columnName=COLUMN_IMAGE) private String image;
	@DatabaseField(columnName=COLUMN_LINK) private String link;
	@DatabaseField(columnName=COLUMN_LATITUDE) private double latitude;
	@DatabaseField(columnName=COLUMN_LONGITUDE) private double longitude;
	@DatabaseField(columnName=COLUMN_ADDRESS) private String address;
	@DatabaseField(columnName=COLUMN_PHONE) private String phone;
	@DatabaseField(columnName=COLUMN_EMAIL) private String email;
	@DatabaseField(columnName=COLUMN_FAVORITE) private boolean favorite;
	private boolean isHotel;
	@DatabaseField(columnName=COLUMN_CITY) private String city;
	private transient int distance;


	// empty constructor
	public PoiModel()
	{
	}

	public boolean isHotel() {
		return isHotel;
	}

	public void setIsHotel(boolean isHotel) {
		this.isHotel = isHotel;
	}

	@Override
	public LatLng getPosition()
	{
		return new LatLng(latitude, longitude);
	}


	@Override
	public int compareTo(PoiModel poiModel)
	{
		if(this.getDistance()<poiModel.getDistance()) return -1;
		else if(this.getDistance()>poiModel.getDistance()) return 1;
		else return 0;
	}


	public long getId()
	{
		return id;
	}


	public void setId(long id)
	{
		this.id = id;
	}


	public CategoryModel getCategory()
	{
		return category;
	}


	public void setCategory(CategoryModel category)
	{
		this.category = category;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getIntro()
	{
		return intro;
	}


	public void setIntro(String intro)
	{
		this.intro = intro;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public String getImage()
	{
		return image;
	}


	public void setImage(String image)
	{
		this.image = image;
	}


	public String getLink()
	{
		return link;
	}


	public void setLink(String link)
	{
		this.link = link;
	}


	public double getLatitude()
	{
		return latitude;
	}


	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}


	public double getLongitude()
	{
		return longitude;
	}


	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}


	public String getAddress()
	{
		return address;
	}


	public void setAddress(String address)
	{
		this.address = address;
	}


	public String getPhone()
	{
		return phone;
	}


	public void setPhone(String phone)
	{
		this.phone = phone;
	}


	public String getEmail()
	{
		return email;
	}


	public void setEmail(String email)
	{
		this.email = email;
	}


	public boolean isFavorite()
	{
		return favorite;
	}


	public void setFavorite(boolean favorite)
	{
		this.favorite = favorite;
	}

	public String getCity() { return city; }

	public void setCity(String city) { this.city = city;}


	public int getDistance()
	{
		return distance;
	}


	public void setDistance(int distance)
	{
		this.distance = distance;
	}
}
