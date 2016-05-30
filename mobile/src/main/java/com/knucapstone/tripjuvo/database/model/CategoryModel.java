package com.knucapstone.tripjuvo.database.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;


@DatabaseTable(tableName="categories")
public class CategoryModel
{
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_MARKER = "marker";

	@DatabaseField(columnName=COLUMN_ID, generatedId=true) private long id;
	@DatabaseField(columnName=COLUMN_NAME) private String name;
	@DatabaseField(columnName=COLUMN_IMAGE) private String image;
	@DatabaseField(columnName=COLUMN_MARKER) private String marker;
	@ForeignCollectionField private ForeignCollection<PoiModel> pois; // one to many


	// empty constructor
	public CategoryModel()
	{
	}


	public long getId()
	{
		return id;
	}


	public void setId(long id)
	{
		this.id = id;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getImage()
	{
		return image;
	}


	public void setImage(String image)
	{
		this.image = image;
	}


	public String getMarker()
	{
		return marker;
	}


	public void setMarker(String marker)
	{
		this.marker = marker;
	}


	public List<PoiModel> getPois()
	{
		List<PoiModel> list = new ArrayList<>();
		for(PoiModel m : pois)
		{
			list.add(m);
		}
		return list;
	}


	public void setPois(ForeignCollection<PoiModel> pois)
	{
		this.pois = pois;
	}
}
